package gift.controller;

import gift.config.LoginMember;
import gift.dto.WishRequestDto;
import gift.dto.WishResponseDto;
import gift.entity.Member;
import gift.service.WishService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wishlist")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    // 위시리스트 추가
    @PostMapping
    public ResponseEntity<WishResponseDto> addWish(
            @LoginMember Member member, // JWT 토큰으로 인증된 사용자 정보 주입
            @Valid @RequestBody WishRequestDto requestDto) {
        WishResponseDto response = wishService.addWish(member, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 위시리스트 조회
    @GetMapping
    public ResponseEntity<Page<WishResponseDto>> getWishes(
            @LoginMember Member member,
            Pageable pageable) {
        Page<WishResponseDto> wishes = wishService.getWishesByMember(member, pageable);
        return ResponseEntity.ok(wishes);
    }

    // 위시리스트 수량 변경
    @PutMapping("/{id}")
    public ResponseEntity<WishResponseDto> updateWishQuantity(
            @LoginMember Member member,
            @PathVariable Long id,
            @RequestBody WishRequestDto requestDto) {
        WishResponseDto response = wishService.updateWishQuantity(member, id, requestDto.getQuantity());
        return ResponseEntity.ok(response);
    }

    // 위시리스트 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWish(@LoginMember Member member, @PathVariable Long id) {
        wishService.deleteWish(member, id);
        return ResponseEntity.noContent().build();
    }

} 