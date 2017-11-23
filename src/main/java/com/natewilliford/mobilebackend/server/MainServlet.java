package com.natewilliford.mobilebackend.server;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.natewilliford.mobilebackend.storage.entities.Device;
import com.natewilliford.mobilebackend.storage.entities.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@SuppressWarnings("serial")
public class MainServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PrintWriter writer = resp.getWriter();

    writer.println("Hello, obj!!!");
    System.out.println("MainServlet");

    ObjectifyService.register(User.class);
    ObjectifyService.register(Device.class);


    User user = new User();
    user.displayName = "Some BODY";
    Device device = new Device();
    device.uniqueDeviceId = "23234";
    user.devices.add(device);

    Device device2 = new Device();
    device2.uniqueDeviceId = "abcded";
    user.devices.add(device2);

    ObjectifyService.ofy().save().entity(user).now();
    writer.println("User id: " + user.getId());
    writer.println("device: " + device.uniqueDeviceId);


    QueryResultIterator<User> it = ObjectifyService.ofy().load().type(User.class).filter("devices.uniqueDeviceId = ", "23234").iterator();
    while (it.hasNext()) {
      User loadUser = it.next();
      if (loadUser != null) {
        writer.println("User id: " + loadUser.getId());
        writer.println("User name: " + loadUser.displayName);

      } else {
        writer.println("No user found");
      }
    }

//    User user = ObjectifyService.ofy().load().type(User.class).id(5629499534213120L).now();
//    if (user != null) {
//      writer.println("User id: " + user.getId());
//      writer.println("User name: " + user.displayName);
//
//    } else {
//      writer.println("No user found");
//    }
  }
}