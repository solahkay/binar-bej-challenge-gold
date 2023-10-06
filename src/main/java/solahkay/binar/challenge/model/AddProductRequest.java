package solahkay.binar.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import solahkay.binar.challenge.entity.Merchant;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddProductRequest {

    @NotBlank(message = "Product can't be blank")
    @Size(max = 100)
    private String name;

    @NotNull
    @Positive(message = "Price must be positive value")
    private Long price;

    @Positive(message = "Stock must be positive value")
    private Integer stock;

    @NotNull(message = "Merchant can't be null")
    private Merchant merchant;

}
