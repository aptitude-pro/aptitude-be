package com.skct.domain.study.repository;

import com.skct.domain.study.entity.StudyNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyNoticeRepository extends JpaRepository<StudyNotice, Long> {
    List<StudyNotice> findByStudyIdOrderByCreatedAtDesc(Long studyId);
    void deleteByStudyId(Long studyId);
}
