package solahkay.binar.challenge.entity.identifier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class OrderDetailId implements Serializable {

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "product_id")
    private String productId;

}
