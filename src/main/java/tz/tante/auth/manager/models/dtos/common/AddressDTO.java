package tz.tante.auth.manager.models.dtos.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Address details")
public record AddressDTO(
  long postalCode,

  long streetNumber,

  @NotBlank(message = "Street name is required")
  String streetName,

  @NotBlank(message = "Ward is required")
  String ward,

  @NotBlank(message = "City is required")
  String city,

  @NotBlank(message = "Region is required")
  String region,

  @NotBlank(message = "Country is required")
  String country)
{
}