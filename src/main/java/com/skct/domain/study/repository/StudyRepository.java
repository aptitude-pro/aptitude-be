package com.skct.domain.study.repository;

import com.skct.domain.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {
    Optional<Study> findByInviteCode(String inviteCode);

    @Query("SELECT s FROM Study s JOIN StudyMember sm ON s.id = sm.studyId WHERE sm.userId = :userId")
    List<Study> findByMemberUserId(Long userId);

    List<Study> findByIsPublicTrueOrderByCreatedAtDesc();

    @Query("SELECT s FROM Study s WHERE s.isPublic = true AND s.name LIKE %:keyword% ORDER BY s.createdAt DESC")
    List<Study> findPublicByKeyword(String keyword);
}
