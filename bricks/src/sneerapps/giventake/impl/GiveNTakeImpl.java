package sneerapps.giventake.impl;

import java.util.Collection;

import sneer.bricks.mesh.Me;
import sneer.bricks.things.Thing;
import sneer.lego.Inject;
import sneerapps.giventake.GiveNTake;
import wheel.reactive.sets.SetRegister;
import wheel.reactive.sets.SetSignal;
import wheel.reactive.sets.impl.SetRegisterImpl;

class GiveNTakeImpl implements GiveNTake {

	@Inject
	static private Me _me;

	@Override
	public void advertise(Thing thing) {
		//Refactor This is wierd. This method has to do nothing because things are already "advertised" (they are directly searchable).
	}

	@Override
	public SetSignal<Thing> search(String tags) {
		SetRegister<Thing> result = new SetRegisterImpl<Thing>();

		Collection<GiveNTake> peers = _me.allImmediateContactBrickCounterparts(GiveNTake.class);
		for (GiveNTake peer : peers) {
			SetSignal<Thing> found = peer.search(tags);
			for (Thing thing : found)
				result.add(thing);
		}
		return result.output();
	}

}
