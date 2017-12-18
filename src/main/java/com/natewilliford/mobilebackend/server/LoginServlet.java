package com.natewilliford.mobilebackend.server;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.common.base.Strings;
import com.google.inject.Singleton;
import com.natewilliford.mobilebackend.server.api.*;
import com.natewilliford.mobilebackend.storage.entities.User;
import org.mindrot.jbcrypt.BCrypt;

import static com.natewilliford.mobilebackend.ofy.OfyService.ofy;

@Singleton
@RequestType(LoginRequest.class)
@ResponseType(LoginResponse.class)
public class LoginServlet extends ZeusServlet {

  @Override
  protected void doUnAuthenticatedPost(GenericRequest request) {
    LoginRequest loginRequest = (LoginRequest)request;
    String email = loginRequest.email.replaceAll("\\s+", "").toLowerCase();
    String password = loginRequest.password.replaceAll("\\s+", "");

    if (Strings.isNullOrEmpty(email) || Strings.isNullOrEmpty(password)) {
      writeErrorResponse("Email and password required.");
      return;
    }

    QueryResultIterator<User> existingUsersIterator = ofy().load().type(User.class).filter("email = ", email).iterator();
    if (existingUsersIterator.hasNext()) {
      User user = existingUsersIterator.next();

      if (BCrypt.checkpw(password, user.hashedPassword)) {
        String clientUserId = Auth.getClientUserId(user.getId());

        LoginResponse loginResponse = new LoginResponse();

        loginResponse.user = new ApiUser();
        loginResponse.user.userId = clientUserId;
        loginResponse.user.email = user.email;
        loginResponse.token = Auth.generateToken(clientUserId);
        writeResponse(loginResponse);

      } else {
        writeErrorResponse("Wrong password");
      }
    } else {
      writeErrorResponse("No user found with that email.");

    }
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