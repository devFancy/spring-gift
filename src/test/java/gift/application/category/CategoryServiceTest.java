package gift.application.category;

import gift.IntegrationTestSupport;
import gift.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("카테고리 관리 흐름")
class CategoryServiceTest extends IntegrationTestSupport {

    @Autowired
    private CategoryService categoryService;

    @Nested
    @DisplayName("카테고리를 조회할 때")
    class LookupCategory {

        @Test
        @DisplayName("존재하지 않는 카테고리이면 예외가 발생한다")
        void unknownCategoryRaises() {
            assertThatThrownBy(() -> categoryService.findById(9999L))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("카테고리를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("카테고리를 수정할 때")
    class UpdateCategory {

        @Test
        @DisplayName("존재하지 않는 카테고리를 수정하면 예외가 발생한다")
        void unknownCategoryUpdateRaises() {
            assertThatThrownBy(() -> categoryService.update(9999L, "수정", "#FFFFFF", "img", "설명"))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("카테고리를 찾을 수 없습니다");
        }
    }
}
