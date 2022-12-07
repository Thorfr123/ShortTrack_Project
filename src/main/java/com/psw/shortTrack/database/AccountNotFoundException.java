package com.psw.shortTrack.database;

import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

@SuppressWarnings("serial")
public class AccountNotFoundException extends PSQLException {

	public AccountNotFoundException() {
		super("Account was deleted", PSQLState.FOREIGN_KEY_VIOLATION);
	}
	
	public AccountNotFoundException(String msg) {
		super(msg, PSQLState.FOREIGN_KEY_VIOLATION);
	}

	public AccountNotFoundException(String msg, Throwable cause) {
		super(msg, PSQLState.FOREIGN_KEY_VIOLATION, cause);
	}
	
	public AccountNotFoundException(Throwable cause) {
		super("Account was deleted", PSQLState.FOREIGN_KEY_VIOLATION, cause);
	}

}
