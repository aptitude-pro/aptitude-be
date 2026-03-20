package com.skct.domain.my.repository;

import com.skct.domain.my.entity.MyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MyLogRepository extends JpaRepository<MyLog, Long> {

    Optional<MyLog> findByUserIdAndLogDate(Long userId, LocalDate logDate);

    @Query("SELECT l FROM MyLog l WHERE l.userId = :userId " +
           "AND YEAR(l.logDate) = :year AND MONTH(l.logDate) = :month ORDER BY l.logDate")
    List<MyLog> findByUserIdAndMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month);

    List<MyLog> findByUserIdAndLogDateBetween(Long userId, LocalDate from, LocalDate to);

    @Query("SELECT l FROM MyLog l WHERE l.userId IN :userIds " +
           "AND YEAR(l.logDate) = :year AND MONTH(l.logDate) = :month " +
           "ORDER BY l.logDate, l.userId")
    List<MyLog> findByUserIdInAndMonth(@Param("userIds") List<Long> userIds,
                                       @Param("year") int year,
                                       @Param("month") int month);

    List<MyLog> findByUserIdInAndLogDate(List<Long> userIds, LocalDate logDate);
}
