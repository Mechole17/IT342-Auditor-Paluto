package edu.cit.auditor.paluto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CookRegistrationDTO {
    //User attr
    private String firstname;
    private String lastname;
    private String address;
    private String email;
    private String password;
    private String role;

    //Cook attr
    private Double hourly_rate;
    private Integer years_xp;
    private String bio;


}