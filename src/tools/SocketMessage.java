package tools;

import java.io.Serializable;

/**
 * 用于socket之间通信
 */
public class SocketMessage implements Serializable {
    private MessageTypeEnum messageTypeEnum; //当前数据的类型
    private Object object; //当前传输的对象

    public SocketMessage(MessageTypeEnum messageTypeEnum, Object object) {
        this.messageTypeEnum = messageTypeEnum;
        this.object = object;
    }

    public SocketMessage() {
    }

    public Object getObject() {
        return object;
    }

    public void setMessageTypeEnum(MessageTypeEnum messageTypeEnum) {
        this.messageTypeEnum = messageTypeEnum;
    }

    public MessageTypeEnum getMessageTypeEnum() {
        return messageTypeEnum;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
