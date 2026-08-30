package tz.tante.auth.manager.models.dtos.responses.memberships;


import tz.tante.auth.manager.enums.MembershipRole;

public record MembershipDetailsDTO(
  Long id,
  Long userId,
  Long organizationId,
  String organizationName,
  MembershipRole membershipRole
)
{
}
