package com.example.medappointmentscheduler.domain.model;

import com.example.medappointmentscheduler.utils.Validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChangePasswordModel {
    @NotBlank(message = "{changepass.oldPassword.notEmpty}")
    private String oldPassword;
    @ValidPassword
    private String newPassword;
    @ValidPassword
    private String confirmNewPassword;
}
