package tz.tante.auth.manager.models.dtos.responses.memberships;


import tz.tante.auth.manager.enums.OrganizationMembershipRole;

public record MembershipDetailsDTO(
  Long id,
  Long userId,
  Long organizationId,
  String organizationName,
  OrganizationMembershipRole membershipRole
)
{
}
