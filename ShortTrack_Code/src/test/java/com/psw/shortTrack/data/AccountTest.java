package com.psw.shortTrack.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AccountTest {

	@Test
	void Given_ThatNameIsValid_WhenCheckValidName_ReturnNull() {
		assertNull(Account.checkValidName("Manuel"));
		assertNull(Account.checkValidName("João"));
		assertNull(Account.checkValidName("Mike"));
		assertNull(Account.checkValidName("Jo"));
		assertNull(Account.checkValidName("JoJo"));
		assertNull(Account.checkValidName("Ai"));
	}
	
	@Test
	void Given_ThatNameHasSpecialCharacters_WhenCheckValidName_ReturnNull() {
		assertNull(Account.checkValidName("Doe-Smith"));
		assertNull(Account.checkValidName("d'Arras"));
	}
	
	@Test
	void Given_ThatNameIsMandarin_WhenCheckValidName_ReturnNull() {
		assertNull(Account.checkValidName("手动的"));
	}
	
	@Test
	void Given_ThatNameHasNumbers_WhenCheckValidName_ReturnError() {
		assertEquals("None of the name fields can contain numbers!", Account.checkValidName("2Mike"));
		assertEquals("None of the name fields can contain numbers!", Account.checkValidName("John24"));
		assertEquals("None of the name fields can contain numbers!", Account.checkValidName("Mi4ck23ae2l"));
	}
	
	@Test
	void Given_ThatNameIsEmpty_WhenCheckValidName_ReturnError() {
		assertEquals("None of the name fields can be empty!", Account.checkValidName(""));
	}
	
	@Test
	void Given_ThatNameHasWhiteSpaces_WhenCheckValidName_ReturnError() {
		assertEquals("None of the name fields can contain white spaces!", Account.checkValidName(" Joaquim"));
		assertEquals("None of the name fields can contain white spaces!", Account.checkValidName("Antó nio"));
		assertEquals("None of the name fields can contain white spaces!", Account.checkValidName("Manuel "));
	}
	
	@Test
	void Given_ThatNameExceedsMaximumLength_WhenCheckValidName_ReturnError() {
		assertEquals("Name length exceeds maximum character length allowed!", Account.checkValidName("Istodeveserumnomemuitogigantementegigante"));
	}
	
	@Test
	void Given_ThatEmailIsValid_WhenCheckValidEmail_ReturnNull() {
		assertNull(Account.checkValidEmail("example@email.com"));
	}
	
	@Test
	void Given_ThatEmailIsEmpty_WhenCheckValidEmail_ReturnError() {
		assertEquals("Invalid email!", Account.checkValidEmail(""));
	}
	
	@Test
	void Given_ThatEmailHasWhiteSpaces_WhenCheckValidEmail_ReturnError() {
		assertEquals("Invalid email!", Account.checkValidEmail("mail exemplo@gmail.com"));
	}
	
	@Test
	void Given_ThatEmailMissesOrganization_WhenCheckValidEmail_ReturnError() {
		assertEquals("Invalid email!", Account.checkValidEmail("joaquim24"));
	}
	
	@Test
	void Given_ThatEmailExceedsMaximumLength_WhenCheckValidEmail_ReturnError() {
		assertEquals("Email length exceeds maximum character length allowed!", Account.checkValidEmail("emailgiganteemailgiganteemailgiganteemailgiganteemailgiganteemailgiganteemailgiganteemailgiganteemailgiganteemailgiganteemailgiganteemailgiganteemailgiganteemailgiganteemailgiganteemailgiganteemailgiganteemailgiganteemailgiganteemailgiganteemailgigante@gmail.com"));
	}
	
	@Test
	void Given_ThatPasswordIsValid_WhenCheckValidPassword_ReturnNull() {
		assertNull(Account.checkValidPassword("pass123Strong"));
	}
	
	@Test
	void Given_ThatPasswordLengthIsLowerThanMinimum_WhenCheckValidPassword_ReturnError() {
		assertEquals("Password needs to be at least 8 characters length!", Account.checkValidPassword("eightch"));
	}
	
	@Test
	void Given_ThatPasswordsMatchAndAreValid_WhenCheckValidPassword_ReturnNull() {
		assertNull(Account.checkValidPassword("pass123Strong", "pass123Strong"));
	}
	
	@Test
	void Given_ThatPasswordsDontMatch_WhenCheckValidPassword_ReturnError() {
		assertEquals("The passwords don't match!", Account.checkValidPassword("pass123Strong", "pass321Strong"));
	}
	
	@Test
	void Given_ThatPasswordsAreInvalid_WhenCheckValidPassword_ReturnError() {
		assertEquals("Password needs to be at least 8 characters length!", Account.checkValidPassword("small", "small"));
	}
	
	@Test
	void TestEquals() {
		
		Account acc = new Account("mail", "nome");
		Account acc1 = new Account("mail", "nome");
		Account acc2 = new Account("mail", "nomeErrado");
		Account acc3 = new Account("mailErrado", "nome");
		Account acc4 = new Account("mail", "nome");
		
		assertTrue(acc.equals(acc));
		assertTrue(acc.equals(acc1));
		assertTrue(acc1.equals(acc));
		assertTrue(acc1.equals(acc4));
		assertFalse(acc.equals(acc2));
		assertFalse(acc2.equals(acc3));
		
	}
	
}
