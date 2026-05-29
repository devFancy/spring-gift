package gift.storage.member;

import gift.domain.member.Member;
import gift.domain.member.MemberRepository;
import gift.support.error.CoreException;
import gift.support.error.ErrorType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    public MemberRepositoryImpl(MemberJpaRepository memberJpaRepository) {
        this.memberJpaRepository = memberJpaRepository;
    }

    @Override
    public Member save(Member member) {
        if (member.getId() != null) {
            return updateAndReturn(member);
        }
        MemberEntity entity = MemberEntity.from(member);
        return memberJpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberJpaRepository.findById(id).map(MemberEntity::toDomain);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return memberJpaRepository.findByEmail(email).map(MemberEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return memberJpaRepository.existsByEmail(email);
    }

    @Override
    public List<Member> findAll() {
        return memberJpaRepository.findAll().stream()
            .map(MemberEntity::toDomain)
            .toList();
    }

    @Override
    public void deleteById(Long id) {
        memberJpaRepository.deleteById(id);
    }

    @Override
    public void update(Member member) {
        MemberEntity entity = memberJpaRepository.findById(member.getId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "회원을 찾을 수 없습니다."));
        entity.update(member);
    }

    private Member updateAndReturn(Member member) {
        MemberEntity entity = memberJpaRepository.findById(member.getId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "회원을 찾을 수 없습니다."));
        entity.update(member);
        return entity.toDomain();
    }
}
