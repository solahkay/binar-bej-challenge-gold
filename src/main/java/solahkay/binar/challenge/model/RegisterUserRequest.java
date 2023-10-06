package solahkay.binar.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequest {

    @NotBlank (message = "Username can't be blank")
    @Size(max = 100)
    private String username;

    @Size(max = 100)
    private String name;

    @NotBlank(message = "Email can't be blank")
    @Size(max = 100)
    @Email
    private String email;

    @NotBlank(message = "Password can't be blank")
    @Size(max = 100)
    private String password;

}
