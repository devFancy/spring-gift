package gift.api.option;

import gift.IntegrationTestSupport;
import gift.storage.option.Option;
import gift.storage.option.OptionRepository;
import gift.storage.category.Category;
import gift.storage.category.CategoryRepository;
import gift.storage.product.Product;
import gift.storage.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@DisplayName("상품 옵션 관리")
class OptionControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("상품에 새 옵션을 등록할 때")
    class RegisterOption {

        @Test
        @DisplayName("상품의 옵션 목록에 옵션이 추가된다")
        void addsToProductOptions() throws Exception {
            // given
            Product product = saveProduct();
            String body = """
                {"name": "옵션A", "quantity": 10}
                """;

            // when
            mockMvc.perform(post("/api/v1/products/{productId}/options", product.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isCreated())
                .andDo(document("v1/options/register"));

            // then
            List<Option> options = optionRepository.findByProductId(product.getId());
            assertThat(options).hasSize(1);
            assertThat(options.get(0).getName()).isEqualTo("옵션A");
        }

        @Test
        @DisplayName("같은 상품에 같은 이름의 옵션이 있으면 예외가 발생한다")
        void rejectsDuplicateName() throws Exception {
            // given
            Product product = saveProduct();
            optionRepository.save(new Option(product, "옵션A", 10));
            String body = """
                {"name": "옵션A", "quantity": 5}
                """;

            // when
            mockMvc.perform(post("/api/v1/products/{productId}/options", product.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isBadRequest())
                .andDo(document("v1/options/register-rejected-duplicate"));

            // then
            assertThat(optionRepository.findByProductId(product.getId())).hasSize(1);
        }
    }

    @Nested
    @DisplayName("상품의 옵션 목록을 조회할 때")
    class LookupOptions {

        @Test
        @DisplayName("등록된 옵션이 모두 반환된다")
        void returnsAll() throws Exception {
            // given
            Product product = saveProduct();
            optionRepository.save(new Option(product, "옵션A", 10));
            optionRepository.save(new Option(product, "옵션B", 20));

            // when, then
            mockMvc.perform(get("/api/v1/products/{productId}/options", product.getId()))
                .andExpect(status().isOk())
                .andDo(document("v1/options/list"));
        }
    }

    @Nested
    @DisplayName("상품의 옵션을 삭제할 때")
    class DeleteOption {

        @Test
        @DisplayName("옵션이 두 개 이상이면 선택한 옵션이 사라진다")
        void removesWhenMultipleExist() throws Exception {
            // given
            Product product = saveProduct();
            Option toDelete = optionRepository.save(new Option(product, "옵션A", 10));
            optionRepository.save(new Option(product, "옵션B", 20));

            // when
            mockMvc.perform(delete("/api/v1/products/{productId}/options/{optionId}", product.getId(), toDelete.getId()))
                .andExpect(status().isNoContent())
                .andDo(document("v1/options/delete"));

            // then
            assertThat(optionRepository.findById(toDelete.getId())).isEmpty();
            assertThat(optionRepository.findByProductId(product.getId())).hasSize(1);
        }

        @Test
        @DisplayName("옵션이 하나뿐이면 삭제할 수 없다")
        void rejectsWhenLastOption() throws Exception {
            // given
            Product product = saveProduct();
            Option last = optionRepository.save(new Option(product, "옵션A", 10));

            // when
            mockMvc.perform(delete("/api/v1/products/{productId}/options/{optionId}", product.getId(), last.getId()))
                .andExpect(status().isBadRequest())
                .andDo(document("v1/options/delete-rejected-last"));

            // then
            assertThat(optionRepository.findById(last.getId())).isPresent();
        }
    }

    private Product saveProduct() {
        Category category = categoryRepository.save(new Category("전자기기", "#000000", "img", "설명"));
        return productRepository.save(new Product("상품", 1000, "img", category));
    }
}
