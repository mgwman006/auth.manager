package tz.tante.auth.manager.models.dtos.responses.invitations;

import tz.tante.auth.manager.enums.InvitationStatus;
import tz.tante.auth.manager.enums.OrganizationMembershipRole;

public record InvitationDetailsDTO(
  Long id,
  Long organizationId,
  String organizationName,
  String invitedPhoneNumber,
  OrganizationMembershipRole role,
  InvitationStatus status
)
{
}

