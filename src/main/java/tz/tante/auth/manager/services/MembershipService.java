package tz.tante.auth.manager.services;


import lombok.AllArgsConstructor;
import lombok.Setter;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import tz.tante.auth.manager.clients.NotificationManagerClient;
import tz.tante.auth.manager.enums.MembershipStatus;
import tz.tante.auth.manager.enums.OrganizationMembershipRole;
import tz.tante.auth.manager.exceptions.ResourceExistException;
import tz.tante.auth.manager.exceptions.ResourceNotFoundException;
import tz.tante.auth.manager.exceptions.TanteException;
import tz.tante.auth.manager.models.dtos.requests.memberships.MembershipCreateRequestDTO;
import tz.tante.auth.manager.models.dtos.requests.notifications.NotificationRequest;
import tz.tante.auth.manager.models.dtos.responses.memberships.MembershipDetailsDTO;
import tz.tante.auth.manager.models.entities.Account;
import tz.tante.auth.manager.models.entities.Membership;
import tz.tante.auth.manager.models.entities.Organization;
import tz.tante.auth.manager.models.entities.User;
import tz.tante.auth.manager.repositories.AccountRepository;
import tz.tante.auth.manager.repositories.MembershipRepository;
import tz.tante.auth.manager.repositories.OrganizationRepository;
import tz.tante.auth.manager.utilities.Utils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Setter
@AllArgsConstructor
public class MembershipService
{
  private static final List<MembershipStatus> BLOCKING_STATUSES =
    List.of(MembershipStatus.ACTIVE, MembershipStatus.PENDING_INVITATION);

  private final MembershipRepository membershipRepository;
  private final OrganizationRepository organizationRepository;
  private final AccountRepository accountRepository;
  private final NotificationManagerClient notificationManagerClient;

  @Transactional
  public MembershipDetailsDTO createMembership(Long organizationId, MembershipCreateRequestDTO request)
  {
    // Lock before the first consistent read, so concurrent invitations cannot both pass the duplicate check.
    Organization organization = organizationRepository.findByIdForUpdate(organizationId)
      .orElseThrow(() -> new ResourceNotFoundException("Organisation not found."));

    if (!membershipRepository.existsByOrganizationIdAndUserIdAndStatusAndOrganizationMembershipRoleIn(
      organizationId, request.adminUserId(), MembershipStatus.ACTIVE, List.of(OrganizationMembershipRole.OWNER, OrganizationMembershipRole.ADMIN)))
    {
      throw new AccessDeniedException("Only organisation owners and admins can add members.");
    }

    // The invitee does not need an account yet; the invitation is keyed by phone number.
    String invitedPhone = Utils.normalizePhone(request.phoneNumber());

    Optional<Membership> existing = membershipRepository.findFirstByOrganizationIdAndInvitedPhoneNumberOrderByIdDesc(organizationId, invitedPhone);
    if (existing.isPresent() && BLOCKING_STATUSES.contains(existing.get().getStatus()))
    {
      throw new ResourceExistException("This phone number is already a member or has a pending invitation in the organisation.");
    }

    // Reuse an inactive/declined row so re-inviting does not accumulate duplicate memberships.
    Membership membership = existing.orElseGet(Membership::new);
    membership.setOrganization(organization);
    membership.setInvitedPhoneNumber(invitedPhone);
    membership.setOrganizationMembershipRole(request.role() == null ? OrganizationMembershipRole.MEMBER : request.role());
    membership.setStatus(MembershipStatus.PENDING_INVITATION);
    Membership saved = membershipRepository.save(membership);

    // Same transaction: if the notification cannot be sent, the invitation is rolled back.
    notificationManagerClient.createNotification(buildInvitationNotification(organization, invitedPhone, saved));
    return mapMembershipToMembershipDetailsDTO(saved);
  }

  @Transactional
  public MembershipDetailsDTO acceptInvitation(Long membershipId, String actorPhone)
  {
    Membership membership = getPendingInvitationForPhone(membershipId, actorPhone);

    // The invitee must have an account and a user profile before the membership is activated.
    Account account = accountRepository.findByPhoneNumber(Utils.normalizePhone(actorPhone))
      .orElseThrow(() -> new ResourceNotFoundException("Account not found. Please register before accepting the invitation."));
    User user = account.getUser();
    if (user == null)
    {
      throw new TanteException("Complete your user profile before accepting the invitation.");
    }

    membership.setUser(user);
    membership.setStatus(MembershipStatus.ACTIVE);
    return mapMembershipToMembershipDetailsDTO(membership);
  }

  @Transactional
  public void declineInvitation(Long membershipId, String actorPhone)
  {
    Membership membership = getPendingInvitationForPhone(membershipId, actorPhone);
    membership.setStatus(MembershipStatus.DECLINED);
  }

  public List<MembershipDetailsDTO> getPendingInvitations(String actorPhone)
  {
    return membershipRepository.findByInvitedPhoneNumberAndStatusOrderByIdAsc(Utils.normalizePhone(actorPhone), MembershipStatus.PENDING_INVITATION)
      .stream()
      .map(this::mapMembershipToMembershipDetailsDTO)
      .toList();
  }

  public List<MembershipDetailsDTO> getMembershipsByUserId(Long userId)
  {
    List<Membership> memberships = membershipRepository.findByUserId(userId);
    return memberships.stream()
      .map(this::mapMembershipToMembershipDetailsDTO)
      .toList();
  }

  @Transactional
  public List<MembershipDetailsDTO> getOrganisationMembers(Long organizationId)
  {
    if (!membershipRepository.existsByOrganizationIdAndStatus(organizationId, MembershipStatus.ACTIVE))
    {
      throw new AccessDeniedException("Only active organisation members can view the member list.");
    }

    return membershipRepository.findByOrganizationIdAndStatusOrderByIdAsc(organizationId, MembershipStatus.ACTIVE)
      .stream()
      .map(this::mapMembershipToMembershipDetailsDTO)
      .toList();
  }


  private Membership getPendingInvitationForPhone(Long membershipId, String actorPhone)
  {
    Membership membership = membershipRepository.findById(membershipId)
      .orElseThrow(() -> new ResourceNotFoundException("Invitation not found."));

    if (!Objects.equals(membership.getInvitedPhoneNumber(), Utils.normalizePhone(actorPhone)))
    {
      throw new AccessDeniedException("Only the invited user can respond to this invitation.");
    }

    if (membership.getStatus() != MembershipStatus.PENDING_INVITATION)
    {
      throw new TanteException("This invitation is no longer pending.");
    }

    return membership;
  }

  private NotificationRequest buildInvitationNotification(Organization organization, String invitedPhone, Membership membership)
  {
    String message = "You have been invited to join %s as %s. Create your account to accept or decline the invitation."
      .formatted(organization.getName(), membership.getOrganizationMembershipRole());

    String email = accountRepository.findByPhoneNumber(invitedPhone)
      .map(Account::getEmail)
      .orElse(null);

    return new NotificationRequest(
      "Organisation invitation",
      message,
      "SMS",
      invitedPhone,
      email,
      "MEMBERSHIP",
      String.valueOf(membership.getId())
    );
  }

  private MembershipDetailsDTO mapMembershipToMembershipDetailsDTO(Membership membership)
  {
    return new MembershipDetailsDTO(
      membership.getId(),
      membership.getUser() == null ? null : membership.getUser().getId(),
      membership.getOrganization().getId(),
      membership.getOrganization().getName(),
      membership.getOrganizationMembershipRole(),
      membership.getStatus()
    );
  }
}

