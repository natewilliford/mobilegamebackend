package com.natewilliford.mobilebackend.storage.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
  @Id Long id;
  public String displayName;
  @Index
  public String email;
  public String hashedPassword;

  public List<Device> devices;

  public Inventory inventory = new Inventory();

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

