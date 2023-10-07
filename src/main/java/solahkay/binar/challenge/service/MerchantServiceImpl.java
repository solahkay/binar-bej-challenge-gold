package solahkay.binar.challenge.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solahkay.binar.challenge.entity.Merchant;
import solahkay.binar.challenge.exception.ApiException;
import solahkay.binar.challenge.model.MerchantResponse;
import solahkay.binar.challenge.model.RegisterMerchantRequest;
import solahkay.binar.challenge.model.UpdateStatusMerchantRequest;
import solahkay.binar.challenge.repository.MerchantRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;

    private final ValidationService validationService;

    @Autowired
    public MerchantServiceImpl(MerchantRepository merchantRepository, ValidationService validationService) {
        this.merchantRepository = merchantRepository;
        this.validationService = validationService;
    }

    @Override
    @Transactional
    public void register(RegisterMerchantRequest merchantRequest) {
        validationService.validate(merchantRequest);

        if (merchantRepository.existsByName(merchantRequest.getName())) {
            throw new ApiException("Merchant already exists");
        }

        Optional<Boolean> open = Optional.of(merchantRequest.isOpen());

        Merchant merchant = Merchant.builder()
                .name(merchantRequest.getName())
                .location(merchantRequest.getLocation())
                .open(open.orElse(false))
                .build();

        merchantRepository.save(merchant);

        log.info("Merchant registered: {}", merchant.getName());
    }

    @Override
    @Transactional
    public MerchantResponse updateStatus(Merchant merchant, UpdateStatusMerchantRequest merchantRequest) {
        validationService.validate(merchantRequest);

        merchant.setOpen(merchantRequest.isOpen());

        log.info("Merchant status updated: name={}, location={}, open={}",
                merchant.getName(), merchant.getLocation(), merchant.isOpen());

        return MerchantResponse.builder()
                .name(merchant.getName())
                .location(merchant.getLocation())
                .open(merchant.isOpen())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MerchantResponse> getAllOnlineMerchants() {
        List<Merchant> merchants = merchantRepository.findAll();

        return merchants.stream()
                .filter(Merchant::isOpen)
                .map(this::toMerchantResponse)
                .collect(Collectors.toList());
    }

    private MerchantResponse toMerchantResponse(Merchant merchant) {
        return MerchantResponse.builder()
                .name(merchant.getName())
                .location(merchant.getLocation())
                .open(merchant.isOpen())
                .build();
    }

}
