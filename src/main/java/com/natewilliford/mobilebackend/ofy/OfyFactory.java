package com.natewilliford.mobilebackend.ofy;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.googlecode.objectify.ObjectifyFactory;
import com.natewilliford.mobilebackend.storage.entities.Building;
import com.natewilliford.mobilebackend.storage.entities.Device;
import com.natewilliford.mobilebackend.storage.entities.Inventory;
import com.natewilliford.mobilebackend.storage.entities.User;

public class OfyFactory extends ObjectifyFactory {

  private Injector injector;

  @Inject
  public OfyFactory(Injector injector) {
    this.injector = injector;

    this.register(User.class);
    this.register(Device.class);
    this.register(Inventory.class);
    this.register(Building.class);
  }

  @Override
  public <T> T construct(Class<T> type) {
    return injector.getInstance(type);
  }

  @Override
  public Ofy begin() {
    return new Ofy(this);
  }
}
