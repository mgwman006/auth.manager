package tz.tante.auth.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tz.tante.auth.manager.enums.MembershipRole;

@Getter
@Setter
@Entity
@Table(name = "organisation_memberships")
public class Membership extends BaseEntity
{
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "organisation_id", nullable = false)
  private Organization organization;

  @Enumerated(EnumType.STRING)
  private MembershipRole membershipRole;

  private boolean active = true;
}
