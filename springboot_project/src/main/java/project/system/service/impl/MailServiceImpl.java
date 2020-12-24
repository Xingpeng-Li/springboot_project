package project.system.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.system.service.MailService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 @author:徐必成
 @createDate:2020/11/17
 @update：
 @description: 包括邮件发送和邮件读取的一些相关Service
 */
@Service
public class MailServiceImpl implements MailService {
    public static final Logger logger = LoggerFactory.getLogger(MailService.class);
    @Autowired
    private JavaMailSender javaMailSender;

    private String from="";

    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage simpleMessage=new SimpleMailMessage();
        simpleMessage.setFrom(from);
        simpleMessage.setTo(to);
        simpleMessage.setSubject(subject);
        simpleMessage.setText(content);

        try{
            javaMailSender.send(simpleMessage);
            logger.info("简单邮件已发送");
        }catch (Exception e){
            logger.error("发送简单邮件时发生异常！", e);
        }
    }

    public void sendHtml(String to,String subject,String content) {
        MimeMessage message=javaMailSender.createMimeMessage();
       try {
           MimeMessageHelper helper=new MimeMessageHelper(message,true);
           helper.setFrom(from);
           helper.setTo(to);
           helper.setSubject(subject);
           helper.setText(content,true );

           javaMailSender.send(message);
           logger.info("html邮件发送成功");
       }catch (Exception e){
           logger.error("发送html邮件时发生异常！", e);
       }

    }

    @Override
    public void deleteFile(File... files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Override
    public void sendAttachmentsMail(String to, String subject, String content, MultipartFile multipartFile) throws IOException {
        MimeMessage message=javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper=new MimeMessageHelper(message,true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content,true);

            String fileName = multipartFile.getOriginalFilename();
            // 获取文件后缀
            String prefix=fileName.substring(fileName.lastIndexOf("."));
            File excelFile = File.createTempFile("temp", prefix);

            multipartFile.transferTo(excelFile);

            FileSystemResource fileSystemResource=new FileSystemResource(excelFile);
            helper.addAttachment(fileName, fileSystemResource);

            javaMailSender.send(message);
            logger.info("带附件的邮件已经发送。");

            //程序结束时，删除临时文件
            deleteFile(excelFile);

        } catch (MessagingException e) {
            logger.error("发送带附件的邮件时发生异常！", e);
        }
    }

    //文件拷贝，在用户进行附件下载的时候，可以把附件的InputStream传给用户进行下载
    @Override
    public void copy(InputStream is, OutputStream os) throws IOException {
        byte[] bytes = new byte[1024];
        int len;
        while ((len = is.read(bytes)) != -1) {
            os.write(bytes, 0, len);
        }
        if (os != null)
            os.close();
        is.close();
    }

    //邮件解析
    @Override
    public void getMailTextContent(Part part,StringBuffer content) throws MessagingException, IOException {
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/*") && !isContainTextAttach) {
//            String myMail = "";
//            InputStream is = part.getInputStream();
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(is));
//            String thisLine = reader.readLine();
//            while (thisLine != null) {
//                System.out.println(thisLine);
//                myMail = myMail + thisLine;
//                thisLine = reader.readLine();
//            }
//            part.writeTo(System.out);
            content.append(part.getContent().toString());
        } else if (part.isMimeType("message/rfc822")) {
            getMailTextContent((Part)part.getContent(),content);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent(bodyPart,content);
            }
        }
//        for (int idx = 0; idx < count; idx++) {
//            BodyPart bodyPart = multipart.getBodyPart(idx);
//            System.out.println(bodyPart.getContentType());
//            //通过邮件的不同类型来用不同方式解析
//            if (bodyPart.isMimeType("text/plain")) {
//                System.out.println("plain................." + bodyPart.getContent());
//            } else if (bodyPart.isMimeType("text/html")) {
//                System.out.println("html..................." + bodyPart.getContent());
//            } else if (bodyPart.isMimeType("multipart/*")) {
//                Multipart mpart = (Multipart) bodyPart.getContent();
//                parseMultipart(mpart);
//
//            } else if (bodyPart.isMimeType("application/octet-stream")) {
//                String disposition = bodyPart.getDisposition();
//                System.out.println(disposition);
//                if (disposition.equalsIgnoreCase(BodyPart.ATTACHMENT)) {
//                    String fileName = bodyPart.getFileName();
//                    InputStream is = bodyPart.getInputStream();
//                    //下载附件
//                    //copy(is, new FileOutputStream("D:\\" + fileName));
//                    this.copy(is, new FileOutputStream("D:\\" + fileName));
//                }
//            }
//        }

    }

    //获取发送者信息
    @Override
    public String getSenderFrom(Message message) throws MessagingException {
        InternetAddress[] address =(InternetAddress[]) message.getFrom();
        String from = address[0].getAddress();
        if (from == null){
            from = "";
            System.out.println("无法知道发送者。");
        }
        String personal = address[0].getPersonal();

        if(personal == null){
            personal = "";
            System.out.println("无法知道发送者的姓名.");
        }
        String fromAddr = null;
        if(personal != null || from != null){
            fromAddr = personal + "<" + from + ">";
            //System.out.println("发送者是：" + fromAddr);
        }else{
            System.out.println("无法获得发送者信息.");
        }
        return fromAddr;
    }

    @Override
    public Object getMailContent(Part part) throws MessagingException, IOException {
        StringBuffer bodyText = new StringBuffer(); // 存放邮件内容的StringBuffer对象
        String contentType = part.getContentType();
        // 获得邮件的MimeType类型
        int nameIndex = contentType.indexOf("name");

        boolean conName = false;

        if(nameIndex != -1){
            conName = true;
        }

        if(part.isMimeType("text/plain") && conName == false){
            // text/plain 类型
            bodyText.append((String) part.getContent());
        }else if(part.isMimeType("text/html") && conName == false){
            // text/html 类型
            bodyText.append((String) part.getContent());
        }else  if(part.isMimeType("multipart/*")) {
            // multipart/*
            Multipart multipart = (Multipart) part.getContent();
            int counts = multipart.getCount();
            for (int i = 0; i < counts; i++) {
                getMailContent(multipart.getBodyPart(i));
            }
        }else if(part.isMimeType("message/rfc822")){
            // message/rfc822
            getMailContent((Part) part.getContent());
        }else{

        }

        return bodyText;
    }

    //连接并获取收信箱
    @Override
    public Message[] getMessages(String userMail, String password) throws MessagingException {
        // 准备连接服务器的会话信息
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imap.host", "imap.qq.com");
        props.setProperty("mail.imap.port", "143");

        Session session = Session.getInstance(props);
        Store store = session.getStore("imap");
        store.connect(userMail, password);

        Folder folder = store.getFolder("INBOX");

        folder.open(Folder.READ_WRITE);
        int total = folder.getMessageCount();
        // 得到收件箱文件夹信息，获取邮件列表
        Message[] msgs = folder.getMessages();

        // 关闭资源
        folder.close(false);
        store.close();
        return msgs;
    }

    @Override
    public Store getImapStore() throws NoSuchProviderException {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imap.host", "imap.qq.com");
        props.setProperty("mail.imap.port", "143");

        Session session = Session.getInstance(props);
        //Store store = session.getStore("imap");
        Store store = session.getStore("imap");

        return store;
    }

    @Override
    public Session getSmtpSession() {
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");// 连接协议
        properties.put("mail.smtp.host", "smtp.qq.com");// 主机名
        properties.put("mail.smtp.port", 465);// 端口号
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");// 设置是否使用ssl安全连接 ---一般都使用
        properties.put("mail.debug", "true");// 设置是否显示debug信息 true 会在控制台显示相关信息
        // 得到回话对象
        Session session = Session.getInstance(properties);

        return session;
    }

    @Override
    public boolean isEmail(String email) {

        Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

        Matcher matcher = emailPattern.matcher(email);

        return matcher.find();

    }

}
