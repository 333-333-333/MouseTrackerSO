package Cliente;

import Utillities.Validaciones;

import java.io.IOException;
import java.net.*;

public class Cliente {

    private byte[] MsjEnviadoBytes, MsjRecibidoBytes;
    private int PuertoSalida, PuertoEntrada;
    private DatagramSocket Socket;
    private DatagramPacket DatagramaEnviado, DatagramaRecibido;
    private InetAddress Dirección;
    private String MsjEnviado, MsjRecibido;


    /**
     * Para inicializar la instancia del objeto desde su respectivo launcher.
     */
    public void iniciar() {
        try {
            inicializarAtributos();
            menuPrincipal();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Inicializa los datos con los cuales se trabajará.
     * > El puerto a utilizar para cliente y servidor será el '54321'.
     * > El largo máximo en bytes que pueden tener los mensajes es de 256.
     * @throws SocketException
     */
    private void inicializarAtributos() throws SocketException,
            UnknownHostException {
        this.PuertoEntrada = 51234;
        this.PuertoSalida = 54321;

        this.MsjRecibidoBytes = new byte[256];
        this.DatagramaRecibido = new DatagramPacket(this.MsjRecibidoBytes,256);

        this.Socket = new DatagramSocket(this.PuertoEntrada);
        this.Socket.setReuseAddress(true);
        this.Socket.setSoTimeout(500);

        this.Dirección = InetAddress.getByName("localhost");
    }

    /**
     * Inicia el menú principal. Este método será llamado por su respectivo
     * launcher en el paquete.
     */
    private void menuPrincipal() {
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
                [1] Obtener datos del puntero en el servidor.
                [2] Salir.
                """);
    }

    /**
     * Selecciona una opción del menú principal
     */
    private void seleccionMenuPrincipal() {
        boolean quedarse = true;

        switch (Validaciones.validarIntervalo(1, 2)) {
            case 1 -> {
                enviarTiempoAServidor();
                recibirMensajes();
            }
            case 2 -> quedarse = false;
        }

        if (!quedarse) {
            System.out.println("Saliendo del programa");
            return;
        }

        menuPrincipal();
    }

    /**
     * Permite la entrada de datagramas por un número determinado de segundos.
     */
    private void enviarTiempoAServidor(){
        try {
            int tiempo = preguntarTiempo();
            generarDatagramaInt(tiempo);
            enviarDatagrama();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Genera un datagrama en base a un número ingresado como parámetro.
     * @param numero
     */
    private void generarDatagramaInt(int numero) {
        this.MsjEnviado = String.valueOf(numero);
        this.MsjEnviadoBytes = this.MsjEnviado.getBytes();
        this.DatagramaEnviado = new DatagramPacket(this.MsjEnviadoBytes,
                this.MsjEnviado.length(),
                this.Dirección,
                this.PuertoSalida);
    }

    /**
     * Envía un datagrama mediante el socket.
     * @throws IOException
     */
    private void enviarDatagrama() throws IOException {
        this.Socket.send(this.DatagramaEnviado);
    }

    /**
     * Ejecuta el programa de forma constante, y actúa en caso de que llegue
     * un paquete.
     * En caso de excepción, el programa se detiene y muestra los datos
     * asociados al error.
     */
    public void recibirMensajes() {
        try {
            while (true) {
                recibirPaquete();
                procesarMensaje();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Recibe el paquete a través del socket.
     * @throws IOException
     */
    private void recibirPaquete() throws SocketTimeoutException {
        try {
            System.out.println("\nEsperando al servidor...");
            this.Socket.receive(this.DatagramaRecibido);
        } catch (SocketTimeoutException e) {
            throw new SocketTimeoutException("\nNo han llegado más paquetes.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
     * Desde el cliente, el formato del mensaje era 'x;y', este método
     * toma los valores, los separa, y los muestra de forma más sofisticada.
     * @param mensaje
     * @throws Exception
     */
    private void parametrizarMensaje(String mensaje) throws Exception {
        String[] partesMensaje = mensaje.split(";");
        if (partesMensaje.length !=2) {
            throw new Exception("El paquete no cumple con parámetros");
        }
        System.out.println("\nPosición horizontal: " + partesMensaje[0]
                + "\nPosición vertical: " + partesMensaje[1]);
    }

    /**
     * Pregunta al usuario por cuanto tiempo desea que se ejecute el programa.
     * @return
     */
    private int preguntarTiempo() {
        System.out.println("""
                
                [Recepción de datos]
                ¿Por cuántos segundos deseas obtener la localización del
                puntero? (Número entero).
                """);
        return Validaciones.validarPositivo();
    }

}
