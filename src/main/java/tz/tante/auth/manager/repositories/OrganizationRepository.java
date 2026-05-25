package tz.tante.auth.manager.repositories;

import tz.tante.auth.manager.models.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization,Long>
{
}
