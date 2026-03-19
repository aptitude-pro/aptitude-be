package com.skct.domain.my.service;

import com.skct.domain.my.entity.MyBook;
import com.skct.domain.my.entity.MyLog;
import com.skct.domain.my.entity.MyLogCategory;
import com.skct.domain.my.repository.MyBookRepository;
import com.skct.domain.my.repository.MyLogRepository;
import com.skct.global.exception.CustomException;
import com.skct.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyStudyService {

    private final MyBookRepository bookRepository;
    private final MyLogRepository logRepository;

    // ─── 개인 책 ───

    public List<MyBookDto> getMyBooks(Long userId) {
        return bookRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toBookDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MyBookDto addMyBook(Long userId, String title, Integer year, String examType) {
        MyBook book = MyBook.builder()
                .userId(userId).title(title).year(year).examType(examType)
                .build();
        return toBookDto(bookRepository.save(book));
    }

    @Transactional
    public void deleteMyBook(Long userId, Long bookId) {
        MyBook book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
        if (!book.getUserId().equals(userId))
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        bookRepository.delete(book);
    }

    // ─── 개인 학습 기록 ───

    public List<MyLogDto> getMyLogs(Long userId, String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        return logRepository.findByUserIdAndMonth(userId, ym.getYear(), ym.getMonthValue())
                .stream().map(this::toLogDto).collect(Collectors.toList());
    }

    @Transactional
    public MyLogDto upsertMyLog(Long userId, LocalDate logDate, Long bookId, String memo,
                                List<CategoryInput> categoryInputs) {
        MyLog log = logRepository.findByUserIdAndLogDate(userId, logDate).orElse(null);

        if (log == null) {
            log = MyLog.builder()
                    .userId(userId).logDate(logDate).bookId(bookId).memo(memo)
                    .build();
            log = logRepository.save(log);
        } else {
            log.update(bookId, memo);
            log.clearCategories();
        }

        if (categoryInputs != null) {
            for (CategoryInput ci : categoryInputs) {
                if (ci.getProblemCount() > 0) {
                    MyLogCategory cat = MyLogCategory.builder()
                            .myLog(log).categoryName(ci.getCategoryName())
                            .problemCount(ci.getProblemCount())
                            .build();
                    log.getCategories().add(cat);
                }
            }
        }
        return toLogDto(log);
    }

    @Transactional
    public void deleteMyLog(Long userId, Long logId) {
        MyLog log = logRepository.findById(logId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
        if (!log.getUserId().equals(userId))
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        logRepository.delete(log);
    }

    // ─── 변환 ───

    private MyBookDto toBookDto(MyBook b) {
        return MyBookDto.builder()
                .id(b.getId()).userId(b.getUserId())
                .title(b.getTitle()).year(b.getYear()).examType(b.getExamType())
                .createdAt(b.getCreatedAt())
                .build();
    }

    private MyLogDto toLogDto(MyLog l) {
        List<CategorySummaryDto> cats = l.getCategories().stream()
                .map(c -> CategorySummaryDto.builder()
                        .categoryName(c.getCategoryName())
                        .problemCount(c.getProblemCount())
                        .build())
                .collect(Collectors.toList());
        int total = cats.stream().mapToInt(CategorySummaryDto::getProblemCount).sum();
        return MyLogDto.builder()
                .id(l.getId()).userId(l.getUserId())
                .bookId(l.getBookId()).logDate(l.getLogDate()).memo(l.getMemo())
                .categories(cats).totalProblems(total)
                .build();
    }

    // ─── DTO ───

    @Getter @Builder
    public static class MyBookDto {
        private Long id;
        private Long userId;
        private String title;
        private Integer year;
        private String examType;
        private LocalDateTime createdAt;
    }

    @Getter @Builder
    public static class MyLogDto {
        private Long id;
        private Long userId;
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

    @Getter
    public static class CategoryInput {
        private String categoryName;
        private int problemCount;
    }
}
