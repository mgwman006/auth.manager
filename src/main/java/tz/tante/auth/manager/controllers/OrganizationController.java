package tz.tante.auth.manager.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.tante.auth.manager.models.dtos.common.ApiResponse;
import tz.tante.auth.manager.models.dtos.requests.organisations.OrganizationCreateDTO;
import tz.tante.auth.manager.models.dtos.responses.organisations.OrganizationDetailsDTO;
import tz.tante.auth.manager.services.OrganisationService;

@RestController
@AllArgsConstructor
@RequestMapping("/auth-manager/v1/organisations")
public class OrganizationController
{
  private final OrganisationService organisationService;

  @Operation(security = @SecurityRequirement(name = "bearerAuth"))
  @PostMapping("/{ownerId}")
  public ResponseEntity<ApiResponse<OrganizationDetailsDTO>> createOrganization(
    @PathVariable Long ownerId,
    @Valid @RequestBody OrganizationCreateDTO request
  )
  {
    OrganizationDetailsDTO organizationDetailsDTO = organisationService.createOrganization(ownerId, request);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(ApiResponse.success(organizationDetailsDTO, HttpStatus.CREATED.value()));
  }
}
