package tz.tante.auth.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.tante.auth.manager.models.entities.Membership;

import java.util.List;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long>
{
  List<Membership> findByUserId(Long userId);
}
