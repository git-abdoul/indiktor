package com.fsi.monitoring.sec;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.fsi.monitoring.user.User;


public class SecurityFilter 
implements Filter {

	private SecurityBean securityBean;
	
	public void destroy() {
		// TODO Auto-generated method stub		
	}

	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest)request;
		
		String uri = req.getRequestURI();
		
//		System.out.println("FILTER : " + uri);

		if (!uri.contains("/sec/")) {
			HttpSession session = req.getSession();
			User user = (User)session.getAttribute("user");
			Collection<Long> accessPerms = (Collection<Long>)session.getAttribute("AP");	
			
			if (user == null) {
				RequestDispatcher dispatcher = request.getRequestDispatcher("/sec/login.jsf");
				dispatcher.forward(request, response);
			} else {
				// The user is logged
				if (isResourceAuthorized(user, accessPerms, uri)) {
					chain.doFilter(request,response);
				} else {
					RequestDispatcher dispatcher = request.getRequestDispatcher("/sec/accessDenied.jsf");
					dispatcher.forward(request, response);			
				}
			}
		} else {
			chain.doFilter(request,response);
		}
	}

	public boolean isResourceAuthorized(User user, 
										Collection<Long> accessPerms, 
										String uri) {		
		return true;
	}
	
	
	public void init(FilterConfig arg0) throws ServletException {}
}
