package Cliente;

import Utillities.Validaciones;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Cliente {

    private byte[] MsjRecibidoBytes;
    private DatagramSocket Socket;
    private DatagramPacket PaqueteRecibido;
    private String MsjRecibido;


    /**
     * Para inicializar la instancia del objeto desde su respectivo launcher.
     */
    public void iniciar() {
        try {
            inicializarDatos();
            menuPrincipal();
        } catch (SocketException e) {
            System.err.println(e.getMessage());
        }
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
            case 1 -> abrirEntrada();
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
    private void abrirEntrada(){
        int tiempo = preguntarTiempo();
        ejecutar(tiempo);
    }

    /**
     * Ejecuta el programa de forma constante, y actúa en caso de que llegue
     * un paquete.
     * En caso de excepción, el programa se detiene y muestra los datos
     * asociados al error.
     */
    public void ejecutar(int segundos) {
        try {
            long tiempoEjecucion = System.currentTimeMillis() + segundos * 1000L;
            while (System.currentTimeMillis() <= tiempoEjecucion) {
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
     * > El puerto a utilizar para cliente y servidor será el '54321'.
     * > El largo máximo en bytes que pueden tener los mensajes es de 256.
     * @throws SocketException
     */
    private void inicializarDatos() throws SocketException {
        this.MsjRecibidoBytes = new byte[256];
        this.Socket = new DatagramSocket(54321);
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
                [Envio del paquete]
                ¿Por cuántos segundos deseas compartir la localización
                de tu puntero? (Número entero).
                """);
        return Validaciones.validarPositivo();
    }

}
