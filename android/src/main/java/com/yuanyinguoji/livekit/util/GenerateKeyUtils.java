package com.yuanyinguoji.livekit.util;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import android.util.Log;

public class GenerateKeyUtils{
	
	public static String Algorithm="RSA";//RSA、RSA/ECB/PKCS1Padding
	//RSA key位数
	private static final int KEY_SIZE = 512;
	
	public String[] generateRSAKeyPair(){
		String[] keypair=new String[2];
		try { 
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(Algorithm);   
            //密钥位数    
            keyPairGen.initialize(KEY_SIZE);
            //密钥对    
            KeyPair keyPair = keyPairGen.generateKeyPair();    
     
            // 公钥    
            PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();    
            // 私钥    
            PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();    
            
            String publicKeyString = getKeyString(publicKey); 
            String privateKeyString = getKeyString(privateKey);  
              
            
            keypair[0]=publicKeyString;
            keypair[1]=privateKeyString;
             
        }catch(Exception e){
            System.err.println("Exception:"+e.getMessage()); 
        }   
        return keypair;
	}
	
	 public String getKeyString(Key key) throws Exception {    
         byte[] keyBytes = key.getEncoded();    
         String s =Base64.encodeBase64String(keyBytes);   
         return s;    
   }    
	
}
