package br.com.pvv;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;

public class Client {

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public Client(String ip, int port) throws IOException {
        System.out.println("Cliente conectando no ip e porta:" + ip + ":" + port);
        this.socket = new Socket(ip, port);
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
    }

    public void sendPhase(PublicKey msg) throws IOException {
        // System.out.println("Cliente Enviando: " + msg.toString());
        oos.writeObject(msg);
        oos.flush();
    }

    public PublicKey receivePhase() throws IOException, ClassNotFoundException {
        PublicKey pb = (PublicKey) ois.readObject();
        // System.out.println("PublicKey Recebida: " + pb.toString());
        return pb;
    }

    public void send(MensagemCifrada msg) throws IOException {
        // System.out.println("Cliente Enviando: " + msg.mensagem);
        oos.writeObject(msg);
        oos.flush();
    }

    public MensagemCifrada receive() throws IOException, ClassNotFoundException {
        MensagemCifrada pb = (MensagemCifrada) ois.readObject();
        // System.out.println("PublicKey Recebida: " + pb.toString());
        return pb;
    }
}
