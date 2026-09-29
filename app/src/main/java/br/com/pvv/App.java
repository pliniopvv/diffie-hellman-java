package br.com.pvv;

import java.security.PublicKey;

public class App {

    public static void main(String[] args) throws Exception {
        System.out.println(args[0]);
        String parameter = args[0];

        String mensagem = "Olá, pessoal, segue a mensagem cifrada!";

        if (parameter.equals("--server")) {
            var server = new Server(50000);
            PublicKey received_pb = server.receivePhase();
            var dh = new DiffieHellman(2048);
            var secret = dh.generateSecret(received_pb);
            var sender_pb = dh.getPublic();
            server.sendPhase(sender_pb);
            // finalizada troca de chaves, envio de mensagem:
            MensagemCifrada mc = server.receive();
            String mdecifrada = dh.decifrar(mc);
            System.out.println("Mensagem decifrada: " + mdecifrada);
        } else if (parameter.equals("--client")) {
            var client = new Client("127.0.0.1", 50000);
            var dh = new DiffieHellman(2048);
            var sender_pb = dh.getPublic();
            client.sendPhase(sender_pb);
            PublicKey received_pb = client.receivePhase();
            var secret = dh.generateSecret(received_pb);
            // finalizada troca de chaves, envio de mensagem:
            MensagemCifrada mc = dh.cifrar(mensagem);
            client.send(mc);
        }

    }

}
