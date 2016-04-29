package model;

public class UnknownInitialStateException extends Exception {
	private static final long serialVersionUID = 1L;

	public String getMessage() {
		return "There is no initial state";
	}
}
