package model;

public class NullTransitionsException extends Exception {
	private static final long serialVersionUID = 1L;

	public String getMessage() {
		return "There is no transition";
	}
}
