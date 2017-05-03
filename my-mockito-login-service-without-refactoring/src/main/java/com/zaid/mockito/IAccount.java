package com.zaid.mockito;

public interface IAccount {

  boolean passwordMatches(String anyString);

  void setLoggedIn(boolean b);

  void setRevoked(boolean b);

  boolean isLoggedIn();

  boolean isRevoked();

}
