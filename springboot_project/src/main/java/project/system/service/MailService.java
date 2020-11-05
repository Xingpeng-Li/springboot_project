package com.project.service;

import org.springframework.web.multipart.MultipartFile;

import javax.mail.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface MailService {
    public void sendSimpleMail(String to, String subject, String content);

    public void sendHtml(String to, String subject, String content);

    //程序结束时，删除临时文件
    public void deleteFile(File... files);

    public void sendAttachmentsMail(String to, String subject, String content, MultipartFile multipartFile) throws IOException;

    public  void copy(InputStream is, OutputStream os) throws IOException;

    public void getMailTextContent(Part part,StringBuffer content) throws MessagingException, IOException;

    //获得发件人的地址和姓名
    public String getSenderFrom(Message message) throws MessagingException;

    public Object getMailContent(Part part) throws MessagingException, IOException;

    public Message[] getMessages(String userMail, String password) throws MessagingException;

    public Store getImapStore() throws NoSuchProviderException;

    public Session getSmtpSession();

    public boolean isEmail(String email);
}
