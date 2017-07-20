package application;

public enum StatusTextDB {
	CLIENT_NOT_CONNECTED("Not connected"),
	CLIENT_CONNECTED("Connected"),
	CLIENT_AUTHORIZED("Authorized"),
	SET_APP_OFF("Disconnect"),
	SET_APP_ON("Connect");
	
	private String statusText;
	
	private StatusTextDB(String text) {
		this.statusText = text;
	}
	
	public String get() {
		return statusText;
	}
}
