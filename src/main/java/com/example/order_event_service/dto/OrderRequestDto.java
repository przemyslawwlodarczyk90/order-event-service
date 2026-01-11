package com.example.order_event_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrderRequestDto {

    @NotBlank
    @Schema(example = "SHIP-123456")
    private String shipmentNumber;

    @NotBlank
    @Email
    @Schema(example = "test@test.com")
    private String recipientEmail;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}$", message = "Country code must be ISO-2 (e.g. PL)")
    @Schema(example = "PL")
    private String recipientCountryCode;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}$", message = "Country code must be ISO-2 (e.g. DE)")
    @Schema(example = "DE")
    private String senderCountryCode;

    @Min(0)
    @Max(100)
    @Schema(example = "99")
    private int statusCode;


}

