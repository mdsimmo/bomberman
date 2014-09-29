package io.github.mdsimmo.bomberman.messaging;

public class Message {

	private final String text;
	private final Object[] objects;
	
	public Message (String text, Object...objects) {
		this.text = text;
		this.objects = objects;
	}
	
	private String format() {
		
	}
}
