package com.natewilliford.mobilebackend;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.natewilliford.mobilebackend.ofy.OfyService;
import com.natewilliford.mobilebackend.server.BuyServlet;
import com.natewilliford.mobilebackend.server.LoginServlet;
import com.natewilliford.mobilebackend.server.RegisterServlet;
import com.natewilliford.mobilebackend.server.SyncServlet;

public class GuiceConfig extends GuiceServletContextListener {

  static class ZeusServletModule extends ServletModule {
    @Override
    protected void configureServlets() {
      filter("/*").through(ObjectifyFilter.class);

      serve("/register").with(RegisterServlet.class);
      serve("/login").with(LoginServlet.class);
      serve("/sync").with(SyncServlet.class);
      serve("/buy").with(BuyServlet.class);
    }
  }

  static class ZeusModule extends AbstractModule {
    @Override
    protected void configure() {
      requestStaticInjection(OfyService.class);
      bind(ObjectifyFilter.class).in(Singleton.class);
    }
  }

  @Override
  protected Injector getInjector() {
    return Guice.createInjector(new ZeusServletModule(), new ZeusModule());
  }
}
