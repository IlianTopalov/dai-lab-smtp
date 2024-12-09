package ch.heig.dai.lab.smtp;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class Main {

	private static final int MIN_ADDRESSES_IN_GROUP = 2;
	private static final int MAX_ADDRESSES_IN_GROUP = 5;

	private static final String DEFAULT_CONFIG_FILE_NAME = "config";

	public static void main(String[] args) {
		if ((args.length == 2 && !args[0].equals("-config")) || args.length == 1 || args.length > 2)
			throw new IllegalArgumentException("Correct command: smtp -config <config file>");

		List<String> addresses;
		List<String> messages;

		String configFileName = (args.length == 2 ? args[1] : DEFAULT_CONFIG_FILE_NAME);

		final ServerData server;
		final String addressesPath;
		final String messagesPath;
		final String subject;
		final int groupCount;

		try (BufferedReader configFile = new BufferedReader(new InputStreamReader(
			new FileInputStream(configFileName),
			StandardCharsets.UTF_8
		))) {
			server = new ServerData(
				extractColonValue(configFile.readLine()),
				Integer.parseInt(extractColonValue(configFile.readLine()))
			);

			addressesPath = extractColonValue(configFile.readLine());
			messagesPath = extractColonValue(configFile.readLine());
			subject = extractColonValue(configFile.readLine());
			groupCount = Integer.parseInt(extractColonValue(configFile.readLine()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			addresses = FileUtil.readList(addressesPath);
			messages = FileUtil.readList(messagesPath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (addresses.size() < groupCount * MIN_ADDRESSES_IN_GROUP)
			throw new IllegalArgumentException("Too few addresses.");
		else if (addresses.size() > groupCount * MAX_ADDRESSES_IN_GROUP)
			throw new IllegalArgumentException("Too many addresses.");

		List<List<String>> groups = FileUtil.groupLines(addresses, groupCount);
		try (SMTPSender mailCannon = new SMTPSender(server)) {
			for (List<String> group : groups) {
				int msgIdx = Math.abs(new Random().nextInt() % messages.size());

				String message = messages.get(msgIdx);
				String sender = group.getFirst();

				for (int i = 1; i < group.size(); ++i) {
					String receiver = group.get(i);

					mailCannon.sendMessage(sender, receiver, subject, message);
				}
			}
		}
	}


	/**
	 * Returns the value stored in a String after a colon.
	 *
	 * @param line the line from which to extract a value.
	 *
	 * @return The value.
	 */
	private static String extractColonValue(String line) {
		return line.split(":")[1].trim();
	}
}
