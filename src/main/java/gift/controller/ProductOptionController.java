package gift.controller;

import gift.config.LoginMember;
import gift.dto.ProductOptionRequestDto;
import gift.dto.ProductOptionResponseDto;
import gift.entity.Member;
import gift.entity.MemberRole;
import gift.service.ProductOptionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/{productId}/options")
public class ProductOptionController {

    private final ProductOptionService optionService;

    public ProductOptionController(ProductOptionService optionService) {
        this.optionService = optionService;
    }

    // 옵션 목록 조회 (일반 사용자 & 관리자 모두 접근 가능)
    @GetMapping
    public ResponseEntity<Page<ProductOptionResponseDto>> getOptions(
            @PathVariable Long productId,
            Pageable pageable) {
        Page<ProductOptionResponseDto> options = optionService.getOptionsByProductId(productId, pageable);
        return ResponseEntity.ok(options);
    }

    // 옵션 추가 (관리자 전용)
    @PostMapping
    public ResponseEntity<?> addOption(
            @PathVariable Long productId,
            @Valid @RequestBody ProductOptionRequestDto dto,
            @LoginMember Member member) { // ✅ JwtAuthFilter에서 세팅된 Member 주입
        if (member.getRole() != MemberRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }

        ProductOptionResponseDto response = optionService.addOption(productId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
