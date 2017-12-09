package com.natewilliford.mobilebackend.server.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

public class AuthTests {

  @Test
  public void testGenerateToken() throws UnsupportedEncodingException {
    String clientUserId = "someClientUserId";
    String token = Auth.generateToken(clientUserId);
    assertNotNull(token);

    Algorithm alg = Algorithm.HMAC256(Config.SUPER_SECRET);
    JWTVerifier verifier = JWT.require(alg).withIssuer(Config.JWT_ISSUER).build();
    DecodedJWT decodedJWT = verifier.verify(token);
    assertEquals(decodedJWT.getSubject(), clientUserId);
  }

  @Test
  public void testGetDecodedTokenFromToken() {
    String token = Auth.generateToken("someClientUserId");

    assertNotNull(Auth.getDecodedTokenFromToken(token));
  }

  @Test
  public void testGetDecodedTokenFromTokenWrongKey () throws UnsupportedEncodingException {
    String clientUserId = "someClientUserId";
    Algorithm alg = Algorithm.HMAC256("wrooooong!!!");
    String token = JWT.create().withIssuer(Config.JWT_ISSUER).withSubject(clientUserId).sign(alg);

    assertNull(Auth.getDecodedTokenFromToken(token));
  }

  @Test
  public void testGetServerUserId() {
    assertEquals("AFIMrviJwdo=", Auth.getClientUserId(23094893723894234L));
  }

  @Test
  public void testGetClientUserId() {
    assertEquals((Long) 23094893723894234L, Auth.getServerUserId("AFIMrviJwdo="));
  }
}
