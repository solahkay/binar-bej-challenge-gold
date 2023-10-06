package solahkay.binar.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProductRequest {

    @JsonIgnore
    @NotBlank(message = "Id can't be blank")
    private String id;

    @Size(max = 100)
    private String name;

    @Positive(message = "Price must be positive value")
    private Long price;

    @Positive(message = "Stock must be positive value")
    private Integer stock;

}
