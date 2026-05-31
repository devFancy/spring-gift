package gift.domain.category;

import gift.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("카테고리")
class CategoryTest {

    @Test
    @DisplayName("이름, 색상, 이미지가 유효하면 생성된다")
    void createSuccess() {
        // when
        Category category = new Category("음료", "#FF0000", "https://img.com/drink.jpg", "음료 카테고리");

        // then
        assertThat(category.getName().value()).isEqualTo("음료");
        assertThat(category.getColor().value()).isEqualTo("#FF0000");
        assertThat(category.getImageUrl().value()).isEqualTo("https://img.com/drink.jpg");
    }

    @Test
    @DisplayName("카테고리명이 비어 있으면 예외가 발생한다")
    void nameBlankThrows() {
        assertThatThrownBy(() -> new Category("", "#FF0000", "https://img.com/drink.jpg", null))
            .isInstanceOf(CoreException.class);
    }

    @Test
    @DisplayName("카테고리 색상이 비어 있으면 예외가 발생한다")
    void colorBlankThrows() {
        assertThatThrownBy(() -> new Category("음료", "", "https://img.com/drink.jpg", null))
            .isInstanceOf(CoreException.class);
    }

    @Test
    @DisplayName("카테고리 이미지가 비어 있으면 예외가 발생한다")
    void imageBlankThrows() {
        assertThatThrownBy(() -> new Category("음료", "#FF0000", "", null))
            .isInstanceOf(CoreException.class);
    }
}
