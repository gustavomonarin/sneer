package sneer.kernel.install;

import static sneer.kernel.SneerDirectories.sneerDirectory;
import wheel.io.ui.SwingUser;

public class Installer {

	private final SwingUser _user;


	public Installer(SwingUser user) throws Exception {
		_user = user;

		if (!sneerDirectory().exists()) tryToInstall();
		if (!sneerDirectory().exists()) return;
	}


	private void tryToInstall() {
		new InstallationDialog(_user);
		sneerDirectory().mkdir();
	}

}
