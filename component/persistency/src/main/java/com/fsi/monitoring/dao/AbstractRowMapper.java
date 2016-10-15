package com.fsi.monitoring.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.persistence.PersistenceException;

public abstract class AbstractRowMapper<T> {
	
	public List<T> map(ResultSet rs) throws PersistenceException{
		if(rs==null)
			throw new PersistenceException("Null result set",BaseException.ERROR);
		List<T> list = new ArrayList<T>();
		try {
			while (rs.next()) {    
				list.add(mapRow(rs));
			}
			return list;
		} catch (SQLException e) {
			throw new PersistenceException("SQL exception encountered while iterating on the result set",e,BaseException.ERROR);
		}
		
	}
	
	public abstract T mapRow(ResultSet rs) throws SQLException;
}
