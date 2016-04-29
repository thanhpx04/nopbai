package model;

public interface TransitionInterface<T> {
	public State source();

	public State target();

	public T label();
	
	public void setLabel(T label);
}
