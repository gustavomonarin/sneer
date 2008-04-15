package sneer.bricks.internetaddresskeeper.impl;

import sneer.bricks.contacts.Contact;
import sneer.bricks.internetaddresskeeper.InternetAddress;
import sneer.bricks.internetaddresskeeper.InternetAddressKeeper;
import wheel.reactive.lists.ListSignal;
import wheel.reactive.lists.ListSource;
import wheel.reactive.lists.impl.ListSourceImpl;

public class InternetAddressKeeperImpl implements InternetAddressKeeper {

	private ListSource<InternetAddress> _addresses = new ListSourceImpl<InternetAddress>();
	
	@Override
	public ListSignal<InternetAddress> addresses() {
		return _addresses.output();
	}

	@Override
	public void add(Contact contact, String host, int port) { //Implement Handle contact removal.
		if (!isNewAddress(contact, host, port)) return;
		
		InternetAddress addr = new InternetAddressImpl(contact, host, port);
		_addresses.add(addr);
	}

	private boolean isNewAddress(Contact contact, String host, int port) {
		for (InternetAddress addr : _addresses.output())
			if(addr.contact().equals(contact) 
					&& addr.host().equals(host)
					&& addr.port() == port)
				return false;
		
		return true;
	}
}
