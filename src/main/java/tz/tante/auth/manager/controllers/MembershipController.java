package tz.tante.auth.manager.controllers;

import lombok.AllArgsConstructor;
import lombok.Setter;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tz.tante.auth.manager.models.dtos.common.ApiResponse;
import tz.tante.auth.manager.models.dtos.requests.memberships.MembershipCreateRequestDTO;
import tz.tante.auth.manager.models.dtos.responses.memberships.MembershipDetailsDTO;
import tz.tante.auth.manager.services.MembershipService;

import java.util.List;

@RestController
@AllArgsConstructor
@Setter
@RequestMapping("/v1/memberships")
public class MembershipController
{
  private final MembershipService membershipService;

  @PostMapping("/organisations/{organizationId}")
  public ResponseEntity<ApiResponse<MembershipDetailsDTO>> createMembership(
    @PathVariable Long organizationId, @Valid @RequestBody MembershipCreateRequestDTO request)
  {
    MembershipDetailsDTO membership = membershipService.createMembership(organizationId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(membership, HttpStatus.CREATED.value()));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponse<List<MembershipDetailsDTO>>> getMembershipsByUserId(@PathVariable Long userId)
  {
    List<MembershipDetailsDTO> memberships = membershipService.getMembershipsByUserId(userId);
    return ResponseEntity.status(200)
      .body(ApiResponse.success(memberships, 200));
  }

  @GetMapping("/organisations/{organizationId}")
  public ResponseEntity<ApiResponse<List<MembershipDetailsDTO>>> getOrganisationMembers(@PathVariable Long organizationId)
  {
    List<MembershipDetailsDTO> memberships = membershipService.getOrganisationMembers(organizationId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(memberships, HttpStatus.OK.value()));
  }

  @GetMapping("/invitations/pending")
  public ResponseEntity<ApiResponse<List<MembershipDetailsDTO>>> getPendingInvitations(Principal principal)
  {
    List<MembershipDetailsDTO> invitations = membershipService.getPendingInvitations(principal.getName());
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(invitations, HttpStatus.OK.value()));
  }

  @PostMapping("/{membershipId}/accept")
  public ResponseEntity<ApiResponse<MembershipDetailsDTO>> acceptInvitation(@PathVariable Long membershipId, Principal principal)
  {
    MembershipDetailsDTO membership = membershipService.acceptInvitation(membershipId, principal.getName());
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(membership, HttpStatus.OK.value()));
  }

  @PostMapping("/{membershipId}/decline")
  public ResponseEntity<ApiResponse<Void>> declineInvitation(@PathVariable Long membershipId, Principal principal)
  {
    membershipService.declineInvitation(membershipId, principal.getName());
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(null, HttpStatus.OK.value()));
  }
}
