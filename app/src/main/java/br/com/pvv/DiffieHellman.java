package br.com.pvv;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DiffieHellman {

    private KeyPairGenerator generator;
    private KeyPair keyPair;
    private byte[] secret;

    public DiffieHellman(int keysize) throws NoSuchAlgorithmException {
        var instance = KeyPairGenerator.getInstance("DH");
        instance.initialize(keysize);
        keyPair = instance.generateKeyPair();
    }

    public PublicKey getPublic() {
        return keyPair.getPublic();
    }

    public byte[] generateSecret(PublicKey otherKey) throws NoSuchAlgorithmException, InvalidKeyException {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(keyPair.getPrivate());
        keyAgreement.doPhase(otherKey, true);
        secret = keyAgreement.generateSecret();
        return secret;
    }

    public MensagemCifrada cifrar(String mensagem) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] aesKeyBytes = sha.digest(secret);
        SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);

        MensagemCifrada mc = new MensagemCifrada();

        mc.iv = cipher.getIV();
        mc.mensagem = cipher.doFinal(mensagem.getBytes(StandardCharsets.UTF_8));
        return mc;
    }

    public String decifrar(MensagemCifrada mc) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] aesKeyBytes = sha.digest(secret);
        SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(mc.iv));

        byte[] bytesDecifrados = cipher.doFinal(mc.mensagem);
        String decifrada = new String(bytesDecifrados, StandardCharsets.UTF_8);

        return decifrada;
    }
}
