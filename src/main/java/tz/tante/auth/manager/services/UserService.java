package tz.tante.auth.manager.services;

import lombok.AllArgsConstructor;
import tz.tante.auth.manager.exceptions.TanteException;
import tz.tante.auth.manager.exceptions.ResourceNotFoundException;
import tz.tante.auth.manager.models.dtos.requests.Users.UserCreateRequestDto;
import tz.tante.auth.manager.models.dtos.responses.memberships.MembershipDetailsDTO;
import tz.tante.auth.manager.models.dtos.responses.users.UserDetailsDTO;
import tz.tante.auth.manager.models.entities.Account;
import tz.tante.auth.manager.models.entities.Membership;
import tz.tante.auth.manager.models.entities.User;
import tz.tante.auth.manager.repositories.AccountRepository;
import tz.tante.auth.manager.repositories.UserRepository;
import org.springframework.stereotype.Service;
import tz.tante.auth.manager.utilities.Utils;
import java.util.ArrayList;
import java.util.Set;


@AllArgsConstructor
@Service
public class UserService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;

  public UserDetailsDTO getUserByPhoneNumber(String phoneNumber)
  {
    try
    {
      String normalizedPhoneNumber = Utils.normalizePhone(phoneNumber);
      Account account = accountRepository.findByPhoneNumber(normalizedPhoneNumber)
        .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

      User user = account.getUser();
      if (user == null)
      {
        throw new ResourceNotFoundException("User not found");
      }

      Set<Membership> memberships = user.getMemberships();


      return new UserDetailsDTO(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        account.getPhoneNumber(),
        memberships.stream()
          .map(membership -> new MembershipDetailsDTO
              (
                membership.getId(),
                membership.getUser() == null ? null:membership.getUser().getId(),
                membership.getOrganization() == null ? null:membership.getOrganization().getId(),
                membership.getOrganization() == null ? null:membership.getOrganization().getName(),
                membership.getOrganizationMembershipRole() == null ? null:membership.getOrganizationMembershipRole().name()
              )
          )
          .toList()
      );
    }
    catch (ResourceNotFoundException exception)
    {
      throw exception;
    }
    catch (Exception exception)
    {
      throw new TanteException(exception.getMessage());
    }
  }

  public UserDetailsDTO createUser(UserCreateRequestDto request)
  {
    try
    {
      String normalizedPhoneNumber = Utils.normalizePhone(request.phoneNumber());
      Account account = accountRepository.findByPhoneNumber(normalizedPhoneNumber)
        .orElseThrow(() -> new ResourceNotFoundException("Account not found"));


      User user = new User(request.firstName(), request.lastName());
      user.setAccount(account);

      account.setUser(user);

      user = userRepository.save(user);

      return new UserDetailsDTO(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getAccount().getPhoneNumber(),
        new ArrayList<>()
      );
    }
    catch (ResourceNotFoundException exception)
    {
      throw exception;
    }
    catch (Exception exception)
    {
      throw new TanteException(exception.getMessage());
    }
  }
}
