package tz.tante.auth.manager.services;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tz.tante.auth.manager.enums.OrganizationMembershipRole;
import tz.tante.auth.manager.exceptions.ResourceExistException;
import tz.tante.auth.manager.exceptions.TanteException;
import tz.tante.auth.manager.models.dtos.common.AddressDTO;
import tz.tante.auth.manager.models.dtos.requests.organisations.OrganizationCreateDTO;
import tz.tante.auth.manager.models.dtos.responses.organisations.OrganizationDetailsDTO;
import tz.tante.auth.manager.models.entities.Address;
import tz.tante.auth.manager.models.entities.Membership;
import tz.tante.auth.manager.models.entities.Organization;
import tz.tante.auth.manager.models.entities.User;
import tz.tante.auth.manager.repositories.MembershipRepository;
import tz.tante.auth.manager.repositories.OrganizationRepository;
import tz.tante.auth.manager.repositories.UserRepository;

@Service
@AllArgsConstructor
public class OrganisationService
{
  private final UserRepository userRepository;
  private final OrganizationRepository organizationRepository;
  private final MembershipRepository membershipRepository;

  @Transactional
  public OrganizationDetailsDTO createOrganization(Long ownerId, OrganizationCreateDTO request)
  {
    try
    {
      User user = userRepository.findById(ownerId)
        .orElseThrow( () -> new ResourceExistException("User with id " + ownerId + " not found."));

      AddressDTO addressDTO = request.address();
      Address address = new Address(
        addressDTO.postalCode(),
        addressDTO.streetNumber(),
        addressDTO.streetName(),
        addressDTO.ward(),
        addressDTO.city(),
        addressDTO.region(),
        addressDTO.country()
      );

      Organization organization = new Organization(
        request.name(),
        request.businessEmail(),
        address,
        request.type()
      );

      organization = organizationRepository.save(organization);

      Membership membership = new Membership();
      membership.setOrganizationMembershipRole(OrganizationMembershipRole.OWNER);

      organization.addMembership(membership);
      user.addMembership(membership);

      membershipRepository.save(membership);

      address = organization.getAddress();

      return new OrganizationDetailsDTO(
        organization.getId(),
        organization.getName(),
        organization.getBusinessEmail(),
        new AddressDTO(
          address.getPostalCode(),
          address.getStreetNumber(),
          address.getStreetName(),
          address.getWard(),
          address.getCity(),
          address.getRegion(),
          address.getCountry()
        ),
        organization.getType()
      );
    }
    catch (Exception exception)
    {
      throw new TanteException(exception.getMessage());
    }
  }
}
