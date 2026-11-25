package tz.tante.auth.manager.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tz.tante.auth.manager.models.dtos.common.ApiResponse;
import tz.tante.auth.manager.models.dtos.requests.invitations.InvitationCreateRequestDTO;
import tz.tante.auth.manager.models.dtos.responses.invitations.InvitationDetailsDTO;
import tz.tante.auth.manager.services.OrganizationInvitationService;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@Setter
@RequestMapping("/v1/invitations")
public class InvitationController
{
  private final OrganizationInvitationService invitationService;

  @PostMapping("/organisations/{organizationId}")
  public ResponseEntity<ApiResponse<InvitationDetailsDTO>> createInvitation(
    @PathVariable Long organizationId, @Valid @RequestBody InvitationCreateRequestDTO request)
  {
    InvitationDetailsDTO invitation = invitationService.createInvitation(organizationId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(invitation, HttpStatus.CREATED.value()));
  }

  @GetMapping("/pending")
  public ResponseEntity<ApiResponse<List<InvitationDetailsDTO>>> getPendingInvitations(Principal principal)
  {
    List<InvitationDetailsDTO> invitations = invitationService.getPendingInvitations(principal.getName());
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(invitations, HttpStatus.OK.value()));
  }

  @PostMapping("/{invitationId}/accept")
  public ResponseEntity<ApiResponse<InvitationDetailsDTO>> acceptInvitation(@PathVariable Long invitationId, Principal principal)
  {
    InvitationDetailsDTO invitation = invitationService.acceptInvitation(invitationId, principal.getName());
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(invitation, HttpStatus.OK.value()));
  }

  @PostMapping("/{invitationId}/decline")
  public ResponseEntity<ApiResponse<Void>> declineInvitation(@PathVariable Long invitationId, Principal principal)
  {
    invitationService.declineInvitation(invitationId, principal.getName());
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(null, HttpStatus.OK.value()));
  }
}

