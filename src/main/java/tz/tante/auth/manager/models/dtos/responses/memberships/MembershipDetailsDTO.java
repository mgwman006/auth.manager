package tz.tante.auth.manager.models.dtos.responses.memberships;


public record MembershipDetailsDTO(
  Long id,
  Long userId,
  Long organizationId,
  String organizationName,
  String membershipRole
)
{
}
