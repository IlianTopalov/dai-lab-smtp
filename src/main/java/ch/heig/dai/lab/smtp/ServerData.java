package ch.heig.dai.lab.smtp;

public record ServerData(String host, int port) {
	@Override
	public String toString() {
		return host + ":" + port;
	}
}
