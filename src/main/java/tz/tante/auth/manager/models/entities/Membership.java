package tz.tante.auth.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tz.tante.auth.manager.enums.MembershipStatus;
import tz.tante.auth.manager.enums.OrganizationMembershipRole;

@Getter
@Setter
@Entity
@Table(name = "organisation_memberships")
public class Membership extends BaseEntity
{
  // Null until the invitee has a user profile and accepts the invitation.
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "organisation_id", nullable = false)
  private Organization organization;

  @Enumerated(EnumType.STRING)
  private OrganizationMembershipRole organizationMembershipRole;

  // Normalized phone number of the invitee; identifies the invitation before an account/user exists.
  @Column(length = 20)
  private String invitedPhoneNumber;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private MembershipStatus status = MembershipStatus.PENDING_INVITATION;
}
