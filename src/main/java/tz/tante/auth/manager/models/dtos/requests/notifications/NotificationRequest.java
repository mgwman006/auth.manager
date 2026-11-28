package tz.tante.auth.manager.models.dtos.requests.notifications;

public record NotificationRequest(
  String title,
  String message,
  String type,
  String recipientPhoneNumber,
  String recipientEmail,
  String referenceType,
  String referenceId
)
{
}

