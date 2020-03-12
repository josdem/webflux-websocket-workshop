package com.jos.dem.webflux.websocket.util;

import java.util.Arrays;
import java.util.List;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class MessageGenerator {

  private List<String> messages =
      Arrays.asList("Liam", "Noha", "William", "James", "Oliver", "Benjamin");

  private final Random random = new Random(messages.size());

  public String generate(){
    return messages.get(random.nextInt(messages.size()));
  }

}
