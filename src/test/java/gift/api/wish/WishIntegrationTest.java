package gift.api.wish;

import gift.IntegrationTestSupport;
import gift.auth.JwtProvider;
import gift.category.Category;
import gift.category.CategoryRepository;
import gift.storage.member.Member;
import gift.storage.member.MemberRepository;
import gift.storage.product.Product;
import gift.storage.product.ProductRepository;
import gift.wish.Wish;
import gift.wish.WishRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("위시리스트 영역 작동 보존")
class WishIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("위시리스트 담기 요청 시 데이터베이스에 위시 항목이 저장된다")
    void addWishPersists() throws Exception {
        // given
        Member member = memberRepository.save(new Member("user@example.com", "password"));
        Product product = saveProduct();
        String token = jwtProvider.createToken(member.getEmail());
        String body = """
            {"productId": %d}
            """.formatted(product.getId());

        // when
        mockMvc.perform(post("/api/wishes")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().is2xxSuccessful());

        // then
        List<Wish> wishes = wishRepository.findAll();
        assertThat(wishes).hasSize(1);
        assertThat(wishes.get(0).getMemberId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("인증 토큰 없이 위시리스트 담기 요청하면 권한 거부된다")
    void addWishWithoutAuthRejected() throws Exception {
        Product product = saveProduct();
        String body = """
            {"productId": %d}
            """.formatted(product.getId());

        mockMvc.perform(post("/api/wishes")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isUnauthorized());

        assertThat(wishRepository.count()).isZero();
    }

    @Test
    @DisplayName("다른 사용자의 위시 항목을 삭제하려 하면 권한 거부된다")
    void deleteOtherUsersWishRejected() throws Exception {
        Member owner = memberRepository.save(new Member("owner@example.com", "pw"));
        Member other = memberRepository.save(new Member("other@example.com", "pw"));
        Product product = saveProduct();
        Wish wish = wishRepository.save(new Wish(owner.getId(), product));
        String otherToken = jwtProvider.createToken(other.getEmail());

        mockMvc.perform(delete("/api/wishes/" + wish.getId())
                .header("Authorization", "Bearer " + otherToken))
            .andExpect(status().isForbidden());

        assertThat(wishRepository.findById(wish.getId())).isPresent();
    }

    @Test
    @DisplayName("자신의 위시 항목을 삭제하면 데이터베이스에서 제거된다")
    void deleteOwnWishRemoves() throws Exception {
        Member member = memberRepository.save(new Member("user@example.com", "password"));
        Product product = saveProduct();
        Wish wish = wishRepository.save(new Wish(member.getId(), product));
        String token = jwtProvider.createToken(member.getEmail());

        mockMvc.perform(delete("/api/wishes/" + wish.getId())
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());

        assertThat(wishRepository.findById(wish.getId())).isEmpty();
    }

    private Product saveProduct() {
        Category category = categoryRepository.save(new Category("전자기기", "#000000", "img", "설명"));
        return productRepository.save(new Product("상품", 1000, "img", category));
    }
}
