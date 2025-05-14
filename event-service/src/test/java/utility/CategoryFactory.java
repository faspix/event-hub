package utility;


import com.faspix.shared.dto.ResponseCategoryDTO;

public class CategoryFactory {

    public static ResponseCategoryDTO makeResponseCategory() {
        return ResponseCategoryDTO.builder()
                .categoryId(1L)
                .name("Name")
                .build();
    }


}
