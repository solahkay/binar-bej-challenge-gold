package solahkay.binar.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import solahkay.binar.challenge.enums.MerchantStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchantResponse {

    private String username;

    private String name;

    private String location;

    private MerchantStatus status;

    private List<ProductResponse> products;

}
