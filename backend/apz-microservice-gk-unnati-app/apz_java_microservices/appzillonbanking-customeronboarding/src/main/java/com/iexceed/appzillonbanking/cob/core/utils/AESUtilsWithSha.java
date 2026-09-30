package com.iexceed.appzillonbanking.cob.core.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@SuppressWarnings("restriction")
public class AESUtilsWithSha {
	
	private static final Logger logger = LogManager.getLogger(AESUtilsWithSha.class);
	//private static final String SHA_1 = "SHA-1";
	private static final String SHA_256 = "SHA-256";
	private static final String AES = "AES";

	private AESUtilsWithSha() {}

	public static String encryptString(String pkey, String poriginalstring) {
		String encyptedstring = "";
		try {
			byte[] key = (pkey).getBytes(StandardCharsets.UTF_8);

			MessageDigest sha = MessageDigest.getInstance(SHA_256);
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); // use only first 128 bit
			SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES);

			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

			byte[] encrypted = cipher.doFinal((poriginalstring).getBytes());

			encyptedstring = Base64.encodeBase64String(encrypted);

		} catch (InvalidKeyException|NoSuchAlgorithmException|BadPaddingException|IllegalBlockSizeException|NoSuchPaddingException e) {
			logger.error("Got exception while encrypting text : ",e);
		} 
		return encyptedstring;
	}	
}
