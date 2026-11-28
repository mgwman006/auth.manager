package tz.tante.auth.manager.repositories;

import tz.tante.auth.manager.models.entities.Organization;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization,Long>
{
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select o from Organization o where o.id = :id")
  Optional<Organization> findByIdForUpdate(@Param("id") Long id);
}
