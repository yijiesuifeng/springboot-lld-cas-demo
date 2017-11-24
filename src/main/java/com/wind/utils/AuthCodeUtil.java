/**
 * Project Name:shiro_not_sql
 * File Name:AuthCodeUtil.java
 * Package Name:cn.wind.util
 * Date:2015年11月25日上午10:57:31
 */

package com.wind.utils;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * @author lilandong
 * @desc 验证码
 */
public class AuthCodeUtil {
    private static Logger log = Logger.getLogger(AuthCodeUtil.class);

    public static void createImage(HttpServletResponse resp, HttpServletRequest request) throws IOException {
        int width = 60;
        int height = 30;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("宋体", Font.BOLD, 24));
        Random rand = new Random();
        HttpSession session = request.getSession();
        String codeStr = "";
        for (int i = 0; i < 4; i++) {
            int code = rand.nextInt(10);// 0~9֮
            g.setColor(new Color(rand.nextInt(256), rand.nextInt(256), rand
                    .nextInt(256)));
            g.drawString("" + code, i * 15, 10 + rand.nextInt(20));
            codeStr += String.valueOf(code);
        }
        session.setAttribute("validateCode", codeStr);
        log.info("验证码：" + codeStr);
        for (int i = 0; i < 12; i++) {
            g.setColor(new Color(rand.nextInt(256), rand.nextInt(256), rand
                    .nextInt(256)));
            /*g.drawLine(rand.nextInt(60), rand.nextInt(30), rand.nextInt(60),
                    rand.nextInt(30));*/
            g.drawOval(rand.nextInt(60), rand.nextInt(30), 0, 0);
        }
        g.dispose();
        // 设置这是图片,只有在web中用的时这样做
        resp.setContentType("image/jpeg");
        ImageIO.write(bi, "JPEG", resp.getOutputStream());
    }
}
