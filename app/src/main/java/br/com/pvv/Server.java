package br.com.pvv;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;

public class Server {

    private ServerSocket ss;
    private Socket client;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public Server(int port) throws IOException {
        ss = new ServerSocket(port);
        System.out.println("Servidor esperando na porta " + port);
        client = ss.accept();
        oos = new ObjectOutputStream(client.getOutputStream());
        ois = new ObjectInputStream(client.getInputStream());
        System.out.println("Cliente conectado!");
    }

    public void sendPhase(PublicKey msg) throws IOException {
        // System.out.println("Enviando PublicKey: " + msg.toString());
        oos.writeObject(msg);
        oos.flush();
    }

    public PublicKey receivePhase() throws IOException, ClassNotFoundException {
        PublicKey pb = (PublicKey) ois.readObject();
        // System.out.println("PublicKey recebida: " + pb);
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
