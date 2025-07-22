package gift.wish.adapter.web;

import gift.common.annotation.LoginMember;
import gift.member.domain.model.Member;
import gift.wish.adapter.web.mapper.WishWebMapper;
import gift.wish.application.port.in.WishUseCase;
import gift.wish.application.port.in.dto.WishAddRequest;
import gift.wish.application.port.in.dto.WishResponse;
import gift.wish.application.port.in.dto.WishUpdateQuantityRequest;
import gift.wish.domain.model.Wish;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishUseCase wishUseCase;

    public WishController(WishUseCase wishUseCase) {
        this.wishUseCase = wishUseCase;
    }

    @GetMapping
    public ResponseEntity<Page<WishResponse>> getWishes(Pageable pageable, @LoginMember Member member) {
        Page<Wish> wishes = wishUseCase.getWishes(member.id(), pageable);
        Page<WishResponse> response = wishes.map(WishWebMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<WishResponse> addWish(@RequestBody @Valid WishAddRequest request, @LoginMember Member member) {
        Wish wish = wishUseCase.addWish(request, member.id());
        WishResponse response = WishWebMapper.toResponse(wish);
        return ResponseEntity.created(URI.create("/api/wishes/" + response.wishId())).body(response);
    }

    @PutMapping("/{wishId}/quantity")
    public ResponseEntity<WishResponse> updateWishQuantity(@PathVariable Long wishId,
                                                           @RequestBody @Valid WishUpdateQuantityRequest request,
                                                           @LoginMember Member member) {
        Wish wish = wishUseCase.updateWishQuantity(wishId, request.quantity(), member.id());
        WishResponse response = WishWebMapper.toResponse(wish);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> deleteWish(@PathVariable Long wishId, @LoginMember Member member) {
        wishUseCase.deleteWish(wishId, member.id());
        return ResponseEntity.noContent().build();
    }
} 