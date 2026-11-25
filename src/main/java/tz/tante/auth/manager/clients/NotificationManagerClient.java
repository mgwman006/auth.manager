package tz.tante.auth.manager.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tz.tante.auth.manager.configs.feign.NotificationManagerFeignConfig;
import tz.tante.auth.manager.models.dtos.requests.notifications.NotificationRequest;

@FeignClient(
  name = "notification-manager",
  url = "${notification-manager.base-url}",
  configuration = NotificationManagerFeignConfig.class
)
public interface NotificationManagerClient
{
  @PostMapping("/v1/notifications")
  void createNotification(@RequestBody NotificationRequest request);
}

