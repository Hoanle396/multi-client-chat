package GUI;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

public class User {
	private PrintStream streamOut;
	private InputStream streamIn;
	private String nickname;

	// constructor
	public User(Socket client, String name) throws IOException {
		this.streamOut = new PrintStream(client.getOutputStream());
		this.streamIn = client.getInputStream();
		this.nickname = name;
	}

	// getteur
	public PrintStream getOutStream() {
		return this.streamOut;
	}

	public InputStream getInputStream() {
		return this.streamIn;
	}

	public String getNickname() {
		return this.nickname;
	}

	// print user with his color
	public String toString() {
		return this.getNickname();
	}
}
