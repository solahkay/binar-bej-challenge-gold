package solahkay.binar.challenge.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import solahkay.binar.challenge.entity.Merchant;
import solahkay.binar.challenge.exception.ApiException;
import solahkay.binar.challenge.model.MerchantResponse;
import solahkay.binar.challenge.model.RegisterMerchantRequest;
import solahkay.binar.challenge.model.UpdateStatusMerchantRequest;
import solahkay.binar.challenge.repository.MerchantRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MerchantServiceTest {

    @Autowired
    MerchantService merchantService;

    @Autowired
    MerchantRepository merchantRepository;

    @BeforeEach
    void setUp() {
        merchantRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() {
        RegisterMerchantRequest merchantRequest = new RegisterMerchantRequest();
        merchantRequest.setName("Toko Emas Retail");
        merchantRequest.setLocation("Bandung");
        merchantRequest.setOpen(false);

        merchantService.register(merchantRequest);

        Merchant merchant = merchantRepository.findByName("Toko Emas Retail").orElse(null);

        assertNotNull(merchant);
        assertEquals(merchant.getName(), merchantRequest.getName());
        assertEquals(merchant.getLocation(), merchantRequest.getLocation());
        assertEquals(merchant.isOpen(), merchantRequest.isOpen());
    }

    @Test
    void testRegisterFailed() {
        RegisterMerchantRequest merchant = new RegisterMerchantRequest();
        merchant.setName("Toko Kelontong Pak Budi");
        merchant.setLocation("Jonggol");
        merchant.setOpen(false);

        merchantService.register(merchant);

        RegisterMerchantRequest merchantRequest = new RegisterMerchantRequest();
        merchantRequest.setName("Toko Kelontong Pak Budi");
        merchantRequest.setLocation("Bogor");
        merchantRequest.setOpen(true);

        assertThrows(ApiException.class, () -> merchantService.register(merchantRequest));
    }

    @Test
    void testRegisterFailedNull() {
        assertThrows(IllegalArgumentException.class, () -> merchantService.register(null));
    }

    @Test
    void testUpdateStatusSuccess() {
        RegisterMerchantRequest registerMerchantRequest = new RegisterMerchantRequest();
        registerMerchantRequest.setName("Ucok Klontong");
        registerMerchantRequest.setLocation("Ujung Genteng");
        registerMerchantRequest.setOpen(false);

        merchantService.register(registerMerchantRequest);

        Merchant merchant = merchantRepository.findByName("Ucok Klontong").orElse(null);

        UpdateStatusMerchantRequest merchantRequest = new UpdateStatusMerchantRequest();
        merchantRequest.setOpen(true);

        MerchantResponse merchantResponse = merchantService.updateStatus(merchant, merchantRequest);

        assertNotNull(merchant);
        assertEquals(merchant.getName(), merchantResponse.getName());
        assertEquals(merchant.getLocation(), merchantResponse.getLocation());
        assertEquals(merchant.isOpen(), merchantResponse.isOpen());
    }

    @Test
    void testUpdateStatusFailed() {
        assertThrows(IllegalArgumentException.class, () -> merchantService.updateStatus(null, null));
    }

    @Test
    void testGetAllOnlineMerchants() {
        RegisterMerchantRequest merchantRequest1 = new RegisterMerchantRequest();
        merchantRequest1.setName("Toko Pak Slamet");
        merchantRequest1.setLocation("Depok");
        merchantRequest1.setOpen(false);

        merchantService.register(merchantRequest1);

        RegisterMerchantRequest merchantRequest2 = new RegisterMerchantRequest();
        merchantRequest2.setName("Permata Emas");
        merchantRequest2.setLocation("Brebes");
        merchantRequest2.setOpen(true);

        merchantService.register(merchantRequest2);

        List<MerchantResponse> merchants = merchantService.getAllOnlineMerchants();

        assertNotNull(merchants);
        assertEquals(1, merchants.size());
        assertEquals("Permata Emas", merchants.get(0).getName());
        assertEquals("Brebes", merchants.get(0).getLocation());
    }

}
