package solahkay.binar.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteUserRequest {

    @NotBlank (message = "Username can't be blank")
    @Size(max = 100)
    private String username;

    @NotBlank(message = "Password can't be blank")
    @Size(max = 100)
    private String password;

}
