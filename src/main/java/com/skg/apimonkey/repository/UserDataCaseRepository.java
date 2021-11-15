package com.skg.apimonkey.repository;

import com.skg.apimonkey.domain.data.SwaggerData;
import com.skg.apimonkey.domain.data.UserDataCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDataCaseRepository extends JpaRepository<UserDataCase, Integer> {
    UserDataCase findFirstByDataIdAndDataName(String dataId, String dataName);

    List<UserDataCase> findBySwaggerData(SwaggerData swaggerData);
}