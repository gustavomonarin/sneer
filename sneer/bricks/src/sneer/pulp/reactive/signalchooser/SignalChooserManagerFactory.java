package sneer.pulp.reactive.signalchooser;

import sneer.brickness.OldBrick;
import wheel.reactive.lists.ListSignal;

public interface SignalChooserManagerFactory extends OldBrick{
	
	<T> SignalChooserManager<T> newManager(ListSignal<T> input, ListOfSignalsReceiver<T> listOfSignalsReceiver);
}
