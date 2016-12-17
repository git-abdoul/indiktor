/*
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2004, 2005, 2006], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */

package com.fsi.publisher.snmp;

import com.adventnet.snmp.beans.SnmpTarget;
import com.adventnet.snmp.snmp2.usm.USMUserEntry;
import com.adventnet.snmp.snmp2.usm.USMUserTable;
import com.adventnet.snmp.snmp2.usm.USMUtils;
import com.fsi.monitoring.snmp.SnmpConfig;

class SnmpSession_v3 extends SnmpSession_v2c {

	SnmpSession_v3() {
		version = SnmpTarget.VERSION3;
	}

	@Override
	public void initSpecificConfig(SnmpConfig config) {
		super.initSpecificConfig(config);
		
		String id = new String("");
        if(config.getEngineID() != null && config.getEngineID().length()>0){
            id = config.getEngineID();
            if(id.startsWith("0x") || id.startsWith("0X"))
                id = new String(gethexValue(config.getEngineID()));
        }

        if (config.getUser() != null && config.getUser().length()>0) {
            target.setPrincipal(config.getUser());        
	        
            if ((config.getAuthProtocol() != null && config.getAuthProtocol().length()>0) && (config.getAuthPassword() != null && config.getAuthPassword().length()>0)) {
	            if(config.getAuthProtocol().equals("SHA"))
	                target.setAuthProtocol(target.SHA_AUTH);
	            else if(config.getAuthProtocol().equals("MD5"))
	                target.setAuthProtocol(target.MD5_AUTH);
	            else
	                target.setAuthProtocol(target.NO_AUTH);  
	            
	            target.setAuthPassword(config.getAuthPassword());
	            
	            if (config.getPrivPassword() != null && config.getPrivPassword().length()>0){
	            	target.setPrivPassword(config.getPrivPassword());
		            if(config.getPrivProtocol() != null) {
	                    if(config.getPrivProtocol().equals("AES-128")) {
	                       target.setPrivProtocol(target.CFB_AES_128);
	                    }
	                    else if(config.getPrivProtocol().equals("AES-192")) {
	                       target.setPrivProtocol(target.CFB_AES_192);
	                    }
	                    else if(config.getPrivProtocol().equals("AES-256")) {
	                       target.setPrivProtocol(target.CFB_AES_256);
	                    }
	                    else if(config.getPrivProtocol().equals("3DES")) {
	                       target.setPrivProtocol(target.CBC_3DES);
	                    }
	                    else if(config.getPrivProtocol().equals("DES")) {
	                       target.setPrivProtocol(target.CBC_DES);
	                    }
		            }
	            }
	        }        
        }
        
        // Configure the USM entries on this entity.
        createUSMTable(target.getPrincipal().getBytes(),id.getBytes(),
                       target.getAuthProtocol(),target.getAuthPassword(),target.getPrivProtocol(),
                       target.getPrivPassword(), target);
	}

	private void createUSMTable(byte[] name, byte[] engineID,
								int authProtocol, String authPassword, int privProtocol,
								String privPassword, SnmpTarget target) {
		byte level = 0;

		USMUserTable uut = target.getUSMTable();
		USMUserEntry entry = new USMUserEntry(name, engineID);
		entry.setAuthProtocol(authProtocol);

		if ((authProtocol != USMUserEntry.NO_AUTH) && (authPassword != null)) {
			byte[] authKey = USMUtils.password_to_key(authProtocol,	authPassword.getBytes(), authPassword.getBytes().length,engineID);
			entry.setAuthKey(authKey);
			level = 1;

			if ((privPassword != null) && (privPassword.length() > 0)) {
				byte tempkey[] = null;

				if (privProtocol == USMUserEntry.CFB_AES_192
						|| privProtocol == USMUserEntry.CFB_AES_256
						|| privProtocol == USMUserEntry.CBC_3DES) {

					tempkey = USMUtils.password_to_key(authProtocol,
							privPassword.getBytes(), privPassword.length(),
							engineID, privProtocol);
				} else {
					tempkey = USMUtils.password_to_key(authProtocol,
							privPassword.getBytes(), privPassword.length(),
							engineID);
				}

				byte privKey[] = new byte[tempkey.length];
				System.arraycopy(tempkey, 0, privKey, 0, tempkey.length);
				entry.setPrivProtocol(privProtocol);
				entry.setPrivKey(privKey);
				level |= 2;
			}
		}
		entry.setSecurityLevel(level);
		uut.addEntry(entry);
		target.setSnmpEngineID(engineID);
	}

	private byte[] gethexValue(String value) {
		byte temp;
		byte[] Key = new byte[value.length() / 2 - 1];
		String ss, str;

		ss = value.substring(2);
		for (int i = 0; i < ss.length(); i += 2) {
			str = ss.substring(i, i + 2);
			temp = (byte) Integer.parseInt(str, 16);
			Key[i / 2] = temp;
		}
		return Key;
	}
}
