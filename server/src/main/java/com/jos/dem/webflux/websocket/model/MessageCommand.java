package com.jos.dem.webflux.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageCommand {
  private String nickname;
  private String text;
  private Instant timestamp;
}
