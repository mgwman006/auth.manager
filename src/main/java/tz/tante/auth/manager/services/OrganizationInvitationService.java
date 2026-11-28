package tz.tante.auth.manager.services;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import tz.tante.auth.manager.clients.NotificationManagerClient;
import tz.tante.auth.manager.enums.InvitationStatus;
import tz.tante.auth.manager.enums.OrganizationMembershipRole;
import tz.tante.auth.manager.exceptions.ResourceExistException;
import tz.tante.auth.manager.exceptions.ResourceNotFoundException;
import tz.tante.auth.manager.exceptions.TanteException;
import tz.tante.auth.manager.models.dtos.requests.invitations.InvitationCreateRequestDTO;
import tz.tante.auth.manager.models.dtos.requests.notifications.NotificationRequest;
import tz.tante.auth.manager.models.dtos.responses.invitations.InvitationDetailsDTO;
import tz.tante.auth.manager.models.entities.Account;
import tz.tante.auth.manager.models.entities.Organization;
import tz.tante.auth.manager.models.entities.OrganizationInvitation;
import tz.tante.auth.manager.models.entities.User;
import tz.tante.auth.manager.repositories.AccountRepository;
import tz.tante.auth.manager.repositories.MembershipRepository;
import tz.tante.auth.manager.repositories.OrganizationInvitationRepository;
import tz.tante.auth.manager.repositories.OrganizationRepository;
import tz.tante.auth.manager.repositories.UserRepository;
import tz.tante.auth.manager.utilities.Utils;

import java.util.List;
import java.util.Objects;

@Service
public class OrganizationInvitationService
{
  private static final List<OrganizationMembershipRole> INVITER_ROLES =
    List.of(OrganizationMembershipRole.OWNER, OrganizationMembershipRole.ADMIN);

  private final OrganizationInvitationRepository invitationRepository;
  private final OrganizationRepository organizationRepository;
  private final MembershipRepository membershipRepository;
  private final UserRepository userRepository;
  private final AccountRepository accountRepository;
  private final MembershipService membershipService;
  private final NotificationManagerClient notificationManagerClient;
  private final String userUiBaseUrl;

  public OrganizationInvitationService(
    OrganizationInvitationRepository invitationRepository,
    OrganizationRepository organizationRepository,
    MembershipRepository membershipRepository,
    UserRepository userRepository,
    AccountRepository accountRepository,
    MembershipService membershipService,
    NotificationManagerClient notificationManagerClient,
    @Value("${tante.user.manager.ui.base.url}")
    String userUiBaseUrl)
  {
    this.invitationRepository = invitationRepository;
    this.organizationRepository = organizationRepository;
    this.membershipRepository = membershipRepository;
    this.userRepository = userRepository;
    this.accountRepository = accountRepository;
    this.membershipService = membershipService;
    this.notificationManagerClient = notificationManagerClient;
    this.userUiBaseUrl = userUiBaseUrl;
  }

  @Transactional
  public InvitationDetailsDTO createInvitation(Long organizationId, InvitationCreateRequestDTO request)
  {
    // Lock before the first consistent read, so concurrent invitations cannot both pass the duplicate check.
    Organization organization = organizationRepository.findByIdForUpdate(organizationId)
      .orElseThrow(() -> new ResourceNotFoundException("Organisation not found."));

    if (!membershipRepository.existsByOrganizationIdAndUserIdAndOrganizationMembershipRoleIn(
      organizationId, request.adminUserId(), INVITER_ROLES))
    {
      throw new AccessDeniedException("Only organisation owners and admins can invite members.");
    }

    User invitedBy = userRepository.findById(request.adminUserId())
      .orElseThrow(() -> new ResourceNotFoundException("User not found."));

    String invitedPhone = Utils.normalizePhone(request.phoneNumber());

    if (membershipRepository.existsByOrganizationIdAndUserAccountPhoneNumber(organizationId, invitedPhone))
    {
      throw new ResourceExistException("This phone number is already a member of the organisation.");
    }

    if (invitationRepository.existsByOrganizationIdAndInvitedPhoneNumberAndStatus(organizationId, invitedPhone, InvitationStatus.PENDING))
    {
      throw new ResourceExistException("This phone number already has a pending invitation to the organisation.");
    }

    OrganizationInvitation invitation = new OrganizationInvitation();
    invitation.setOrganization(organization);
    invitation.setInvitedBy(invitedBy);
    invitation.setInvitedPhoneNumber(invitedPhone);
    invitation.setOrganizationMembershipRole(request.role());
    invitation.setStatus(InvitationStatus.PENDING);
    OrganizationInvitation saved = invitationRepository.save(invitation);

    // Same transaction: if the notification cannot be sent, the invitation is rolled back.
    notificationManagerClient.createNotification(buildInvitationNotification(organization, invitedPhone, saved));
    return mapInvitationToDTO(saved);
  }

