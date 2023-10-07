package solahkay.binar.challenge.entity.identifier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class OrderDetailId implements Serializable {

    private String order;

    private String product;

}
