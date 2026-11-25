package tz.tante.auth.manager.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.tante.auth.manager.enums.MembershipStatus;
import tz.tante.auth.manager.enums.OrganizationMembershipRole;
import tz.tante.auth.manager.models.entities.Membership;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long>
{
  List<Membership> findByUserId(Long userId);

  boolean existsByOrganizationIdAndUserAccountPhoneNumberAndStatusAndOrganizationMembershipRoleIn(
    Long organizationId, String phoneNumber, MembershipStatus status, Collection<OrganizationMembershipRole> roles);

  Optional<Membership> findFirstByOrganizationIdAndInvitedPhoneNumberOrderByIdDesc(Long organizationId, String invitedPhoneNumber);

  boolean existsByOrganizationIdAndStatus(Long organizationId, MembershipStatus status);

  List<Membership> findByOrganizationIdAndStatusOrderByIdAsc(Long organizationId, MembershipStatus status);

  List<Membership> findByInvitedPhoneNumberAndStatusOrderByIdAsc(String invitedPhoneNumber, MembershipStatus status);

  boolean existsByOrganizationIdAndUserIdAndStatusAndOrganizationMembershipRoleIn(Long organizationId, @NotNull(message = "Admin user ID is required") Long aLong, MembershipStatus membershipStatus, List<OrganizationMembershipRole> owner);
}

