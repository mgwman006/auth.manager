package tz.tante.auth.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.tante.auth.manager.enums.OrganizationMembershipRole;
import tz.tante.auth.manager.models.entities.Membership;

import java.util.Collection;
import java.util.List;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long>
{
  List<Membership> findByUserId(Long userId);

  List<Membership> findByOrganizationIdOrderByIdAsc(Long organizationId);

  boolean existsByOrganizationId(Long organizationId);

  boolean existsByOrganizationIdAndUserId(Long organizationId, Long userId);

  boolean existsByOrganizationIdAndUserIdAndOrganizationMembershipRoleIn(
    Long organizationId, Long userId, Collection<OrganizationMembershipRole> roles);

  boolean existsByOrganizationIdAndUserAccountPhoneNumber(Long organizationId, String phoneNumber);
}

