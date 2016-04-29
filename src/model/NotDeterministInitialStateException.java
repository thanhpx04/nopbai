package model;

public class NotDeterministInitialStateException extends Exception {
	private static final long serialVersionUID = 1L;
	private State s1, s2;

	public NotDeterministInitialStateException(State s1, State s2) {
		this.s1 = s1;
		this.s2 = s2;
	}

	public String getMessage() {
		return "There are 2 initial states: " + s1.getLabel() + " and " + s2.getLabel();
	}
}
