package tz.tante.auth.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tz.tante.auth.manager.enums.InvitationStatus;
import tz.tante.auth.manager.enums.OrganizationMembershipRole;

@Getter
@Setter
@Entity
@Table(name = "organisation_invitations")
public class OrganizationInvitation extends BaseEntity
{
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private Organization organization;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private User invitedBy;

  // Normalized phone number of the invitee; the invitee may not have an account yet.
  @Column(nullable = false, length = 20)
  private String invitedPhoneNumber;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private OrganizationMembershipRole organizationMembershipRole;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private InvitationStatus status = InvitationStatus.PENDING;
}

