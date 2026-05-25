package tz.tante.auth.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.tante.auth.manager.models.entities.Membership;

public interface MembershipRepository extends JpaRepository<Membership, Long>
{
}
