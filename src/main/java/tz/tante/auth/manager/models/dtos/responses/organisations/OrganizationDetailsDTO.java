package tz.tante.auth.manager.models.dtos.responses.organisations;

import tz.tante.auth.manager.enums.OrganizationType;
import tz.tante.auth.manager.models.dtos.common.AddressDTO;

public record OrganizationDetailsDTO(
  long id,
  String name,
  String businessEmail,
  AddressDTO address,
  OrganizationType type
)
{
}
