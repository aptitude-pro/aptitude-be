package com.skct.domain.study.repository;

import com.skct.domain.study.entity.StudyBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyBookRepository extends JpaRepository<StudyBook, Long> {
    List<StudyBook> findByStudyIdOrderByCreatedAtDesc(Long studyId);
}
