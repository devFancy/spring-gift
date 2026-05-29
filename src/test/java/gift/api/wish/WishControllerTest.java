package gift.api.wish;

import gift.IntegrationTestSupport;
import gift.domain.category.Category;
import gift.domain.category.CategoryRepository;
import gift.domain.member.Member;
import gift.domain.member.MemberRepository;
import gift.domain.product.Product;
import gift.domain.product.ProductRepository;
import gift.domain.wish.Wish;
import gift.domain.wish.WishRepository;
import gift.infrastructure.JwtProvider;
import gift.storage.wish.WishJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@DisplayName("선물 위시리스트")
class WishControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private WishJpaRepository wishJpaRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Nested
    @DisplayName("회원이 상품을 위시리스트에 담을 때")
    class AddWish {

        @Test
        @DisplayName("위시리스트에 항목이 저장된다")
        void persistsWish() throws Exception {
            // given
            Member member = memberRepository.save(new Member("user@example.com", "password"));
            Product product = saveProduct();
            String token = jwtProvider.createToken(member.getEmail().value());
            String body = """
                {"productId": %d}
                """.formatted(product.getId());

            // when
            mockMvc.perform(post("/api/v1/wishes")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().is2xxSuccessful())
                .andDo(document("v1/wishes/add"));

            // then
            var wishes = wishJpaRepository.findAll();
            assertThat(wishes).hasSize(1);
            assertThat(wishes.get(0).getMemberId()).isEqualTo(member.getId());
        }

        @Test
        @DisplayName("인증 토큰이 없으면 위시리스트에 담을 수 없다")
        void rejectsWithoutAuthentication() throws Exception {
            Product product = saveProduct();
            String body = """
                {"productId": %d}
                """.formatted(product.getId());

            mockMvc.perform(post("/api/v1/wishes")
                    .header("Authorization", "Bearer invalid-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isUnauthorized())
                .andDo(document("v1/wishes/add-rejected-unauthorized"));

            assertThat(wishJpaRepository.count()).isZero();
        }
    }

    @Nested
    @DisplayName("회원이 위시 항목을 삭제할 때")
    class RemoveWish {

        @Test
        @DisplayName("자신의 위시 항목이면 위시리스트에서 사라진다")
        void removesOwn() throws Exception {
            Member member = memberRepository.save(new Member("user@example.com", "password"));
            Product product = saveProduct();
            Wish wish = wishRepository.save(new Wish(member.getId(), product));
            String token = jwtProvider.createToken(member.getEmail().value());

            mockMvc.perform(delete("/api/v1/wishes/{id}", wish.getId())
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent())
                .andDo(document("v1/wishes/remove"));

            assertThat(wishRepository.findById(wish.getId())).isEmpty();
        }

        @Test
        @DisplayName("다른 회원의 위시 항목은 삭제할 수 없다")
        void rejectsOtherUsersWish() throws Exception {
            Member owner = memberRepository.save(new Member("owner@example.com", "pw"));
            Member other = memberRepository.save(new Member("other@example.com", "pw"));
            Product product = saveProduct();
            Wish wish = wishRepository.save(new Wish(owner.getId(), product));
            String otherToken = jwtProvider.createToken(other.getEmail().value());

            mockMvc.perform(delete("/api/v1/wishes/{id}", wish.getId())
                    .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isForbidden())
                .andDo(document("v1/wishes/remove-rejected-forbidden"));

            assertThat(wishRepository.findById(wish.getId())).isPresent();
        }
    }

    private Product saveProduct() {
        Category category = categoryRepository.save(new Category("전자기기", "#000000", "img", "설명"));
        return productRepository.save(new Product("상품", 1000, "img", category));
    }
}
