package com.skg.apimonkey.domain.user;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "user")
public class User implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Integer id;
    @Column(name = "LOGIN", length = 95)
    private String login;
    @Column(name = "PASSWORD", length = 95)
    private String password;
    @Column(name = "TYPE", length = 45)
    @Enumerated(EnumType.STRING)
    private UserType type;
}