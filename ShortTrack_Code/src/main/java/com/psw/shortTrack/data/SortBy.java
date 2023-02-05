package com.psw.shortTrack.data;

public enum SortBy {
	Name("Name"),
	CreatedDate("Created Date"),
	DeadlineDate("Deadline"),
	Completed("Completed");
	
	private String text;
	SortBy(String text) {
		this.text = text;
	}
	
    public static SortBy fromString(String text) {
        for (SortBy b : SortBy.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("There is no sort by " + text + "option!");
    }
	
	public static String[] options() {
		SortBy[] opts = values();
		String options[] = new String[opts.length];
		
		for (int i = 0; i < opts.length; i++) {
			options[i] = opts[i].text;
		}
		return options;
	}
}
