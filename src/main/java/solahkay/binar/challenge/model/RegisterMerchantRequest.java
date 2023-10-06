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
public class RegisterMerchantRequest {

    @NotBlank(message = "Name can't be blank")
    @Size(max = 100)
    private String name;

    @NotBlank (message = "Location can't be blank")
    @Size(max = 255)
    private String location;

    private boolean open;

}
