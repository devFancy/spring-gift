package gift.api.order;

import gift.IntegrationTestSupport;
import gift.domain.category.Category;
import gift.domain.category.CategoryRepository;
import gift.domain.member.Member;
import gift.domain.member.MemberRepository;
import gift.domain.option.Option;
import gift.domain.option.OptionRepository;
import gift.domain.order.Order;
import gift.domain.order.OrderRepository;
import gift.domain.product.Product;
import gift.domain.product.ProductRepository;
import gift.infrastructure.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("선물 주문")
class OrderControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Nested
    @DisplayName("회원이 주문을 생성할 때")
    class CreateOrder {

        @Test
        @DisplayName("재고와 포인트가 차감된 채 주문이 저장된다")
        void persistsOrderWithStockAndPointDeducted() throws Exception {
            // given
            Member member = saveMemberWithPoint("buyer@example.com", 100_000);
            Option option = saveOption(2_000, 10);
            String token = jwtProvider.createToken(member.getEmail().value());
            String body = """
                {"optionId": %d, "quantity": 3, "message": "선물입니다"}
                """.formatted(option.getId());

            // when
            mockMvc.perform(post("/api/v1/orders")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isCreated());

            // then
            Option reloadedOption = optionRepository.findById(option.getId()).orElseThrow();
            assertThat(reloadedOption.getQuantity()).isEqualTo(7);

            Member reloadedMember = memberRepository.findById(member.getId()).orElseThrow();
            assertThat(reloadedMember.getPoint()).isEqualTo(100_000 - 2_000 * 3);

            List<Order> orders = orderRepository.findByMemberId(member.getId(), PageRequest.of(0, 100)).getContent();
            assertThat(orders).hasSize(1);
            assertThat(orders.get(0).getQuantity()).isEqualTo(3);
            assertThat(orders.get(0).getMemberId()).isEqualTo(member.getId());
        }

        @Test
        @DisplayName("인증 토큰이 잘못되면 주문할 수 없다")
        void rejectsWithoutValidAuth() throws Exception {
            // given
            Option option = saveOption(2_000, 10);
            String body = """
                {"optionId": %d, "quantity": 1}
                """.formatted(option.getId());

            // when
            mockMvc.perform(post("/api/v1/orders")
                    .header("Authorization", "Bearer invalid-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("존재하지 않는 옵션을 주문하면 예외가 발생한다")
        void unknownOptionRaises() throws Exception {
            // given
            Member member = saveMemberWithPoint("buyer@example.com", 100_000);
            String token = jwtProvider.createToken(member.getEmail().value());
            String body = """
                {"optionId": 9999, "quantity": 1}
                """;

            // when, then
            mockMvc.perform(post("/api/v1/orders")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("회원이 주문 내역을 조회할 때")
    class ListOrders {

        @Test
        @DisplayName("본인의 주문 내역을 받는다")
        void returnsOwnOrders() throws Exception {
            // given
            Member member = saveMemberWithPoint("buyer@example.com", 100_000);
            Option option = saveOption(2_000, 10);
            orderRepository.save(new Order(option, member.getId(), 1, "m1", LocalDateTime.now()));
            orderRepository.save(new Order(option, member.getId(), 2, "m2", LocalDateTime.now()));
            String token = jwtProvider.createToken(member.getEmail().value());

            // when, then
            mockMvc.perform(get("/api/v1/orders")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        }
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
