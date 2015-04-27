/*
 * MessageHandler.java
 * 
 * Created on Apr 27, 2015
 */

package eionet.acladmin.servlets;

import javax.servlet.http.HttpServletRequest;

/**
 * Handler for attaching messages in pages : http://www.eionet.europa.eu/software/design/usermessages
 */
public class MessageHandler {
    
    public static String SYSTEM_MESSAGE_KEY = "message-system";
    public static String ADVICE_MESSAGE_KEY = "message-advice";
    
    public static String SYSTEM_MESSAGE_CLASS = "system-msg";
    public static String ADVICE_MESSAGE_CLASS = "advice-msg";
    
    public static String SAVE_ACL_SUCCESS_MESSAGE = "ACL saved successfully";
    public static String SAVE_GROUPS_SUCCESS_MESSAGE = "Groups saved successfully";
    
    public void attachSystemMessage(HttpServletRequest req, String messageText){
         Message message = new Message(messageText,SYSTEM_MESSAGE_CLASS);
         req.setAttribute(SYSTEM_MESSAGE_KEY, message);
    }
    
    public void attachAdviceMessage(HttpServletRequest req, String messageText){
         Message message = new Message(messageText,ADVICE_MESSAGE_CLASS);
         req.setAttribute(ADVICE_MESSAGE_KEY, message);
    }
    
    public class Message{
        private final String messageText;
        private final String classText;
               
        public Message(String messageText, String classText){
            this.messageText = messageText;
            this.classText = classText;
        }
        
        public String getMessageText(){
            return this.messageText;
        }
        
        public String getClassText(){
            return this.classText;
        }
    }
}
