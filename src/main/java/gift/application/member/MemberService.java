package gift.application.member;

import gift.infrastructure.auth.JwtProvider;
import gift.domain.member.policy.MemberEmailPolicy;
import gift.storage.member.Member;
import gift.storage.member.MemberRepository;
import gift.support.error.CoreException;
import gift.support.error.ErrorType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public MemberService(MemberRepository memberRepository, JwtProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public String register(String email, String password) {
        MemberEmailPolicy.ensureNotDuplicated(memberRepository.existsByEmail(email));
        Member member = memberRepository.save(new Member(email, password));
        return jwtProvider.createToken(member.getEmail());
    }

    public String login(String email, String password) {
        Member member = findByEmail(email);
        verifyPassword(member, password);
        return jwtProvider.createToken(member.getEmail());
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "회원을 찾을 수 없습니다."));
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Transactional
    public Member createForAdmin(String email, String password) {
        MemberEmailPolicy.ensureNotDuplicated(memberRepository.existsByEmail(email));
        return memberRepository.save(new Member(email, password));
    }

    @Transactional
    public Member update(Long id, String email, String password) {
        Member member = findById(id);
        member.update(email, password);
        return memberRepository.save(member);
    }

    @Transactional
    public Member chargePoint(Long id, int amount) {
        Member member = findById(id);
        member.chargePoint(amount);
        return memberRepository.save(member);
    }

    @Transactional
    public void delete(Long id) {
        memberRepository.deleteById(id);
    }

    private Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new CoreException(ErrorType.INVALID_REQUEST, "이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    private void verifyPassword(Member member, String password) {
        if (member.getPassword() == null || !member.getPassword().equals(password)) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
    }
}
