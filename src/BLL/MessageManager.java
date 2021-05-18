package BLL;

import BE.Message;
import BE.ScreenBit;
import BE.User;
import DAL.MessageDAL;

import java.util.List;

public class MessageManager {

    private MessageDAL messageDAL;

    public MessageManager(){
        messageDAL = new MessageDAL();
    }

    public void addMessage(User user, Message newMessage, List<ScreenBit> assignedScreenBits) {
        messageDAL.addMessage(user, newMessage, assignedScreenBits);
    }

    public List<Message> getUsersMessages(User user) {
        return messageDAL.getUsersMessages(user);
    }

    public void loadScreenBitsMessages(ScreenBit screen){
        messageDAL.loadScreenBitsMessages(screen);
    }
}
