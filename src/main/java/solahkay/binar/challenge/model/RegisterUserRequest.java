package solahkay.binar.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequest {

    @NotBlank
    @Size(max = 40)
    private String username;

    @NotBlank
    @Size(max = 100)
    private String password;

    @Size(max = 255)
    private String name;

    @Email
    @NotBlank
    @Size(max = 100)
    private String email;

}
