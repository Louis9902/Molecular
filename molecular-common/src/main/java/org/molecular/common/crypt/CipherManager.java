/*
 * This file ("CipherManager.java") is part of the molecular-project by Louis.
 * Copyright © 2017 Louis
 *
 * The molecular-project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The molecular-project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with molecular-project.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.molecular.common.crypt;

import org.molecular.api.Molecular;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Sources for this class: The Java Documentation of the following classes:<br>
 * <a href="https://docs.oracle.com/javase/7/docs/api/javax/crypto/KeyGenerator.html">KeyGenerator</a><br>
 * <a href="https://docs.oracle.com/javase/7/docs/api/java/security/KeyPairGenerator.html">KeyPairGenerator</a><br>
 * <a href="https://docs.oracle.com/javase/7/docs/api/java/security/spec/X509EncodedKeySpec.html">X509EncodedKeySpec</a><br>
 * <a href="http://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Cipher">Cipher Guides</a><br>
 * <p>
 * Inspired by <a href= "https://stackoverflow.com/a/6262776/6620258">Encryption with AES algorithm in Java</a>
 *
 * @author Louis
 */
public final class CipherManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("org.molecular.util");

    private CipherManager() {
    }

    public static byte[] generateVerifyToken(int length) {
        byte[] bytes = new byte[length];
        Molecular.RANDOM.nextBytes(bytes);
        return bytes;
    }

    public static SecretKey generateNewSharedKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("SecretKey creation failed because of missing algorithm", e);
        }
        return null;
    }

    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator pairGenerator = KeyPairGenerator.getInstance("RSA");
            pairGenerator.initialize(1024);
            return pairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("PublicKey creation failed because of missing algorithm", e);
        }
        return null;
    }

    public static PublicKey decodePublicKey(@Nonnull byte[] encodedKey) {
        try {
            EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(encodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(encodedKeySpec);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("PublicKey decoding failed because of missing algorithm", e);
        } catch (InvalidKeySpecException e) {
            LOGGER.error("PublicKey decoding failed because of invalid key spec", e);
        }
        return null;
    }

    public static SecretKey decryptSharedKey(@Nonnull PrivateKey key, @Nonnull byte[] secretKeyEncrypted) {
        return new SecretKeySpec(CipherManager.decryptData(key, secretKeyEncrypted), "AES");
    }

    public static byte[] encryptData(@Nonnull Key key, @Nonnull byte[] data) {
        return CipherManager.cipherOperation(Cipher.ENCRYPT_MODE, key, data);
    }

    public static byte[] decryptData(@Nonnull Key key, @Nonnull byte[] data) {
        return CipherManager.cipherOperation(Cipher.DECRYPT_MODE, key, data);
    }

    private static byte[] cipherOperation(int operationMode, @Nonnull Key key, @Nonnull byte[] data) {
        try {
            return CipherManager.createCipherInstance(operationMode, key.getAlgorithm(), key).doFinal(data);
        } catch (NullPointerException e) {
            LOGGER.error("Cipher data operation failed because of null Cipher", e);
        } catch (IllegalBlockSizeException e) {
            LOGGER.error("Cipher data operation failed because of incorrect cipher block length", e);
        } catch (BadPaddingException e) {
            LOGGER.error("Cipher data operation failed because of invalid padded input data", e);
        }
        return null;
    }

    private static Cipher createCipherInstance(int operationMode, @Nonnull String transformation, @Nonnull Key key) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(operationMode, key);
            return cipher;
        } catch (InvalidKeyException e) {
            LOGGER.error("Cipher creation failed because of invalid key", e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Cipher creation failed because of missing algorithm", e);
        } catch (NoSuchPaddingException e) {
            LOGGER.error("Cipher creation failed because of missing padding", e);
        }
        return null;
    }

    public static Cipher createNetworkCipherInstance(int operationMode, @Nonnull Key key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(operationMode, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        } catch (InvalidKeyException e) {
            LOGGER.error("Cipher creation failed because of invalid key", e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Cipher creation failed because of missing algorithm", e);
        } catch (NoSuchPaddingException e) {
            LOGGER.error("Cipher creation failed because of missing padding", e);
        } catch (InvalidAlgorithmParameterException e) {
            LOGGER.error("Cipher creation failed because of invalid algorithm parameter", e);
        }
        return null;
    }

}
