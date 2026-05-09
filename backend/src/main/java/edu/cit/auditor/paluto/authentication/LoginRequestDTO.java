package edu.cit.auditor.paluto.authentication;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}

