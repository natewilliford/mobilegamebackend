package com.natewilliford.mobilebackend.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.common.base.Strings;
import com.google.inject.Singleton;
import com.natewilliford.mobilebackend.server.api.ApiUser;
import com.natewilliford.mobilebackend.server.api.Auth;
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

import static com.natewilliford.mobilebackend.ofy.OfyService.ofy;

@Singleton
public class LoginServlet extends HttpServlet {

  public LoginServlet() {
    super();
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    System.out.println("Login post");

    String jsonRequest = req.getParameter("json");

    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    LoginRequest loginRequest = mapper.readValue(jsonRequest, LoginRequest.class);
    LoginResponse loginResponse = new LoginResponse();

    String email = loginRequest.email.replaceAll("\\s+", "").toLowerCase();
    String password = loginRequest.password.replaceAll("\\s+", "");

    if (Strings.isNullOrEmpty(email) || Strings.isNullOrEmpty(password)) {

      System.out.println("Email and password required.");

      loginResponse.error = "Email and password required.";
      sendResponse(loginResponse, resp.getWriter());
      return;
    }

    try {
      QueryResultIterator<User> existingUsersIterator = ofy().load().type(User.class).filter("email = ", email).iterator();
      if (existingUsersIterator.hasNext()) {
        User user = existingUsersIterator.next();
        if (BCrypt.checkpw(password, user.hashedPassword)) {

//          loginResponse.success = true;
          String clientUserId = Auth.getClientUserId(user.getId());
          loginResponse.user = new ApiUser();
          loginResponse.user.userId = clientUserId;
          loginResponse.user.email = user.email;
          loginResponse.token = Auth.generateToken(clientUserId);
          sendResponse(loginResponse, resp.getWriter());

        } else {
          loginResponse.error = "Wrong password";
          sendResponse(loginResponse, resp.getWriter());
        }
      } else {
        loginResponse.error = "No user found with that email.";
        sendResponse(loginResponse, resp.getWriter());
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
  public ApiUser user;
  public String token;
}