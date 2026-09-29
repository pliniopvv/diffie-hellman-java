package br.com.pvv;

import java.io.Serializable;

public class MensagemCifrada implements Serializable {
    public byte[] iv;
    public byte[] mensagem;
}