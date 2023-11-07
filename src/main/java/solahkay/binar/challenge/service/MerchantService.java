package solahkay.binar.challenge.service;

import org.springframework.data.domain.Page;
import solahkay.binar.challenge.model.RegisterMerchantRequest;
import solahkay.binar.challenge.model.MerchantResponse;
import solahkay.binar.challenge.model.TokenResponse;
import solahkay.binar.challenge.model.UpdateMerchantRequest;

public interface MerchantService {

    TokenResponse registerMerchant(RegisterMerchantRequest request);

    MerchantResponse getMerchant(String merchantUsername);

    MerchantResponse updateStatusMerchant(String merchantUsername, UpdateMerchantRequest request);

    Page<MerchantResponse> getAllOpenMerchant(int page, int size);

}
