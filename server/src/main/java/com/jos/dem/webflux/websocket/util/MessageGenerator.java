package com.jos.dem.webflux.websocket.util;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class MessageGenerator {

  private List<String> messages =
      Arrays.asList("Bonjour", "Hola", "Zdravstvuyte", "Salve", "Guten Tag", "Hello");

  private final Random random = new Random(messages.size());

  public String generate(){
      return messages.get(random.nextInt(messages.size()));
  }
}
