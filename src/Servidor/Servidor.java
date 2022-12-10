package Servidor;

import java.awt.*;
import java.io.IOException;
import java.net.*;

import static java.lang.Thread.sleep;

public class Servidor {

    private DatagramSocket Socket;
    private DatagramPacket DatagramaEnviado, DatagramaRecibido;
    private InetAddress Dirección;
    private String MsjEnviado, MsjRecibido;
    private byte[] MsjEnviadoBytes, MsjRecibidoBytes;
    private int PuertoEntrada, PuertoSalida;


    /**
     * Inicializa la instancia del objeto desde su respectivo launcher.
     */
    public void iniciar() {
        try {
            inicializarAtributos();
            ejecutar();
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
    }


    /**
     * Establece parámetros de conexión para el programa.
     * @throws SocketException
     * @throws UnknownHostException
     */
    private void inicializarAtributos() throws SocketException,
                                               UnknownHostException {
        try {
            this.PuertoEntrada = 54321;
            this.PuertoSalida = 51234;

            this.Socket = new DatagramSocket(this.PuertoEntrada);
            this.Dirección = InetAddress.getByName("localhost");

            this.MsjRecibidoBytes = new byte[256];
            this.DatagramaRecibido = new DatagramPacket(this.MsjRecibidoBytes,256);

            System.out.println("Conectado!");
        } catch (SocketException e) {
            throw new SocketException("El socket no es válido");
        } catch (UnknownHostException e) {
            throw new UnknownHostException("Host desconocido");
        }
    }

    /**
     * Inicia el ciclo de ejecución para la clase.
     */
    private void ejecutar() {
        try{
            recibirDatagrama();
            int segundos = Integer.parseInt(this.MsjRecibido);
            enviarRáfagaPaquetes(segundos);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            ejecutar();
        }
    }

    /**
     * Recibe datagramas de la clase 'Cliente'
     * @throws IOException
     */
    private void recibirDatagrama() throws IOException {
        System.out.println("Esperando datagrama...");
        this.Socket.receive(this.DatagramaRecibido);
        this.MsjRecibido = new String(this.MsjRecibidoBytes).trim();
    }


    /**
     * Envía una ráfaga de mensajes cada 0.2 segundos durante la cantidad de
     * segundos ingresadas como parámetro.
     * @throws IOException
     * @throws InterruptedException
     */
    private void enviarRáfagaPaquetes(int segundos) throws IOException,
            InterruptedException {
        int ciclos = segundos * 5;
        int contador = 0;
        try {
            while(contador <= ciclos) {
                if (contador % 50 == 0) {
                    System.out.println("Se envían los datos del puntero.");
                }

                Point ubicaciónPuntero = obtenerUbicacionPuntero();

                double[] coordenadasPuntero = extraerCoordenadas(ubicaciónPuntero);
                double x = coordenadasPuntero[0];
                double y = coordenadasPuntero[1];

                String datosPaquete = parametrizarDatos(x, y);
                generarMensaje(datosPaquete);
                enviarMensaje();

                sleep(200);
                contador += 1;
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
            if (this.MsjEnviadoBytes.length > 256) {
                throw new IOException("El paquete es de más de 256 bytes.");
            }
            this.DatagramaEnviado = new DatagramPacket(this.MsjEnviadoBytes,
                    this.MsjEnviadoBytes.length,
                    this.Dirección,
                    this.PuertoSalida);
            this.Socket.send(this.DatagramaEnviado);
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
        this.MsjEnviado = mensaje;
        this.MsjEnviadoBytes = mensaje.getBytes();
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
