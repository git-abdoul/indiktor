package com.fsi.monitoring.user.dao.impl;


import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.dao.AbstractDataSourceDAO;
import com.fsi.monitoring.user.AccessPerm;
import com.fsi.monitoring.user.Role;
import com.fsi.monitoring.user.User;
import com.fsi.monitoring.user.dao.UserDAO;

public class AbstractUserDAO 
extends AbstractDataSourceDAO 
implements UserDAO {
	protected DateTimeFormatter dateInFileName = DateTimeFormat.forPattern("yyyy-MM-dd_HH.mm.ss.SS");
	
	protected final static Logger LOG = Logger.getLogger(AbstractUserDAO.class);

		
	
	protected AbstractUserDAO(){}

	public User getUser(String login, String password) 
	throws PersistenceException {
		User user = null;
			
		Connection con = null;
	    PreparedStatement pStmt = null;
	    ResultSet rs = null;

	    String query = "SELECT usr.ID,FIRSTNAME,LASTNAME,EMAIL,PHONE1 FROM USER_LOGIN log, IKR_USER usr WHERE usr.ID=log.ID AND LOGIN=? and PASSWORD=?";
	    try {
	    
	    	con = dataSource.getConnection();

	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1, login);
	        pStmt.setString(2, password);

            rs = pStmt.executeQuery();
            
            if (rs.next()) {
            	user = getUser(rs);
            }
        } catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
        	closeResultSet(rs);
            closeStatement(pStmt);
            closeConnection(con);
        }
        
        if (user != null) {
        	List<Long> userRoleIds = getUserRoleIds(user.getId());
        	user.setRoleIds(userRoleIds);
        }
        
		return user;
	}
	
	protected User getUser(ResultSet rs)
	throws SQLException {
		User user = null;

        long id = rs.getLong("ID");
        String firstname = rs.getString("FIRSTNAME");
        String lastname = rs.getString("LASTNAME");
        String email = rs.getString("EMAIL");
        String phone1 = rs.getString("PHONE1");
        	
        user = new User(id,
        				firstname,
        				lastname,
        				email,
        				phone1);
		return user;
	}

	public Map<Long, AccessPerm> getAccessPerms() throws PersistenceException {
		Map<Long, AccessPerm> res = new HashMap<Long, AccessPerm>();
		
		String query = "SELECT * FROM ACCESS_PERM";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			rs = pStmt.executeQuery();

			while (rs.next()) {       	       
				long id = rs.getLong("ID");
				String name = rs.getString("NAME");
				String description = rs.getString("DESCRIPTION");
				AccessPerm accessPerm = new AccessPerm(id,name,description);
				
 	          	res.put(Long.valueOf(id),accessPerm);
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				

		return res;	
	}	

	public Map<Long, Role> getRoles()
	throws PersistenceException {
		Map<Long, Role> res = new HashMap<Long, Role>();
		
		String query = "SELECT * FROM ROLE";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			rs = pStmt.executeQuery();

			while (rs.next()) {       	       
				long id = rs.getLong("ID");
				String name = rs.getString("NAME");
				String description = rs.getString("DESCRIPTION");
				
				List<Long> accessPermIds = getRoleAccessPermIds(id);
				
				Role role = new Role(id,
								     name,
								     description,
								     accessPermIds);
				
 	          	res.put(Long.valueOf(id),role);
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				

		return res;	
	}
	
	protected List<Long> getRoleAccessPermIds(long roleId)
	throws PersistenceException {
		List<Long> res = new ArrayList<Long>();
		
		String query = "SELECT * FROM ROLE_ACCESS_PERM WHERE ROLE_ID=?";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setLong(1, roleId);
			rs = pStmt.executeQuery();

			while (rs.next()) {       	       
				long accessPermId = rs.getLong("ACCESS_PERM_ID");
 	          	res.add(Long.valueOf(accessPermId));
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				

		return res;			
	}
	
	protected List<Long> getUserRoleIds(long userId)
	throws PersistenceException {
		List<Long> res = new ArrayList<Long>();		
		String query = "SELECT * FROM USER_ROLE WHERE USER_ID=?";
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			pStmt = con.prepareStatement(query.toUpperCase());
			pStmt.setLong(1, userId);
			rs = pStmt.executeQuery();
			while (rs.next()) {       	       
				long accessPermId = rs.getLong("ROLE_ID");
 	          	res.add(Long.valueOf(accessPermId));
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				
		return res;			
	}	


	public long createRole(Role role) throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
		ResultSet rs = null;  
		
		long res = 0;
	    
	    String query = "INSERT INTO ROLE (NAME,DESCRIPTION) VALUES (?,?)";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,role.getName());
	        pStmt.setString(2,role.getDescription());
	        
            pStmt.executeUpdate();
            
            rs = pStmt.getGeneratedKeys();
          
            if (rs.next()) {
                 res = rs.getLong(1);           
            }
            
            insertRoleAccessPerms(res,role.getAccessPermIds());
            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
        
        return res;
	}
	
	protected void insertRoleAccessPerms(long roleId, List<Long> accessPermIds)
	throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null; 
	    
	    String query = "INSERT INTO ROLE_ACCESS_PERM (ROLE_ID,ACCESS_PERM_ID) VALUES (?,?)";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1,roleId);
	        
	        for (Long accessPermId : accessPermIds) {
	        	pStmt.setLong(2,accessPermId);
	        	pStmt.executeUpdate();
	        }
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }		
	}
	
	protected void insertUserRoles(long userId, List<Long> roleIds)
	throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null; 
	    
	    String query = "INSERT INTO USER_ROLE (USER_ID,ROLE_ID) VALUES (?,?)";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1,userId);
	        
	        for (Long roleId : roleIds) {
	        	pStmt.setLong(2,roleId);
	        	pStmt.executeUpdate();
	        }
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }		
	}	
	
	
	protected void deleteRoleAccessPerms(long roleId)
	throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null; 
	    
	    String query = "DELETE FROM ROLE_ACCESS_PERM WHERE ROLE_ID=?";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1,roleId);
	        pStmt.executeUpdate();
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }		
	}
	
	protected void deleteUserRoles(long userId)
	throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null; 
	    
	    String query = "DELETE FROM USER_ROLE WHERE USER_ID=?";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1,userId);
	        pStmt.executeUpdate();
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }		
	}	
	
	protected void deleteRoleUsers(long roleId)
	throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null; 
	    
	    String query = "DELETE FROM USER_ROLE WHERE ROLE_ID=?";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1,roleId);
	        pStmt.executeUpdate();
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }		
	}	


	public void updateRole(Role role) throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;

	    String query = "UPDATE ROLE SET NAME=?,DESCRIPTION=? WHERE ID=?";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,role.getName());
	        pStmt.setString(2,role.getDescription());
	        pStmt.setLong(3,role.getId());
            pStmt.executeUpdate();
            
            deleteRoleAccessPerms(role.getId());
            insertRoleAccessPerms(role.getId(),role.getAccessPermIds());
            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}
	
	public void updateUser(User user) throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;

	    String query = "UPDATE IKR_USER SET FIRSTNAME=?,LASTNAME=?,EMAIL=?,PHONE1=? WHERE ID=?";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,user.getFirstName());
	        pStmt.setString(2,user.getLastName());
	        pStmt.setString(3,user.getEmail());
	        pStmt.setString(4,user.getPhone1());
	        pStmt.setLong(5,user.getId());
            pStmt.executeUpdate();
            
            deleteUserRoles(user.getId());
            insertUserRoles(user.getId(),user.getRoleIds());
            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}	
	
	public void deleteUser(long userId)
	throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null; 
	    
	    String query = "DELETE FROM IKR_USER WHERE ID=?";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1,userId);
	        pStmt.executeUpdate();
	        
	        deleteUserRoles(userId);
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }		
	}
	
	public void deleteRole(long roleId)
	throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null; 
	    
	    String query = "DELETE FROM ROLE WHERE ID=?";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1,roleId);
	        pStmt.executeUpdate();
	        
	        deleteRoleAccessPerms(roleId);
	        deleteRoleUsers(roleId);
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }		
	}


	public Map<Long, User> getUsers() throws PersistenceException {
		Map<Long, User> res = new HashMap<Long, User>();
		
		String query = "SELECT usr.ID,FIRSTNAME,LASTNAME,EMAIL,PHONE1 FROM IKR_USER usr";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			rs = pStmt.executeQuery();

			while (rs.next()) {       	       
				User user = getUser(rs);
				List<Long> userRoleIds = getUserRoleIds(user.getId());
            	user.setRoleIds(userRoleIds);
 	          	res.put(Long.valueOf(user.getId()),user);
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				

		return res;	
	}

	public Map<Long, User> getUsers(Collection<Long> userIds)
	throws PersistenceException {
		Map<Long, User> res = new HashMap<Long, User>();
		
		String query = "SELECT usr.ID,FIRSTNAME,LASTNAME,EMAIL,PHONE1 FROM IKR_USER usr" 
					+ getWhereClause("usr.ID", userIds);

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			rs = pStmt.executeQuery();

			while (rs.next()) {       	       
				User user = getUser(rs);
				
 	          	res.put(Long.valueOf(user.getId()),user);
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}
		
		return res;
	}	
	

	public User getUser(long userId) throws PersistenceException {
		User user = null;
		
		Connection con = null;
	    PreparedStatement pStmt = null;
	    ResultSet rs = null;

	    String query = "SELECT usr.ID,FIRSTNAME,LASTNAME,EMAIL,PHONE1 FROM IKR_USER usr WHERE usr.ID=?";
	    try {
	    	con = dataSource.getConnection();

	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1, userId);

            rs = pStmt.executeQuery();
            
            if (rs.next()) {
            	user = getUser(rs);
            	List<Long> userRoleIds = getUserRoleIds(userId);
            	user.setRoleIds(userRoleIds);
            }
        } catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
        	closeResultSet(rs);
            closeStatement(pStmt);
            closeConnection(con);
        }			
		return user;
	}
	
	public String getPassword(long userId) throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
	    ResultSet rs = null;

	    String query = "SELECT login.ID,PASSWORD FROM USER_LOGIN login WHERE login.ID=?";
	    try {
	    	con = dataSource.getConnection();

	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1, userId);

            rs = pStmt.executeQuery();
            
            if (rs.next()) {
            	return rs.getString("PASSWORD");
            }
        } catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
        	closeResultSet(rs);
            closeStatement(pStmt);
            closeConnection(con);
        }			
		return "";
	}
	
	public String getLogin(long userId) throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
	    ResultSet rs = null;

	    String query = "SELECT login.ID,LOGIN FROM USER_LOGIN login WHERE login.ID=?";
	    try {
	    	con = dataSource.getConnection();

	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1, userId);

            rs = pStmt.executeQuery();
            
            if (rs.next()) {
            	return rs.getString("LOGIN");
            }
        } catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
        	closeResultSet(rs);
            closeStatement(pStmt);
            closeConnection(con);
        }			
		return "";
	}


	public void createUser(User user, long userId) throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
		ResultSet rs = null;  
	    
	    String query = "INSERT INTO IKR_USER (ID,FIRSTNAME,LASTNAME,EMAIL,PHONE1) VALUES (?,?,?,?,?)";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1, userId);
	        pStmt.setString(2,user.getFirstName());
	        pStmt.setString(3,user.getLastName());
	        pStmt.setString(4,user.getEmail());
	        pStmt.setString(5,user.getPhone1());
	        
            pStmt.executeUpdate();            
            insertUserRoles(userId,user.getRoleIds());
            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}


	public void createLogin(long userId, String login, String password)
	throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
	    
	    String query = "INSERT INTO USER_LOGIN (ID,LOGIN,PASSWORD) VALUES (?,?,?)";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1,userId);
	        pStmt.setString(2,login);
	        pStmt.setString(3,password);
            pStmt.executeUpdate();
            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
		
	}
	
	public void deleteUserLogin(long userId)
	throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null; 
	    
	    String query = "DELETE FROM USER_LOGIN WHERE ID=?";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setLong(1,userId);
	        pStmt.executeUpdate();
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }		
	}


	public void resetLogin(long userId, String login, String password)
			throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
	    
	    String query = "UPDATE USER_LOGIN SET LOGIN=?,PASSWORD=? WHERE ID=?";
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,login);
	        pStmt.setString(2,password);
	        pStmt.setLong(3,userId);
            pStmt.executeUpdate();
            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
	}
	
	public void updatePassword(long userId, String password)
	throws PersistenceException {
		Connection con = null;
	    PreparedStatement pStmt = null;
	    
	    String query = "UPDATE USER_LOGIN SET PASSWORD=? WHERE ID=?";       
	       
	    try {
	    	con = dataSource.getConnection();
	        	
	        pStmt = con.prepareStatement(query.toUpperCase());
	        pStmt.setString(1,password);
	        pStmt.setLong(2,userId);
            pStmt.executeUpdate();
            
	    }	catch(SQLException e) {
        	throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
        } finally {
            closeStatement(pStmt);
            closeConnection(con);
        }
		
	}


	public Map<Long, User> searchUsers(String searchQuery)
	throws PersistenceException {
		Map<Long, User> res = new HashMap<Long, User>();
		
		String query = "SELECT usr.ID,FIRSTNAME,LASTNAME,EMAIL,PHONE1 FROM IKR_USER usr " +
					   " WHERE (FIRSTNAME like ? OR LASTNAME like ?) AND usr.ID!=1 ORDER BY LASTNAME DESC";

		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			pStmt = con.prepareStatement(query.toUpperCase()); 
			pStmt.setString(1, "%" + searchQuery + "%");
			pStmt.setString(2, "%" + searchQuery + "%");
			rs = pStmt.executeQuery();

			while (rs.next()) {       	       
				User user = getUser(rs);
				
 	          	res.put(Long.valueOf(user.getId()),user);
			}
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage(), e, BaseException.EXCEPTION);
		} finally {
			closeResultSet(rs);
			closeStatement(pStmt);
			closeConnection(con);
		}				

		return res;	
		
		
	}
 }