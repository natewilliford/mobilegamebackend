package com.natewilliford.mobilebackend.server;

import com.google.inject.Singleton;
import com.natewilliford.mobilebackend.constants.Buildings;
import com.natewilliford.mobilebackend.server.api.*;
import com.natewilliford.mobilebackend.storage.entities.Building;
import com.natewilliford.mobilebackend.storage.entities.User;

import java.util.Date;

import static com.natewilliford.mobilebackend.ofy.OfyService.ofy;

@Singleton
@RequestType(BuyRequest.class)
@ResponseType(BuyResponse.class)
public class BuyServlet extends ZeusServlet{

  @Override
  protected void doAuthenticatedPost(GenericRequest request, User user) {
    BuyRequest buyRequest = (BuyRequest)request;
    if (buyRequest.buildingType == Buildings.TYPE_FARM) {
      if (user.inventory.gold >= Buildings.PRICE_FARM) {
        Building building = new Building();
        building.buildingType = Buildings.TYPE_FARM;
        building.lastCollected = new Date();
        user.inventory.gold -= Buildings.PRICE_FARM;
        user.inventory.buildings.add(building);
        ofy().save().entity(user).now();

        BuyResponse buyResponse = new BuyResponse();
        buyResponse.building = building;
        writeResponse(buyResponse);
      } else {
        writeErrorResponse("Insufficient funds.");
      }
    } else {
      writeErrorResponse("Invalid building type.");
    }
  }
}

class BuyRequest extends AuthorizedRequest {
  public int buildingType;
}

class BuyResponse extends GenericResponse {
  public Building building;
}