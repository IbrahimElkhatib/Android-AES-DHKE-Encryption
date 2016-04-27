/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ibrahim.aefs;

import java.math.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.spec.DHParameterSpec;

public class DHKE {

    public static int keyLength = 256;
    public static BigInteger prKeyAlice;
    public static BigInteger p;

    public static BigInteger[] createDHKEKey() throws Exception {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("DiffieHellman");//key generator

    kpg.initialize(256);
    KeyPair kp = kpg.generateKeyPair();//key pair

        BigInteger y = ((javax.crypto.interfaces.DHPublicKey) kp.getPublic()).getY();//public key
        prKeyAlice = ((javax.crypto.interfaces.DHPrivateKey) kp.getPrivate()).getX(); // private
        DHParameterSpec params = ((javax.crypto.interfaces.DHPublicKey) kp.getPublic()).getParams();
        p = params.getP();
BigInteger q = params.getG();
System.out.println("y="+y );
System.out.println("p="+p );
System.out.println("g="+q );
//            pubKeyBob=createSpecificKey(p,q,y)[1];

                    return new BigInteger[]{prKeyAlice,y,p,q};

  }
    public static BigInteger createKeyfromPublic(BigInteger pubKeyBob) {
        BigInteger commonKeyAlice= pubKeyBob.modPow(prKeyAlice, p);
        BigInteger commonKeyAliceAES = commonKeyAlice.shiftRight(commonKeyAlice.bitLength() - keyLength);
        System.out.println("commonKeyAlice:"+commonKeyAlice);
        return commonKeyAliceAES;
    }


    public static BigInteger[] createSpecificKey(BigInteger p, BigInteger g, BigInteger pubKeyAlice) throws Exception {
        
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("DiffieHellman");

    DHParameterSpec param = new DHParameterSpec(p, g);
    kpg.initialize(param);
    KeyPair kp = kpg.generateKeyPair();

//    KeyFactory kfactory = KeyFactory.getInstance("DiffieHellman");
//
//    DHPublicKeySpec kspec = (DHPublicKeySpec) kfactory.getKeySpec(kp.getPublic(),
//        DHPublicKeySpec.class);

BigInteger prKeyBob= ((javax.crypto.interfaces.DHPrivateKey) kp.getPrivate()).getX();
BigInteger y = ((javax.crypto.interfaces.DHPublicKey) kp.getPublic()).getY();
BigInteger commonKeyBob= pubKeyAlice.modPow(prKeyBob,p);
System.out.println("commonKeyBob:"+commonKeyBob);

        BigInteger commonKeyBobAES = commonKeyBob.shiftRight(commonKeyBob.bitLength() - keyLength);
        System.out.println("commonKeyBobAES:"+commonKeyBobAES);

        return new BigInteger[]{commonKeyBobAES,y};

  }
    
}
