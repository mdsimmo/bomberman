package io.github.mdsimmo.bomberman.messaging;

public enum Messages {

	PERMISSION_DENIED("deny-permission", "You do not have the needed permission!");
	
	private final String path;
	private final String message;
	
	Messages(String path, String message) {
		this.path = path;
		this.message = message;
	}
	
	public Message getMessage(Language lang) {
		return new Message(lang.trans)
	}
}