package tz.tante.auth.manager.models.dtos.responses.users;

import tz.tante.auth.manager.models.dtos.responses.memberships.MembershipDetailsDTO;
import java.util.List;

public record UserDetailsDTO(
  Long id,
  String firstName,
  String lastName,
  String phoneNumber,
  List<MembershipDetailsDTO> memberships
)
{
}
