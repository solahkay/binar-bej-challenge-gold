package solahkay.binar.challenge.service;

import org.springframework.data.domain.Page;
import solahkay.binar.challenge.entity.Merchant;
import solahkay.binar.challenge.model.MerchantRequest;
import solahkay.binar.challenge.model.MerchantResponse;
import solahkay.binar.challenge.model.RegisterMerchantRequest;
import solahkay.binar.challenge.model.UpdateStatusMerchantRequest;

import java.util.List;

public interface MerchantService {

    void register(RegisterMerchantRequest merchantRequest);

    MerchantResponse updateStatus(Merchant merchant, UpdateStatusMerchantRequest merchantRequest);

    Page<MerchantResponse> getAllOnlineMerchants(MerchantRequest merchantRequest);

}
