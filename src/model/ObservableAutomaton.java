package model;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ObservableAutomaton<T> extends DeterministicAutomaton<T> {

	public ObservableAutomaton(List<Transition<T>> transitions) throws NotDeterministTransitionException,
			UnknownInitialStateException, NotDeterministInitialStateException, NullTransitionsException {
		super(transitions);
	}

	private Observable observable = new Observable() {
		@Override
		public void notifyObservers(Object object) {
			setChanged();
			super.notifyObservers(object);
		}
	};

	@Override
	protected State changeCurrentState(TransitionInterface<T> t) {
		observable.notifyObservers(t);
		return super.changeCurrentState(t);
	}

	public void addObserver(Observer o) {
		observable.addObserver(o);
	}
}
