package gift.application.order;

import gift.IntegrationTestSupport;
import gift.domain.category.Category;
import gift.domain.category.CategoryRepository;
import gift.domain.member.Member;
import gift.domain.member.MemberRepository;
import gift.domain.option.Option;
import gift.domain.option.OptionRepository;
import gift.domain.order.Order;
import gift.domain.product.Product;
import gift.domain.product.ProductRepository;
import gift.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("선물 주문")
class OrderServiceTest extends IntegrationTestSupport {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("회원이 주문하면 주문이 저장되고 재고와 포인트가 차감된다")
    void placeSavesOrderAndDeductsStockAndPoint() {
        // given
        Member member = saveMemberWithPoint("buyer@example.com", 10_000);
        Option option = saveOption(2_000, 5);

        // when
        Order order = orderService.place(member.getId(), option.getId(), 2, "선물");

        // then
        assertThat(order.getId()).isNotNull();
        assertThat(order.getQuantity().value()).isEqualTo(2);

        Option updatedOption = optionRepository.findById(option.getId()).orElseThrow();
        assertThat(updatedOption.getQuantity().value()).isEqualTo(3);

        Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updatedMember.getPoint().value()).isEqualTo(6_000);
    }

    @Test
    @DisplayName("회원이 주문을 생성할 때 옵션 재고보다 많이 주문하면 예외가 발생한다")
    void stockShortageRaises() {
        // given
        Member member = saveMemberWithPoint("buyer@example.com", 100_000);
        Option option = saveOption(2_000, 2);

        // when, then
        assertThatThrownBy(() -> orderService.place(member.getId(), option.getId(), 3, "선물"))
            .isInstanceOf(CoreException.class)
            .hasMessageContaining("재고");
    }

    @Test
    @DisplayName("회원이 주문을 생성할 때 회원 포인트보다 큰 금액을 주문하면 예외가 발생한다")
    void pointShortageRaises() {
        // given
        Member member = saveMemberWithPoint("buyer@example.com", 1_000);
        Option option = saveOption(2_000, 10);

        // when, then
        assertThatThrownBy(() -> orderService.place(member.getId(), option.getId(), 1, "선물"))
            .isInstanceOf(CoreException.class)
            .hasMessageContaining("포인트");
    }

    private Option saveOption(int price, int quantity) {
        Category category = categoryRepository.save(new Category("전자기기", "#000000", "img", "설명"));
        Product product = productRepository.save(new Product("상품", price, "img", category));
        return optionRepository.save(new Option(product, "옵션A", quantity));
    }

    private Member saveMemberWithPoint(String email, int point) {
        Member member = new Member(email, "password");
        member.chargePoint(point);
        return memberRepository.save(member);
    }
}
