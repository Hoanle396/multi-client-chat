package GUI;

import java.util.Scanner;

public class UserHandler implements Runnable {

	private Server server;
	private User user;

	public UserHandler(Server server, User user) {
		this.server = server;
		this.user = user;
		this.server.broadcastAllUsers();
	}

	public void run() {
		String message;

		Scanner sc = new Scanner(this.user.getInputStream());
		while (sc.hasNextLine()) {
			message = sc.nextLine();
			server.broadcastMessages(message, user);
		}
		// end of Thread
		server.removeUser(user);
		this.server.broadcastAllUsers();
		sc.close();
	}
}

