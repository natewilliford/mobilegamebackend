package com.natewilliford.mobilebackend.storage.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Device {
  @Id Long id;
  @Index public String uniqueDeviceId;

  public Long getId() {
    return id;
  }
}