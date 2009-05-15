package sneer.skin.widgets.reactive.autoscroll.impl;

import static sneer.commons.environments.Environments.my;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import sneer.commons.lang.ByRef;
import sneer.hardware.cpu.lang.Consumer;
import sneer.hardware.gui.guithread.GuiThread;
import sneer.pulp.events.EventSource;
import sneer.pulp.reactive.Signals;
import sneer.pulp.reactive.collections.CollectionChange;
import sneer.pulp.reactive.collections.ListSignal;
import sneer.pulp.reactive.collections.impl.SimpleListReceiver;
import sneer.skin.main.synth.Synth;

class AutoScroll<T> extends JScrollPane{
	
	{ my(Synth.class).attach(this); }

	private boolean _shouldAutoscroll = true;
	
	AutoScroll(JComponent keyTypeSource, ListSignal<T> inputSignal, Consumer<CollectionChange<T>> receiver) {
		keyTypeSource.addKeyListener(new KeyAdapter(){@Override public void keyReleased(KeyEvent e) {
			if(_shouldAutoscroll) 
				placeAtEnd();
		}});
		initReceivers(inputSignal, receiver);
	}
	
	public AutoScroll(EventSource<T> eventSource) {
		initReceivers(eventSource);
	}
	
	private boolean isAtEnd() {
		final ByRef<Boolean> result = ByRef.newInstance();
		my(GuiThread.class).invokeAndWait(new Runnable(){ @Override public void run() {
			result.value =  scrollModel().getValue() + scrollModel().getExtent() == scrollModel().getMaximum();
		}});
		return result.value;
	}		
	
	private void placeAtEnd() {
		my(GuiThread.class).invokeLater(new Runnable(){ @Override public void run() {
			scrollModel().setValue(scrollModel().getMaximum()-scrollModel().getExtent());
		}});
	}
	
	private BoundedRangeModel scrollModel() {
		return getVerticalScrollBar().getModel();
	}	
	
	private void initReceivers(ListSignal<T> inputSignal, Consumer<CollectionChange<T>> consumer) {
		initPreChangeReceiver(inputSignal);		
		my(Signals.class).receive(this, consumer, inputSignal);
		initPosChangeReceiver(inputSignal);
	}
	
	private void initReceivers(EventSource<T> eventSource) {
		my(Signals.class).receive(this, new Consumer<T>(){ @Override public void consume(T value) {
			if(_shouldAutoscroll) placeAtEnd();
		}}, eventSource);
	}

	@SuppressWarnings("unused")
	private SimpleListReceiver<T> _preChangeReceiverAvoidGc;
	private void initPreChangeReceiver(ListSignal<T> inputSignal) {
		_preChangeReceiverAvoidGc = new MySimpleListReceiver(inputSignal){ @Override protected void fire() {
			_shouldAutoscroll = isAtEnd();
		}};
	}
	
	@SuppressWarnings("unused")
	private SimpleListReceiver<T> _posChangeReceiverAvoidGc;
	private void initPosChangeReceiver(ListSignal<T> inputSignal) {
		_posChangeReceiverAvoidGc = new MySimpleListReceiver(inputSignal){ @Override protected void fire() {
				if(_shouldAutoscroll) placeAtEnd();
		}};
	}
	
	private abstract class MySimpleListReceiver extends SimpleListReceiver<T>{
		public MySimpleListReceiver(ListSignal<T> inputSignal) { super(inputSignal); }
		@Override protected void elementAdded(T newElement) { fire();}
		@Override protected void elementPresent(T element) 		{ fire();}
		@Override protected void elementRemoved(T element) 	{ fire();}

		protected abstract void fire();
	}
}