package com.epam.finalproject.cinema.web.listener;

import javax.servlet.*;
import javax.servlet.annotation.*;


@WebListener
public class ContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		String path = ctx.getRealPath("/WEB-INF/log4j2.log");
		System.setProperty("logFile", path);
	}
}
