package com.skct.domain.study.repository;

import com.skct.domain.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {
    Optional<Study> findByInviteCode(String inviteCode);

    /** soft-deleted 스터디 포함해서 초대코드 존재 여부 확인 (코드 재사용 방지) */
    @Query(value = "SELECT COUNT(*) > 0 FROM studies WHERE invite_code = :code", nativeQuery = true)
    boolean existsByInviteCodeIgnoringSoftDelete(@Param("code") String code);

    @Query("SELECT s FROM Study s JOIN StudyMember sm ON s.id = sm.studyId WHERE sm.userId = :userId")
    List<Study> findByMemberUserId(Long userId);

    List<Study> findByIsPublicTrueOrderByCreatedAtDesc();

    @Query("SELECT s FROM Study s WHERE s.isPublic = true AND s.name LIKE %:keyword% ORDER BY s.createdAt DESC")
    List<Study> findPublicByKeyword(String keyword);
}
