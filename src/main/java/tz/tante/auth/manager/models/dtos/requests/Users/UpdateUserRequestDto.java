package tz.tante.auth.manager.models.dtos.requests.Users;

public record UpdateUserRequestDto(
        String firstName,
        String lastName,
        String email,
        String passWord) {
}
