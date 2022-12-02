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


    /**
     * Ejecuta el programa de forma constante, y actúa en caso de que llegue
     * un paquete.
     * En caso de excepción, el programa se detiene y muestra los datos
     * asociados al error.
     */
    public void ejecutar() {
        try {
            inicializarDatos();
            while (true) {
                System.out.println("\nEsperando cliente...");
                recibirPaquete();
                procesarMensaje();
            }
        } catch (Exception e) {
            System.err.println(e.getClass());
            System.err.println(e.getMessage());
        }
    }

    /**
     * Inicializa los datos con los cuales se trabajará.
     * > El puerto a utilizar para cliente y servidor será el '3333'.
     * > El largo máximo en bytes que pueden tener los mensajes es de 256.
     * @throws SocketException
     */
    private void inicializarDatos() throws SocketException {
        this.MsjRecibidoBytes = new byte[256];
        this.Socket = new DatagramSocket(3333);
        this.PaqueteRecibido = new DatagramPacket(this.MsjRecibidoBytes,256);

    }

    /**
     * Recibe el paquete a través del socket.
     * @throws IOException
     */
    private void recibirPaquete() throws IOException {
        this.Socket.receive(this.PaqueteRecibido);
    }

    /**
     * Pasa el mensaje a variable global y lo parametriza.
     */
    private void procesarMensaje() {
        try {
            this.MsjRecibido = new String(this.MsjRecibidoBytes).trim();
            parametrizarMensaje(this.MsjRecibido);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Desde el cliente, el formato del mensaje era 'x;y;seg', este método
     * toma los valores, los separa, y los muestra de forma más sofisticada.
     * @param mensaje
     * @throws Exception
     */
    private void parametrizarMensaje(String mensaje) throws Exception {
        String[] partesMensaje = mensaje.split(";");
        if (partesMensaje.length !=3) {
            throw new Exception("El paquete no cumple con parámetros");
        }
        System.out.println("\nTiempo: " + partesMensaje[2]
                        + "\nPosición horizontal: " + partesMensaje[0]
                        + "\nPosición vertical: " + partesMensaje[1]);
    }

}
