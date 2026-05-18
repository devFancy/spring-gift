package gift.api.product;

import gift.IntegrationTestSupport;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@DisplayName("상품 카탈로그")
class ProductControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("새 상품을 등록할 때")
    class RegisterProduct {

        @Test
        @DisplayName("상품 카탈로그에 노출된다")
        void addsToCatalog() throws Exception {
            // given
            Category category = saveCategory();
            String body = """
                {"name": "테스트상품", "price": 1000, "imageUrl": "img", "categoryId": %d}
                """.formatted(category.getId());

            // when
            mockMvc.perform(post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isCreated())
                .andDo(document("v1/products/register"));

            // then
            List<Product> all = productRepository.findAll();
            assertThat(all).hasSize(1);
            assertThat(all.get(0).getName()).isEqualTo("테스트상품");
            assertThat(all.get(0).getPrice()).isEqualTo(1000);
        }

        @Test
        @DisplayName("담당 MD와 협의하지 않고 상품명에 카카오를 사용하면 예외가 발생한다")
        void rejectsKakaoNameWithoutNegotiation() throws Exception {
            // given
            Category category = saveCategory();
            String body = """
                {"name": "카카오상품", "price": 1000, "imageUrl": "img", "categoryId": %d}
                """.formatted(category.getId());

            // when
            mockMvc.perform(post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isBadRequest())
                .andDo(document("v1/products/register-rejected-kakao"));

            // then
            assertThat(productRepository.count()).isZero();
        }
    }

    @Nested
    @DisplayName("상품을 조회할 때")
    class LookupProduct {

        @Test
        @DisplayName("존재하지 않는 상품이면 예외가 발생한다")
        void raisesIfNotFound() throws Exception {
            mockMvc.perform(get("/api/v1/products/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andDo(document("v1/products/get-not-found"));
        }
    }

    @Nested
    @DisplayName("상품을 수정할 때")
    class UpdateProduct {

        @Test
        @DisplayName("변경된 정보가 카탈로그에 반영된다")
        void appliesChanges() throws Exception {
            // given
            Category category = saveCategory();
            Product saved = productRepository.save(new Product("기존상품", 1000, "img", category));
            String body = """
                {"name": "수정상품", "price": 2000, "imageUrl": "img2", "categoryId": %d}
                """.formatted(category.getId());

            // when
            mockMvc.perform(put("/api/v1/products/{id}", saved.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk())
                .andDo(document("v1/products/update"));

            // then
            Product reloaded = productRepository.findById(saved.getId()).orElseThrow();
            assertThat(reloaded.getName()).isEqualTo("수정상품");
            assertThat(reloaded.getPrice()).isEqualTo(2000);
        }
    }

    @Nested
    @DisplayName("상품을 삭제할 때")
    class DeleteProduct {

        @Test
        @DisplayName("카탈로그에서 사라진다")
        void removesFromCatalog() throws Exception {
            // given
            Category category = saveCategory();
            Product saved = productRepository.save(new Product("기존상품", 1000, "img", category));

            // when
            mockMvc.perform(delete("/api/v1/products/{id}", saved.getId()))
                .andExpect(status().isNoContent())
                .andDo(document("v1/products/delete"));

            // then
            assertThat(productRepository.findById(saved.getId())).isEmpty();
        }
    }

    private Category saveCategory() {
        return categoryRepository.save(new Category("전자기기", "#000000", "img", "설명"));
    }
}
