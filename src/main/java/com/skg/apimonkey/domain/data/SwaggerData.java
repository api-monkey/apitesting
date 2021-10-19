package com.skg.apimonkey.domain.data;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "swagger_data")
public class SwaggerData implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "URL")
    private String url;

    @Column(name = "HASH_ID")
    private String hashId;

    @Column(name = "PAGE_CONTENT", columnDefinition = "TEXT")
    private String pageContent;

    @Column(name="UPDATED_DATE")
    private Date updatedDate;

    @Column(name="CREATED_DATE")
    private Date createdDate;
}