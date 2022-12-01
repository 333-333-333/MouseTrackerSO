package Cliente;

import Utillities.Validaciones;

import java.awt.*;
import java.io.IOException;
import java.net.*;

import static java.lang.Thread.sleep;

public class Cliente {

    private DatagramSocket Socket;
    private DatagramPacket Packet;
    private InetAddress Dirección;
    private String Mensaje;
    private byte[] MensajeBytes;
    private int Puerto;

    /**
     * Inicia el menú principal. Este método será llamado por su respectivo
     * launcher en el paquete.
     */
    public void menuPrincipal() {
        opcionesMenuPrincipal();
        seleccionMenuPrincipal();
    }

    /**
     * Imprime por consola las opciones del menú principal.
     */
    private void opcionesMenuPrincipal() {
        System.out.println("""
                [ICC 260-1]
                
                ¿Qué deseas hacer?
                [1] Mandar datos del puntero al servidor.
                [2] Salir.
                """);
    }

    /**
     * Selecciona una opción del menú principal
     */
    private void seleccionMenuPrincipal() {
        boolean quedarse = true;

        switch (Validaciones.validarIntervalo(1, 2)) {
            case 1 -> enviarDatos();
            case 2 -> quedarse = false;
        }

        if (!quedarse) {
            System.out.println("Saliendo del programa");
            return;
        }

        menuPrincipal();
    }

    /**
     * Envía los datos al servidor, y pide la cantidad de segundos para
     * enviarlos.
     */
    private void enviarDatos() {
        try {
            conectar();
            int tiempo = preguntarTiempo();
            enviarRáfagaPaquetes(tiempo);
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
            this.Puerto = 3333;
        } catch (SocketException e) {
            throw new SocketException("El socket no es válido");
        } catch (UnknownHostException e) {
            throw new UnknownHostException("Host desconocido");
        }
    }

    /**
     * Pregunta al usuario por cuanto tiempo desea que se ejecute el programa.
     * @return
     */
    private int preguntarTiempo() {
        System.out.println("""
                [Envio del paquete]
                ¿Por cuántos segundos deseas compartir la localización
                de tu puntero? (Número entero).
                """);
        return Validaciones.validarPositivo();
    }

    /**
     * Envía una ráfaga de mensajes cada 0.1 segundos durante la cantidad de
     * segundos ingresadas como parámetro.
     * @param segundos
     * @throws IOException
     * @throws InterruptedException
     */
    private void enviarRáfagaPaquetes(int segundos) throws IOException,
            InterruptedException {
        try {
            for (float ms = 0 ; ms <=  segundos; ms += 0.1) {
                Point ubicaciónPuntero = obtenerUbicacionPuntero();

                double[] coordenadasPuntero = extraerCoordenadas(ubicaciónPuntero);
                double x = coordenadasPuntero[0];
                double y = coordenadasPuntero[1];

                String datosPaquete = parametrizarDatos(x, y, ms);
                generarMensaje(datosPaquete);
                enviarMensaje();

                sleep(100);
            }
        } catch (InterruptedException e) {
            throw new InterruptedException("Algo interrumpió el proceso.");
        }
    }

    /**
     * Envía un paquete de datagrama a la dirección localhost, y al puerto
     * asignado en la función conectar(), el 3333.
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
     * @param tiempo
     * @return
     */
    private String parametrizarDatos(double x, double y, float tiempo) {
        return x + ";" + y + ";" + tiempo;
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
