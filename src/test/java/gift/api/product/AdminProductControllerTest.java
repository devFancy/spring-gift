package gift.api.product;

import gift.IntegrationTestSupport;
import gift.domain.category.Category;
import gift.domain.category.CategoryRepository;
import gift.domain.product.Product;
import gift.domain.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        @DisplayName("운영자가 상품 정보를 입력해 등록하면 상품 카탈로그에 저장된다")
        void savesToCatalog() throws Exception {
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
            assertThat(products.get(0).getName().value()).isEqualTo("운영자상품");
            assertThat(products.get(0).getPrice().value()).isEqualTo(1500);
        }
    }
}
