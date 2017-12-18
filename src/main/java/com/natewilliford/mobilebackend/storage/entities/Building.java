package com.natewilliford.mobilebackend.storage.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Subclass;

import java.util.Date;


@Entity
public class Building {
  @Id Long id;
  public Integer buildingType;
  public Date lastCollected;
}
