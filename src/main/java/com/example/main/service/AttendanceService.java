package com.example.main.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.main.dto.AttendanceRecordDTO;
import com.example.main.dto.BulkMarkAttendanceRequest;
import com.example.main.dto.MarkAttendanceRequest;
import com.example.main.dto.OverallStudentAttendanceDTO;
import com.example.main.dto.StudentAttendanceSummaryDTO;
import com.example.main.dto.UserDTO;
import com.example.main.exception.InvalidOperationException;
import com.example.main.exception.ResourceNotFoundException;
import com.example.main.mapper.AttendanceRecordMapper;
import com.example.main.model.AttendanceRecord;
import com.example.main.model.AttendanceStatus;
import com.example.main.model.Lecture;
import com.example.main.model.Student;
import com.example.main.model.Subject; // Ensure this imports the Subject entity
import com.example.main.model.Teacher;
import com.example.main.repository.AttendanceRecordRepository;

import java.time.LocalDate;
// import java.time.LocalDateTime; // No longer needed for direct parameter passing
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing AttendanceRecord entities.
 * Handles business logic related to marking, retrieving, and reporting attendance.
 */
@Service
@Transactional
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final LectureService lectureService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final SubjectService subjectService; // Now configured to provide Subject entities when needed
    private final AttendanceRecordMapper attendanceRecordMapper;

    public AttendanceService(AttendanceRecordRepository attendanceRecordRepository,
                             LectureService lectureService,
                             StudentService studentService,
                             TeacherService teacherService,
                             SubjectService subjectService,
                             AttendanceRecordMapper attendanceRecordMapper) {
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.lectureService = lectureService;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.subjectService = subjectService;
        this.attendanceRecordMapper = attendanceRecordMapper;
    }

    /**
     * Marks attendance for multiple students in a specific lecture.
     * This is typically used by a teacher for a class.
     * @param request The DTO containing lecture ID and a list of student attendance statuses.
     * @param teacherId The ID of the teacher marking attendance (for audit trail).
     * @return A list of AttendanceRecordDTOs of the marked records.
     * @throws ResourceNotFoundException if lecture, student, or teacher is not found.
     * @throws InvalidOperationException if the lecture date is in the future.
     */
    public List<AttendanceRecordDTO> markBulkAttendance(BulkMarkAttendanceRequest request, Long teacherId) {
        Lecture lecture = lectureService.getLectureEntityById(request.getLectureId());
        Teacher markedByTeacher = teacherService.getTeacherEntityById(teacherId);

        // Ensure attendance is not marked for future lectures
        if (lecture.getLectureDate().isAfter(LocalDate.now())) {
            throw new InvalidOperationException("Cannot mark attendance for a future lecture.");
        }

        List<AttendanceRecordDTO> markedRecords = new ArrayList<>();

        for (MarkAttendanceRequest markRequest : request.getAttendanceRecords()) {
            Student student = studentService.getStudentEntityById(markRequest.getStudentId());

            Optional<AttendanceRecord> existingRecordOptional = attendanceRecordRepository.findByLectureAndStudent(lecture, student);
            AttendanceRecord attendanceRecord;

            if (existingRecordOptional.isPresent()) {
                // Update existing record
                attendanceRecord = existingRecordOptional.get();
                attendanceRecord.setStatus(markRequest.getStatus());
                attendanceRecord.setMarkingTimestamp(java.time.LocalDateTime.now()); // Keep LocalDateTime here if appropriate for marking timestamp
                attendanceRecord.setMarkedByTeacher(markedByTeacher); // Update who marked it if it changed
            } else {
                // Create new record
                attendanceRecord = new AttendanceRecord();
                attendanceRecord.setLecture(lecture);
                attendanceRecord.setStudent(student);
                attendanceRecord.setStatus(markRequest.getStatus());
                attendanceRecord.setMarkedByTeacher(markedByTeacher);
                attendanceRecord.setMarkingTimestamp(java.time.LocalDateTime.now()); // Keep LocalDateTime here if appropriate for marking timestamp
            }
            AttendanceRecord savedRecord = attendanceRecordRepository.save(attendanceRecord);
            markedRecords.add(attendanceRecordMapper.toAttendanceRecordDTO(savedRecord));
        }
        return markedRecords;
    }

    /**
     * Updates a single attendance record.
     * This can be used for correcting a mistake by a teacher.
     * @param recordId The ID of the attendance record to update.
     * @param updatedStatus The new attendance status.
     * @param teacherId The ID of the teacher updating attendance.
     * @return The updated AttendanceRecordDTO.
     * @throws ResourceNotFoundException if the attendance record or teacher is not found.
     * @throws InvalidOperationException if attempting to modify an old record beyond a grace period.
     */
    public AttendanceRecordDTO updateAttendanceRecord(Long recordId, AttendanceStatus updatedStatus, Long teacherId) {
        AttendanceRecord existingRecord = attendanceRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with ID: " + recordId));

        Teacher teacher = teacherService.getTeacherEntityById(teacherId);

        // Optional: Implement a grace period for modifications, e.g., 24 hours after lecture or marking
        // For example:
        // LocalDateTime gracePeriodEnd = existingRecord.getLecture().getLectureDate().atTime(existingRecord.getLecture().getEndTime()).plusHours(24);
        // if (LocalDateTime.now().isAfter(gracePeriodEnd)) {
        //      throw new InvalidOperationException("Attendance record can no longer be modified.");
        // }

        existingRecord.setStatus(updatedStatus);
        existingRecord.setMarkingTimestamp(java.time.LocalDateTime.now()); // Update timestamp of last modification
        existingRecord.setMarkedByTeacher(teacher); // Update who modified it

        AttendanceRecord savedRecord = attendanceRecordRepository.save(existingRecord);
        return attendanceRecordMapper.toAttendanceRecordDTO(savedRecord);
    }

    /**
     * Retrieves all attendance records for a specific lecture.
     * @param lectureId The ID of the lecture.
     * @return A list of AttendanceRecordDTOs for the lecture.
     * @throws ResourceNotFoundException if the lecture is not found.
     */
    public List<AttendanceRecordDTO> getAttendanceByLecture(Long lectureId) {
        Lecture lecture = lectureService.getLectureEntityById(lectureId);
        return attendanceRecordRepository.findByLecture(lecture).stream()
                .map(attendanceRecordMapper::toAttendanceRecordDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all attendance records for a specific student, optionally within a date range.
     * @param studentId The ID of the student.
     * @param startDate (Optional) Start date for the report.
     * @param endDate (Optional) End date for the report.
     * @return A list of AttendanceRecordDTOs for the student.
     * @throws ResourceNotFoundException if the student is not found.
     */
    public List<AttendanceRecordDTO> getAttendanceByStudent(Long studentId, LocalDate startDate, LocalDate endDate) {
        Student student = studentService.getStudentEntityById(studentId);
        List<AttendanceRecord> records;

        if (startDate != null && endDate != null) {
            // FIX: Pass LocalDate directly to the repository method
            records = attendanceRecordRepository.findByStudentAndLectureDateBetween(
                    student, startDate, endDate); // Changed from startDate.atStartOfDay(), endDate.atTime(23, 59, 59)
        } else {
            records = attendanceRecordRepository.findByStudent(student);
        }
        return records.stream()
                .map(attendanceRecordMapper::toAttendanceRecordDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calculates the overall attendance summary for a specific student.
     * @param studentId The ID of the student.
     * @return OverallStudentAttendanceDTO containing summary per subject and overall percentage.
     * @throws ResourceNotFoundException if the student is not found.
     */
    public OverallStudentAttendanceDTO getStudentOverallAttendanceSummary(Long studentId) {
        Student student = studentService.getStudentEntityById(studentId);

        // FIX: Now calling getAllSubjectEntities() which returns List<Subject>
        List<Subject> subjectsStudentIsEnrolledIn = subjectService.getAllSubjectEntities().stream()
                .filter(subject -> {
                    // Line 189: 'subject' is now a Subject entity, so getDepartment() is valid
                    return subject.getDepartment() != null && subject.getDepartment().getId().equals(student.getDepartment().getId());
                })
                .collect(Collectors.toList());
        // Line 183: The type mismatch error is resolved because subjectsStudentIsEnrolledIn is correctly List<Subject>

        List<StudentAttendanceSummaryDTO> subjectSummaries = new ArrayList<>();
        long overallTotalLectures = 0;
        long overallLecturesAttended = 0;

        // Assuming studentGroup is a combination of Batch Year and Section
        String studentGroup = student.getBatchYear() + (student.getSection() != null && !student.getSection().isEmpty() ? " " + student.getSection() : "");

        for (Subject subject : subjectsStudentIsEnrolledIn) {
            // Count lectures attended (PRESENT) for this student in this subject
            long lecturesAttended = attendanceRecordRepository.countPresentByStudentAndSubject(studentId, subject.getId());

            // Count total lectures for this subject up to today's date for this student's group
            long totalLectures = attendanceRecordRepository.countTotalLecturesForSubjectAndStudentGroup(
                    subject.getId(), studentGroup, LocalDate.now()); // Pass today's date to only count past/current lectures

            double attendancePercentage = (totalLectures > 0) ? ((double) lecturesAttended / totalLectures) * 100 : 0.0;

            subjectSummaries.add(new StudentAttendanceSummaryDTO(
                    subject.getId(),
                    subject.getName(),
                    subject.getCode(),
                    totalLectures,
                    lecturesAttended,
                    attendancePercentage
            ));

            overallTotalLectures += totalLectures;
            overallLecturesAttended += lecturesAttended;
        }

        double overallPercentage = (overallTotalLectures > 0) ? ((double) overallLecturesAttended / overallTotalLectures) * 100 : 0.0;

        OverallStudentAttendanceDTO overallDTO = new OverallStudentAttendanceDTO();
        overallDTO.setStudentId(student.getId());
        overallDTO.setUser(student.getUser() != null ? UserDTO.fromEntity(student.getUser()):null);
        overallDTO.setRollNumber(student.getRollNumber());
        overallDTO.setDepartmentName(student.getDepartment() != null ? student.getDepartment().getName() : null);
        overallDTO.setBatchYear(student.getBatchYear());
        overallDTO.setSection(student.getSection());
        overallDTO.setOverallPercentage(overallPercentage);
        overallDTO.setSubjectSummaries(subjectSummaries);

        return overallDTO;
    }
}