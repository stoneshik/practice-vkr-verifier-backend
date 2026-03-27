package vkr.verifier.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vkr.verifier.model.ReportStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ReportResponseDto {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("reportStatus")
    private ReportStatus reportStatus;

    @JsonProperty("reportJson")
    private String reportJson;

    @JsonProperty("createdAt")
    private Instant createdAt;
}
