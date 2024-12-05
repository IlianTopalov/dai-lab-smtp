package ch.heig.dai.lab.smtp;

import java.io.*;
import java.util.List;
import java.util.Random;

public class Main {

	private static final ServerData SERVER = new ServerData("localhost", 1025);

	private static final int MIN_ADDRESSES_IN_GROUP = 2;
	private static final int MAX_ADDRESSES_IN_GROUP = 5;

	private static final String SUBJECT = "Prank";

	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("Correct command: smtp <addresses file> <messages file> <group count>");

			return;
		}

		List<String> addresses;
		List<String> messages;

		try {
			addresses = ListReader.readList(args[0]);
			messages = ListReader.readList(args[1]);
		} catch (IOException e) {
			System.err.println(e.getMessage());

			return;
		}

		int groupCount = Integer.parseInt(args[2]);

		if (addresses.size() < groupCount * MIN_ADDRESSES_IN_GROUP) {
			System.err.println("Too few addresses.");

			return;
		} else if (addresses.size() > groupCount * MAX_ADDRESSES_IN_GROUP) {
			System.err.println("Too many addresses.");

			return;
		}

		List<List<String>> groups = Grouper.groupLines(addresses, groupCount);

		for (List<String> group : groups) {
			int msgIdx = Math.abs(new Random().nextInt() % messages.size());

			String message = messages.get(msgIdx);
			String sender = group.getFirst();

			for (int i = 1; i < group.size(); ++i) {
				String receiver = group.get(i);

				SMTP.sendMessage(SERVER, sender, receiver, SUBJECT, message);
			}
		}
	}
}
