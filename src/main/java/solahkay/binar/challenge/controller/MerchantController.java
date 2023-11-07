package solahkay.binar.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import solahkay.binar.challenge.model.MerchantResponse;
import solahkay.binar.challenge.model.PagingResponse;
import solahkay.binar.challenge.model.RegisterMerchantRequest;
import solahkay.binar.challenge.model.TokenResponse;
import solahkay.binar.challenge.model.UpdateMerchantRequest;
import solahkay.binar.challenge.model.WebResponse;
import solahkay.binar.challenge.service.MerchantService;

import java.util.List;

@RestController
@RequestMapping("api/v1/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    @Autowired
    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @PostMapping(
            path = "register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public WebResponse<TokenResponse> registerMerchant(@RequestBody RegisterMerchantRequest request) {
        TokenResponse tokenResponse = merchantService.registerMerchant(request);
        return WebResponse.<TokenResponse>builder().data(tokenResponse).build();
    }

    @GetMapping(
            path = "{username}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<MerchantResponse> getMerchant(@PathVariable("username") String merchantUsername) {
        MerchantResponse merchantResponse = merchantService.getMerchant(merchantUsername);
        return WebResponse.<MerchantResponse>builder().data(merchantResponse).build();
    }

    @PatchMapping(
            path = "{username}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<MerchantResponse> updateStatusMerchant(@PathVariable("username") String merchantUsername,
                                                              @RequestBody UpdateMerchantRequest request) {
        MerchantResponse merchantResponse = merchantService.updateStatusMerchant(merchantUsername, request);
        return WebResponse.<MerchantResponse>builder().data(merchantResponse).build();
    }

    @GetMapping(
            path = "online",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<MerchantResponse>> getAllOpenMerchant(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<MerchantResponse> allOpenMerchant = merchantService.getAllOpenMerchant(page, size);
        return WebResponse.<List<MerchantResponse>>builder()
                .data(allOpenMerchant.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(allOpenMerchant.getNumber())
                        .totalPage(allOpenMerchant.getTotalPages())
                        .size(allOpenMerchant.getSize())
                        .build())
                .build();
    }

}
