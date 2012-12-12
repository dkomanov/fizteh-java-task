package ru.fizteh.fivt.students.babushkinOleg.chat;

import ru.fizteh.fivt.students.babushkinOleg.chat.Message;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.IOException;
import java.io.InputStream;

/**
 * Contains methods to create a message, that could be transmitted via network.
 * It's just for protocol explanation
 */

public final class MessageUtils {
    private MessageUtils() {
    }
    
    public static Message getMessage(InputStream in) throws Exception {
    	MessageType type = null;
        byte[] name = null;
        byte[] message = null;
    	switch ((byte)in.read()){
    		case 1 : 
    			type = MessageType.HELLO;
    			break;
    		case 2 : 
    			type = MessageType.MESSAGE;
    			break;
    		case 3 : 
    			type = MessageType.BYE;
    			break;
    		case 127 : 
    			type = MessageType.ERROR;
    			break;
    	}
    	int messangesNumber = in.read();
    	if (type == null)
    		throw new RuntimeException("Incorrect message specification");
    	if (type == MessageType.MESSAGE || type == MessageType.HELLO){
	    	ByteBuffer buffer = ByteBuffer.allocate(4);
	    	for (int i = 0; i < 4; ++i) {
	            int tmp;
	            if ((tmp = in.read()) < 0) {
	                throw new RuntimeException("Cannot read message");
	            }
	            buffer.put((byte) tmp);
	        }
	    	buffer.position(0);
	    	int length = buffer.getInt();
	        if (length > 100 || length <= 0) {
	            throw new RuntimeException("Incorrect length");
	        }
	        name = new byte[length];
	        for (int i = 0; i < length; ++i) {
	            int tmp;
	            if ((tmp = in.read()) < 0) {
	                throw new RuntimeException("Cannot read message");
	            }
	            name[i] = (byte) tmp;
	        }    
    	}
        if (type == MessageType.MESSAGE || type == MessageType.ERROR){
        	ByteBuffer buffer = ByteBuffer.allocate(4);
		    for (int i = 0; i < 4; ++i) {
		        int tmp;
		        if ((tmp = in.read()) < 0) {
		            throw new RuntimeException("Cannot read message");
		        }
		        buffer.put((byte) tmp);
		    }
		    buffer.position(0);
		    int length = buffer.getInt();
		    if (length > 1000 || length <= 0) {
		        throw new RuntimeException("Incorrect length");
		    }
		    message = new byte[length];
		    for (int i = 0; i < length; ++i) {
		        int tmp;
		        if ((tmp = in.read()) < 0) {
		            throw new RuntimeException("Cannot read message");
		        }
		        message[i] = (byte) tmp;
		    }
        }
    	return new Message(type, 
    			name == null? "" : new String(name), 
    			message == null? "" : new String(message));
    }

    public static byte[] hello(String name) {
        return getMessageBytes(MessageType.HELLO, name.getBytes());
    }

    public static byte[] bye() {
        return getMessageBytes(MessageType.BYE);
    }

    public static byte[] error(String message) {
        return getMessageBytes(MessageType.ERROR, message.getBytes());
    }

    public static byte[] getMessageBytes(MessageType type, byte[]... messages) {
        int messagesLength = 0;
        for (byte[] bytes : messages) {
            messagesLength += 4 + bytes.length;
        }
        // message-type (1 byte) + messages count (1 byte)
        // + messages counts * (message-length (4 byte) + message body)
        ByteBuffer buffer = ByteBuffer.allocate(1 + 1 + messagesLength)
                .order(ByteOrder.BIG_ENDIAN);
        buffer.put(type.getId());
        buffer.put((byte) messages.length);
        for (byte[] bytes : messages) {
            buffer.putInt(bytes.length).put(bytes);
        }
        return buffer.array();
    }
}