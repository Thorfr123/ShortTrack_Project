package com.psw.shortTrack.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

public class DatabaseTest {

	@Test
	void Given_UserHasValidCredentials_When_ClickLoginButton_Then_SuccessfullLogin() {
		try {
			assertEquals(true, AccountsDatabase.checkLogin("teste", "12345"));
			
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
	@Test
	void Given_UserHasWrongCredentials_When_ClickLoginButton_Then_FailedLogin() {
		try {
			assertEquals(false, AccountsDatabase.checkLogin("wrongUser", "wrongPass"));
			
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
}
