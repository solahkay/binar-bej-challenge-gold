package solahkay.binar.challenge.service;

import org.springframework.data.domain.Page;
import solahkay.binar.challenge.model.CreateMerchantRequest;
import solahkay.binar.challenge.model.MerchantResponse;
import solahkay.binar.challenge.model.UpdateMerchantRequest;

public interface MerchantService {

    void createMerchant(CreateMerchantRequest request);

    MerchantResponse getMerchant(String merchantName);

    MerchantResponse updateStatusMerchant(String merchantName, UpdateMerchantRequest request);

    Page<MerchantResponse> getAllOpenMerchant(int page, int size);

}
