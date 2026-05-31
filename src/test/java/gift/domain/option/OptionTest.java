package gift.domain.option;

import gift.domain.category.Category;
import gift.domain.product.Product;
import gift.support.error.CoreException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("상품 옵션")
class OptionTest {

    private Product product;

    @BeforeEach
    void setUp() {
        Category category = new Category("음료", "#FF0000", "https://img.com/drink.jpg", null);
        product = new Product("아이스 아메리카노", 4500, "https://img.com/iced.jpg", category);
    }

    @Test
    @DisplayName("유효한 이름과 수량으로 생성된다")
    void createSuccess() {
        // when
        Option option = new Option(product, "L 사이즈", 100);

        // then
        assertThat(option.getName().value()).isEqualTo("L 사이즈");
        assertThat(option.getQuantity().value()).isEqualTo(100);
    }

    @Test
    @DisplayName("옵션명이 정확히 50자면 생성된다")
    void nameAtLimit() {
        new Option(product, "a".repeat(50), 10);
    }

    @Test
    @DisplayName("옵션명이 50자를 초과하면 예외가 발생한다")
    void nameTooLongThrows() {
        assertThatThrownBy(() -> new Option(product, "a".repeat(51), 10))
            .isInstanceOf(CoreException.class);
    }

    @Test
    @DisplayName("옵션명이 비어 있으면 예외가 발생한다")
    void nameBlankThrows() {
        assertThatThrownBy(() -> new Option(product, "", 10))
            .isInstanceOf(CoreException.class);
    }

    @Test
    @DisplayName("재고 수량이 0이면 생성된다")
    void zeroQuantityAllowed() {
        new Option(product, "L 사이즈", 0);
    }

    @Test
    @DisplayName("재고 수량이 음수이면 예외가 발생한다")
    void negativeQuantityThrows() {
        assertThatThrownBy(() -> new Option(product, "L 사이즈", -1))
            .isInstanceOf(CoreException.class);
    }

    @Test
    @DisplayName("재고 이하 수량을 차감하면 재고가 감소한다")
    void subtractQuantitySuccess() {
        // given
        Option option = new Option(product, "L 사이즈", 10);

        // when
        option.subtractQuantity(3);

        // then
        assertThat(option.getQuantity().value()).isEqualTo(7);
    }

    @Test
    @DisplayName("남은 재고 전량을 차감하면 재고가 0이 된다")
    void subtractAllStock() {
        // given
        Option option = new Option(product, "L 사이즈", 5);

        // when
        option.subtractQuantity(5);

        // then
        assertThat(option.getQuantity().value()).isZero();
    }

    @Test
    @DisplayName("재고보다 많은 수량을 차감하면 예외가 발생한다")
    void subtractMoreThanStockThrows() {
        // given
        Option option = new Option(product, "L 사이즈", 5);

        // when & then
        assertThatThrownBy(() -> option.subtractQuantity(6))
            .isInstanceOf(CoreException.class);
    }
}
