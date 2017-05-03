package com.zaid.mockito;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class LoginServiceTest {
  
  private IAccount account;
  private IAccountRepository accountRepository;
  private LoginService service;
  
  @Before
  public void init() {
     account = mock(IAccount.class);
     accountRepository = mock(IAccountRepository.class);
     when(accountRepository.find(anyString())).thenReturn(account);
     service = new LoginService(accountRepository);
  }

  private void willPasswordMatch(boolean value) {
     when(account.passwordMatches(anyString())).thenReturn(value);
  }

  @Test
  public void itShouldSetAccountToLoggedInWhenPasswordMatches() {    
    willPasswordMatch(true);
    service.login("zaid", "password");
    verify(account, times(1)).setLoggedIn(true);
  }

  @Test
  public void itShouldSetAccountToRevokedAfterThreeFailedLoginAttempts() {
    willPasswordMatch(false);
    for (int i = 0; i < 3; ++i)
      service.login("zaid", "password");

    verify(account, times(1)).setRevoked(true);
  }
  
  @Test
  public void itShouldNotSetAccountLoggedInIfPasswordDoesNotMatch() {
     willPasswordMatch(false);
     service.login("zaid", "password");
     verify(account, never()).setLoggedIn(true);
  }
  
  @Test
  public void itShouldNotRevokeSecondAccountAfterTwoFailedAttemptsFirstAccount() {
     willPasswordMatch(false);

     IAccount secondAccount = mock(IAccount.class);
     when(secondAccount.passwordMatches(anyString())).thenReturn(false);
     when(accountRepository.find("schuchert")).thenReturn(secondAccount);

     service.login("zaid", "password");
     service.login("zaid", "password");
     service.login("schuchert", "password");

     verify(secondAccount, never()).setRevoked(true);
  }
  
  @Test(expected = AccountLoginLimitReachedException.class)
  public void itShouldNowAllowConcurrentLogins() {
     willPasswordMatch(true);
     when(account.isLoggedIn()).thenReturn(true);
     service.login("zaid", "password");
  }
  
  @Test(expected = AccountNotFoundException.class)
  public void ItShouldThrowExceptionIfAccountNotFound() {
     when(accountRepository.find("schuchert")).thenReturn(null);
     service.login("schuchert", "password");
  }
  
  @Test(expected = AccountRevokedException.class)
  public void ItShouldNotBePossibleToLogIntoRevokedAccount() {
     willPasswordMatch(true);
     when(account.isRevoked()).thenReturn(true);
     service.login("zaid", "password");
  }
}
