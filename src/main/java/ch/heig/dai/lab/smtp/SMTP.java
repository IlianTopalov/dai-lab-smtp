package ch.heig.dai.lab.smtp;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SMTP {

	private static final String MSG_HELO = "HELO client";
	private static final String MSG_FROM = "MAIL FROM: <%s>";
	private static final String MSG_TO = "RCPT TO: <%s>";
	private static final String MSG_DATA = "DATA";
	private static final String MSG_BODY =
		"""
		From: %s
		To: %s
		Subject: Troll

		%s
		\r
		.\r
		
		""";
	private static final String MSG_QUIT = "QUIT";

	private static final int OK_CODE = 250;
	private static final int DATA_CODE = 354;
	private static final int QUIT_CODE = 221;

	private SMTP() {}



	public static void sendMessage(Socket connection, String from, String to, String content) throws IOException {
		try (
			var os = new BufferedWriter(new OutputStreamWriter(
				connection.getOutputStream(),
				StandardCharsets.UTF_8
			));
			var is = new BufferedReader(new InputStreamReader(
				connection.getInputStream(),
				StandardCharsets.UTF_8
			))
		) {
			// Message initiation
			sendAndCheckResponse(os, is, MSG_HELO);

			// From
			sendAndCheckResponse(os, is, MSG_FROM.formatted(from));

			// To
			sendAndCheckResponse(os, is, MSG_TO.formatted(to));

			// Data
			sendAndCheckResponse(os, is, MSG_DATA, DATA_CODE);

			// Content
			sendAndCheckResponse(os, is, MSG_BODY.formatted(from, to, content));

			// Quit
			sendAndCheckResponse(os, is, MSG_QUIT, QUIT_CODE);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}

	public static void checkResponseCode(String response, int code) throws IOException {
		int responseCode;
		try {
			responseCode = Integer.parseInt(response.split(" ")[0]);
		} catch (NumberFormatException e) {
			throw new IOException("Invalid response code.");
		}

		if (responseCode != code)
			throw new IOException("Expected response code: %d\nActual response code: %d\n".formatted(code, responseCode));
	}

	public static void sendAndCheckResponse(BufferedWriter os, BufferedReader is, String message) throws IOException {
		sendAndCheckResponse(os, is, message, OK_CODE);
	}

	public static void sendAndCheckResponse(BufferedWriter os, BufferedReader is, String message, int code) throws IOException {
		os.write(message + "\n");
		os.flush();
		checkResponseCode(is.readLine(), code);
	}
}
