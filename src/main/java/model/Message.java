package model;

import java.util.Objects;

public class Message {
	private final String source;
	private final String type;
	private final String payload;

	public Message(String source, String type, String payload) {
		this.source = source;
		this.type = type;
		this.payload = payload;
	}

	public String getSource() {
		return source;
	}

	public String getType() {
		return type;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Message))
			return false;
		Message message = (Message) o;
		return Objects.equals(getSource(), message.getSource()) && Objects.equals(getType(), message.getType());
	}

	@Override
	public final int hashCode() {
		return Objects.hash(getSource(), getType());
	}

	public String getMapKey() {
		return source + type;
	}

}
