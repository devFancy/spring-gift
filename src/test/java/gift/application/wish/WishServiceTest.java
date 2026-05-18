package gift.application.wish;

import gift.IntegrationTestSupport;
import gift.category.Category;
import gift.category.CategoryRepository;
import gift.storage.member.Member;
import gift.storage.member.MemberRepository;
import gift.storage.product.Product;
import gift.storage.product.ProductRepository;
import gift.storage.wish.Wish;
import gift.storage.wish.WishRepository;
import gift.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("선물 위시리스트 흐름")
class WishServiceTest extends IntegrationTestSupport {

    @Autowired
    private WishService wishService;

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("회원이 위시리스트에 담을 때")
    class AddWish {

        @Test
        @DisplayName("같은 상품을 두 번 담아도 위시 항목은 하나만 유지된다")
        void duplicateAddKeepsSingleEntry() {
            // given
            Member member = memberRepository.save(new Member("user@example.com", "pw"));
            Product product = saveProduct();

            // when
            Wish first = wishService.add(member.getId(), product.getId());
            Wish second = wishService.add(member.getId(), product.getId());

            // then
            assertThat(second.getId()).isEqualTo(first.getId());
            assertThat(wishRepository.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("존재하지 않는 상품을 담으면 예외가 발생한다")
        void unknownProductRaises() {
            // given
            Member member = memberRepository.save(new Member("user@example.com", "pw"));

            // when, then
            assertThatThrownBy(() -> wishService.add(member.getId(), 9999L))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("회원이 위시 항목을 삭제할 때")
    class RemoveWish {

        @Test
        @DisplayName("존재하지 않는 위시 항목이면 예외가 발생한다")
        void unknownWishRaises() {
            // given
            Member member = memberRepository.save(new Member("user@example.com", "pw"));

            // when, then
            assertThatThrownBy(() -> wishService.remove(member.getId(), 9999L))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("위시 항목을 찾을 수 없습니다");
        }
    }

    private Product saveProduct() {
        Category category = categoryRepository.save(new Category("전자기기", "#000000", "img", "설명"));
        return productRepository.save(new Product("상품", 1000, "img", category));
    }
}
