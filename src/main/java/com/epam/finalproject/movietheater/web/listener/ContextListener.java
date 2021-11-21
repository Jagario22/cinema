package com.epam.finalproject.movietheater.web.listener;

import com.epam.finalproject.movietheater.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.movietheater.web.constants.Locale;

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
