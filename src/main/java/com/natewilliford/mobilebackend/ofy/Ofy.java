package com.natewilliford.mobilebackend.ofy;

import com.googlecode.objectify.cmd.Loader;
import com.googlecode.objectify.impl.ObjectifyImpl;

public class Ofy extends ObjectifyImpl<Ofy> {

  public Ofy(OfyFactory base) {
    super(base);
  }

  @Override
  public OfyLoader load() {
    return new OfyLoader(this);
  }
}
