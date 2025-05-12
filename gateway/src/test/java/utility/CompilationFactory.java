package utility;

import com.faspix.dto.external.ResponseCompilationDTO;

import java.util.List;

public class CompilationFactory {

    public static ResponseCompilationDTO makeCompilation() {
        return new ResponseCompilationDTO(
                1L,
                "Test Compilation",
                true,
                List.of(1L, 2L)
        );
    }

}
