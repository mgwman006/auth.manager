package tz.tante.auth.manager.models.dtos.responses.accounts;


public record AccountDetailsDto(
  Long id,
  String phoneNumber,
  String email,
  boolean enabled
) { }
