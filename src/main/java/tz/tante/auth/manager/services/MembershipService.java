package tz.tante.auth.manager.services;


import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import tz.tante.auth.manager.models.dtos.responses.memberships.MembershipDetailsDTO;
import tz.tante.auth.manager.models.entities.Membership;
import tz.tante.auth.manager.repositories.MembershipRepository;

import java.util.List;

@Service
@Setter
@AllArgsConstructor
public class MembershipService
{
  private final MembershipRepository membershipRepository;

  public List<MembershipDetailsDTO> getMembershipsByUserId(Long userId)
  {
    List<Membership> memberships = membershipRepository.findByUserId(userId);
    return memberships.stream()
      .map(this::mapMembershipToMembershipDetailsDTO)
      .toList();
  }

  private MembershipDetailsDTO mapMembershipToMembershipDetailsDTO(Membership membership)
  {
    return new MembershipDetailsDTO(
      membership.getId(),
      membership.getUser().getId(),
      membership.getOrganization().getId(),
      membership.getOrganization().getName(),
      membership.getMembershipRole()
    );
  }
}
