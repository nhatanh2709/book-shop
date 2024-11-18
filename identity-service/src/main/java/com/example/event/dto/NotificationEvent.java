package com.example.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEvent {
    String channel;
    String recipient;
    String templateCode;
    Map<String , Object> param;
    String subject;
    String body;
}
