package com.natewilliford.mobilebackend.server;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.inject.Singleton;
import com.googlecode.objectify.Key;
import com.natewilliford.mobilebackend.server.api.Auth;
import com.natewilliford.mobilebackend.server.api.AuthorizedRequest;
import com.natewilliford.mobilebackend.server.api.GenericResponse;
import com.natewilliford.mobilebackend.storage.entities.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.natewilliford.mobilebackend.ofy.OfyService.ofy;

@Singleton
public class SyncServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String jsonRequest = req.getParameter("json");
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    SyncRequest syncRequest = mapper.readValue(jsonRequest, SyncRequest.class);

    DecodedJWT decodedToken = Auth.getDecodedTokenFromToken(syncRequest.token);
    if (decodedToken == null) {
//      resp.getWriter().write();
      resp.sendError(401);
      return;
    }

    Long serverClientId = Auth.getServerUserId(decodedToken.getSubject());
    SyncResponse syncResponse = new SyncResponse();

    try {

      QueryResultIterator<User> existingUsersIterator = ofy().load().type(User.class).filterKey(Key.create(null, User.class, serverClientId)).iterator();
      if (existingUsersIterator.hasNext()) {
//        User user = existingUsersIterator.next();
        syncResponse.foo = "bar";
        resp.getWriter().println(mapper.writeValueAsString(syncResponse));
      } else {
        syncResponse.error = "User not found.";
        resp.getWriter().println(mapper.writeValueAsString(syncResponse));
      }
    } catch (RuntimeException e) {
      syncResponse.error = "Error loading user.";
      System.err.println("Error loading user: " + e.getMessage());
      resp.getWriter().println(mapper.writeValueAsString(syncResponse));
    }
  }
}

class SyncRequest extends AuthorizedRequest {

}

class SyncResponse extends GenericResponse {
  public String foo;
}
