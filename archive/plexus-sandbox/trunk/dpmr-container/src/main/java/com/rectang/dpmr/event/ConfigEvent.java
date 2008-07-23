package com.rectang.dpmr.event;

public class ConfigEvent {

  private String key, value, oldValue;

  public ConfigEvent(String key, String value, String oldValue) {
    this.key = key;
    this.value = value;
    this.oldValue = oldValue;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public String getOldValue() {
    return oldValue;
  }
}

