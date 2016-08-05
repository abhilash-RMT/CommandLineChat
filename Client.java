import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	Socket connection;
	ObjectOutputStream outputStream;
	ObjectInputStream inputStream;
	String message = "";
	String serverIP;

	public Client(String serverIP) {
		this.serverIP = serverIP;
	}

	public void startChatting() {
		try {
			connectToServer();
			setupStreams();
			whileChatting();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void connectToServer() throws UnknownHostException, IOException {
		System.out.println("Attempting a connection!");
		connection = new Socket(InetAddress.getByName(serverIP), 9999);
		System.out.println("Connected to " + connection.getInetAddress().getHostName());

	}

	public void setupStreams() throws IOException {
		outputStream = new ObjectOutputStream(connection.getOutputStream());
		outputStream.flush();
		inputStream = new ObjectInputStream(connection.getInputStream());
		System.out.println("Streams are setup for client!");
	}

	public void whileChatting() throws ClassNotFoundException, IOException {
		Scanner in = new Scanner(System.in);

		while (true) {
			System.out.print("CLIENT - ");
			String inputMessage = in.nextLine();
			sendMessage(inputMessage);
			System.out.println();
			message = (String) inputStream.readObject();
			showMessage(message);
		}
	}

	public void sendMessage(String message) {
		try {
			outputStream.writeObject("CLIENT - " + message);
			outputStream.flush();
			showMessage("\nCLIENT - " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showMessage(String message) {
		System.out.println(message);
	}

	public static void main(String[] args) {
		Client client = new Client("127.0.0.1");
		client.startChatting();
	}

	class SendMessageFromClient implements Runnable {

		@Override
		public void run() {
			Scanner in = new Scanner(System.in);

			while (true) {
				System.out.print("CLIENT - ");
				String inputMessage = in.nextLine();
				sendMessage(inputMessage);
				System.out.println();
			}
		}

	}

	class GetMessageFromServer implements Runnable {

		@Override
		public void run() {
			String message = "";
			while (true) {
				try {
					message = (String) inputStream.readObject();
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
				showMessage(message);
			}
		}

	}

}
