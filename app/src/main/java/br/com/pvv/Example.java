package br.com.pvv;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Example {

    public static void main(String[] args) throws Exception {
        // --- 1. Seu código original de troca de chaves ---
        KeyPairGenerator kpgA = KeyPairGenerator.getInstance("DH");
        kpgA.initialize(2048);
        KeyPair keyPairA = kpgA.generateKeyPair();

        KeyPairGenerator kpgB = KeyPairGenerator.getInstance("DH");
        kpgB.initialize(2048);
        KeyPair keyPairB = kpgB.generateKeyPair();

        PublicKey publicKeyA = keyPairA.getPublic();
        PublicKey publicKeyB = keyPairB.getPublic();

        KeyAgreement kaA = KeyAgreement.getInstance("DH");
        kaA.init(keyPairA.getPrivate());
        kaA.doPhase(publicKeyB, true);
        byte[] secretA = kaA.generateSecret();

        KeyAgreement kaB = KeyAgreement.getInstance("DH");
        kaB.init(keyPairB.getPrivate());
        kaB.doPhase(publicKeyA, true);
        byte[] secretB = kaB.generateSecret();

        // --- 2. Derivação da Chave AES (usando SHA-256) ---
        // O segredo do DH tem tamanho variável, o SHA-256 garante uma chave de 32 bytes (256 bits) para o AES.
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] aesKeyBytesA = sha.digest(secretA);
        byte[] aesKeyBytesB = sha.digest(secretB);
        
        SecretKeySpec aesKeyA = new SecretKeySpec(aesKeyBytesA, "AES");
        SecretKeySpec aesKeyB = new SecretKeySpec(aesKeyBytesB, "AES");

        // --- 3. ALICE CRIPTOGRAFA A MENSAGEM ---
        String mensagemOriginal = "Olá Bob, este é um segredo enviado com segurança!";
        
        Cipher cipherEncrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipherEncrypt.init(Cipher.ENCRYPT_MODE, aesKeyA);
        
        // Capturamos o IV gerado automaticamente pelo Cipher (necessário para decriptografar)
        byte[] iv = cipherEncrypt.getIV(); 
        byte[] mensagemCifrada = cipherEncrypt.doFinal(mensagemOriginal.getBytes(StandardCharsets.UTF_8));
        
        System.out.println("Mensagem criptografada (em bytes): " + Arrays.toString(mensagemCifrada));

        // --- 4. BOB DECRIPITOGRAFA A MENSAGEM ---
        Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // Para decriptografar, precisamos usar a mesma chave (aesKeyB) e o mesmo IV enviado por Alice
        cipherDecrypt.init(Cipher.DECRYPT_MODE, aesKeyB, new IvParameterSpec(iv));
        
        byte[] bytesDecriptografados = cipherDecrypt.doFinal(mensagemCifrada);
        String mensagemDecriptografada = new String(bytesDecriptografados, StandardCharsets.UTF_8);

        System.out.println("Mensagem decriptografada por Bob: " + mensagemDecriptografada);
    }
}