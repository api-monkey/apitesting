package com.skg.apimonkey.repository;

import com.skg.apimonkey.domain.data.ErrorMessageLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorMessageLogRepository extends JpaRepository<ErrorMessageLog, Integer> {
}