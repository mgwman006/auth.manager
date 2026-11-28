package tz.tante.auth.manager.services;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import tz.tante.auth.manager.enums.OrganizationMembershipRole;
import tz.tante.auth.manager.models.dtos.responses.memberships.MembershipDetailsDTO;
import tz.tante.auth.manager.models.entities.Membership;
import tz.tante.auth.manager.models.entities.Organization;
import tz.tante.auth.manager.models.entities.User;
import tz.tante.auth.manager.repositories.MembershipRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class MembershipService
{
  private final MembershipRepository membershipRepository;

  // Membership is only created by accepting an invitation or by creating an organisation (OWNER).
  @Transactional
  public MembershipDetailsDTO createMembership(Organization organization, User user, OrganizationMembershipRole role)
  {
    Membership membership = new Membership();
    membership.setOrganizationMembershipRole(role);
    organization.addMembership(membership);
    user.addMembership(membership);

    return mapMembershipToMembershipDetailsDTO(membershipRepository.save(membership));
  }

  public List<MembershipDetailsDTO> getMembershipsByUserId(Long userId)
  {
    return membershipRepository.findByUserId(userId)
      .stream()
      .map(this::mapMembershipToMembershipDetailsDTO)
      .toList();
  }

  @Transactional
  public List<MembershipDetailsDTO> getOrganisationMembers(Long organizationId)
  {
    if (!membershipRepository.existsByOrganizationId(organizationId))
    {
      throw new AccessDeniedException("Only organisation members can view the member list.");
    }

    return membershipRepository.findByOrganizationIdOrderByIdAsc(organizationId)
      .stream()
      .map(this::mapMembershipToMembershipDetailsDTO)
      .toList();
  }

  private MembershipDetailsDTO mapMembershipToMembershipDetailsDTO(Membership membership)
  {
    return new MembershipDetailsDTO(
      membership.getId(),
      membership.getUser() == null ? null : membership.getUser().getId(),
      membership.getUser() == null ? null : membership.getUser().getFirstName(),
      membership.getUser() == null ? null : membership.getUser().getLastName(),
      membership.getOrganization() == null ? null : membership.getOrganization().getId(),
      membership.getOrganization() == null ? null : membership.getOrganization().getName(),
      membership.getOrganizationMembershipRole()
    );
  }
}

