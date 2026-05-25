package tz.tante.auth.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.tante.auth.manager.models.entities.Account;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>
{
  Optional<Account> findByEmail(String email);
  Optional<Account> findByPhoneNumber(String phoneNumber);
  boolean existsByPhoneNumber(String phoneNumber);
}
