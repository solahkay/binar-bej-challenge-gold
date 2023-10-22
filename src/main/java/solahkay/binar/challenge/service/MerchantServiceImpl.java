package solahkay.binar.challenge.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import solahkay.binar.challenge.entity.Merchant;
import solahkay.binar.challenge.enums.MerchantStatus;
import solahkay.binar.challenge.model.CreateMerchantRequest;
import solahkay.binar.challenge.model.MerchantResponse;
import solahkay.binar.challenge.model.ProductResponse;
import solahkay.binar.challenge.model.UpdateMerchantRequest;
import solahkay.binar.challenge.repository.MerchantRepository;

import java.util.List;
import java.util.UUID;
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
    public void createMerchant(CreateMerchantRequest request) {
        validationService.validate(request);

        if (merchantRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Merchant already exist");
        }

        Merchant merchant = Merchant.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .location(request.getLocation())
                .status(MerchantStatus.CLOSED)
                .build();

        merchantRepository.save(merchant);
    }

    @Override
    @Transactional(readOnly = true)
    public MerchantResponse getMerchant(String merchantName) {
        Merchant merchant = merchantRepository.findByName(merchantName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant not found"));

        return toMerchantResponse(merchant);
    }

    public static MerchantResponse toMerchantResponse(Merchant merchant) {
        List<ProductResponse> productResponses = merchant.getProducts().stream()
                .map(ProductServiceImpl::toProductResponse)
                .collect(Collectors.toList());

        return MerchantResponse.builder()
                .name(merchant.getName())
                .location(merchant.getLocation())
                .status(merchant.getStatus())
                .products(productResponses)
                .build();
    }

    @Override
    @Transactional
    public MerchantResponse updateStatusMerchant(String merchantName, UpdateMerchantRequest request) {
        validationService.validate(request);

        Merchant merchant = merchantRepository.findByName(merchantName)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant not found"));

        merchant.setStatus(request.getStatus());
        merchantRepository.save(merchant);

        return toMerchantResponse(merchant);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MerchantResponse> getAllOpenMerchant(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Merchant> merchants = merchantRepository.findAllByStatus(
                MerchantStatus.OPEN,
                pageable
        );

        List<MerchantResponse> merchantResponses = merchants.getContent().stream()
                .map(MerchantServiceImpl::toMerchantResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(merchantResponses, pageable, merchants.getTotalElements());
    }

}
