package gift.member.adapter.persistence.adapter;

import gift.member.adapter.persistence.mapper.MemberEntityMapper;
import gift.member.adapter.persistence.repository.MemberJpaRepository;
import gift.member.domain.model.Member;
import gift.member.domain.port.out.MemberRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MemberPersistenceAdapter implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    public MemberPersistenceAdapter(MemberJpaRepository memberJpaRepository) {
        this.memberJpaRepository = memberJpaRepository;
    }

    @Override
    public Member save(Member member) {
        var entity = memberJpaRepository.save(MemberEntityMapper.toEntity(member));
        return MemberEntityMapper.toDomain(entity);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return memberJpaRepository.findByEmail(email)
                .map(MemberEntityMapper::toDomain);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberJpaRepository.findById(id)
                .map(MemberEntityMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return memberJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsById(Long id) {
        return memberJpaRepository.existsById(id);
    }

    @Override
    public List<Member> findAll() {
        return memberJpaRepository.findAll().stream()
                .map(MemberEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        memberJpaRepository.deleteById(id);
    }
} 