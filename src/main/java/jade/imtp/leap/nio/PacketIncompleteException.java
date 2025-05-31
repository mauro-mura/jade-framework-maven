package jade.imtp.leap.nio;



import java.io.IOException;

class PacketIncompleteException extends IOException {

	public PacketIncompleteException(String message) {
		super(message);
	}
}