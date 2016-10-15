package com.fsi.monitoring.sec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.icefaces.application.PushRenderer;

import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.user.Role;
import com.fsi.monitoring.user.User;
import com.fsi.monitoring.user.UserPM;
import com.fsi.monitoring.user.exception.LoginFailedException;
import com.fsi.monitoring.util.FacesUtils;

public class SecurityBean {
	
	protected final static Logger logger = Logger.getLogger(SecurityBean.class);	
	
	private User user;
	
	private boolean loginError = false;

	private String login;
	private String password;
	
	private Collection<Long> accessPerms = new HashSet<Long>();
	
//	private PersistentFacesState state;
//	private RenderManager renderManager;

	public SecurityBean() {
//		state = PersistentFacesState.getInstance();
	}
	
	public User getUser() {
		return user;
	}
	
	public boolean isLogged() {
		return user != null;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}	
	
	public String logout() {
		HttpSession session = FacesUtils.getHttpSession(false);
//		SessionRenderer.removeCurrentSession(SessionRenderer.ALL_SESSIONS);
		PushRenderer.removeCurrentSession(PushRenderer.ALL_SESSIONS);
		session.invalidate();
		
		return "login";
	}
	
	public String validate() {
		
		String action = null;
		loginError = false;
		
		UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());
		
		User user = null;
		try {
			user = userPM.login(login, password);
			
			if (user != null) {			
				HttpSession session = FacesUtils.getHttpSession(false);
				session.setAttribute("user", user);
	
				this.user = user;
				this.password = null;
	
				Map<Long, Role> roles = userPM.getRoles();
				
				for(long roleId : user.getRoleIds()) {
					Role role = roles.get(roleId);
					accessPerms.addAll(role.getAccessPermIds());
				}
				
//				OnDemandRenderer renderer = renderManager.getOnDemandRenderer("all");
//				renderer.add(this);
				
				PushRenderer.addCurrentSession(PushRenderer.ALL_SESSIONS);
				
				loginError = false;				
				action =  "home";
			} else {				
				loginError =  true;
				action = "login";
			}
		} catch(LoginFailedException lfexc) {
			loginError =  true;
			action = "login";
		}	catch (Exception exc) {
			System.out.println(exc);
		}
		return action;
	}
	
//	private void generateLoginErrorMessage() {
//		ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//		error.init();
//		error.setRendered(loginError);
//		error.setModal(true);
//		error.setType(ErrorMessageBean.WARNING);
//		error.addMessage("Login Error");
//	}

	public boolean isAuthorized16() {
		return isAuthorized(16);
	}
	
	public boolean isAuthorized15() {
		return isAuthorized(15);
	}
	
	public boolean isAdmin() {
		if (user == null) {
			return false;
		} else if (user.getId() == 1) {
			// admin
			return true;
		}
		
		return false;
	}
	
	public boolean isAuthorized1516() {
		return (isAuthorized(15) || isAuthorized(16));
	}	
	
	public boolean isAuthorized(long accessPermId) {
		if (user == null) {
			return false;
		} else if (user.getId() == 1) {
			// admin
			return true;
		}
		
		return accessPerms.contains(accessPermId);
	}

//	public PersistentFacesState getState() {
//		return state;
//	}
//
//	public void renderingException(RenderingException arg0) {
//		logger.error("Error when rendering RealTimeManager", arg0);
//	}
//	
//	public void setRenderManager(RenderManager renderManager) {
//		this.renderManager = renderManager;
//	}
//
//	public void dispose() {
//		OnDemandRenderer renderer = renderManager.getOnDemandRenderer("all");
//		renderer.remove(this);
//	}
	
	public Collection<String> getBidon() {
		ArrayList<String> res = new ArrayList<String>();
		res.add("111");
		res.add("3333");
		return res;
	}

	public boolean isLoginError() {
		return loginError;
	}
}
