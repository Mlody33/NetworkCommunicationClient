package application;

public enum ClientStatuses {
	CLIENT_TITLE("CLIENT"),
	CONNECTION_THREAD("CONNECTION_WITH_SERVER_THREAD"),
	CLIENT_NOT_CONNECTED("Not connected"),
	CLIENT_CONNECTED("Connected"),
	CLIENT_AUTHORIZED("Authorized"),
	CLIENT_NOT_AUTHORIZED("Not authorized"),
	SET_APP_OFFLINE("Disconnect"),
	SET_APP_ONLINE("Connect"),
	OK("OK"),
	NONE("NONE"),
	NO("NO"),
	WRONG_AUTH_FIELD_VALUE("Type authorization code");
	
	private String statusText;
	
	private ClientStatuses(String text) {
		this.statusText = text;
	}
	
	public String get() {
		return statusText;
	}
}
