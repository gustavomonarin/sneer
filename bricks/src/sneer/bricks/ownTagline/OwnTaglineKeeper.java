package sneer.bricks.ownTagline;

import sneer.lego.Brick;
import wheel.lang.Omnivore;
import wheel.reactive.Signal;

public interface OwnTaglineKeeper extends Brick {

	Signal<String> tagline();

	Omnivore<String> taglineSetter();

	void setTagline(String name);

	String getTagline();
}
