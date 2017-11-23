package com.natewilliford.mobilebackend.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.common.base.Strings;
import com.googlecode.objectify.ObjectifyService;
import com.natewilliford.mobilebackend.server.api.GenericRequest;
import com.natewilliford.mobilebackend.server.api.GenericResponse;
import com.natewilliford.mobilebackend.storage.entities.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class LoginServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String jsonRequest = req.getParameter("json");
    ObjectifyService.register(User.class);

    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    LoginRequest loginRequest = mapper.readValue(jsonRequest, LoginRequest.class);
    LoginResponse loginResponse = new LoginResponse();

    String email = loginRequest.email.replaceAll("\\s+", "").toLowerCase();
    String password = loginRequest.password.replaceAll("\\s+", "");

    if (Strings.isNullOrEmpty(email) || Strings.isNullOrEmpty(password)) {
      loginResponse.error = "Email and password required.";
      sendResponse(loginResponse, resp.getWriter());
      return;
    }

    try {
      QueryResultIterator<User> existingUsersIterator = ObjectifyService.ofy().load().type(User.class).filter("email = ", email).iterator();
      if (existingUsersIterator.hasNext()) {
        User user = existingUsersIterator.next();
        if (BCrypt.checkpw(password, user.hashedPassword)) {

          loginResponse.success = true;
          sendResponse(loginResponse, resp.getWriter());

        } else {
          loginResponse.error = "Wrong password";
          sendResponse(loginResponse, resp.getWriter());
        }

      }
    } catch (RuntimeException e) {
      loginResponse.error = "Error when logging in.";
      System.err.println("Error when logging in: " + e.getMessage());
      sendResponse(loginResponse, resp.getWriter());
      return;
    }
  }

  private void sendResponse(LoginResponse response, PrintWriter writer) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    writer.println(mapper.writeValueAsString(response));
  }
}


class LoginRequest extends GenericRequest {
  public String email;
  public String password;
}

class LoginResponse extends GenericResponse {
  public boolean success;
}