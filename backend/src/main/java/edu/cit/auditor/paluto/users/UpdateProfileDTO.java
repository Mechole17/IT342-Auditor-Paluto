package edu.cit.auditor.paluto.users;

import lombok.Data;

@Data
public class UpdateProfileDTO {
    private String firstname;
    private String lastname;
    private String address;
}