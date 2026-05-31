package gift.infrastructure.oauth.client;

import gift.domain.order.Order;
import gift.domain.product.Product;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoMessageClient {
    private final RestClient restClient;

    public KakaoMessageClient(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    public void sendToMe(String accessToken, Order order, Product product) {
        var templateObject = buildTemplate(order, product);

        var params = new LinkedMultiValueMap<String, String>();
        params.add("template_object", templateObject);

        restClient.post()
            .uri("https://kapi.kakao.com/v2/api/talk/memo/default/send")
            .header("Authorization", "Bearer " + accessToken)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .body(params)
            .retrieve()
            .toBodilessEntity();
    }

    private String buildTemplate(Order order, Product product) {
        var totalPrice = String.format("%,d", product.getPrice().value() * order.getQuantity().value());
        var messageText = order.getMessage() != null && !order.getMessage().value().isBlank()
            ? "\\n\\n💌 " + order.getMessage().value()
            : "";
        return """
            {
                "object_type": "text",
                "text": "🎁 선물이 도착했어요!\\n\\n%s (%s)\\n수량: %d개\\n금액: %s원%s",
                "link": {},
                "button_title": "선물 확인하기"
            }
            """.formatted(
            product.getName().value(),
            order.getOption().getName().value(),
            order.getQuantity().value(),
            totalPrice,
            messageText
        );
    }
}
