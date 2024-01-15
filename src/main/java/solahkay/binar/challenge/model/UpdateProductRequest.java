package solahkay.binar.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProductRequest {

    @Size(max = 255)
    private String name;

    @Min(1)
    private Long price;

    @Min(0)
    private Long quantity;

    @NotBlank
    @Size(max = 100)
    private String merchantUsername;

}
