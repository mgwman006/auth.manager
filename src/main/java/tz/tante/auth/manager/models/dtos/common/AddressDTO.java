package tz.tante.auth.manager.models.dtos.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Address details")
public record AddressDTO(
  @NotBlank(message = "Street is required")
  String street,

  @NotBlank(message = "Area is required")
  String area,

  @NotBlank(message = "City is required")
  String city,

  @NotBlank(message = "Region is Required")
  String region,

  @NotBlank(message = "Country is required")
  String country
)
{
}
