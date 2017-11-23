package com.natewilliford.mobilebackend.storage.entities;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
  @Id Long id;
  public String displayName;
  @Index public String email;

  public List<Device> devices;

  public User() {
    devices = new ArrayList<>();
  }

  public Long getId() {
    return id;
  }


//  public void addDevice(Device device) {
//    devices.add(Ref.create(device));
//  }

}