  @Transactional
  public List<InvitationDetailsDTO> getPendingInvitations(String actorPhone)
  {
    return invitationRepository.findByInvitedPhoneNumberAndStatusOrderByIdAsc(Utils.normalizePhone(actorPhone), InvitationStatus.PENDING)
      .stream()
      .map(this::mapInvitationToDTO)
      .toList();
  }

  @Transactional
  public InvitationDetailsDTO acceptInvitation(Long invitationId, String actorPhone)
  {
    OrganizationInvitation invitation = getPendingInvitationForPhone(invitationId, actorPhone);

    // The invitee must have an account and a user profile before the membership is created.
    Account account = accountRepository.findByPhoneNumber(Utils.normalizePhone(actorPhone))
      .orElseThrow(() -> new ResourceNotFoundException("Account not found. Please register before accepting the invitation."));
    User user = account.getUser();
    if (user == null)
    {
      throw new TanteException("Complete your user profile before accepting the invitation.");
    }

    if (membershipRepository.existsByOrganizationIdAndUserId(invitation.getOrganization().getId(), user.getId()))
    {
      throw new ResourceExistException("You are already a member of this organisation.");
    }

    invitation.setStatus(InvitationStatus.ACCEPTED);
    membershipService.createMembership(invitation.getOrganization(), user, invitation.getOrganizationMembershipRole());
    return mapInvitationToDTO(invitation);
  }

  @Transactional
  public void declineInvitation(Long invitationId, String actorPhone)
  {
    OrganizationInvitation invitation = getPendingInvitationForPhone(invitationId, actorPhone);
    invitation.setStatus(InvitationStatus.DECLINED);
  }

  private OrganizationInvitation getPendingInvitationForPhone(Long invitationId, String actorPhone)
  {
    OrganizationInvitation invitation = invitationRepository.findById(invitationId)
      .orElseThrow(() -> new ResourceNotFoundException("Invitation not found."));

    if (!Objects.equals(invitation.getInvitedPhoneNumber(), Utils.normalizePhone(actorPhone)))
    {
      throw new AccessDeniedException("Only the invited user can respond to this invitation.");
    }

    if (invitation.getStatus() != InvitationStatus.PENDING)
    {
      throw new TanteException("This invitation is no longer pending.");
    }

    return invitation;
  }

  private NotificationRequest buildInvitationNotification(Organization organization, String invitedPhone, OrganizationInvitation invitation)
  {
    String invitationsUrl =userUiBaseUrl+ "/invitations";
    String message = ("You have been invited to join %s as %s. "
      + "Click the link to view and accept or decline your invitation: %s")
      .formatted(organization.getName(), invitation.getOrganizationMembershipRole(), invitationsUrl);

    String email = accountRepository.findByPhoneNumber(invitedPhone)
      .map(Account::getEmail)
      .orElse(null);

    return new NotificationRequest(
      "Organisation invitation",
      message,
      "SMS",
      invitedPhone,
      email,
      "ORGANIZATION_INVITATION",
      String.valueOf(invitation.getId())
    );
  }

  private InvitationDetailsDTO mapInvitationToDTO(OrganizationInvitation invitation)
  {
    return new InvitationDetailsDTO(
      invitation.getId(),
      invitation.getOrganization().getId(),
      invitation.getOrganization().getName(),
      invitation.getInvitedPhoneNumber(),
      invitation.getOrganizationMembershipRole(),
      invitation.getStatus()
    );
  }
}

