package com.example.main.mapper;
import org.springframework.stereotype.Component;

import com.example.main.dto.AttendanceRecordDTO;
import com.example.main.dto.MarkAttendanceRequest;
import com.example.main.model.AttendanceRecord;

import java.time.LocalDateTime;

/**
 * Mapper class for converting between AttendanceRecord entity and AttendanceRecord DTOs.
 */
@Component
public class AttendanceRecordMapper {

    private final LectureMapper lectureMapper;
    private final StudentMapper studentMapper;

    public AttendanceRecordMapper(LectureMapper lectureMapper, StudentMapper studentMapper) {
        this.lectureMapper = lectureMapper;
        this.studentMapper = studentMapper;
    }

    /**
     * Converts an AttendanceRecord entity to an AttendanceRecordDTO.
     * Includes nested DTOs for Lecture and Student.
     * @param record The AttendanceRecord entity.
     * @return The corresponding AttendanceRecordDTO.
     */
    public AttendanceRecordDTO toAttendanceRecordDTO(AttendanceRecord record) {
        if (record == null) {
            return null;
        }
        AttendanceRecordDTO dto = new AttendanceRecordDTO();
        dto.setId(record.getId());
        dto.setStatus(record.getStatus());
        dto.setMarkingTimestamp(record.getMarkingTimestamp());

        if (record.getLecture() != null) {
            dto.setLectureId(record.getLecture().getId());
            dto.setLecture(lectureMapper.toLectureDTO(record.getLecture()));
        }
        if (record.getStudent() != null) {
            dto.setStudentId(record.getStudent().getId());
            dto.setStudent(studentMapper.toStudentDTO(record.getStudent()));
        }
        if (record.getMarkedByTeacher() != null && record.getMarkedByTeacher().getUser() != null) {
            dto.setMarkedByTeacherId(record.getMarkedByTeacher().getId());
            dto.setMarkedByTeacherName(record.getMarkedByTeacher().getUser().getFirstName() + " " + record.getMarkedByTeacher().getUser().getLastName());
        }
        return dto;
    }

    /**
     * Converts a MarkAttendanceRequest DTO to an AttendanceRecord entity.
     * Note: Lecture, Student, and MarkedByTeacher objects need to be fetched and set in the service layer.
     * @param request The MarkAttendanceRequest DTO.
     * @return The corresponding AttendanceRecord entity (with associations set to null initially).
     */
    public AttendanceRecord toAttendanceRecord(MarkAttendanceRequest request) {
        if (request == null) {
            return null;
        }
        AttendanceRecord record = new AttendanceRecord();
        record.setStatus(request.getStatus());
        record.setMarkingTimestamp(LocalDateTime.now()); // Set current timestamp
        // Lecture, Student, MarkedByTeacher will be set in the service layer
        return record;
    }
}
