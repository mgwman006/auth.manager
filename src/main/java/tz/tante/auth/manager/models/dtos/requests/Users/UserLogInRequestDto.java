package tz.tante.auth.manager.models.dtos.requests.Users;

public record UserLogInRequestDto(
        String email,
        String passWord) {
}
