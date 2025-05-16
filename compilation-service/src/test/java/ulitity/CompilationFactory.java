package ulitity;

import com.faspix.dto.RequestCompilationDTO;
import com.faspix.dto.ResponseCompilationDTO;
import com.faspix.entity.Compilation;

import java.util.List;

public class CompilationFactory {

    public static Compilation makeCompilation() {
        return Compilation.builder()
                .id(null)
                .title("title")
                .events(List.of(1L, 2L))
                .pinned(false)
                .build();
    }

    public static RequestCompilationDTO makeRequestCompilation() {
        return RequestCompilationDTO.builder()
                .title("title")
                .events(List.of(1L, 2L))
                .pinned(false)
                .build();
    }

}
