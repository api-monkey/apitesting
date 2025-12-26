package com.skg.apimonkey.domain.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "error_messages_log")
public class ErrorMessageLog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "URL")
    private String url;

    @Column(name = "USER_LOGIN")
    private String userLogin;

    @Column(name = "ERROR_MESSAGE", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "STACK_TRACE", columnDefinition = "LONGTEXT")
    private String stackTrace;

    @Column(name = "CREATED_DATE")
    private Date createdDate;
}