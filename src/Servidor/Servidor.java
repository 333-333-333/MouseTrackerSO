package Servidor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Servidor {

    private byte[] MsjRecibidoBytes;
    private DatagramSocket Socket;
    private DatagramPacket PaqueteRecibido;
    private String MsjRecibido;

    public void ejecutar() {
        try {
            inicializarDatos();
            while (true) {
                recibirPaquete();
                procesarMensaje();
            }
        } catch (Exception e) {
            System.err.println(e.getClass());
            System.err.println(e.getMessage());
        }
    }

    private void inicializarDatos() throws SocketException {
        this.MsjRecibidoBytes = new byte[256];
        this.Socket = new DatagramSocket(3333);
        this.PaqueteRecibido = new DatagramPacket(this.MsjRecibidoBytes,256);
    }

    private void recibirPaquete() throws IOException {
        this.Socket.receive(this.PaqueteRecibido);
    }

    private void procesarMensaje() {
        this.MsjRecibido = new String(this.MsjRecibidoBytes).trim();
        System.out.println(this.MsjRecibido);
    }

}
