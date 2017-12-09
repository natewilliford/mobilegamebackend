package com.natewilliford.mobilebackend.ofy;

import com.google.inject.Inject;
import com.googlecode.objectify.ObjectifyService;

public class OfyService {
  @Inject
  public static void setObjectifyFactory(OfyFactory factory) {
    ObjectifyService.setFactory(factory);
  }

  /**
   * @return our extension to Objectify
   */
  public static Ofy ofy() {
    return (Ofy)ObjectifyService.ofy();
  }

  public static OfyFactory factory() {
    return (OfyFactory)ObjectifyService.factory();
  }
}
