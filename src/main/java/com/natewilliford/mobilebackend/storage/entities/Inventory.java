package com.natewilliford.mobilebackend.storage.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Inventory {
  @Id Long id;
  public Long gold;
  public List<Building> buildings = new ArrayList<>();

}
