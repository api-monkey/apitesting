package com.skg.apimonkey.domain.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Slf4j
@Table(name = "user_data_case")
public class UserDataCase implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "DATA_ID")
    private String dataId;

    @Column(name = "DATA_NAME")
    private String dataName;

    @ManyToOne
    @JoinColumn(name="SWAGGER_DATA_ID")
    private SwaggerData swaggerData;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "DATA_CASE_CONTENT", columnDefinition = "LONGTEXT")
    private String dataCaseContent;

    @Column(name="UPDATED_DATE")
    private Date updatedDate;

    @Column(name="CREATED_DATE")
    private Date createdDate;
}