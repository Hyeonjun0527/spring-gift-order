package gift.controller;

import gift.dto.OptionRequestDto;
import gift.dto.OptionResponseDto;
import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.service.OptionService;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final OptionService optionService;

    public ProductController(ProductService productService,  OptionService optionService) {
        this.productService = productService;
        this.optionService = optionService;
    }

    // 상품 관련 API
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto requestDto) {

        ProductResponseDto responseDto = productService.create(requestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.id())
                .toUri(); // location 생성

        return ResponseEntity.created(location).body(responseDto);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(productService.find(productId));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable("productId") Long productId,
            @Valid @RequestBody ProductRequestDto requestDto
    ) {

        return ResponseEntity.ok(productService.update(productId, requestDto));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") Long productId) {
        productService.delete(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getProducts(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        List<ProductResponseDto> responseDtoList = productService.getAllProducts(pageable);
        return ResponseEntity.ok(responseDtoList);
    }

    // 상품 옵션 관련 API
    @PostMapping("/{productId}/options")
    public ResponseEntity<OptionResponseDto> createOption(
            @PathVariable Long productId,
            @Valid @RequestBody OptionRequestDto requestDto
    ) {

        OptionResponseDto responseDto = optionService.create(productId,requestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.id())
                .toUri();

        return ResponseEntity.created(location).body(responseDto);
    }

    @GetMapping("/{productId}/options")
    public ResponseEntity<List<OptionResponseDto>> getOptions(
            @PathVariable Long productId
    ){
        List<OptionResponseDto> options = optionService.find(productId);
        return ResponseEntity.ok(options);
    }

    @PutMapping("/{productId}/options/{optionId}")
    public ResponseEntity<OptionResponseDto> updateOption(
            @PathVariable Long productId,
            @PathVariable Long optionId,
            @Valid @RequestBody OptionRequestDto requestDto
    ){

        return ResponseEntity.ok(optionService.update(productId, optionId, requestDto));
    }

    @DeleteMapping("/{productId}/options/{optionId}")
    public ResponseEntity<Void> deleteOption(
            @PathVariable Long productId,
            @PathVariable Long optionId
    ){
        optionService.delete(productId, optionId);
        return ResponseEntity.noContent().build();
    }
}
