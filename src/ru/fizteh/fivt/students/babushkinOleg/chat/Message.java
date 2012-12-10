package ru.fizteh.fivt.students.babushkinOleg.chat;

public class Message {
    private final MessageType type;
    private final String name;
    private final String text;

    public Message(MessageType type, String name, String text) {
        this.type = type;
        this.name = name;
        this.text = text;
    }
    
    public MessageType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }
    
    public byte[] toByte() throws RuntimeException{
    	switch (getType()) {
	        case HELLO:
	            return MessageUtils.getMessageBytes(MessageType.HELLO, getName().getBytes());
	        case BYE:
	            return MessageUtils.getMessageBytes(MessageType.BYE);
	        case ERROR:
	            return MessageUtils.getMessageBytes(MessageType.ERROR, getText().getBytes());
	        case MESSAGE:
	            return MessageUtils.getMessageBytes(MessageType.MESSAGE, getName().getBytes(), getText().getBytes());
	        default:
	            throw new RuntimeException("Unknown message type.");
	    }
    }
}