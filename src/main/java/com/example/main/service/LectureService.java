package com.example.main.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.main.dto.LectureDTO;
import com.example.main.exception.InvalidOperationException;
import com.example.main.exception.ResourceNotFoundException;
import com.example.main.mapper.LectureMapper;
import com.example.main.model.Lecture;
import com.example.main.model.Subject;
import com.example.main.model.Teacher;
import com.example.main.repository.LectureRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing Lecture entities.
 * Handles business logic related to lecture scheduling, retrieval, and updates.
 */
@Service
@Transactional
public class LectureService {

    private final LectureRepository lectureRepository;
    private final SubjectService subjectService; // Inject SubjectService to fetch Subject entity
    private final TeacherService teacherService; // Inject TeacherService to fetch Teacher entity
    private final LectureMapper lectureMapper;

    public LectureService(LectureRepository lectureRepository, SubjectService subjectService, TeacherService teacherService, LectureMapper lectureMapper) {
        this.lectureRepository = lectureRepository;
        this.subjectService = subjectService;
        this.teacherService = teacherService;
        this.lectureMapper = lectureMapper;
    }

    /**
     * Schedules a new lecture.
     * @param lectureDTO The DTO containing lecture details.
     * @return The scheduled LectureDTO.
     * @throws ResourceNotFoundException if the associated subject or teacher is not found.
     * @throws InvalidOperationException if end time is before start time.
     */
    public LectureDTO scheduleLecture(LectureDTO lectureDTO) {
        Subject subject = subjectService.getSubjectEntityById(lectureDTO.getSubjectId());
        Teacher teacher = teacherService.getTeacherEntityById(lectureDTO.getTeacherId());

        if (lectureDTO.getEndTime().isBefore(lectureDTO.getStartTime())) {
            throw new InvalidOperationException("Lecture end time cannot be before start time.");
        }

        Lecture lecture = lectureMapper.toLecture(lectureDTO);
        lecture.setSubject(subject);
        lecture.setTeacher(teacher);

        Lecture savedLecture = lectureRepository.save(lecture);
        return lectureMapper.toLectureDTO(savedLecture);
    }

    /**
     * Retrieves a lecture by its ID.
     * @param id The ID of the lecture.
     * @return The LectureDTO if found.
     * @throws ResourceNotFoundException if the lecture is not found.
     */
    public LectureDTO getLectureById(Long id) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with ID: " + id));
        return lectureMapper.toLectureDTO(lecture);
    }

    /**
     * Retrieves a lecture entity by its ID. Used internally by other services.
     * @param id The ID of the lecture.
     * @return The Lecture entity if found.
     * @throws ResourceNotFoundException if the lecture is not found.
     */
    public Lecture getLectureEntityById(Long id) {
        return lectureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with ID: " + id));
    }

    /**
     * Retrieves all lectures.
     * @return A list of all LectureDTOs.
     */
    public List<LectureDTO> getAllLectures() {
        return lectureRepository.findAll().stream()
                .map(lectureMapper::toLectureDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves lectures by teacher.
     * @param teacherId The ID of the teacher.
     * @param date (Optional) Filter by date. If null, returns all for teacher.
     * @return A list of LectureDTOs for the specified teacher.
     * @throws ResourceNotFoundException if the teacher is not found.
     */
    public List<LectureDTO> getLecturesByTeacher(Long teacherId, LocalDate date) {
        Teacher teacher = teacherService.getTeacherEntityById(teacherId);
        List<Lecture> lectures;
        if (date != null) {
            lectures = lectureRepository.findByTeacherAndLectureDate(teacher, date);
        } else {
            lectures = lectureRepository.findByTeacher(teacher);
        }
        return lectures.stream()
                .map(lectureMapper::toLectureDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves lectures by student group and optionally by date.
     * @param studentGroup The student group identifier.
     * @param date (Optional) Filter by date. If null, returns all for student group.
     * @return A list of LectureDTOs for the specified student group.
     */
    public List<LectureDTO> getLecturesByStudentGroup(String studentGroup, LocalDate date) {
        List<Lecture> lectures;
        if (date != null) {
            lectures = lectureRepository.findByStudentGroupAndLectureDate(studentGroup, date);
        } else {
            lectures = lectureRepository.findByStudentGroup(studentGroup);
        }
        return lectures.stream()
                .map(lectureMapper::toLectureDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing lecture.
     * @param id The ID of the lecture to update.
     * @param lectureDTO The DTO containing updated lecture details.
     * @return The updated LectureDTO.
     * @throws ResourceNotFoundException if the lecture, subject, or teacher is not found.
     * @throws InvalidOperationException if end time is before start time.
     */
    public LectureDTO updateLecture(Long id, LectureDTO lectureDTO) {
        Lecture existingLecture = lectureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with ID: " + id));

        Subject subject = subjectService.getSubjectEntityById(lectureDTO.getSubjectId());
        Teacher teacher = teacherService.getTeacherEntityById(lectureDTO.getTeacherId());

        if (lectureDTO.getEndTime().isBefore(lectureDTO.getStartTime())) {
            throw new InvalidOperationException("Lecture end time cannot be before start time.");
        }

        existingLecture.setSubject(subject);
        existingLecture.setTeacher(teacher);
        existingLecture.setLectureDate(lectureDTO.getLectureDate());
        existingLecture.setStartTime(lectureDTO.getStartTime());
        existingLecture.setEndTime(lectureDTO.getEndTime());
        existingLecture.setStudentGroup(lectureDTO.getStudentGroup());
        existingLecture.setRoomNumber(lectureDTO.getRoomNumber());

        Lecture updatedLecture = lectureRepository.save(existingLecture);
        return lectureMapper.toLectureDTO(updatedLecture);
    }

    /**
     * Deletes a lecture by its ID.
     * NOTE: Deleting a lecture will typically cascade delete associated attendance records.
     * Consider implications for historical data.
     * @param id The ID of the lecture to delete.
     * @throws ResourceNotFoundException if the lecture is not found.
     */
    public void deleteLecture(Long id) {
        if (!lectureRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lecture not found with ID: " + id);
        }
        lectureRepository.deleteById(id);
    }
}
