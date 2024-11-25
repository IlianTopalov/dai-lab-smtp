package ch.heig.dai.lab.smtp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class Main {

	private static final String HOST = "localhost";
	private static final int PORT = 1025;

	public static void main(String[] args) {
		if (args.length != 4) {
			System.err.println("Correct command: smtp <addresses file> <messages file> <group count>");
		}

		List<String> addresses;
		List<String> messages;

		try {
			addresses = ListReader.readList(args[1]);
			messages = ListReader.readList(args[2]);
		} catch (IOException e) {
			System.err.println(e.getMessage());

			return;
		}

		int groupCount = Integer.parseInt(args[3]);

		List<List<String>> groups = Grouper.groupLines(addresses, groupCount);

		for (List<String> group : groups) {
			int msgIdx = new Random().nextInt() % messages.size();

			String message = messages.get(msgIdx);
			String sender = group.getFirst();

			try (
				Socket socket = new Socket(HOST, PORT);
				var os = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream(),
					StandardCharsets.UTF_8
				))
			) {
				for (int i = 1; i < group.size(); ++i) {

					// Treat the message to write it in SMTP format

					os.write(message + '\n');
					os.flush();
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}
}
