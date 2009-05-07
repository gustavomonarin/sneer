package sneer.pulp.internetaddresskeeper.impl;

import sneer.pulp.contacts.Contact;
import sneer.pulp.internetaddresskeeper.InternetAddress;
import sneer.pulp.internetaddresskeeper.InternetAddressKeeper;
import sneer.pulp.reactive.collections.SetRegister;
import sneer.pulp.reactive.collections.SetSignal;
import sneer.pulp.reactive.collections.impl.SetRegisterImpl;

class InternetAddressKeeperImpl implements InternetAddressKeeper {

	private SetRegister<InternetAddress> _addresses = new SetRegisterImpl<InternetAddress>();
	
	@Override
	public void remove(InternetAddress address) {
		_addresses.remove(address);
	}	
	
	@Override
	public SetSignal<InternetAddress> addresses() {
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
