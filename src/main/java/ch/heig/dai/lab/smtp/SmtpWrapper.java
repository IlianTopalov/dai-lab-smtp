package ch.heig.dai.lab.smtp;

public class SmtpWrapper {

	private SmtpWrapper() {}

	public static String wrap(String from, String to, String content) {
		/**
		 *                      EHLO bar.com
		 *                      MAIL FROM:%s
		 *                      RCPT TO:%s
		 *                      DATA
		 *                      Date: Thu, 21 May 1998 05:33:29 -0700
		 *                      From: %s
		 *                      Subject: The Next Meeting of the Board
		 *                      To: %s
		 *
		 *                      %s
		 *
		 *                      QUIT
		 */

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
