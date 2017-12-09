package com.natewilliford.mobilebackend.server.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Auth {

  private static Algorithm getAlgorithm () throws UnsupportedEncodingException {
    return Algorithm.HMAC256(Config.SUPER_SECRET);
  }

  public static String generateToken(String clientUserId) {
    String token;
    Date now = new Date();
    Calendar cal = new GregorianCalendar();
    cal.setTime(now);
    cal.add(Calendar.DATE, 14);
//    cal.add(Calendar.SECOND, 30);
    Date exp = cal.getTime();
    try {
      Algorithm alg = getAlgorithm();
      token = JWT.create()
          .withIssuer(Config.JWT_ISSUER)
          .withSubject(clientUserId)
          .withIssuedAt(now)
          .withExpiresAt(exp)
          .sign(alg);
    } catch (UnsupportedEncodingException e) {
      System.err.println("Unsupported encoding when generating JWT algorithm.");
      return null;
    } catch (JWTCreationException e){
      System.err.println("Could not create JWT.");
      return null;
    }
    return token;
  }

  public static DecodedJWT getDecodedTokenFromToken(String token) {
    try {
      Algorithm alg = getAlgorithm();
      JWTVerifier verifier = JWT.require(alg).withIssuer(Config.JWT_ISSUER).build();
      return verifier.verify(token);
    } catch (UnsupportedEncodingException e) {
      System.err.println("Unsupported encoding when generating JWT algorithm.");
      return null;
    } catch (JWTVerificationException e) {
      return null;
    }
  }

  public static Long getServerUserId(String clientUserId) {
    byte[] decoded = DatatypeConverter.parseBase64Binary(clientUserId);
    return ByteBuffer.wrap(decoded).getLong();
  }

  public static String getClientUserId(Long serverUserId) {
    ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / Byte.SIZE);
    buffer.putLong(serverUserId);

    byte[] bytes = buffer.array();
    return DatatypeConverter.printBase64Binary(bytes);
  }
}
