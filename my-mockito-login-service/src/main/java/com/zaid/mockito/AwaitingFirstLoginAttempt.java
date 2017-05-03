package com.zaid.mockito;

public class AwaitingFirstLoginAttempt extends LoginServiceState {

  @Override
  public void handleIncorrectPassword(LoginServiceContext context, IAccount account, String password) {
    context.setState(new AfterFirstFailedLoginAttempt(account.getId()));
    
  }
 

  }

