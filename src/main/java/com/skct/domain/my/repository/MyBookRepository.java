package com.skct.domain.my.repository;

import com.skct.domain.my.entity.MyBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyBookRepository extends JpaRepository<MyBook, Long> {
    List<MyBook> findByUserIdOrderByCreatedAtDesc(Long userId);
}
