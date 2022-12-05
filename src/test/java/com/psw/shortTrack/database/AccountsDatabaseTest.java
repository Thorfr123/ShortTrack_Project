package com.psw.shortTrack.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import com.psw.shortTrack.data.Account;

public class AccountsDatabaseTest {

	@Test
	void Given_ThatUserHasValidCredentials_When_ClickLoginButton_Then_SuccessfullLogin() {
		try {
			if (AccountsDatabase.getAccount("teste@gmail.com") == null) {
				fail("Test account was deleted");
			}
			assertTrue(AccountsDatabase.checkLogin("teste@gmail.com", "12345"));
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
	@Test
	void Given_ThatUserHasWrongCredentials_When_ClickLoginButton_Then_FailedLogin() {
		try {
			if (AccountsDatabase.getAccount("fakeMail@mail.com") != null) {
				fail("Fake account has been created");
			}
			assertFalse(AccountsDatabase.checkLogin("fakeMail@mail.com", "wrongPass"));
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
	void Given_ThatUserHasValidEmailAndWrongPassword_When_ClickLoginButton_Then_FailedLogin() {
		try {
			assertFalse(AccountsDatabase.checkLogin("teste@gmail.com", "wrongPassword"));
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
	@Test
	void Given_ThatUserHasValidPasswordAndWrongEmail_When_ClickLoginButton_Then_FailedLogin() {
		try {
			assertFalse(AccountsDatabase.checkLogin("fakeMail@gmail.com", "12345"));
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
	@Test
	void Given_ThatEmailIsNotInUse_When_CheckEmail_Then_ReturnTrue() {
		try {
			if (AccountsDatabase.getAccount("notInUse@email.com") != null) {
				fail("Someone created an account with this email");
			}
			assertTrue(AccountsDatabase.checkEmail("notInUse@email.com"));
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
	@Test
	void Given_ThatEmailIsInUse_When_CheckEmail_Then_ReturnFalse() {
		try {
			if (AccountsDatabase.getAccount("teste@gmail.com") == null) {
				fail("Teste account was deleted");
			}
			assertFalse(AccountsDatabase.checkEmail("teste@gmail.com"));
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
	@Test
	void Given_ThatUserCreatesNewValidAccount_When_ClickCreateAccount_Then_ReturnTrue() {
		Account acc = new Account("notInUse@email.com", "Full Name");
		try {
			assertTrue(AccountsDatabase.createAccount(acc, "validPass"));
			
			AccountsDatabase.deleteAccount("notInUse@email.com");
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
	@Test
	void Given_ThatUserCreatesNewInvalidAccount_When_CreateAccount_Then_ReturnFalse() {
		try {
			Account acc = AccountsDatabase.getAccount("teste@gmail.com");
			if (acc == null)
				fail("Test account was deleted");
			assertFalse(AccountsDatabase.createAccount(acc, "validPass"));
		} catch (SQLException e) {
			System.out.println("ERROR");
			fail("Network problems");
		}
	}
	
	@Test
	void Given_ThatUserDeletesValidAccount_When_DeleteAccount_Then_ReturnTrue() {
		Account acc = new Account("notInUse@email.com", "Full Name");
		try {
			if (AccountsDatabase.createAccount(acc, "validPass") == false)
				fail("Not in use account already in use");
			assertTrue(AccountsDatabase.deleteAccount(acc.getEmail()));
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
	@Test
	void Given_ThatUserDeletesInValidAccount_When_DeleteAccount_Then_ReturnTrue() {
		Account acc = new Account("notInUse@email.com", "Full Name");
		try {
			if (AccountsDatabase.getAccount(acc.getEmail()) != null)
				fail("Not in use account already in use");
			assertFalse(AccountsDatabase.deleteAccount(acc.getEmail()));
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
	@Test
	void Given_ThatAccountExists_When_GetAccount_ReturnAccount() {
		try {
			assertNotNull(AccountsDatabase.getAccount("teste@gmail.com"));
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
	@Test
	void Given_ThatAccountNotExists_When_GetAccount_ReturnNull() {
		try {
			assertNull(AccountsDatabase.getAccount("notInUse@email.com"));
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
	@Test
	void Given_ThatParamsAreValid_When_ChangePassword_ReturnTrue() {
		try {
			assertTrue(AccountsDatabase.changePassword("ggg@gmail.com", "12345", "123456"));
			AccountsDatabase.changePassword("ggg@gmail.com", "123456", "12345");
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
	@Test
	void Given_ThatParamsAreInValid_When_ChangePassword_ReturnFalse() {
		try {
			assertFalse(AccountsDatabase.changePassword("ggg@gmail.com", "123456", "1234567"));
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
	@Test
	void Given_ThatParamsAreValid_When_ChangeName_ReturnTrue() {
		try {
			assertTrue(AccountsDatabase.changeName("ggg@gmail.com", "New Name"));
			AccountsDatabase.changeName("ggg@gmail.com", "Conta Teste");
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
	@Test
	void Given_ThatParamsAreInValid_When_ChangeName_ReturnTrue() {
		try {
			assertFalse(AccountsDatabase.changeName("notInUse@gmail.com", "Not Valid"));
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
	@Test
	void Given_ThatUserChangesToValidEmail_When_ChangeEmail_ReturnTrue() {
		try {
			// Return da funcao
			assertTrue(AccountsDatabase.changeEmail("ggg@gmail.com", "gggAlterado@gmail.com"));
			// Email alterado na tabela de accounts
			assertNull(AccountsDatabase.getAccount("ggg@gmail.com"));
			assertNotNull(AccountsDatabase.getAccount("gggAlterado@gmail.com"));
			// Email alterado nos members
			assertEquals(0 , GroupsDatabase.getAllGroups("ggg@gmail.com").size());
			assertNotEquals(0 , GroupsDatabase.getAllGroups("gggAlterado@gmail.com").size());
			// Return to original
			AccountsDatabase.changeEmail("gggAlterado@gmail.com", "ggg@gmail.com");
		} catch (SQLException e) {
			fail("Network problems");
		}
	}
	
	@Test
	void Given_ThatUserChangesToInvalidEmail_When_ChangeEmail_ReturnFalse() {
		try {
			// Return da funcao
			assertFalse(AccountsDatabase.changeEmail("notInUse@gmail.com", "teste@gmail.com"));
			// Email alterado na tabela de accounts
			assertNotNull(AccountsDatabase.getAccount("ggg@gmail.com"));
			// Email alterado nos members
			assertNotEquals(0 , GroupsDatabase.getAllGroups("ggg@gmail.com").size());
			assertNotEquals(0 , GroupsDatabase.getAllGroups("teste@gmail.com").size());
		} catch (SQLException e) {
			System.out.println(e.getSQLState());
			fail("Network problems");
		}
	}
}
