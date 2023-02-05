package com.psw.shortTrack.database;

import java.sql.SQLException;

@SuppressWarnings("serial")
public class NotFoundException extends SQLException {

	public NotFoundException() {
	}

	public NotFoundException(String reason) {
		super(reason);
	}

}
