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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("운영자 상품 화면")
class AdminProductControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("상품 등록 폼을 제출할 때")
    class CreateForm {

        @Test
        @DisplayName("네 개의 폼 필드가 record 로 자동 binding 되어 상품이 저장된다")
        void recordAutoBindingCreatesProduct() throws Exception {
            // given
            Category category = categoryRepository.save(new Category("전자기기", "#000000", "img", "설명"));

            // when
            mockMvc.perform(post("/admin/products")
                    .param("name", "운영자상품")
                    .param("price", "1500")
                    .param("imageUrl", "img-url")
                    .param("categoryId", category.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));

            // then
            List<Product> products = productRepository.findAll();
            assertThat(products).hasSize(1);
            assertThat(products.get(0).getName()).isEqualTo("운영자상품");
            assertThat(products.get(0).getPrice()).isEqualTo(1500);
        }
    }
}
