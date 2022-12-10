package Servidor;

import Utillities.Validaciones;

import java.awt.*;
import java.io.IOException;
import java.net.*;

import static java.lang.Thread.sleep;

public class Servidor {

    private DatagramSocket Socket;
    private DatagramPacket Packet;
    private InetAddress Dirección;
    private String Mensaje;
    private byte[] MensajeBytes;
    private int Puerto;


    /**
     * Envía los datos al servidor, y pide la cantidad de segundos para
     * enviarlos.
     */
    public void iniciar() {
        try {
            conectar();
            enviarRáfagaPaquetes();
            System.out.println("[Fin de la transmisión]");
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
    }


    /**
     * Establece parámetros de conexión para el programa.
     * @throws SocketException
     * @throws UnknownHostException
     */
    private void conectar() throws SocketException, UnknownHostException {
        try {
            this.Socket = new DatagramSocket();
            this.Dirección = InetAddress.getByName("localhost");
            this.Puerto = 54321;
            System.out.println("Conectado!");
        } catch (SocketException e) {
            throw new SocketException("El socket no es válido");
        } catch (UnknownHostException e) {
            throw new UnknownHostException("Host desconocido");
        }
    }



    /**
     * Envía una ráfaga de mensajes cada 0.2 segundos durante la cantidad de
     * segundos ingresadas como parámetro.
     * @throws IOException
     * @throws InterruptedException
     */
    private void enviarRáfagaPaquetes() throws IOException,
            InterruptedException {
        int contadorMensaje = 0;
        try {
            while(true) {
                if (contadorMensaje % 50 == 0) {
                    System.out.println("Aún se envían los datos del puntero.");
                }

                Point ubicaciónPuntero = obtenerUbicacionPuntero();

                double[] coordenadasPuntero = extraerCoordenadas(ubicaciónPuntero);
                double x = coordenadasPuntero[0];
                double y = coordenadasPuntero[1];

                String datosPaquete = parametrizarDatos(x, y);
                generarMensaje(datosPaquete);
                enviarMensaje();

                sleep(200);
                contadorMensaje += 1;
            }
        } catch (InterruptedException e) {
            throw new InterruptedException("Algo interrumpió el proceso.");
        }
    }

    /**
     * Envía un paquete de datagrama a la dirección localhost, y al puerto
     * asignado en la función conectar(), el 54321
     * .
     * @throws IOException
     */
    private void enviarMensaje() throws IOException {
        try {
            if (this.MensajeBytes.length > 256) {
                throw new IOException("El paquete es de más de 256 bytes.");
            }
            this.Packet = new DatagramPacket(this.MensajeBytes,
                    this.MensajeBytes.length,
                    this.Dirección,
                    this.Puerto);
            this.Socket.send(this.Packet);
        } catch (Exception e) {
            throw new IOException("Error al enviar el paquete.");
        }

    }

    /**
     * Dada una string, establece los valores de las variables globales de
     * Mensaje y su equivalente en bytes, MensajeBytes.
     * @param mensaje
     */
    private void generarMensaje(String mensaje) {
        this.Mensaje = mensaje;
        this.MensajeBytes = mensaje.getBytes();
    }

    /**
     * Para enviar un paquete, los datos se deben serializar, es decir, pasar
     * los datos a String para encajar con los parámetros.
     * @param x
     * @param y
     * @return
     */
    private String parametrizarDatos(double x, double y) {
        return x + ";" + y + ";";
    }

    /**
     * Dada la ubicación del puntero, extrae las coordenadas en X e Y, y
     * las pasa a un arreglo d <double>.
     * @param puntero
     * @return
     */
    private double[] extraerCoordenadas(Point puntero) {
        double x = puntero.getX();
        double y = puntero.getY();
        return new double[]{x, y};
    }

    /**
     * Obtiene la ubicación actual del puntero (Mouse).
     * @return
     */
    private Point obtenerUbicacionPuntero() {
        PointerInfo ubicaciónPuntero = MouseInfo.getPointerInfo();
        return ubicaciónPuntero.getLocation();
    }

}
