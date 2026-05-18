package gift.application.product;

import gift.IntegrationTestSupport;
import gift.storage.category.Category;
import gift.storage.category.CategoryRepository;
import gift.storage.product.Product;
import gift.storage.product.ProductRepository;
import gift.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("상품 카탈로그 흐름")
class ProductServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("상품을 등록할 때")
    class RegisterProduct {

        @Test
        @DisplayName("운영자는 상품명에 카카오를 포함시킬 수 있다")
        void adminCanIncludeKakaoInName() {
            // given
            Category category = saveCategory();

            // when
            Product saved = productService.create("카카오 콜라보", 5000, "img", category.getId(), true);

            // then
            assertThat(saved.getName()).isEqualTo("카카오 콜라보");
            assertThat(productRepository.findById(saved.getId())).isPresent();
        }

        @Test
        @DisplayName("존재하지 않는 카테고리로 등록하면 예외가 발생한다")
        void registeringWithUnknownCategoryRaises() {
            assertThatThrownBy(() -> productService.create("상품", 5000, "img", 9999L, false))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("카테고리를 찾을 수 없습니다");
        }
    }

    private Category saveCategory() {
        return categoryRepository.save(new Category("전자기기", "#000000", "img", "설명"));
    }
}
