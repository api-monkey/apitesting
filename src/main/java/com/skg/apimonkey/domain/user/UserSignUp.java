package com.skg.apimonkey.domain.user;

import com.skg.apimonkey.validator.PasswordMatches;
import com.skg.apimonkey.validator.ValidEmail;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@PasswordMatches
@Data
public class UserSignUp implements Serializable {
    private Integer id;
    @NotNull
    @NotEmpty
    @ValidEmail
    @Size(min = 1, max = 255)
    private String email;
    @NotNull
    @NotEmpty
    @Size(min = 2, max = 255)
    private String firstName;
    @NotNull
    @NotEmpty
    @Size(min = 2, max = 255)
    private String lastName;
    @NotNull
    @NotEmpty
    @Size(min = 3, max = 95)
    private String password;
    @NotNull
    @NotEmpty
    @Size(min = 3, max = 95)
    private String matchingPassword;
}