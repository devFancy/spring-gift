package gift.api.product;

import gift.IntegrationTestSupport;
import gift.category.Category;
import gift.category.CategoryRepository;
import gift.storage.product.Product;
import gift.storage.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("상품 영역 작동 보존")
class ProductIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("상품 생성 시 데이터베이스에 상품이 저장된다")
    void createPersistsProduct() throws Exception {
        // given
        Category category = saveCategory();
        String body = """
            {"name": "테스트상품", "price": 1000, "imageUrl": "img", "categoryId": %d}
            """.formatted(category.getId());

        // when
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());

        // then
        List<Product> all = productRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getName()).isEqualTo("테스트상품");
        assertThat(all.get(0).getPrice()).isEqualTo(1000);
    }

    @Test
    @DisplayName("존재하지 않는 상품을 조회하면 예외 응답이 반환된다")
    void findOneReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/products/9999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("상품명에 카카오가 포함되면 예외 응답이 반환된다")
    void createWithKakaoNameRejected() throws Exception {
        // given
        Category category = saveCategory();
        String body = """
            {"name": "카카오상품", "price": 1000, "imageUrl": "img", "categoryId": %d}
            """.formatted(category.getId());

        // when
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());

        // then
        assertThat(productRepository.count()).isZero();
    }

    @Test
    @DisplayName("상품을 수정하면 데이터베이스 값이 갱신된다")
    void updateChangesPersistedValues() throws Exception {
        // given
        Category category = saveCategory();
        Product saved = productRepository.save(new Product("기존상품", 1000, "img", category));
        String body = """
            {"name": "수정상품", "price": 2000, "imageUrl": "img2", "categoryId": %d}
            """.formatted(category.getId());

        // when
        mockMvc.perform(put("/api/products/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk());

        // then
        Product reloaded = productRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getName()).isEqualTo("수정상품");
        assertThat(reloaded.getPrice()).isEqualTo(2000);
    }

    @Test
    @DisplayName("상품을 삭제하면 데이터베이스에서 제거된다")
    void deleteRemovesProduct() throws Exception {
        // given
        Category category = saveCategory();
        Product saved = productRepository.save(new Product("기존상품", 1000, "img", category));

        // when
        mockMvc.perform(delete("/api/products/" + saved.getId()))
            .andExpect(status().isNoContent());

        // then
        assertThat(productRepository.findById(saved.getId())).isEmpty();
    }

    private Category saveCategory() {
        return categoryRepository.save(new Category("전자기기", "#000000", "img", "설명"));
    }
}
