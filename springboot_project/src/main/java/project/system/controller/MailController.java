package project.system.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.system.common.utils.RequestUtil;
import project.system.domain.User;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.mapper.UserMapper;
import project.system.response.CommonReturnType;
import project.system.response.response.TokenInfoResponse;
import project.system.service.LoginService;
import project.system.service.MailService;
import project.system.service.UserService;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

/*
 @author:徐必成
 @createDate:2020/12/8
 @update：
 @description: 包括邮件发送和邮件读取
 */
@RestController
public class MailController extends BaseController {
    private final Logger logger=LoggerFactory.getLogger(this.getClass());
    @Resource
    private MailService mailService;
    @Resource
    private LoginService loginService;
    @Resource
    private UserService userService;

    static String from = "";
    static String authorizationCode = "";

    //发送邮件
    @PostMapping(value = "/sendAttachmentsMail")
    public CommonReturnType sendAttachmentsMail(HttpServletRequest request, @RequestParam(value = "multipartFile", required = false)MultipartFile multipartFile) throws IOException, MessagingException {
        String to = request.getParameter("to");
        String subject = request.getParameter("subject");
        String content = request.getParameter("content");
        if(!mailService.isEmail(to)){
            throw new BusinessException(EmBusinessError.MAIL_FROMATE_ERROR);
        }
        if(from.equals("") || authorizationCode.equals("")){
            throw new BusinessException(EmBusinessError.UNLOGIN,"fail");
        }
        // 得到会话对象
        Session session = mailService.getSmtpSession();

        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));//一个收件人
        message.setSubject(subject);

        if(multipartFile == null){
            // 设置邮件内容
            Multipart mp = new MimeMultipart("related");
            BodyPart bodyPart = new MimeBodyPart();
            //设置邮件格式
            bodyPart.setDataHandler(new DataHandler(content,"text/html;charset=UTF-8"));

            mp.addBodyPart(bodyPart);
            message.setContent(mp);// 设置邮件内容对象
            // 得到邮差对象
            Transport transport = session.getTransport();
            try{
                // 连接自己的邮箱账户
                transport.connect(from, authorizationCode);// 密码为QQ邮箱开通的stmp服务后得到的客户端授权码
                // 发送邮件
                transport.sendMessage(message, message.getAllRecipients());
            }catch (Exception e){
                throw new BusinessException(EmBusinessError.MAIL_SEND_ERROR,"fail");
            }finally {
                transport.close();
            }
        }else {
            //添加附件
            String fileName = multipartFile.getOriginalFilename();
            String prefix=fileName.substring(fileName.lastIndexOf("."));
            File excelFile = File.createTempFile("temp", prefix);
            multipartFile.transferTo(excelFile);

            FileSystemResource fileSystemResource=new FileSystemResource(excelFile);


            String path = String.valueOf(fileSystemResource);
            path = path.substring(6,path.length()-1);
            path = path.replaceAll("\\\\", "\\\\\\\\");
            //System.out.println(path);
            path = path.replaceAll("\\\\", "/");
            File usFile = new File(path);

            // 添加附件的内容
            Multipart multipart = new MimeMultipart();
            BodyPart attachmentBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(usFile);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            //MimeUtility.encodeWord可以避免文件名乱码
            attachmentBodyPart.setFileName(MimeUtility.encodeWord(usFile.getName()));
            multipart.addBodyPart(attachmentBodyPart);
            //设置邮件格式
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(content, "text/html;charset=UTF-8");
            multipart.addBodyPart(contentPart);
            message.setText("");

            message.setContent(multipart);
            //保存并且生成邮件对象
            message.saveChanges();
            Transport transport = session.getTransport();
            try{
                transport.connect(from, authorizationCode);
                // 发送邮件
                transport.sendMessage(message, message.getAllRecipients());
            }catch (Exception e){
                throw new BusinessException(EmBusinessError.MAIL_SEND_ERROR,"fail");
            }finally {
                transport.close();
                mailService.deleteFile(excelFile);
            }
        }





        return CommonReturnType.create(null);
    }

    //读取QQ邮箱内容,网易邮箱不可授权读取
    //获取邮箱中各种类型邮件数量
    @GetMapping(value = "/mail/receive")
    public CommonReturnType receive() throws Exception {
        Map<String,Object> map = new HashMap<>();

        if(from.equals("") || authorizationCode.equals("")){
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
        // 创建IMAP协议的Store对象
        Store store = mailService.getImapStore();

        // 连接邮件服务器
        //store.connect("bicheng_xu@163.com", "SJVMRZSLJHSSWWHN");
        store.connect(from, authorizationCode);
        // 获得收件箱
        Folder folder = store.getFolder("INBOX");
        try{
            // 以读写模式打开收件箱
            folder.open(Folder.READ_WRITE);

            // 获得收件箱的邮件列表
            Message[] messages = folder.getMessages();

            // 返回不同状态的邮件数量
            map.put("收件箱总邮件数量",messages.length);
            map.put("未读邮件数量",folder.getUnreadMessageCount());
            map.put("已删除邮件数量",folder.getDeletedMessageCount());
            map.put("新邮件数量",folder.getNewMessageCount());
        }catch (Exception e){
            throw new BusinessException(EmBusinessError.MAIL_RECEIVE_ERROR);
        }finally {
            // 关闭资源
            folder.close(false);
            store.close();
        }
        //返回邮件数量信息
        return CommonReturnType.create(map);
    }

    //分别读取QQ邮箱内容
    //读取所有邮箱内容
    @GetMapping(value = "mail/all")
    public CommonReturnType getAllMailContent() throws MessagingException, IOException {
        List<Map<String,Object>> mapList = new ArrayList<>();

        if(from.equals("") || authorizationCode.equals("")){
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
        Store store = mailService.getImapStore();
        store.connect(from, authorizationCode);

        Folder folder = store.getFolder("INBOX");

        folder.open(Folder.READ_WRITE);
        int total = folder.getMessageCount();

        StringBuffer content;

        try{
            // 得到收件箱文件夹信息，获取邮件列表
            Message[] msgs = folder.getMessages();

            for (int i = total-1; i > total-5; i--) {
                Message a = msgs[i];
                //   获取邮箱邮件名字及时间
                Map<String,Object> map = new HashMap<>();
                map.put("id",i);
                map.put("邮件主题",a.getSubject());
                map.put("接受时间",a.getReceivedDate().toLocaleString());
                map.put("发送方", mailService.getSenderFrom(a));
                mapList.add(map);
            }
        }catch (Exception e){
            throw new BusinessException(EmBusinessError.MAIL_RECEIVE_ERROR);
        }
        finally {
            // 关闭资源
            folder.close(false);
            store.close();
        }
        //返回邮件数量信息
        return CommonReturnType.create(mapList);
    }

    //根据传过来的id获取邮件内容
    @PostMapping(value = "mail/content")
    public CommonReturnType mailContent(HttpServletRequest request) throws MessagingException {
        String id = request.getParameter("id");

        if(from.equals("") || authorizationCode.equals("")){
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }

        Store store = mailService.getImapStore();
        store.connect(from, authorizationCode);

        Folder folder = store.getFolder("INBOX");

        folder.open(Folder.READ_WRITE);
        int total = folder.getMessageCount();

        StringBuffer content;

        Map<String,Object> map = new HashMap<>();
        try{
            // 得到收件箱文件夹信息，获取邮件列表
            Message[] msgs = folder.getMessages();

            int i = Integer.parseInt(id);
            Message a = msgs[i];
            content = new StringBuffer();
            mailService.getMailTextContent(a,content);
            String mailcontent = content.substring(0);
            if(mailcontent.contains("<html>")){
                mailcontent = mailcontent.substring(mailcontent.indexOf("<html>"));
            }else if(mailcontent.contains("<!doctype html>")){
                mailcontent = mailcontent.substring(mailcontent.indexOf("<!doctype html>"));
            }else if(mailcontent.contains("<!DOCTYPE html>")){
                mailcontent = mailcontent.substring(mailcontent.indexOf("<!DOCTYPE html>"));
            }else{
                mailcontent = "<pre>" + mailcontent + "</pre>";
            }
            map.put("content",mailcontent);

        }catch (Exception e){
            throw new BusinessException(EmBusinessError.MAIL_RECEIVE_ERROR);
        }
        finally {
            // 关闭资源
            folder.close(false);
            store.close();
        }

        return CommonReturnType.create(map);
    }

    //登录邮箱
    @PostMapping(value = "mail/login")
    public CommonReturnType mailLogin(HttpServletRequest request) throws MessagingException {
        String userMail = request.getParameter("userMail");
        String mailPassword = request.getParameter("mailPassword");

        int userId;
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin()) {
                userId = Integer.parseInt(tokenInfoResponse.getUserId());
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
        User user = userService.selectByPrimaryKey(userId);
        if(!user.getUserEmail().equals(userMail) || !user.getEmailAuthorizationCode().equals(mailPassword)){

            Session session = mailService.getSmtpSession();

            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(userMail));

            message.setRecipient(Message.RecipientType.TO, new InternetAddress(userMail));//一个收件人
            message.setSubject("邮箱绑定。");
            message.setText("您的邮箱成功与办公系统绑定！");
            Transport transport = session.getTransport();
            try{
                transport.connect(userMail, mailPassword);// 密码为QQ邮箱开通的stmp服务后得到的客户端授权码
                transport.sendMessage(message, message.getAllRecipients());
            }catch (Exception e){
                throw new BusinessException(EmBusinessError.MAIL_BINDING_ERROR);
            }finally {
                transport.close();
            }
            user.setUserEmail(userMail);
            user.setEmailAuthorizationCode(mailPassword);

            userService.updateByPrimaryKeySelective(user);
            //保存用户名和授权码
        }
        //保存用户名和授权码
        from = userMail;
        authorizationCode = mailPassword;
        return CommonReturnType.create(null);
    }

    //如果用户有登录历史，不用填写账号和授权码
    @GetMapping(value = "mail/already")
    public CommonReturnType mailAlready(HttpServletRequest request){
        int userId;
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin()) {
                userId = Integer.parseInt(tokenInfoResponse.getUserId());
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
        User user = userService.selectByPrimaryKey(userId);
        Map<String,String> map = new HashMap<>();
        map.put("userMail","");
        map.put("mailPassword","");
        if(user.getUserEmail()!=null && user.getEmailAuthorizationCode()!=null){
            map.put("userMail",user.getUserEmail());
            map.put("mailPassword",user.getEmailAuthorizationCode());
        }
        return CommonReturnType.create(map);
    }

}
