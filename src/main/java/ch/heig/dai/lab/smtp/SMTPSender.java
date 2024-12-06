package ch.heig.dai.lab.smtp;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SMTPSender implements Closeable {

	private static final String MSG_HELO = "HELO client";
	private static final String MSG_FROM = "MAIL FROM: <%s>";
	private static final String MSG_TO = "RCPT TO: <%s>";
	private static final String MSG_DATA = "DATA";
	private static final String MSG_BODY =
		"""
		From: %s
		To: %s
		Subject: %s

		%s\r
		.""";
	private static final String MSG_QUIT = "QUIT";

	private static final int START_CODE = 220;
	private static final int OK_CODE = 250;
	private static final int DATA_CODE = 354;

	private final Socket connection;
	private final BufferedWriter os;
	private final BufferedReader is;

	public SMTPSender(ServerData server) {
		Socket socket = null;
		BufferedWriter os = null;
		BufferedReader is = null;

		// Try to connect
		try {
			socket = new Socket(server.host(), server.port());
			os = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream(),
				StandardCharsets.UTF_8
			));
			is = new BufferedReader(new InputStreamReader(
				socket.getInputStream(),
				StandardCharsets.UTF_8
			));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		this.connection = socket;
		this.os = os;
		this.is = is;

		// Initial SMTP message
		try {
			checkResponseCode(is.readLine(), START_CODE);

			// Message initiation
			sendAndCheckResponse(MSG_HELO);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
		try {
			// From
			sendAndCheckResponse(MSG_FROM.formatted(from));

			// To
			sendAndCheckResponse(MSG_TO.formatted(to));

			// Data
			sendAndCheckResponse(MSG_DATA, DATA_CODE);
			sendAndCheckResponse(MSG_BODY.formatted(from, to, subject, content));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public void close() {
		try {
			send(MSG_QUIT);

			os.close();
			is.close();
			connection.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public String toString() {
		return "SMTP sender - MX at " + connection.getInetAddress().getHostAddress() + ":" + connection.getPort();
	}



	private void checkResponseCode(String response, int code) throws IOException {
		int responseCode;
		try {
			responseCode = Integer.parseInt(response.split(" ")[0]);
		} catch (NumberFormatException e) {
			throw new IOException("Invalid response code.");
		}

		if (responseCode != code)
			throw new IOException("Expected response code: %d\nActual response code: %d\n".formatted(code, responseCode));
	}

	private void send(String message) throws IOException {
		os.write(message + "\r\n");
		os.flush();
	}

	private void sendAndCheckResponse(String message) throws IOException {
		sendAndCheckResponse(message, OK_CODE);
	}

	private void sendAndCheckResponse(String message, int code) throws IOException {
		send(message);
		checkResponseCode(is.readLine(), code);
	}
}
