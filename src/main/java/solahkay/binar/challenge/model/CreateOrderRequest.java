package solahkay.binar.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.SortedSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotBlank(message = "Destination address can't be blank")
    @Size(max = 100)
    private String destinationAddress;

    private boolean completed;

    private SortedSet<OrderDetailRequest> orderDetails;

}
