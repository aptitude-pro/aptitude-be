package com.skct.domain.study.repository;

import com.skct.domain.study.entity.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
    List<StudyMember> findByStudyId(Long studyId);
    Optional<StudyMember> findByStudyIdAndUserId(Long studyId, Long userId);
    boolean existsByStudyIdAndUserId(Long studyId, Long userId);
    long countByStudyId(Long studyId);
    void deleteByStudyIdAndUserId(Long studyId, Long userId);
}
