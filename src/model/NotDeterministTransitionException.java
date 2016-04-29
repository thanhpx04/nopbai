package model;

public class NotDeterministTransitionException extends Exception {
	private static final long serialVersionUID = 1L;
	private TransitionInterface<?> t1, t2;

	public NotDeterministTransitionException(TransitionInterface<?> t1, TransitionInterface<?> t2) {
		this.t1 = t1;
		this.t2 = t2;
	}

	public String getMessage() {
		return "There are duplicated transitions '" + t1.label() + "' from " + t1.source().getLabel() + " to "
				+ t1.target().getLabel() + " and from " + t2.source().getLabel() + " to " + t2.target().getLabel();
	}
}
