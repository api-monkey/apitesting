package com.skg.apimonkey.domain.user;

import com.skg.apimonkey.domain.user.auth2.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "login")
})
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "LOGIN", nullable = false, length = 95)
    private String login;
    @Column(name = "PASSWORD", length = 95)
    private String password;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "TYPE", nullable = false, length = 45)
    @Enumerated(EnumType.STRING)
    private UserType type;
    @Enumerated(EnumType.STRING)
    @Column(name = "PROVIDER", nullable = false, length = 95)
    private AuthProvider provider;
    @Column(name = "PROVIDER_ID")
    private String providerId;
    @Column(name = "IMAGE_URL")
    private String imageUrl;
    @Column(name = "EMAIL_VERIFIED")
    @Type(type="org.hibernate.type.NumericBooleanType")
    private boolean emailVerified = false;
    @Column(name = "CREATED")
    private Date created;
}