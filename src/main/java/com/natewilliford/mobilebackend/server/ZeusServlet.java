package com.natewilliford.mobilebackend.server;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.natewilliford.mobilebackend.server.api.*;
import com.natewilliford.mobilebackend.storage.entities.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.natewilliford.mobilebackend.ofy.OfyService.ofy;

public abstract class ZeusServlet extends HttpServlet {

  private ObjectMapper mapper;
  private HttpServletRequest servletRequest;
  private HttpServletResponse servletResponse;

  public ZeusServlet() {
    RequestType requestTypeAnnotation = getClass().getAnnotation(RequestType.class);
    if (requestTypeAnnotation == null) {
      throw new RuntimeException("Required RequestType annotation missing.");
    }
    Class requestTypeClass = requestTypeAnnotation.value();
    if (!GenericRequest.class.isAssignableFrom(requestTypeClass)) {
      throw new RuntimeException("RequestType annotation must extend GenericRequest.");
    }

    ResponseType responseTypeAnnotation = getClass().getAnnotation(ResponseType.class);
    if (responseTypeAnnotation == null) {
      throw new RuntimeException("Required ResponseType annotation missing.");
    }
    Class responseTypeClass = responseTypeAnnotation.value();
    if (!GenericResponse.class.isAssignableFrom(responseTypeClass)) {
      throw new RuntimeException("RequestType annotation must extend GenericResponse.");
    }

    this.mapper = new ObjectMapper();
    // TODO: Enable for prod?
     this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }

  protected void doAuthenticatedPost(GenericRequest request, User user) {
    throw new RuntimeException("Calling unimplemented method: doAuthenticatedPost");
  }

  protected void doUnAuthenticatedPost(GenericRequest request) {
    throw new RuntimeException("Calling unimplemented method: doUnAuthenticatedPost");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    this.servletRequest = req;
    this.servletResponse = resp;

    String jsonRequest = req.getParameter("json");

//    System.out.println("jsonRequest: " + jsonRequest);

    RequestType requestTypeAnnotation = this.getClass().getAnnotation(RequestType.class);
    Class requestTypeClass = requestTypeAnnotation.value();

    if (AuthorizedRequest.class.isAssignableFrom(requestTypeClass)) {
      AuthorizedRequest request = (AuthorizedRequest)mapper.readValue(jsonRequest, requestTypeClass);
      DecodedJWT decodedToken = Auth.getDecodedTokenFromToken(request.token);
      if (decodedToken == null) {
        writeErrorResponse("Missing auth token for authenticated request.");
        return;
      }

      Long serverClientId = Auth.getServerUserId(decodedToken.getSubject());
      QueryResultIterator<User> existingUsersIterator = ofy().load().type(User.class).filterKey(Key.create(null, User.class, serverClientId)).iterator();
      if (existingUsersIterator.hasNext()) {
        User user = existingUsersIterator.next();
        doAuthenticatedPost(request, user);
      } else {
        writeErrorResponse("User not found.");
      }
    } else {
      GenericRequest request = (GenericRequest)getMapper().readValue(jsonRequest, requestTypeClass);
      doUnAuthenticatedPost(request);
    }
  }

  protected void writeErrorResponse(String message) {
    GenericResponse resp = new GenericResponse();
    resp.error = message;
    writeResponse(resp);
  }

  protected void writeResponse(GenericResponse resp) {
    try {
      servletResponse.getWriter().println(getMapper().writeValueAsString(resp));
    } catch (JsonProcessingException e) {
      System.err.println("Exception processing JSON: " + e.getMessage());
    } catch (IOException e) {
      System.err.println("IO Exception while writing message: " + e.getMessage());
    }
  }

  protected ObjectMapper getMapper() {
    return this.mapper;
  }
}
