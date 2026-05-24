package tz.tante.reporting.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.tante.reporting.manager.models.entities.Lease;

public interface LeaseRepository extends JpaRepository<Lease, Long>
{
}
