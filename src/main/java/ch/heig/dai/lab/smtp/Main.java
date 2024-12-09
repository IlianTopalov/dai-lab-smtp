package ch.heig.dai.lab.smtp;

import java.io.*;
import java.util.List;
import java.util.Random;

public class Main {

	private static final int MIN_ADDRESSES_IN_GROUP = 2;
	private static final int MAX_ADDRESSES_IN_GROUP = 5;

	public static void main(String[] args) {
		if ((args.length == 2 && !args[0].equals("-config")) || args.length == 1 || args.length > 2)
			throw new IllegalArgumentException("Correct command: smtp -config <config file>");

		List<String> config;

		try {
			if (args.length == 2) {
				config = FileUtil.readList(args[0]);
			} else {
				config = FileUtil.readList("smtpSender.config");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		String h = config.getFirst();
		final String host = h.substring(h.indexOf(':'), h.length() - 1);

		String p = config.get(1);
		final int port = Integer.parseInt(p.substring(p.indexOf(':'), p.length() - 1));

		final ServerData SERVER = new ServerData(host, port);

		String addr = config.get(2);
		final String addr_path = addr.substring(addr.indexOf(':'), addr.length() - 1);

		String msg = config.get(3);
		final String msg_path = msg.substring(msg.indexOf(':'), msg.length() - 1);

		String s = config.get(4);
		final String SUBJECT = s.substring(s.indexOf(':'), s.length() - 1);

		String g = config.get(5);
		final int groupCount = Integer.parseInt(g.substring(g.indexOf(':'), g.length() - 1));

		List<String> addresses;
		List<String> messages;

		try {
			addresses = FileUtil.readList(addr_path);
			messages = FileUtil.readList(msg_path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (addresses.size() < groupCount * MIN_ADDRESSES_IN_GROUP)
			throw new IllegalArgumentException("Too few addresses.");
		else if (addresses.size() > groupCount * MAX_ADDRESSES_IN_GROUP)
			throw new IllegalArgumentException("Too many addresses.");

		List<List<String>> groups = FileUtil.groupLines(addresses, groupCount);
		try (SMTPSender mailCannon = new SMTPSender(SERVER)) {
			for (List<String> group : groups) {
				int msgIdx = Math.abs(new Random().nextInt() % messages.size());

				String message = messages.get(msgIdx);
				String sender = group.getFirst();

				for (int i = 1; i < group.size(); ++i) {
					String receiver = group.get(i);

					mailCannon.sendMessage(sender, receiver, SUBJECT, message);
				}
			}
		}
	}
}
