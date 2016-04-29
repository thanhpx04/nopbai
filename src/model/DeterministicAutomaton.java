package model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class DeterministicAutomaton<T> {
	private State initialState = null;
	private final Map<State, Map<T, TransitionInterface<T>>> transitions;

	public DeterministicAutomaton(List<Transition<T>> transitions) throws NotDeterministTransitionException,
			UnknownInitialStateException, NotDeterministInitialStateException, NullTransitionsException {
		this.transitions = new HashMap<State, Map<T, TransitionInterface<T>>>();
		for (TransitionInterface<T> t : transitions) {
			addState(t.source());
			addState(t.target());

			Map<T, TransitionInterface<T>> map = this.transitions.get(t.source());
			if (map.containsKey(t.label()))
				throw new NotDeterministTransitionException(t, map.get(t.label()));
			else
				map.put(t.label(), t);
		}
		if (transitions.size() == 0)
			throw new NullTransitionsException();
		else if (initialState == null)
			throw new UnknownInitialStateException();
	}

	protected final void addState(State s) throws NotDeterministInitialStateException {
		if (!transitions.containsKey(s)) {
			transitions.put(s, new HashMap<T, TransitionInterface<T>>());
			if (s.initial()) {
				if (initialState == null)
					initialState = s;
				else
					throw new NotDeterministInitialStateException(s, initialState);
			}
		}
	}

	public State initialState() {
		return initialState;
	}

	public TransitionInterface<T> transition(State s, Object label) {
		if (!transitions.containsKey(s))
			throw new NoSuchElementException();
		return transitions.get(s).get(label);
	}

	public boolean recognize(Object[] word) {
		return recognize(Arrays.asList(word).iterator());
	}

	public boolean recognize(Iterator<Object> word) {
		State s = initialState;
		while (word.hasNext()) {
			TransitionInterface<T> t = transition(s, word.next());
			if (t == null)
				return false;
			else
				s = changeCurrentState(t);
		}
		return s.terminal();
	}

	protected State changeCurrentState(TransitionInterface<T> t) {
		return t.target();
	}
}
