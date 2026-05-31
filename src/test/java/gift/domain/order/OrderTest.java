package gift.domain.order;

import gift.domain.order.vo.Quantity;
import gift.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("주문")
class OrderTest {

    @Test
    @DisplayName("주문 수량이 1 이상이면 생성된다")
    void quantityAtMinimum() {
        // when
        Quantity quantity = new Quantity(1);

        // then
        assertThat(quantity.value()).isEqualTo(1);
    }

    @Test
    @DisplayName("주문 수량이 0이면 예외가 발생한다")
    void zeroQuantityThrows() {
        assertThatThrownBy(() -> new Quantity(0))
            .isInstanceOf(CoreException.class);
    }

    @Test
    @DisplayName("주문 수량이 음수이면 예외가 발생한다")
    void negativeQuantityThrows() {
        assertThatThrownBy(() -> new Quantity(-1))
            .isInstanceOf(CoreException.class);
    }
}
