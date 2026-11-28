package tz.tante.auth.manager.models.dtos.requests.invitations;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import tz.tante.auth.manager.enums.OrganizationMembershipRole;

public record InvitationCreateRequestDTO(
  @NotBlank(message = "Phone number is required")
  @Pattern(regexp = "^(?:0[0-9]{9}|\\+?255[0-9]{9})$", message = "Enter a valid Tanzanian phone number")
  String phoneNumber,

  @NotNull(message = "Admin user ID is required")
  Long adminUserId,

  @NotNull(message = "Role is required")
  OrganizationMembershipRole role
)
{
}

