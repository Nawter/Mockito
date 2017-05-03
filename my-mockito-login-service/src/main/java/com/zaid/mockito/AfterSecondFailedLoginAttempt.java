package com.zaid.mockito;

public class AfterSecondFailedLoginAttempt  extends LoginServiceState{
  private String previousAccountId;
 
  public AfterSecondFailedLoginAttempt(String previousAccountId) {
    this.previousAccountId = previousAccountId;
  }
 
 

  @Override
  public void handleIncorrectPassword(LoginServiceContext context, IAccount account, String password) {
    if (previousAccountId.equals(account.getId())) {
      account.setRevoked(true);
      context.setState(new AwaitingFirstLoginAttempt());
   } else {
      context.setState(new AfterFirstFailedLoginAttempt(account.getId()));
   }    
  }
}
