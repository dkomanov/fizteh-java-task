package ru.fizteh.fivt.students.altimin.chat;

import org.junit.Test;
import ru.fizteh.fivt.chat.MessageType;
import ru.fizteh.fivt.chat.MessageUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;

/**
 * User: altimin
 * Date: 12/8/12
 * Time: 12:01 AM
 */
public class MessageReaderTest {

    @Test
    public void testHello() throws IOException {
        String name = "name1 name2";
        assertEquals(
                new Message(MessageType.HELLO, name),
                new MessageReader(new ByteArrayInputStream(MessageUtils.hello(name))).read()
        );
    }

    @Test
    public void testMessage() throws IOException {
        String name = "name1 name2";
        String message = "Lorem ipsum";
        assertEquals(
                new Message(MessageType.MESSAGE, name, message),
                new MessageReader(new ByteArrayInputStream(MessageUtils.message(name, message))).read()
        );
    }

    @Test
    public void testBye() throws IOException {
        assertEquals(
                new Message(MessageType.BYE),
                new MessageReader(new ByteArrayInputStream(MessageUtils.bye())).read()
        );
    }

    @Test
    public void testError() throws IOException {
        String message = "Boss! Everything is bad!";
        assertEquals(
                new Message(MessageType.ERROR, message),
                new MessageReader(new ByteArrayInputStream(MessageUtils.error(message))).read()
        );
    }

    @Test
    public void testMessageToArray() throws IOException {
        Message message = new Message(MessageType.MESSAGE, "a", "b");
        Message hello = new Message(MessageType.HELLO, "a");
        Message bye = new Message(MessageType.BYE);
        Message error = new Message(MessageType.ERROR, "a");
        assertEquals(hello, new MessageReader(new ByteArrayInputStream(hello.toByteArray())).read());
        assertEquals(bye, new MessageReader(new ByteArrayInputStream(bye.toByteArray())).read());
        assertEquals(message, new MessageReader(new ByteArrayInputStream(message.toByteArray())).read());
        assertEquals(error, new MessageReader(new ByteArrayInputStream(error.toByteArray())).read());
    }
}

