package com.fsi.fwk.encryption;

import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.encryption.DESException;

public class DES {

	private SecretKey key;
	private KeySpec keySpec;
	private Cipher cipher;
	AlgorithmParameterSpec paramSpec;

	private byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
			(byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03 };

	private int iterationCount = 19;

	public DES(String passPhrase) {

		try {
			keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt,
					iterationCount);
			key = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
					.generateSecret(keySpec);
			cipher = Cipher.getInstance(key.getAlgorithm());
			paramSpec = new PBEParameterSpec(salt, iterationCount);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}

	}

	public String encrypt(String password) throws DESException {
		if(password == null){
			
		}
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			byte[] utf8 = password.getBytes("UTF-8");
			byte[] enc = cipher.doFinal(utf8);
			return new sun.misc.BASE64Encoder().encode(enc);
		} catch (Exception e) {
			throw new DESException(e.getMessage(), e, BaseException.EXCEPTION);
		}

	}

	public String decrypt(String password) throws DESException {
		try {
			cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
			byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(password);
			byte[] utf8 = cipher.doFinal(dec);
			String result = new String(utf8, "UTF-8");
			return result;

		} catch (Exception e) {
			throw new DESException(e.getMessage(), e, BaseException.EXCEPTION);
		}
	}
}
