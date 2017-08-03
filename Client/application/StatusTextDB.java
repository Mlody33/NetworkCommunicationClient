package application;

public enum StatusTextDB {
	TITLE_OF_APP("CLIENT"),
	CLIENT_NOT_CONNECTED("Not connected"),
	CLIENT_CONNECTED("Connected"),
	CLIENT_AUTHORIZED("Authorized"),
	CLIENT_NOT_AUTHORIZED("Not authorized"),
	SET_APP_OFFLINE("Disconnect"),
	SET_APP_ONLINE("Connect"),
	OK("OK"),
	NONE("NONE"),
	NOT("NOT");
	
	private String statusText;
	
	private StatusTextDB(String text) {
		this.statusText = text;
	}
	
	public String get() {
		return statusText;
	}
}
