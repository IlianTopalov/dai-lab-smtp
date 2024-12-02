package ch.heig.dai.lab.smtp;

public class SmtpWrapper {

	private SmtpWrapper() {}

	public static String wrap(String from, String to, String content) {
		return String.format(
			"""
			HELO
			MAIL FROM:%s
			RCPT TO:%s
			DATA
			From: %s
			Subject: Troll
			To: %s

			%s

			QUIT
			""", from, to, from, to, content);
	}
}
