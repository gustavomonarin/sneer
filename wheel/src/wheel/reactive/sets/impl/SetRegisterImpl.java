//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Kalecser Kurtz, Fabio Roger Manera.

package wheel.reactive.sets.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import wheel.lang.Omnivore;
import wheel.reactive.Register;
import wheel.reactive.Signal;
import wheel.reactive.impl.AbstractNotifier;
import wheel.reactive.impl.RegisterImpl;
import wheel.reactive.sets.SetRegister;
import wheel.reactive.sets.SetSignal;
import wheel.reactive.sets.SetSignal.SetValueChange;


public class SetRegisterImpl<T> implements SetRegister<T> {


	private class MyOutput extends AbstractNotifier<SetValueChange<T>> implements SetSignal<T> {

		@Override
		public void addSetReceiver(Omnivore<SetValueChange<T>> receiver) {
			addReceiver(receiver);
		}

		@Override
		public void removeSetReceiver(Object receiver) {
			removeReceiver(receiver);
		}

		@Override
		public Set<T> currentElements() {
			synchronized (_contents) {
				return contentsCopy();
			}
		}

		@Override
		public int currentSize() {
			return size().currentValue();
		}

		@Override
		public Iterator<T> iterator() {
			return _contents.iterator();
		}

		@Override
		protected void initReceiver(Omnivore<SetValueChange<T>> receiver) {
			receiver.consume(new SetValueChangeImpl<T>(contentsCopy(), null));
		}

		@Override
		protected void notifyReceivers(SetValueChange<T> valueChange) {
			super.notifyReceivers(valueChange);
		}

		private Set<T> contentsCopy() {
			return new HashSet<T>(_contents);
		}

		@Override
		public Signal<Integer> size() {
			return _size.output();
		}
		
	}

	private final Set<T> _contents = new HashSet<T>();
	private final Register<Integer> _size = new RegisterImpl<Integer>(0);

	private final MyOutput _output = new MyOutput();

	
	public SetSignal<T> output() {
		return _output;
	}

	public void add(T elementAdded) {
		change(new SetValueChangeImpl<T>(elementAdded, null));
	}

	public void remove(T elementRemoved) {
		change(new SetValueChangeImpl<T>(null, elementRemoved));
	}

	
	public void change(SetValueChange<T> change) {
		synchronized (_contents) {
			assertValidChange(change);
			_contents.addAll(change.elementsAdded());
			_contents.removeAll(change.elementsRemoved());
			_output.notifyReceivers(change);
			
			updateSize();
		}
	}

	private void updateSize() {
		Integer size = _contents.size();
		if (size != _size.output().currentValue())
			_size.setter().consume(size);
	}

	private void assertValidChange(SetValueChange<T> change) {
		if (change.elementsAdded().removeAll(_contents))
			throw new IllegalArgumentException("SetSource already contained at least one element being added.");
		if (!_contents.containsAll(change.elementsRemoved()))
			throw new IllegalArgumentException("SetSource did not contain all elements being removed.");
	}

}
