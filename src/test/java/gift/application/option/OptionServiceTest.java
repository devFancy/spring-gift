package gift.application.option;

import gift.IntegrationTestSupport;
import gift.storage.category.Category;
import gift.storage.category.CategoryRepository;
import gift.storage.option.Option;
import gift.storage.option.OptionRepository;
import gift.storage.product.Product;
import gift.storage.product.ProductRepository;
import gift.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("상품 옵션 관리 흐름")
class OptionServiceTest extends IntegrationTestSupport {

    @Autowired
    private OptionService optionService;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("옵션을 등록할 때")
    class RegisterOption {

        @Test
        @DisplayName("존재하지 않는 상품에 등록하려 하면 예외가 발생한다")
        void unknownProductRaises() {
            assertThatThrownBy(() -> optionService.register(9999L, "옵션A", 10))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("옵션을 삭제할 때")
    class RemoveOption {

        @Test
        @DisplayName("다른 상품의 옵션 식별자로 삭제하려 하면 예외가 발생한다")
        void crossProductRemovalRaises() {
            // given
            Product productA = saveProduct("상품A");
            Product productB = saveProduct("상품B");
            optionRepository.save(new Option(productA, "옵션X", 5));
            optionRepository.save(new Option(productA, "옵션W", 5));
            Option onB = optionRepository.save(new Option(productB, "옵션Y", 5));
            optionRepository.save(new Option(productB, "옵션Z", 5));

            // when, then: 상품A 의 옵션이 아닌 식별자로 삭제 시도
            assertThatThrownBy(() -> optionService.remove(productA.getId(), onB.getId()))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("옵션을 찾을 수 없습니다");
        }
    }

    private Product saveProduct(String name) {
        Category category = categoryRepository.save(new Category(name + "-카테고리", "#000000", "img", "설명"));
        return productRepository.save(new Product(name, 1000, "img", category));
    }
}
