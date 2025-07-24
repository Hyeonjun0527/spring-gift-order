package gift.member.adapter.web;

import gift.common.annotation.RequireAdmin;
import gift.member.adapter.web.mapper.MemberWebMapper;
import gift.member.application.port.in.MemberUseCase;
import gift.member.application.port.in.dto.CreateMemberRequest;
import gift.member.application.port.in.dto.MemberResponse;
import gift.member.application.port.in.dto.UpdateMemberRequest;
import gift.member.domain.model.Member;
import gift.product.application.port.in.ProductUseCase;
import gift.product.application.port.in.dto.AdminUpdateProductRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final MemberUseCase memberUseCase;
    private final ProductUseCase productUseCase;

    public AdminController(MemberUseCase memberUseCase, ProductUseCase productUseCase) {
        this.memberUseCase = memberUseCase;
        this.productUseCase = productUseCase;
    }

    @RequireAdmin
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        List<Member> members = memberUseCase.getAllMembers();
        List<MemberResponse> memberResponses = members.stream()
                .map(MemberWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(memberResponses);
    }

    @RequireAdmin
    @PostMapping("/members")
    public ResponseEntity<Void> createMember(@Valid @RequestBody CreateMemberRequest request) {
        Member member = memberUseCase.createMember(request);
        return ResponseEntity.created(URI.create("/api/admin/members/" + member.id())).build();
    }

    @RequireAdmin
    @PutMapping("/members/{id}")
    public ResponseEntity<Void> updateMember(@PathVariable Long id,
                                           @Valid @RequestBody UpdateMemberRequest request) {
        memberUseCase.updateMember(id, request);
        return ResponseEntity.noContent().build();
    }

    @RequireAdmin
    @DeleteMapping("/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberUseCase.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @RequireAdmin
    @PutMapping("/products/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id,
                                              @Valid @RequestBody AdminUpdateProductRequest request) {
        productUseCase.updateProductForAdmin(id, request);
        return ResponseEntity.noContent().build();
    }
} 