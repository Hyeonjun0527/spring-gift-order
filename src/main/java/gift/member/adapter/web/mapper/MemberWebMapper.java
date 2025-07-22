package gift.member.adapter.web.mapper;

import gift.member.application.port.in.dto.MemberResponse;
import gift.member.domain.model.Member;

public class MemberWebMapper {

    public static MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.id(),
                member.email(),
                member.role(),
                member.createdAt()
        );
    }
} 