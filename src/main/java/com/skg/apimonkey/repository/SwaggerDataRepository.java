package com.skg.apimonkey.repository;

import com.skg.apimonkey.domain.data.SwaggerData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SwaggerDataRepository extends JpaRepository<SwaggerData, Integer> {
    SwaggerData findFirstByUrl(String url);

    SwaggerData findFirstByHashId(String url);
}