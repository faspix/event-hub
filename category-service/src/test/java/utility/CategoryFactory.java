package utility;

import com.faspix.dto.RequestCategoryDTO;
import com.faspix.dto.ResponseCategoryDTO;
import com.faspix.entity.Category;

public class CategoryFactory {

    public static Category makeCategory() {
        return Category.builder()
                .categoryId(1L)
                .name("Name")
                .build();
    }

    public static RequestCategoryDTO makeRequestCategory() {
        return RequestCategoryDTO.builder()
                .name("Name")
                .build();
    }

    public static ResponseCategoryDTO makeResponseCategory() {
        return ResponseCategoryDTO.builder()
                .categoryId(1L)
                .name("Name")
                .build();
    }

}
