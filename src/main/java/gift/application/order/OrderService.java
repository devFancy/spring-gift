package gift.application.order;

import gift.domain.member.Member;
import gift.domain.member.MemberRepository;
import gift.domain.option.Option;
import gift.domain.option.OptionRepository;
import gift.domain.order.Order;
import gift.domain.order.OrderRepository;
import gift.infrastructure.oauth.client.KakaoMessageClient;
import gift.support.error.CoreException;
import gift.support.error.ErrorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final MemberRepository memberRepository;
    private final KakaoMessageClient kakaoMessageClient;

    public OrderService(
        OrderRepository orderRepository,
        OptionRepository optionRepository,
        MemberRepository memberRepository,
        KakaoMessageClient kakaoMessageClient
    ) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.memberRepository = memberRepository;
        this.kakaoMessageClient = kakaoMessageClient;
    }

    public Page<Order> findByMember(Long memberId, Pageable pageable) {
        return orderRepository.findByMemberId(memberId, pageable);
    }

    @Transactional
    public Order place(Long memberId, Long optionId, int quantity, String message) {
        Member member = findMember(memberId);
        Option option = findOption(optionId);
        option.subtractQuantity(quantity);
        int totalPrice = option.getProduct().getPrice() * quantity;
        member.deductPoint(totalPrice);
        memberRepository.update(member);
        optionRepository.update(option);
        Order saved = orderRepository.save(new Order(option, member.getId(), quantity, message));
        notifyKakao(member, saved, option);
        return saved;
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "회원을 찾을 수 없습니다."));
    }

    private Option findOption(Long optionId) {
        return optionRepository.findById(optionId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "옵션을 찾을 수 없습니다."));
    }

    private void notifyKakao(Member member, Order order, Option option) {
        if (member.getKakaoAccessToken() == null) {
            return;
        }
        try {
            kakaoMessageClient.sendToMe(member.getKakaoAccessToken(), order, option.getProduct());
        } catch (Exception ignored) {
        }
    }
}
