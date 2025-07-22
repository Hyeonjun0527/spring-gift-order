package gift.controller;

import gift.config.LoginMember;
import gift.dto.PaginationResponse;
import gift.dto.WishRequest;
import gift.dto.WishResponse;
import gift.entity.Member;
import gift.service.WishService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping
    public PaginationResponse<WishResponse> getWishlistPaged(
        @LoginMember
        Member member,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        return wishService.getWishlistPaged(member.getId(), pageable);
    }

    @PostMapping
    public ResponseEntity<WishResponse> addToWishlist(
        @RequestBody
        WishRequest request,
        @LoginMember
        Member member
    ) {
        WishResponse response = wishService.addToWishlist(request, member);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> removeFromWishlist(
        @PathVariable
        Long wishId,
        @LoginMember
        Member member
    ) {
        wishService.removeFromWishlist(wishId, member);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
