package com.skct.domain.study.repository;

import com.skct.domain.study.entity.StudyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudyLogRepository extends JpaRepository<StudyLog, Long> {

    Optional<StudyLog> findByUserIdAndStudyIdAndLogDate(Long userId, Long studyId, LocalDate logDate);

    @Query("SELECT l FROM StudyLog l WHERE l.userId = :userId AND l.studyId = :studyId " +
           "AND YEAR(l.logDate) = :year AND MONTH(l.logDate) = :month ORDER BY l.logDate")
    List<StudyLog> findByUserIdAndStudyIdAndMonth(
            @Param("userId") Long userId,
            @Param("studyId") Long studyId,
            @Param("year") int year,
            @Param("month") int month);

    List<StudyLog> findByStudyIdAndLogDate(Long studyId, LocalDate logDate);
}
