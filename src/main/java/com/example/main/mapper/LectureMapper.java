package com.example.main.mapper;
import org.springframework.stereotype.Component;

import com.example.main.dto.LectureDTO;
import com.example.main.model.Lecture;

/**
 * Mapper class for converting between Lecture entity and Lecture DTO.
 */
@Component
public class LectureMapper {

    /**
     * Converts a Lecture entity to a LectureDTO.
     * @param lecture The Lecture entity.
     * @return The corresponding LectureDTO.
     */
    public LectureDTO toLectureDTO(Lecture lecture) {
        if (lecture == null) {
            return null;
        }
        LectureDTO dto = new LectureDTO();
        dto.setId(lecture.getId());
        dto.setLectureDate(lecture.getLectureDate());
        dto.setStartTime(lecture.getStartTime());
        dto.setEndTime(lecture.getEndTime());
        dto.setStudentGroup(lecture.getStudentGroup());
        dto.setRoomNumber(lecture.getRoomNumber());

        if (lecture.getSubject() != null) {
            dto.setSubjectId(lecture.getSubject().getId());
            dto.setSubjectName(lecture.getSubject().getName());
            dto.setSubjectCode(lecture.getSubject().getCode());
        }
        if (lecture.getTeacher() != null) {
            dto.setTeacherId(lecture.getTeacher().getId());
            dto.setTeacherName(lecture.getTeacher().getUser().getFirstName() + " " + lecture.getTeacher().getUser().getLastName());
        }
        return dto;
    }

    /**
     * Converts a LectureDTO to a Lecture entity.
     * Note: The Subject and Teacher objects need to be fetched and set in the service layer,
     * as the DTO only contains their IDs.
     * @param dto The LectureDTO.
     * @return The corresponding Lecture entity (with subject and teacher set to null initially).
     */
    public Lecture toLecture(LectureDTO dto) {
        if (dto == null) {
            return null;
        }
        Lecture lecture = new Lecture();
        lecture.setId(dto.getId()); // ID might be null for new entities
        lecture.setLectureDate(dto.getLectureDate());
        lecture.setStartTime(dto.getStartTime());
        lecture.setEndTime(dto.getEndTime());
        lecture.setStudentGroup(dto.getStudentGroup());
        lecture.setRoomNumber(dto.getRoomNumber());
        // Subject and Teacher will be set in the service layer after fetching them by ID
        return lecture;
    }
}
