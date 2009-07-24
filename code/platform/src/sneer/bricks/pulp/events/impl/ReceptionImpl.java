package sneer.bricks.pulp.events.impl;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.pulp.events.EventSource;
import sneer.foundation.lang.Consumer;

/** Hold a reference to the EventSource (Signal) the receiver is receiving, so that this source is not GCd. */
class ReceptionImpl implements Contract {

	
	private final EventSource<?> _sourceReferenceToAvoidGc;
	private final Consumer<?> _receiver;

	
	<T> ReceptionImpl(EventSource<? extends T> eventSource, Consumer<? super T> receiver) {
		_receiver = receiver;
		_sourceReferenceToAvoidGc = eventSource;

		eventSource.addReceiver(receiver);
	}


	@Override
	public void dispose() {
		_sourceReferenceToAvoidGc.removeReceiver(_receiver);
	}
	
}