package application;

public enum Signal {
	NONE(0),
	CONNECT(1),
	DISCONNECT(2),
	AUTHORIZE(3);
	
	private int signalCode;
	
	Signal(int signalNumber) {
		this.signalCode = signalNumber;
	}
	
	public int get() {
		return this.signalCode;
	}
}
