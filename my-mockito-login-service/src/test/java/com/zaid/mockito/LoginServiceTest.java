package com.zaid.mockito;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class LoginServiceTest {

  private IAccount account;
  private IAccountRepository accountRepository;
  private LoginService service;

  @Before
  public void init() {
    account = mock(IAccount.class);
    accountRepository = mock(IAccountRepository.class);
    when(accountRepository.find(anyString())).thenReturn(account);
    when(account.getId()).thenReturn("zaid");
    service = new LoginService(accountRepository);
  }

  // Test 1:Basic Happy Path
  @Test
  public void itShouldSetAccountToLoggedInWhenPasswordMatches() {
    willPasswordMatch(true);
    service.login("zaid", "password");
    verify(account, times(1)).setLoggedIn(true);
  }

  // Test 2:Three Failed Logins Causes Account to be Revoked
  @Test
  public void itShouldSetAccountToRevokedAfterThreeFailedLoginAttempts() {
    willPasswordMatch(false);

    for (int i = 0; i < 3; ++i)
      service.login("zaid", "password");

    verify(account, times(1)).setRevoked(true);
  }

  // Test 3:setLoggedIn not called if password does not match
  @Test
  public void itShouldNotSetAccountLoggedInIfPasswordDoesNotMatch() {
    willPasswordMatch(false);
    service.login("zaid", "password");
    verify(account, never()).setLoggedIn(true);
  }

  // Test 4: Two Fails on One Account Followed By Fail on Second Account
  @Test
  public void itShouldNotRevokeSecondAccountAfterTwoFailedAttemptsFirstAccount() {
    willPasswordMatch(false);
    IAccount secondAccount = mock(IAccount.class);
    when(secondAccount.passwordMatches(anyString())).thenReturn(false);
    when(accountRepository.find("schuchert")).thenReturn(secondAccount);

    service.login("zaid", "password");
    service.login("zaid", "password");
    service.login("schuchert", "password");
    // Verify that the secondAccount is not revoked.
    verify(secondAccount, never()).setRevoked(true);
  }


  // Test 5: Do not allow a second login
  @Test(expected = AccountLoginLimitReachedException.class)
  public void itShouldNowAllowConcurrentLogins() {
    willPasswordMatch(true);
    when(account.isLoggedIn()).thenReturn(true);
    service.login("zaid", "password");
  }

  // Test 6: AccountNotFoundException thrown if account is not found
  @Test(expected = AccountNotFoundException.class)
  public void itShouldThrowExceptionIfAccountNotFound() {
    when(accountRepository.find("schuchert")).thenReturn(null);
    service.login("schuchert", "password");
  }

  // Test 7: Cannot login to revoked account
  @Test(expected = AccountRevokedException.class)
  public void itShouldNotBePossibleToLoginIntoRevokedAccount() {
    willPasswordMatch(true);
    when(account.isRevoked()).thenReturn(true);
    service.login("zaid", "password");
  }

  @Test
  public void itShouldResetBackToInitialStateAfterSuccessfulLogin() {
     willPasswordMatch(false);
     service.login("brett", "password");
     service.login("brett", "password");
     willPasswordMatch(true);
     service.login("brett", "password");
     willPasswordMatch(false);
     service.login("brett", "password");
     verify(account, never()).setRevoked(true);
  }
 

  private void willPasswordMatch(boolean value) {
    when(account.passwordMatches(anyString())).thenReturn(value);
  }
}
