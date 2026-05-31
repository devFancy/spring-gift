package gift.api.category;

import gift.RestDocsSupport;
import gift.application.category.CategoryService;
import gift.domain.category.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("카테고리 관리 API 문서화")
class CategoryControllerDocsTest extends RestDocsSupport {

    private final CategoryService categoryService = mock(CategoryService.class);

    @Override
    protected Object initController() {
        return new CategoryController(categoryService);
    }

    @Test
    @DisplayName("카테고리 목록을 조회하면 저장된 카테고리가 반환된다")
    void getCategories() throws Exception {
        // given
        Category category = new Category(1L, "음료", "#FF0000", "https://img.com/drink.jpg", "음료 카테고리");
        given(categoryService.findAll()).willReturn(List.of(category));

        // when & then
        mockMvc.perform(get("/api/v1/categories"))
            .andExpect(status().isOk())
            .andDo(document("category/list",
                responseFields(
                    fieldWithPath("result").description("SUCCESS / ERROR"),
                    fieldWithPath("data[].id").description("카테고리 ID"),
                    fieldWithPath("data[].name").description("카테고리명"),
                    fieldWithPath("data[].color").description("카테고리 색상"),
                    fieldWithPath("data[].imageUrl").description("카테고리 이미지 URL"),
                    fieldWithPath("data[].description").description("카테고리 설명").optional(),
                    fieldWithPath("error").description("에러 정보 (성공 시 null)").optional()
                )
            ));
    }

    @Test
    @DisplayName("새 카테고리를 등록하면 카테고리 목록에 추가된다")
    void createCategory() throws Exception {
        // given
        Category category = new Category(1L, "음료", "#FF0000", "https://img.com/drink.jpg", "설명");
        given(categoryService.create(any(), any(), any(), any())).willReturn(category);

        String body = """
            {"name": "음료", "color": "#FF0000", "imageUrl": "https://img.com/drink.jpg", "description": "음료 카테고리"}
            """;

        // when & then
        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andDo(document("category/register",
                requestFields(
                    fieldWithPath("name").description("카테고리명"),
                    fieldWithPath("color").description("카테고리 색상 코드"),
                    fieldWithPath("imageUrl").description("카테고리 이미지 URL"),
                    fieldWithPath("description").description("카테고리 설명").optional()
                ),
                responseFields(
                    fieldWithPath("result").description("SUCCESS / ERROR"),
                    fieldWithPath("data.id").description("카테고리 ID"),
                    fieldWithPath("data.name").description("카테고리명"),
                    fieldWithPath("data.color").description("카테고리 색상"),
                    fieldWithPath("data.imageUrl").description("카테고리 이미지 URL"),
                    fieldWithPath("data.description").description("카테고리 설명").optional(),
                    fieldWithPath("error").description("에러 정보 (성공 시 null)").optional()
                )
            ));
    }
}
