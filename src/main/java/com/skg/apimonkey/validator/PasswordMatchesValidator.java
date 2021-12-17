package com.skg.apimonkey.validator;

import com.skg.apimonkey.domain.user.UserSignUp;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
  implements ConstraintValidator<PasswordMatches, Object> {
    
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        UserSignUp user = (UserSignUp) obj;
        return user.getPassword().equals(user.getMatchingPassword());
    }
}