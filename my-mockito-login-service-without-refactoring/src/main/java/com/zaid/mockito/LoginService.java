package com.zaid.mockito;

public class LoginService {
  private String previousAccountId = "";
  private final IAccountRepository accountRepository;
  private int failedAttempts = 0;

  public LoginService(IAccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public void login(String accountId, String password) {
    IAccount account = accountRepository.find(accountId);
    if(account == null)
      throw new AccountNotFoundException();
    if (account.passwordMatches(password)) {
      if (account.isLoggedIn())
        throw new AccountLoginLimitReachedException();
      if(account.isRevoked())
        throw new AccountRevokedException();
      account.setLoggedIn(true);
    } else {
      if (previousAccountId.equals(accountId))
        ++failedAttempts;
      else {
        failedAttempts = 1;
        previousAccountId = accountId;
      }
    }
    if (failedAttempts == 3)
      account.setRevoked(true);
  }
}
