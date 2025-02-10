package ulitity;

import com.faspix.dto.RequestCompilationDTO;
import com.faspix.dto.ResponseCompilationDTO;
import com.faspix.dto.ResponseEventShortDTO;
import com.faspix.entity.Compilation;

import java.util.Collections;
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

    public static ResponseCompilationDTO makeResponseCompilation() {
        return ResponseCompilationDTO.builder()
                .id(null)
                .title("title")
                .events(Collections.singletonList(ResponseEventShortDTO.builder()
                        .title("title of event")
                        .eventId(1L)
                        .build()))
                .pinned(false)
                .build();
    }
}
