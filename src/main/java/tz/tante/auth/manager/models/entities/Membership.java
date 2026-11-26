package tz.tante.auth.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tz.tante.auth.manager.enums.OrganizationMembershipRole;

@Getter
@Setter
@Entity
@Table(name = "organisation_memberships")
public class Membership extends BaseEntity
{
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organisation_id", nullable = false)
  private Organization organization;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private OrganizationMembershipRole organizationMembershipRole;
}
