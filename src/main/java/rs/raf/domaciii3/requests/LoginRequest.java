package rs.raf.domaciii3.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "Username is mandatory field")
    private String username;

    @NotBlank(message = "Password is mandatory field")
    private String password;
}
