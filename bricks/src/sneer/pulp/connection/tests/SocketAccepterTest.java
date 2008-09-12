package sneer.pulp.connection.tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.Socket;

import org.junit.Test;

import sneer.kernel.container.Inject;
import sneer.kernel.container.tests.TestThatIsInjected;
import sneer.pulp.connection.SocketAccepter;
import sneer.pulp.network.ByteArraySocket;
import sneer.pulp.network.impl.ByteArraySocketImpl;
import sneer.pulp.port.PortKeeper;
import wheel.reactive.impl.Receiver;

public class SocketAccepterTest extends TestThatIsInjected {

	@Inject
	private static PortKeeper _portKeeper;
	
	@Inject
	private static SocketAccepter _accepter;
	
	@Test(timeout=3000)
	public void testSocketAccept() throws Exception {
		_portKeeper.portSetter().consume(9090);

		@SuppressWarnings("unused")
		Receiver<ByteArraySocket> _acceptedSocketReceiverAvoidGc = new Receiver<ByteArraySocket>(_accepter.lastAcceptedSocket()) { @Override public void consume(ByteArraySocket socket) {
			try {
				byte[] request = socket.read();
				String s = new String(request);
				socket.write(s.toUpperCase().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}};

		SneerClient client = new SneerClient();
		while(!client.connected(9090));
		String reply = client.talk("hello");
		assertEquals("HELLO", reply);
	}
}


class SneerClient {
	
	private ByteArraySocket _socket;
	
	public boolean connected(int port) {
		try {
			_socket = new ByteArraySocketImpl(new Socket("127.0.0.1", port));
			return true;
		} catch (Exception ignored) {
			//ignored.printStackTrace(System.err);
			return false;
		}
	}
	
	public String talk(String message) throws IOException {
		_socket.write(message.getBytes());
		byte[] buffer = _socket.read();
		return new String(buffer);
	}
}