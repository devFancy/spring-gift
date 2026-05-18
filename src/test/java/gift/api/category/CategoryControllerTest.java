package gift.api.category;

import gift.IntegrationTestSupport;
import gift.storage.category.Category;
import gift.storage.category.CategoryRepository;
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
@DisplayName("카테고리 관리")
class CategoryControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("새 카테고리를 등록할 때")
    class RegisterCategory {

        @Test
        @DisplayName("카탈로그에 카테고리가 노출된다")
        void addsToCatalog() throws Exception {
            // given
            String body = """
                {"name": "테스트카테고리", "color": "#FFFFFF", "imageUrl": "img", "description": "설명"}
                """;

            // when
            mockMvc.perform(post("/api/v1/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isCreated())
                .andDo(document("v1/categories/register"));

            // then
            List<Category> all = categoryRepository.findAll();
            assertThat(all).hasSize(1);
            assertThat(all.get(0).getName()).isEqualTo("테스트카테고리");
        }
    }

    @Nested
    @DisplayName("카테고리 목록을 조회할 때")
    class LookupCategories {

        @Test
        @DisplayName("저장된 카테고리가 모두 반환된다")
        void returnsAll() throws Exception {
            // given
            categoryRepository.save(new Category("전자기기", "#000000", "img", "설명1"));
            categoryRepository.save(new Category("패션", "#FFFFFF", "img", "설명2"));

            // when, then
            mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andDo(document("v1/categories/list"));
        }
    }

    @Nested
    @DisplayName("카테고리를 수정할 때")
    class UpdateCategory {

        @Test
        @DisplayName("변경된 정보가 카탈로그에 반영된다")
        void appliesChanges() throws Exception {
            // given
            Category saved = categoryRepository.save(new Category("기존", "#000000", "img", "설명"));
            String body = """
                {"name": "수정", "color": "#FFFFFF", "imageUrl": "img2", "description": "설명수정"}
                """;

            // when
            mockMvc.perform(put("/api/v1/categories/{id}", saved.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk())
                .andDo(document("v1/categories/update"));

            // then
            Category reloaded = categoryRepository.findById(saved.getId()).orElseThrow();
            assertThat(reloaded.getName()).isEqualTo("수정");
        }

        @Test
        @DisplayName("존재하지 않는 카테고리면 예외가 발생한다")
        void raisesIfNotFound() throws Exception {
            String body = """
                {"name": "수정", "color": "#FFFFFF", "imageUrl": "img", "description": "설명"}
                """;

            mockMvc.perform(put("/api/v1/categories/{id}", 9999L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isNotFound())
                .andDo(document("v1/categories/update-not-found"));
        }
    }

    @Nested
    @DisplayName("카테고리를 삭제할 때")
    class DeleteCategory {

        @Test
        @DisplayName("카탈로그에서 사라진다")
        void removesFromCatalog() throws Exception {
            // given
            Category saved = categoryRepository.save(new Category("기존", "#000000", "img", "설명"));

            // when
            mockMvc.perform(delete("/api/v1/categories/{id}", saved.getId()))
                .andExpect(status().isNoContent())
                .andDo(document("v1/categories/delete"));

            // then
            assertThat(categoryRepository.findById(saved.getId())).isEmpty();
        }
    }
}
