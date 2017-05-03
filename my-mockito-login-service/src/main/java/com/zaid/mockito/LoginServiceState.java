package com.zaid.mockito;

public abstract class LoginServiceState {
  
  public final void login(LoginServiceContext context, IAccount account, String password) {
    if (account.passwordMatches(password)) {
      if (account.isLoggedIn())
        throw new AccountLoginLimitReachedException();
      if (account.isRevoked())
        throw new AccountRevokedException();
      account.setLoggedIn(true);
      context.setState(new AwaitingFirstLoginAttempt());
    } else {
      handleIncorrectPassword(context, account, password);
    }
  }

  public abstract void handleIncorrectPassword(LoginServiceContext context, IAccount account,
      String password);
}
