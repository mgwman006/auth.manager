package tz.tante.auth.manager.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.tante.auth.manager.enums.OrganizationType;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "organizations")
@NoArgsConstructor
@AllArgsConstructor
public class Organization extends BaseEntity
{
  private String name;

  private String businessEmail;

  @Embedded
  private Address address;

  @Enumerated(EnumType.STRING)
  private OrganizationType type;

  @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
  private Set<Membership> memberships = new HashSet<>();

  public Organization(String name, String s, Address address, OrganizationType type)
  {
    this.name = name;
    this.businessEmail = s;
    this.address = address;
    this.type = type;
  }

  public void addMembership(Membership membership)
  {
    memberships.add(membership);
    membership.setOrganization(this);
  }
}
