package GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.html.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Client extends Thread {

	final JTextPane panelChat = new JTextPane();
	final JTextPane panelUsers = new JTextPane();
	final JTextField inputMessage = new JTextField();
	private Thread read;
	private String serverName;
	private int PORT;
	private String name;
	BufferedReader input;
	PrintWriter output;
	Socket server;

	public Client() {
		this.serverName = "localhost";
		this.PORT = 12345;
		this.name = "HOANLE";

		final JFrame frame = new JFrame("Chat");
		frame.getContentPane().setLayout(null);
		frame.setSize(700, 500);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panelChat.setBounds(25, 25, 490, 320);
		panelChat.setEditable(false);
		JScrollPane scrollChat = new JScrollPane(panelChat);
		scrollChat.setBounds(25, 25, 490, 320);

		
		panelUsers.setBounds(520, 25, 156, 320);
		panelUsers.setEditable(false);
		JScrollPane scrollUsersList = new JScrollPane(panelUsers);
		scrollUsersList.setBounds(520, 25, 156, 320);

		// Field message user input
		inputMessage.setBounds(0, 350, 400, 50);
		inputMessage.setMargin(new Insets(6, 6, 6, 6));
		final JScrollPane inputMessageSP = new JScrollPane(inputMessage);
		inputMessageSP.setBounds(25, 350, 650, 50);

		// button send
		final JButton btnSend = new JButton("Send");
		btnSend.setBounds(575, 410, 100, 35);

		// button Disconnect
		final JButton btnDisconnect = new JButton("Disconnect");
		btnDisconnect.setBounds(25, 410, 130, 35);

		inputMessage.addKeyListener(new KeyAdapter() {
			// send message on Enter
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
		});

		// Click on send button
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				sendMessage();
			}
		});

		// Connection view
		final JTextField inputNickname = new JTextField(this.name);
		final JTextField inputPort = new JTextField(Integer.toString(this.PORT));
		final JTextField inputAddress = new JTextField(this.serverName);
		final JButton btnConnect = new JButton("Connect");

		
		inputAddress.setBounds(25, 380, 135, 40);
		inputNickname.setBounds(375, 380, 135, 40);
		inputPort.setBounds(200, 380, 135, 40);
		btnConnect.setBounds(575, 380, 100, 40);

		frame.getContentPane().add(btnConnect);
		frame.getContentPane().add(scrollChat);
		frame.getContentPane().add(scrollUsersList);
		frame.getContentPane().add(inputNickname);
		frame.getContentPane().add(inputPort);
		frame.getContentPane().add(inputAddress);
		frame.setVisible(true);

		// info sur le Chat
		appendToPane(panelChat, "Multiple Client chat");

		// On connect
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					name = inputNickname.getText();
					String port = inputPort.getText();
					serverName = inputAddress.getText();
					PORT = Integer.parseInt(port);
					server = new Socket(serverName, PORT);

					appendToPane(panelChat, "Connected to " + server.getRemoteSocketAddress() + "");

					input = new BufferedReader(new InputStreamReader(server.getInputStream()));
					output = new PrintWriter(server.getOutputStream(), true);
					// send nickname to server
					output.println(name);

					// create new Read Thread
					read = new Read();
					read.start();
					
					frame.setTitle(name);
					frame.remove(inputNickname);
					frame.remove(inputPort);
					frame.remove(inputAddress);
					frame.remove(btnConnect);
					frame.getContentPane().add(btnSend);
					frame.getContentPane().add(inputMessageSP);
					frame.getContentPane().add(btnDisconnect);
					frame.revalidate();
					frame.repaint();				
					panelChat.setBackground(Color.WHITE);
					panelUsers.setBackground(Color.WHITE);
				} catch (Exception ex) {
					appendToPane(panelChat, "Could not connect to Server");
					JOptionPane.showMessageDialog(frame, ex.getMessage());
				}
			}

		});

		// on disconnect
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				frame.getContentPane().add(inputNickname);
				frame.getContentPane().add(inputPort);
				frame.getContentPane().add(inputAddress);
				frame.getContentPane().add(btnConnect);
				frame.remove(btnSend);
				frame.remove(inputMessageSP);
				frame.remove(btnDisconnect);
				frame.revalidate();
				frame.repaint();
				read.interrupt();
				panelUsers.setText(null);
				panelChat.setBackground(Color.LIGHT_GRAY);
				panelUsers.setBackground(Color.LIGHT_GRAY);
				appendToPane(panelChat, "Connection closed.");
				output.close();
			}
		});

	}

	// envoi des messages
	public void sendMessage() {
		try {
			String message = inputMessage.getText().trim();
			if (message.equals("")) {
				return;
			}
			output.println(message);
			inputMessage.setText(null);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage());
			System.exit(0);
		}
	}

	private void appendToPane(JTextPane tp, String msg) {
		String oldMessage = tp.getText();

		try {
			tp.setText(oldMessage + "\n" + msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		new Client();
	}

	// read new incoming messages
	class Read extends Thread {
		public void run() {
			String message;
			while (!Thread.currentThread().isInterrupted()) {
				try {
					message = input.readLine();
					if (message != null) {
						if (message.charAt(0) == '[') {
							message = message.substring(1, message.length() - 1);
							ArrayList<String> ListUser = new ArrayList<String>(Arrays.asList(message.split(", ")));
							panelUsers.setText(null);
							for (String user : ListUser) {
								appendToPane(panelUsers, "@" + user);
							}
						} else {
							appendToPane(panelChat, message);
						}
					}
				} catch (IOException ex) {
					System.err.println("Failed to parse incoming message");
				}
			}
		}
	}

}