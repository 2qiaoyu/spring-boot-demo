package com.joham.springbootdemo.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


/**
 * 邮件发送
 *
 * @author joham
 */
@RestController
public class MailController {

    @Autowired
    private MailService mailService;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 发送普通邮件
     *
     * @return
     */
    @RequestMapping(value = "/sendMail1", method = RequestMethod.POST)
    public String sendMail1() {
        mailService.sendSimpleMail("694113006@qq.com", "test simple mail", " hello this is simple mail");
        return "1";
    }

    /**
     * 发送html邮件
     *
     * @return
     */
    @RequestMapping(value = "/sendMail2", method = RequestMethod.POST)
    public String sendMail2() {
        String content = "<html>\n" +
                "<body>\n" +
                "    <h3>hello world ! 这是一封Html邮件!</h3>\n" +
                "</body>\n" +
                "</html>";
        mailService.sendHtmlMail("694113006@qq.com", "test simple mail", content);
        return "1";
    }

    /**
     * 发送带附件的邮件
     *
     * @return
     */
    @RequestMapping(value = "/sendMail3", method = RequestMethod.POST)
    public String sendMail3() {
        String filePath = "/htdocs/logs/kstore_newthird_site_error.log";
        mailService.sendAttachmentsMail("694113006@qq.com", "主题：带附件的邮件", "有附件，请查收！", filePath);
        return "1";
    }

    /**
     * 发送带静态资源的邮件
     *
     * @return
     */
    @RequestMapping(value = "/sendMail4", method = RequestMethod.POST)
    public String sendMail4() {
        String rscId = "neo006";
        String content = "<html><body>这是有图片的邮件：<img src=\'cid:" + rscId + "\' ></body></html>";
        String imgPath = "/Users/joham/Documents/图片/00.jpg";
        mailService.sendInlineResourceMail("694113006@qq.com", "主题：这是有图片的邮件", content, imgPath, rscId);
        return "1";
    }

    /**
     * 发送模板邮件
     *
     * @return
     */
    @RequestMapping(value = "/sendMail5", method = RequestMethod.POST)
    public String sendMail5() {
        //创建邮件正文
        Context context = new Context();
        context.setVariable("id", "006");
        String emailContent = templateEngine.process("emailTemplate", context);
        mailService.sendHtmlMail("694113006@qq.com", "主题：这是模板邮件", emailContent);
        return "1";
    }
}
