package com.psw.shortTrack.database;

import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

@SuppressWarnings("serial")
public class AccountNotFound extends PSQLException {

	public AccountNotFound() {
		super("Account was deleted", PSQLState.FOREIGN_KEY_VIOLATION);
	}
	
	public AccountNotFound(String msg) {
		super(msg, PSQLState.FOREIGN_KEY_VIOLATION);
	}

	public AccountNotFound(String msg, Throwable cause) {
		super(msg, PSQLState.FOREIGN_KEY_VIOLATION, cause);
	}
	
	public AccountNotFound(Throwable cause) {
		super("Account was deleted", PSQLState.FOREIGN_KEY_VIOLATION, cause);
	}

}
