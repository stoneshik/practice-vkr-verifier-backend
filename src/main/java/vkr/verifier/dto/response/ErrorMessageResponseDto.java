package vkr.verifier.dto.response;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorMessageResponseDto {
    @JsonProperty("message")
    private String message;

    @Builder.Default
    @JsonProperty("violations")
    private List<String> violations = new ArrayList<>();

    @JsonProperty("time")
    private Instant time;
}
