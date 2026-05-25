package tz.tante.auth.manager.models.dtos.requests.organisations;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tz.tante.auth.manager.enums.OrganizationType;
import tz.tante.auth.manager.models.dtos.common.AddressDTO;

@Schema(description = "Payload to create an organization")
public record OrganizationCreateDTO(
  @Schema(example = "Acme Corporation")
  @NotBlank(message = "Organization name is required")
  String name,

  @Schema(example = "info@tante.tz")
  @Email(message = "Invalid business email")
  @NotBlank(message = "Business Email is Required")
  String businessEmail,

  @Valid
  @NotNull(message = "Address is Required")
  AddressDTO address,

  @NotNull(message = "Organization Type is Required")
  OrganizationType type
)
{
}
