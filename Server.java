import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

	ServerSocket serverSocket;
	Socket connection;
	ObjectOutputStream outputStream;
	ObjectInputStream inputStream;

	public void startRunning() {
		try {
			serverSocket = new ServerSocket(9999, 100);
			while (true) {
				waitForConnection();
				setUpStreams();
				whileChatting();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Closing the chat!");

			try {
				outputStream.close();
				inputStream.close();
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void waitForConnection() throws IOException {
		System.out.println("Waiting for people to connect!");
		connection = serverSocket.accept();
		System.out.println("connected to " + connection.getInetAddress().getHostName());
	}

	public void setUpStreams() throws IOException {
		outputStream = new ObjectOutputStream(connection.getOutputStream());
		outputStream.flush();

		inputStream = new ObjectInputStream(connection.getInputStream());
		System.out.println("Streams are now setup!");
	}

	public void whileChatting() {
		String message = "You are now connected!";
		System.out.println(message);

		Thread sendMessageFromServer = new Thread(new SendMessageFromServer());
		Thread getMessageFromClient = new Thread(new GetMessageFromClient());
		sendMessageFromServer.start();
		getMessageFromClient.start();
	}

	public void sendMessage(String message) {
		try {
			outputStream.writeObject("SERVER - " + message);
			outputStream.flush();
			showMessage("\nSERVER - " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showMessage(String message) {
		System.out.println(message);
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.startRunning();
	}

	class SendMessageFromServer implements Runnable {

		@Override
		public void run() {
			Scanner in = new Scanner(System.in);

			while (true) {
				System.out.print("SERVER - ");
				String inputMessage = in.nextLine();
				sendMessage(inputMessage);
				System.out.println();
			}
		}

	}

	class GetMessageFromClient implements Runnable {

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
