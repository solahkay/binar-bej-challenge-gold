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
import solahkay.binar.challenge.entity.Role;
import solahkay.binar.challenge.entity.User;
import solahkay.binar.challenge.enums.MerchantStatus;
import solahkay.binar.challenge.enums.UserRole;
import solahkay.binar.challenge.model.MerchantResponse;
import solahkay.binar.challenge.model.ProductResponse;
import solahkay.binar.challenge.model.RegisterMerchantRequest;
import solahkay.binar.challenge.model.TokenResponse;
import solahkay.binar.challenge.model.UpdateMerchantRequest;
import solahkay.binar.challenge.repository.MerchantRepository;
import solahkay.binar.challenge.repository.RoleRepository;
import solahkay.binar.challenge.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final ValidationService validationService;

    private final JwtService jwtService;

    @Autowired
    public MerchantServiceImpl(MerchantRepository merchantRepository,
                               UserRepository userRepository,
                               RoleRepository roleRepository,
                               ValidationService validationService,
                               JwtService jwtService) {
        this.merchantRepository = merchantRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.validationService = validationService;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public TokenResponse registerMerchant(RegisterMerchantRequest request) {
        validationService.validate(request);

        User user = userRepository.findByUsername(request.getUserUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (merchantRepository.existsByNameOrUser(request.getName(), user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Merchant already exist");
        }

        Set<Role> roles = user.getRoles();
        log.info("roles: {}", roles);
        Role merchantRole = roleRepository.findByName(UserRole.MERCHANT);
        roles.add(merchantRole);
        user.setRoles(roles);

        userRepository.save(user);

        Merchant merchant = Merchant.builder()
                .username(request.getUsername())
                .name(request.getName())
                .location(request.getLocation())
                .status(MerchantStatus.CLOSED)
                .user(user)
                .build();

        merchantRepository.save(merchant);

        String token = jwtService.generateToken(user);
        return TokenResponse.builder()
                .token(token)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MerchantResponse getMerchant(String merchantUsername) {
        Merchant merchant = merchantRepository.findByUsername(merchantUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant not found"));

        return toMerchantResponse(merchant);
    }

    public static MerchantResponse toMerchantResponse(Merchant merchant) {
        List<ProductResponse> productResponses = merchant.getProducts().stream()
                .map(ProductServiceImpl::toProductResponse)
                .collect(Collectors.toList());

        return MerchantResponse.builder()
                .username(merchant.getUsername())
                .name(merchant.getName())
                .location(merchant.getLocation())
                .status(merchant.getStatus())
                .products(productResponses)
                .build();
    }

    @Override
    @Transactional
    public MerchantResponse updateStatusMerchant(String merchantUsername, UpdateMerchantRequest request) {
        validationService.validate(request);

        Merchant merchant = merchantRepository.findByUsername(merchantUsername)
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
