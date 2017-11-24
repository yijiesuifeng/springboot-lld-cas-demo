package com.wind.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @author Lilandong
 * @date 2017/11/14
 */
@WebListener
public class MyServletContextListener implements ServletContextListener {
    private final static Logger logger = LoggerFactory.getLogger(MyServletContextListener.class);
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.info("================ServletContex初始化,"+servletContextEvent.getServletContext().getServerInfo());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        logger.info("================ServletContex销毁");
    }
}
