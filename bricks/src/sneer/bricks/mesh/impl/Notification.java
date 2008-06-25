package sneer.bricks.mesh.impl;

import sneer.bricks.keymanager.PublicKey;
import sneer.lego.Brick;

class Notification implements Ambassador {

	private final PublicKey _publicKey;
	private final Class<? extends Brick> _brickInterface;
	private final String _signalName;
	private final Object _newValue;

	public Notification(PublicKey publicKey, Class<? extends Brick> brickInterface, String signalName, Object newValue) {
		_publicKey = publicKey;
		_brickInterface = brickInterface;
		_signalName = signalName;
		_newValue = newValue;
	}

	public void visit(Visitable visitable) {
		visitable.handleNotification(_publicKey, _brickInterface, _signalName, _newValue);
	}

}
