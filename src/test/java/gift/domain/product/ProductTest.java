package gift.domain.product;

import gift.domain.category.Category;
import gift.support.error.CoreException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("상품")
class ProductTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category("음료", "#FF0000", "https://img.com/drink.jpg", null);
    }

    @Test
    @DisplayName("유효한 이름, 가격, 이미지로 생성된다")
    void createSuccess() {
        // when
        Product product = new Product("아이스 아메리카노", 4500, "https://img.com/iced.jpg", category);

        // then
        assertThat(product.getName().value()).isEqualTo("아이스 아메리카노");
        assertThat(product.getPrice().value()).isEqualTo(4500);
    }

    @Test
    @DisplayName("상품명이 정확히 15자면 생성된다")
    void nameAtLimit() {
        new Product("a".repeat(15), 1000, "https://img.com/a.jpg", category);
    }

    @Test
    @DisplayName("상품명이 15자를 초과하면 예외가 발생한다")
    void nameTooLongThrows() {
        assertThatThrownBy(() -> new Product("a".repeat(16), 1000, "https://img.com/a.jpg", category))
            .isInstanceOf(CoreException.class);
    }

    @Test
    @DisplayName("상품명이 비어 있으면 예외가 발생한다")
    void nameBlankThrows() {
        assertThatThrownBy(() -> new Product("", 1000, "https://img.com/a.jpg", category))
            .isInstanceOf(CoreException.class);
    }

    @Test
    @DisplayName("상품명에 허용되지 않는 특수문자가 포함되면 예외가 발생한다")
    void nameInvalidCharThrows() {
        assertThatThrownBy(() -> new Product("상품@이름", 1000, "https://img.com/a.jpg", category))
            .isInstanceOf(CoreException.class);
    }

    @Test
    @DisplayName("상품 가격이 1원 이상이면 생성된다")
    void priceAtMinimum() {
        new Product("상품", 1, "https://img.com/a.jpg", category);
    }

    @Test
    @DisplayName("상품 가격이 0원이면 예외가 발생한다")
    void zeroPriceThrows() {
        assertThatThrownBy(() -> new Product("상품", 0, "https://img.com/a.jpg", category))
            .isInstanceOf(CoreException.class);
    }
}
