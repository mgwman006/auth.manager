package tz.tante.auth.manager.models.dtos.responses.accounts;

public record AccountAuthResponseDTO(
  String  phoneNumber,
  String jwtToken
) {}
