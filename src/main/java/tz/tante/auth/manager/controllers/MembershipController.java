package tz.tante.auth.manager.controllers;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tz.tante.auth.manager.models.dtos.common.ApiResponse;
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

  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponse<List<MembershipDetailsDTO>>> getMembershipsByUserId(@PathVariable Long userId)
  {
    List<MembershipDetailsDTO> memberships = membershipService.getMembershipsByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(memberships, HttpStatus.OK.value()));
  }

  @GetMapping("/organisations/{organizationId}")
  public ResponseEntity<ApiResponse<List<MembershipDetailsDTO>>> getOrganisationMembers(@PathVariable Long organizationId)
  {
    List<MembershipDetailsDTO> memberships = membershipService.getOrganisationMembers(organizationId);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(memberships, HttpStatus.OK.value()));
  }
}

