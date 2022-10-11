package GUI;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

	private int port;
	private List<User> clients;
	private ServerSocket server;

	public static void main(String[] args) throws IOException {
		new Server(12345).run();
	}

	public Server(int port) {
		this.port = port;
		this.clients = new ArrayList<User>();
	}

	public void run() throws IOException {
		server = new ServerSocket(port) {
			protected void finalize() throws IOException {
				this.close();
			}
		};

		while (true) {
			// accepts a new client
			Socket sv = server.accept();

			// get nickname of newUser
			Scanner scanner = new Scanner(sv.getInputStream());
			String nickname = scanner.nextLine();
			nickname = nickname.replace(",", ""); // ',' use for serialisation
			nickname = nickname.replace(" ", "_");
			// create new User
			User newUser = new User(sv, nickname);

			// add newUser message to list
			this.clients.add(newUser);

			// Welcome msg
			newUser.getOutStream().println("Welcome : " + newUser.toString());

			// create a new thread for newUser incoming messages handling
			new Thread(new UserHandler(this, newUser)).start();
		}
	}

	public void removeUser(User user) {
		this.clients.remove(user);
	}

	public void broadcastMessages(String msg, User userSender) {
		for (User client : this.clients) {
			client.getOutStream().println(userSender.toString() + ": " + msg + "");
		}
	}

	public void broadcastAllUsers() {
		for (User client : this.clients) {
			client.getOutStream().println(this.clients);
		}
	}
}