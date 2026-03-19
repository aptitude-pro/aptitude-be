package com.skct.domain.study.service;

import com.skct.domain.result.entity.ExamResult;
import com.skct.domain.result.repository.ExamResultRepository;
import com.skct.domain.study.entity.Study;
import com.skct.domain.study.entity.StudyBook;
import com.skct.domain.study.entity.StudyLog;
import com.skct.domain.study.entity.StudyLogCategory;
import com.skct.domain.study.entity.StudyMember;
import com.skct.domain.study.entity.StudyNotice;
import com.skct.domain.study.repository.StudyBookRepository;
import com.skct.domain.study.repository.StudyLogRepository;
import com.skct.domain.study.repository.StudyMemberRepository;
import com.skct.domain.study.repository.StudyNoticeRepository;
import com.skct.domain.study.repository.StudyRepository;
import com.skct.domain.user.entity.User;
import com.skct.domain.user.repository.UserRepository;
import com.skct.global.exception.CustomException;
import com.skct.global.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

    private final StudyRepository studyRepository;
    private final StudyMemberRepository memberRepository;
    private final StudyNoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final ExamResultRepository resultRepository;
    private final StudyBookRepository bookRepository;
    private final StudyLogRepository logRepository;

    public List<StudyResponse> getMyStudies(Long userId) {
        List<Study> studies = studyRepository.findByMemberUserId(userId);
        return studies.stream()
                .map(s -> toResponse(s, userId))
                .collect(Collectors.toList());
    }

    public List<StudyResponse> getPublicStudies(String keyword, Long userId) {
        List<Study> studies = (keyword != null && !keyword.isBlank())
                ? studyRepository.findPublicByKeyword(keyword)
                : studyRepository.findByIsPublicTrueOrderByCreatedAtDesc();
        return studies.stream()
                .map(s -> toResponse(s, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public StudyResponse joinPublicStudy(Long studyId, Long userId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
        if (!study.isPublic()) throw new CustomException(ErrorCode.ACCESS_DENIED);
        if (memberRepository.existsByStudyIdAndUserId(studyId, userId))
            throw new CustomException(ErrorCode.ALREADY_JOINED_STUDY);
        long currentCount = memberRepository.countByStudyId(studyId);
        if (currentCount >= study.getMaxMembers())
            throw new CustomException(ErrorCode.STUDY_FULL);

        memberRepository.save(StudyMember.builder()
                .studyId(studyId).userId(userId).role(StudyMember.Role.MEMBER).build());
        study.updateMemberCount((int) (currentCount + 1));
        return toResponse(study, userId);
    }

    public StudyDetailResponse getStudy(Long studyId, Long userId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));

        StudyMember myMembership = memberRepository.findByStudyIdAndUserId(studyId, userId).orElse(null);
        List<StudyMember> members = memberRepository.findByStudyId(studyId);

        List<Long> memberUserIds = members.stream().map(StudyMember::getUserId).toList();
        Map<Long, User> userMap = userRepository.findAllById(memberUserIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        List<MemberDto> memberDtos = members.stream()
                .map(m -> {
                    User u = userMap.get(m.getUserId());
                    return MemberDto.builder()
                            .userId(m.getUserId())
                            .nickname(u != null ? u.getNickname() : "알 수 없음")
                            .role(m.getRole().name())
                            .joinedAt(m.getJoinedAt())
                            .build();
                })
                .collect(Collectors.toList());

        return StudyDetailResponse.builder()
                .id(study.getId())
                .name(study.getName())
                .examType(study.getExamType())
                .inviteCode(study.getInviteCode())
                .maxMembers(study.getMaxMembers())
                .isPublic(study.isPublic())
                .memberCount((int) memberRepository.countByStudyId(studyId))
                .myRole(myMembership != null ? myMembership.getRole().name() : null)
                .members(memberDtos)
                .build();
    }

    public List<RankingDto> getRanking(Long studyId) {
        studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
        List<StudyMember> members = memberRepository.findByStudyId(studyId);
        List<Long> userIds = members.stream().map(StudyMember::getUserId).toList();

        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        Map<Long, List<ExamResult>> resultMap = resultRepository.findByUserIdIn(userIds).stream()
                .collect(Collectors.groupingBy(ExamResult::getUserId));

        return members.stream().map(m -> {
            List<ExamResult> results = resultMap.getOrDefault(m.getUserId(), List.of());
            User user = userMap.get(m.getUserId());

            int bestScore = results.stream().mapToInt(ExamResult::getTotalScore).max().orElse(0);
            int avgScore = results.isEmpty() ? 0 : (int) Math.round(results.stream().mapToInt(ExamResult::getTotalScore).average().orElse(0));

            return RankingDto.builder()
                    .userId(m.getUserId())
                    .nickname(user != null ? user.getNickname() : "알 수 없음")
                    .examCount(results.size())
                    .bestScore(bestScore)
                    .avgScore(avgScore)
                    .build();
        })
        .sorted(Comparator.comparingInt(RankingDto::getBestScore).reversed())
        .collect(Collectors.toList());
    }

    @Transactional
    public StudyResponse createStudy(Long userId, String name, String examType, Integer maxMembers, boolean isPublic) {
        String inviteCode = generateInviteCode();

        Study study = Study.builder()
                .name(name)
                .examType(examType)
                .inviteCode(inviteCode)
                .maxMembers(maxMembers)
                .isPublic(isPublic)
                .createdBy(userId)
                .memberCount(1)
                .build();

        study = studyRepository.save(study);

        StudyMember leader = StudyMember.builder()
                .studyId(study.getId())
                .userId(userId)
                .role(StudyMember.Role.LEADER)
                .build();
        memberRepository.save(leader);

        return toResponse(study, userId);
    }

    @Transactional
    public StudyResponse joinStudy(Long userId, String inviteCode) {
        Study study = studyRepository.findByInviteCode(inviteCode.toUpperCase())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INVITE_CODE));

        if (memberRepository.existsByStudyIdAndUserId(study.getId(), userId)) {
            throw new CustomException(ErrorCode.ALREADY_JOINED_STUDY);
        }

        long currentCount = memberRepository.countByStudyId(study.getId());
        if (currentCount >= study.getMaxMembers()) {
            throw new CustomException(ErrorCode.STUDY_FULL);
        }

        StudyMember member = StudyMember.builder()
                .studyId(study.getId())
                .userId(userId)
                .role(StudyMember.Role.MEMBER)
                .build();
        memberRepository.save(member);
        study.updateMemberCount((int) (currentCount + 1));

        return toResponse(study, userId);
    }

    @Transactional
    public void leaveStudy(Long studyId, Long userId) {
        if (!memberRepository.existsByStudyIdAndUserId(studyId, userId)) {
            throw new CustomException(ErrorCode.STUDY_NOT_FOUND);
        }
        memberRepository.deleteByStudyIdAndUserId(studyId, userId);
        long count = memberRepository.countByStudyId(studyId);
        Study study = studyRepository.findById(studyId).orElseThrow();
        study.updateMemberCount((int) count);
    }

    @Transactional
    public NoticeDto createNotice(Long studyId, Long userId, String title, String content) {
        StudyMember member = memberRepository.findByStudyIdAndUserId(studyId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));
        if (member.getRole() != StudyMember.Role.LEADER) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        StudyNotice notice = StudyNotice.builder()
                .studyId(studyId)
                .authorId(userId)
                .title(title)
                .content(content)
                .build();
        notice = noticeRepository.save(notice);
        return toNoticeDto(notice);
    }

    public StudyDashboardResponse getDashboard(Long studyId, Long userId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
        if (!memberRepository.existsByStudyIdAndUserId(studyId, userId))
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        List<StudyMember> members = memberRepository.findByStudyId(studyId);

        List<Long> dashboardUserIds = members.stream().map(StudyMember::getUserId).toList();
        Map<Long, User> dashboardUserMap = userRepository.findAllById(dashboardUserIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        Map<Long, List<ExamResult>> dashboardResultMap = resultRepository
                .findByUserIdIn(dashboardUserIds).stream()
                .filter(r -> study.getExamType().equals(r.getExamType()))
                .collect(Collectors.groupingBy(ExamResult::getUserId));

        List<MemberStatDto> memberStats = members.stream().map(m -> {
            User user = dashboardUserMap.get(m.getUserId());
            List<ExamResult> results = dashboardResultMap.getOrDefault(m.getUserId(), List.of());
            return MemberStatDto.builder()
                    .userId(m.getUserId())
                    .nickname(user != null ? user.getNickname() : "알 수 없음")
                    .examCount(results.size())
                    .build();
        }).collect(Collectors.toList());

        Map<String, List<TimeSeriesPoint>> grouped = new LinkedHashMap<>();
        for (StudyMember m : members) {
            User user = dashboardUserMap.get(m.getUserId());
            String nickname = user != null ? user.getNickname() : "알 수 없음";
            List<ExamResult> results = dashboardResultMap.getOrDefault(m.getUserId(), List.of());
            for (ExamResult r : results) {
                String key = buildGroupKey(r);
                grouped.computeIfAbsent(key, k -> new ArrayList<>())
                        .add(TimeSeriesPoint.builder()
                                .nickname(nickname)
                                .date(r.getCreatedAt().format(DateTimeFormatter.ofPattern("MM/dd")))
                                .score(r.getTotalScore())
                                .examTitle(r.getExamTitle())
                                .build());
            }
        }

        List<TimeSeriesGroup> timeSeries = grouped.entrySet().stream()
                .map(e -> TimeSeriesGroup.builder()
                        .groupKey(e.getKey())
                        .points(e.getValue())
                        .build())
                .collect(Collectors.toList());

        return StudyDashboardResponse.builder()
                .memberStats(memberStats)
                .timeSeries(timeSeries)
                .build();
    }

    private String buildGroupKey(ExamResult r) {
        if (r.getExamYear() != null && r.getExamPeriod() != null && r.getPlatform() != null) {
            return r.getExamYear() + "_" + r.getExamPeriod() + "_" + r.getPlatform();
        }
        return r.getExamTitle() != null ? r.getExamTitle() : "기타";
    }

    @Transactional
    public void deleteStudy(Long studyId, Long userId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
        if (!study.getCreatedBy().equals(userId))
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        study.softDelete();
    }

    @Transactional
    public void kickMember(Long studyId, Long leaderId, Long targetUserId) {
        StudyMember leader = memberRepository.findByStudyIdAndUserId(studyId, leaderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));
        if (leader.getRole() != StudyMember.Role.LEADER)
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        if (leaderId.equals(targetUserId))
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        memberRepository.deleteByStudyIdAndUserId(studyId, targetUserId);
        long count = memberRepository.countByStudyId(studyId);
        studyRepository.findById(studyId).ifPresent(s -> s.updateMemberCount((int) count));
    }

    @Transactional
    public void deleteNotice(Long studyId, Long userId, Long noticeId) {
        StudyMember member = memberRepository.findByStudyIdAndUserId(studyId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));
        if (member.getRole() != StudyMember.Role.LEADER)
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        noticeRepository.deleteById(noticeId);
    }

    public List<NoticeDto> getNotices(Long studyId) {
        studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
        return noticeRepository.findByStudyIdOrderByCreatedAtDesc(studyId).stream()
                .map(this::toNoticeDto)
                .collect(Collectors.toList());
    }

    // ────────────────── Book ──────────────────

    public List<StudyBookDto> getBooks(Long studyId) {
        studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
        return bookRepository.findByStudyIdOrderByCreatedAtDesc(studyId).stream()
                .map(this::toBookDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudyBookDto addBook(Long studyId, Long userId, String title, String author, String publisher, String isbn) {
        if (!memberRepository.existsByStudyIdAndUserId(studyId, userId))
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        StudyBook book = StudyBook.builder()
                .studyId(studyId).registeredBy(userId)
                .title(title).author(author).publisher(publisher).isbn(isbn)
                .build();
        return toBookDto(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long studyId, Long userId, Long bookId) {
        StudyBook book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
        StudyMember member = memberRepository.findByStudyIdAndUserId(studyId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));
        boolean isLeader = member.getRole() == StudyMember.Role.LEADER;
        boolean isRegistrant = book.getRegisteredBy().equals(userId);
        if (!isLeader && !isRegistrant) throw new CustomException(ErrorCode.ACCESS_DENIED);
        bookRepository.delete(book);
    }

    // ────────────────── StudyLog ──────────────────

    public List<StudyLogDto> getMyLogs(Long studyId, Long userId, String yearMonth) {
        if (!memberRepository.existsByStudyIdAndUserId(studyId, userId))
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        YearMonth ym = YearMonth.parse(yearMonth);
        return logRepository.findByUserIdAndStudyIdAndMonth(userId, studyId, ym.getYear(), ym.getMonthValue())
                .stream().map(this::toLogDto).collect(Collectors.toList());
    }

    @Transactional
    public StudyLogDto upsertLog(Long studyId, Long userId, LocalDate logDate,
                                  Long bookId, String memo, List<CategoryInput> categoryInputs) {
        if (!memberRepository.existsByStudyIdAndUserId(studyId, userId))
            throw new CustomException(ErrorCode.ACCESS_DENIED);

        StudyLog log = logRepository.findByUserIdAndStudyIdAndLogDate(userId, studyId, logDate)
                .orElse(null);

        if (log == null) {
            log = StudyLog.builder()
                    .userId(userId).studyId(studyId).logDate(logDate)
                    .bookId(bookId).memo(memo)
                    .build();
            log = logRepository.save(log);
        } else {
            log.update(bookId, memo);
            log.clearCategories();
        }

        if (categoryInputs != null) {
            for (CategoryInput ci : categoryInputs) {
                if (ci.getProblemCount() > 0) {
                    StudyLogCategory cat = StudyLogCategory.builder()
                            .studyLog(log).categoryName(ci.getCategoryName())
                            .problemCount(ci.getProblemCount())
                            .build();
                    log.getCategories().add(cat);
                }
            }
        }
        return toLogDto(log);
    }

    @Transactional
    public void deleteLog(Long studyId, Long userId, Long logId) {
        StudyLog log = logRepository.findById(logId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
        if (!log.getUserId().equals(userId) || !log.getStudyId().equals(studyId))
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        logRepository.delete(log);
    }

    public List<MemberLogDto> getMemberLogs(Long studyId, Long requesterId, String yearMonth) {
        if (!memberRepository.existsByStudyIdAndUserId(studyId, requesterId))
            throw new CustomException(ErrorCode.ACCESS_DENIED);

        YearMonth ym = YearMonth.parse(yearMonth);
        List<StudyLog> logs = logRepository.findByStudyIdAndMonth(studyId, ym.getYear(), ym.getMonthValue());

        List<Long> userIds = logs.stream().map(StudyLog::getUserId).distinct().toList();
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        List<Long> bookIds = logs.stream()
                .filter(l -> l.getBookId() != null).map(StudyLog::getBookId).distinct().toList();
        Map<Long, String> bookTitleMap = bookRepository.findAllById(bookIds).stream()
                .collect(Collectors.toMap(StudyBook::getId, StudyBook::getTitle));

        return logs.stream().map(l -> {
            User user = userMap.get(l.getUserId());
            List<CategorySummaryDto> cats = l.getCategories().stream()
                    .map(c -> CategorySummaryDto.builder()
                            .categoryName(c.getCategoryName())
                            .problemCount(c.getProblemCount())
                            .build())
                    .collect(Collectors.toList());
            int total = cats.stream().mapToInt(CategorySummaryDto::getProblemCount).sum();
            return MemberLogDto.builder()
                    .userId(l.getUserId())
                    .nickname(user != null ? user.getNickname() : "알 수 없음")
                    .logDate(l.getLogDate())
                    .bookId(l.getBookId())
                    .bookTitle(l.getBookId() != null ? bookTitleMap.get(l.getBookId()) : null)
                    .totalProblems(total)
                    .categories(cats)
                    .memo(l.getMemo())
                    .build();
        }).collect(Collectors.toList());
    }

    public List<MemberTodayDto> getTodaySummary(Long studyId, Long requesterId) {
        if (!memberRepository.existsByStudyIdAndUserId(studyId, requesterId))
            throw new CustomException(ErrorCode.ACCESS_DENIED);

        List<StudyMember> members = memberRepository.findByStudyId(studyId);
        List<Long> userIds = members.stream().map(StudyMember::getUserId).toList();
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        LocalDate today = LocalDate.now();
        List<StudyLog> todayLogs = logRepository.findByStudyIdAndLogDate(studyId, today);
        Map<Long, StudyLog> logByUser = todayLogs.stream()
                .collect(Collectors.toMap(StudyLog::getUserId, l -> l));

        return members.stream().map(m -> {
            User user = userMap.get(m.getUserId());
            StudyLog log = logByUser.get(m.getUserId());
            List<CategorySummaryDto> cats = log == null ? List.of()
                    : log.getCategories().stream()
                        .map(c -> CategorySummaryDto.builder()
                                .categoryName(c.getCategoryName())
                                .problemCount(c.getProblemCount())
                                .build())
                        .collect(Collectors.toList());
            int total = cats.stream().mapToInt(CategorySummaryDto::getProblemCount).sum();
            return MemberTodayDto.builder()
                    .userId(m.getUserId())
                    .nickname(user != null ? user.getNickname() : "알 수 없음")
                    .hasLog(log != null)
                    .totalProblems(total)
                    .categories(cats)
                    .memo(log != null ? log.getMemo() : null)
                    .build();
        }).collect(Collectors.toList());
    }

    private StudyBookDto toBookDto(StudyBook b) {
        return StudyBookDto.builder()
                .id(b.getId()).studyId(b.getStudyId())
                .title(b.getTitle()).author(b.getAuthor())
                .publisher(b.getPublisher()).isbn(b.getIsbn())
                .registeredBy(b.getRegisteredBy())
                .createdAt(b.getCreatedAt())
                .build();
    }

    private StudyLogDto toLogDto(StudyLog l) {
        List<CategorySummaryDto> cats = l.getCategories().stream()
                .map(c -> CategorySummaryDto.builder()
                        .categoryName(c.getCategoryName())
                        .problemCount(c.getProblemCount())
                        .build())
                .collect(Collectors.toList());
        int total = cats.stream().mapToInt(CategorySummaryDto::getProblemCount).sum();
        return StudyLogDto.builder()
                .id(l.getId()).userId(l.getUserId()).studyId(l.getStudyId())
                .bookId(l.getBookId()).logDate(l.getLogDate()).memo(l.getMemo())
                .categories(cats).totalProblems(total)
                .build();
    }

    private StudyResponse toResponse(Study s, Long userId) {
        StudyMember.Role myRole = null;
        if (userId != null) {
            myRole = memberRepository.findByStudyIdAndUserId(s.getId(), userId)
                    .map(StudyMember::getRole)
                    .orElse(null);
        }
        return StudyResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .examType(s.getExamType())
                .inviteCode(s.getInviteCode())
                .maxMembers(s.getMaxMembers())
                .isPublic(s.isPublic())
                .memberCount(s.getMemberCount())
                .myRole(myRole != null ? myRole.name() : null)
                .build();
    }

    private NoticeDto toNoticeDto(StudyNotice n) {
        return NoticeDto.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .createdAt(n.getCreatedAt())
                .build();
    }

    private String generateInviteCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        do {
            sb.setLength(0);
            for (int i = 0; i < 6; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
        } while (studyRepository.countByInviteCodeIgnoringSoftDelete(sb.toString()) > 0);
        return sb.toString();
    }

    @Getter @Builder
    public static class StudyResponse {
        private Long id;
        private String name;
        private String examType;
        private String inviteCode;
        private Integer maxMembers;
        @JsonProperty("isPublic")
        private boolean isPublic;
        private Integer memberCount;
        private String myRole;
        private Double avgScore;
    }

    @Getter @Builder
    public static class StudyDetailResponse {
        private Long id;
        private String name;
        private String examType;
        private String inviteCode;
        private Integer maxMembers;
        @JsonProperty("isPublic")
        private boolean isPublic;
        private Integer memberCount;
        private String myRole;
        private List<MemberDto> members;
    }

    @Getter @Builder
    public static class MemberDto {
        private Long userId;
        private String nickname;
        private String role;
        private LocalDateTime joinedAt;
    }

    @Getter @Builder
    public static class RankingDto {
        private Long userId;
        private String nickname;
        private int examCount;
        private int bestScore;
        private int avgScore;
    }

    @Getter @Builder
    public static class NoticeDto {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime createdAt;
    }

    @Getter @Builder
    public static class StudyDashboardResponse {
        private List<MemberStatDto> memberStats;
        private List<TimeSeriesGroup> timeSeries;
    }

    @Getter @Builder
    public static class MemberStatDto {
        private Long userId;
        private String nickname;
        private int examCount;
    }

    @Getter @Builder
    public static class TimeSeriesPoint {
        private String nickname;
        private String date;
        private int score;
        private String examTitle;
    }

    @Getter @Builder
    public static class TimeSeriesGroup {
        private String groupKey;
        private List<TimeSeriesPoint> points;
    }

    @Getter @Builder
    public static class StudyBookDto {
        private Long id;
        private Long studyId;
        private String title;
        private String author;
        private String publisher;
        private String isbn;
        private Long registeredBy;
        private LocalDateTime createdAt;
    }

    @Getter @Builder
    public static class StudyLogDto {
        private Long id;
        private Long userId;
        private Long studyId;
        private Long bookId;
        private LocalDate logDate;
        private String memo;
        private int totalProblems;
        private List<CategorySummaryDto> categories;
    }

    @Getter @Builder
    public static class CategorySummaryDto {
        private String categoryName;
        private int problemCount;
    }

    @Getter @Builder
    public static class MemberTodayDto {
        private Long userId;
        private String nickname;
        private boolean hasLog;
        private int totalProblems;
        private String memo;
        private List<CategorySummaryDto> categories;
    }

    @Getter @Builder
    public static class MemberLogDto {
        private Long userId;
        private String nickname;
        private LocalDate logDate;
        private Long bookId;
        private String bookTitle;
        private int totalProblems;
        private String memo;
        private List<CategorySummaryDto> categories;
    }

    @Getter
    public static class CategoryInput {
        private String categoryName;
        private int problemCount;
    }
}
