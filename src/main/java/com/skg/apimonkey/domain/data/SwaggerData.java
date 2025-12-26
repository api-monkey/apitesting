package com.skg.apimonkey.domain.data;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "swagger_data")
public class SwaggerData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "URL")
    private String url;

    @Column(name = "PASSED_URL")
    private String passedUrl;

    @Column(name = "HASH_ID")
    private String hashId;

    @Column(name = "PAGE_CONTENT", columnDefinition = "LONGTEXT")
    private String pageContent;

    @Column(name = "UPDATED_DATE")
    private Date updatedDate;

    @Column(name = "CREATED_DATE")
    private Date createdDate;
}