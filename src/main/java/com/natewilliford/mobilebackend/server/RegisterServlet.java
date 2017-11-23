package com.natewilliford.mobilebackend.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.common.base.Strings;
import com.googlecode.objectify.ObjectifyService;
import com.natewilliford.mobilebackend.server.api.ApiUser;
import com.natewilliford.mobilebackend.server.api.GenericRequest;
import com.natewilliford.mobilebackend.server.api.GenericResponse;
import com.natewilliford.mobilebackend.storage.entities.Device;
import com.natewilliford.mobilebackend.storage.entities.User;
import org.mindrot.jbcrypt.BCrypt;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class RegisterServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    ObjectifyService.register(User.class);
    ObjectifyService.register(Device.class);

//    req.getAttribute()
    String jsonRequest = req.getParameter("json");

    System.out.println(jsonRequest);
    PrintWriter writer = resp.getWriter();

//    JsonFactory jsonFactory = new JsonFactory();

    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    RegisterRequest registerRequest = mapper.readValue(jsonRequest, RegisterRequest.class);
    RegisterResponse registerResponse = new RegisterResponse();

    String email = registerRequest.email.replaceAll("\\s+", "").toLowerCase();
    String password = registerRequest.password.replaceAll("\\s+", "");

    if (Strings.isNullOrEmpty(email) || Strings.isNullOrEmpty(password)) {
      registerResponse.error = "Email and password required.";
      sendResponse(registerResponse, writer);
      return;
    }

    QueryResultIterator<User> existingUsersIterator = ObjectifyService.ofy().load().type(User.class).filter("email = ", email).iterator();
    if (existingUsersIterator.hasNext()) {
      registerResponse.error = "User with that email already exists.";
      sendResponse(registerResponse, writer);
      return;
    }

    try {
      User user = new User();
      user.email = email;
      user.hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

      ObjectifyService.ofy().save().entity(user).now();


      ApiUser apiUser = new ApiUser();
      apiUser.userId = user.getId().toString();

      registerResponse.user = apiUser;

      writer.println(mapper.writeValueAsString(registerResponse));
    } catch (RuntimeException e) {
      registerResponse.error = "Couldn't save new user.";
      System.err.println("Error creating user: " + e.getMessage());
      sendResponse(registerResponse, writer);
      return;
    }

////    JacksonFactory jackson = new JacksonFactory();
////    JsonFactory jsonFactory = new JsonFactory();
////    JsonParser parser = jsonFactory.createJsonParser(requestJson);
//
//    ObjectMapper mapper = new ObjectMapper();
//    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//
//    RegisterRequest registerRequest = mapper.readValue(jsonRequest, RegisterRequest.class);
//
//    System.out.println("----" + registerRequest.uniqueDeviceId + "-----");
//    PrintWriter writer = resp.getWriter();
////    writer.println("Farts!!!");
//
//    RegisterResponse registerResponse = new RegisterResponse();
////    registerResponse.userId = "123xyz";
////    registerResponse.someOtherShit = "some studip hsit";
//    registerResponse.user = new User();
//    registerResponse.user.userId = "abc123";
//
//
//    writer.println(mapper.writeValueAsString(registerResponse));

  }

  private void sendResponse(RegisterResponse response, PrintWriter writer) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    writer.println(mapper.writeValueAsString(response));
  }
}

class RegisterRequest extends GenericRequest {
  public String uniqueDeviceId;
  public String email;
  public String password;
}

class RegisterResponse extends GenericResponse {
  public ApiUser user;
}