package com.natewilliford.mobilebackend.server;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.common.base.Strings;
import com.google.inject.Singleton;
import com.googlecode.objectify.ObjectifyService;
import com.natewilliford.mobilebackend.server.api.*;
import com.natewilliford.mobilebackend.storage.entities.User;
import org.mindrot.jbcrypt.BCrypt;

@Singleton
@RequestType(RegisterRequest.class)
@ResponseType(RegisterResponse.class)
public class RegisterServlet extends ZeusServlet {

  @Override
  protected void doUnAuthenticatedPost(GenericRequest request) {
    RegisterRequest registerRequest = (RegisterRequest)request;
    String email = registerRequest.email.replaceAll("\\s+", "").toLowerCase();
    String password = registerRequest.password.replaceAll("\\s+", "");

    if (Strings.isNullOrEmpty(email) || Strings.isNullOrEmpty(password)) {
      writeErrorResponse("Email and password required.");
      return;
    }

    QueryResultIterator<User> existingUsersIterator = ObjectifyService.ofy().load().type(User.class).filter("email = ", email).iterator();
    if (existingUsersIterator.hasNext()) {
      writeErrorResponse("User with that email already exists.");
      return;
    }

    try {
      User user = new User();
      user.email = email;
      user.hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

      // Initial user inventory.
      user.inventory.gold = 200L;

      // Create and save the user.
      ObjectifyService.ofy().save().entity(user).now();

      RegisterResponse registerResponse = new RegisterResponse();
      String clientUserId = Auth.getClientUserId(user.getId());
      ApiUser apiUser = new ApiUser();
      apiUser.userId = clientUserId;
      apiUser.email = user.email;
      registerResponse.user = apiUser;
      registerResponse.token = Auth.generateToken(clientUserId);

      writeResponse(registerResponse);
    } catch (RuntimeException e) {
      System.err.println("Error creating user: " + e.getMessage());
      writeErrorResponse("Couldn't save new user.");
    }
  }
}

class RegisterRequest extends GenericRequest {
  public String uniqueDeviceId;
  public String email;
  public String password;
}

class RegisterResponse extends GenericResponse {
  public ApiUser user;
  public String token;
}