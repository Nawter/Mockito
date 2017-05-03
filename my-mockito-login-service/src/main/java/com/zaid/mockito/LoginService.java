package com.zaid.mockito;

public class LoginService extends LoginServiceContext{  
  private final IAccountRepository accountRepository;  

  public LoginService(IAccountRepository accountRepository) {
    super(new AwaitingFirstLoginAttempt());
    this.accountRepository = accountRepository;
  }

  public void login(String accountId, String password) {
    IAccount account = accountRepository.find(accountId);
    
    if(account == null)
      throw new AccountNotFoundException();   
    
    getState().login(this, account, password);
  }
  
 
  
}
