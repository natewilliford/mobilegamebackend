package com.natewilliford.mobilebackend.server;

import com.google.inject.Singleton;
import com.natewilliford.mobilebackend.server.api.*;
import com.natewilliford.mobilebackend.storage.entities.User;

@Singleton
@RequestType(SyncRequest.class)
@ResponseType(SyncResponse.class)
public class SyncServlet extends ZeusServlet {

  @Override
  protected void doAuthenticatedPost(GenericRequest request, User user) {
    SyncResponse response = new SyncResponse();
    response.gameState = new GameState();
    response.gameState.inventory = user.inventory;
    writeResponse(response);
  }
}

class SyncRequest extends AuthorizedRequest {}

class SyncResponse extends GenericResponse {
  public GameState gameState;
}
