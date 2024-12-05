package ch.heig.dai.lab.smtp;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SMTPSender {

	private static final String MSG_HELO = "HELO client";
	private static final String MSG_FROM = "MAIL FROM: <%s>";
	private static final String MSG_TO = "RCPT TO: <%s>";
	private static final String MSG_DATA = "DATA";
	private static final String MSG_BODY =
		"""
		From: %s
		To: %s
		Subject: %s

		%s
		\r
		.\r
  
		""";
	private static final String MSG_QUIT = "QUIT";

	private static final int START_CODE = 220;
	private static final int OK_CODE = 250;
	private static final int DATA_CODE = 354;

	private final ServerData server;

	public SMTPSender(ServerData server) {
		this.server = server;
	}

	/**
	 * Sends a message to the registered SMTP server.
	 *
	 * @param from    sender
	 * @param to      recipient
	 * @param subject subject of the e-mail
	 * @param content body of the e-mail
	 */
	public void sendMessage(String from, String to, String subject, String content) {
		try (
			Socket connection = new Socket(server.host(), server.port());
			var os = new BufferedWriter(new OutputStreamWriter(
				connection.getOutputStream(),
				StandardCharsets.UTF_8
			));
			var is = new BufferedReader(new InputStreamReader(
				connection.getInputStream(),
				StandardCharsets.UTF_8
			))
		) {
			checkResponseCode(is.readLine(), START_CODE);

			// Message initiation
			sendAndCheckResponse(os, is, MSG_HELO);

			// From
			sendAndCheckResponse(os, is, MSG_FROM.formatted(from));

			// To
			sendAndCheckResponse(os, is, MSG_TO.formatted(to));

			// Data
			sendAndCheckResponse(os, is, MSG_DATA, DATA_CODE);

			// Content
			sendAndCheckResponse(os, is, MSG_BODY.formatted(from, to, subject, content));

			// Quit
			send(os, MSG_QUIT);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}

	@Override
	public String toString() {
		return "SMTP sender - MX at " + server;
	}



	private static void checkResponseCode(String response, int code) throws IOException {
		int responseCode;
		try {
			responseCode = Integer.parseInt(response.split(" ")[0]);
		} catch (NumberFormatException e) {
			throw new IOException("Invalid response code.");
		}

		if (responseCode != code)
			throw new IOException("Expected response code: %d\nActual response code: %d\n".formatted(code, responseCode));
	}

	private static void send(BufferedWriter os, String message) throws IOException {
		os.write(message + "\r\n");
		os.flush();
	}

	private static void sendAndCheckResponse(BufferedWriter os, BufferedReader is, String message) throws IOException {
		sendAndCheckResponse(os, is, message, OK_CODE);
	}

	private static void sendAndCheckResponse(BufferedWriter os, BufferedReader is, String message, int code) throws IOException {
		send(os, message);
		checkResponseCode(is.readLine(), code);
	}
}
