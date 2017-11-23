package com.natewilliford.mobilebackend.server;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class RegisterServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

//    req.getAttribute()
    String jsonRequest = req.getParameter("json");

    System.out.println(jsonRequest);

//    JacksonFactory jackson = new JacksonFactory();
//    JsonFactory jsonFactory = new JsonFactory();
//    JsonParser parser = jsonFactory.createJsonParser(requestJson);

    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    RegisterRequest registerRequest = mapper.readValue(jsonRequest, RegisterRequest.class);

    System.out.println("----" + registerRequest.uniqueDeviceId + "-----");
    PrintWriter writer = resp.getWriter();
//    writer.println("Farts!!!");

    RegisterResponse registerResponse = new RegisterResponse();
//    registerResponse.userId = "123xyz";
//    registerResponse.someOtherShit = "some studip hsit";
    registerResponse.user = new User();
    registerResponse.user.userId = "abc123";


    writer.println(mapper.writeValueAsString(registerResponse));

  }
}

class RegisterRequest {
  public String uniqueDeviceId;
}

class RegisterResponse {
  public User user;
}

class User {
  public String userId;
}