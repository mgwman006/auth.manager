package tz.tante.auth.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.tante.auth.manager.enums.InvitationStatus;
import tz.tante.auth.manager.models.entities.OrganizationInvitation;

import java.util.List;

@Repository
public interface OrganizationInvitationRepository extends JpaRepository<OrganizationInvitation, Long>
{
  boolean existsByOrganizationIdAndInvitedPhoneNumberAndStatus(Long organizationId, String invitedPhoneNumber, InvitationStatus status);

  List<OrganizationInvitation> findByInvitedPhoneNumberAndStatusOrderByIdAsc(String invitedPhoneNumber, InvitationStatus status);
}

