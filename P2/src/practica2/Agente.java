package practica2;

import com.eclipsesource.json.*;
import es.upv.dsic.gti_ia.core.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 * Esta clase contiene los atributos y métodos para el comportamiento de nuestro
 * agente
 *
 * @author Juan José Jiménez García
 * @author Gregorio Carvajal Expósito
 * @author Emilio Chica Jimenéz
 * @author Miguel Angel Torres López
 * @author Antonio Javier Benítez Guijarro
 */
public class Agente extends SingleAgent {

    private static final int NUM_PERCEPCIONES = 4;
    private static final String AGENT_NAME = "GugelCarRedForest";
    private Pair<Integer, Integer> posicion;
    private boolean pisando_objetivo;
    private boolean crashed;
    private boolean sin_solucion;
    private int cont_bateria;
    private String server_key;
    private final int mundo_elegido;
    private Mapa map;
    private Heuristica heuristic;

    /**
     * Constructor de la clase Agente
     *
     * @param aid El ID del agente
     * @param mundo String que indica el mundo al que se va a conectar el agente
     * @throws Exception
     * @author Juan José Jiménez García
     */
    public Agente(AgentID aid, int mundo) throws Exception {
        super(aid);
        this.mundo_elegido = mundo;
    }

    /**
     * Inicialización de las variables antes de la ejecución del agente
     *
     * @author Juan José Jiménez García
     */
    @Override
    public void init() {

        System.out.println("\n\nInicializando estado del agente...");

        this.map = new Mapa();
        this.map.inicializarMapa();
        this.heuristic = new Heuristica();
        this.pisando_objetivo = false;
        this.crashed = false;
        this.posicion = new Pair(0, 0);
        this.server_key = "";
        this.cont_bateria = 0;
        this.sin_solucion = false;
    }

    /**
     * Método que ejecutará el agente cuando se inicie
     *
     * @author Juan José Jiménez García
     * @author Gregorio Carvajal Expósito
     * @author Emilio Chica Jimenéz
     */
    @Override
    public void execute() {

        try {
            System.out.println("Ejecutando el agente...");
            // ESTADO: Inicializar
            enviarMensajeAlServidor(Acciones.login);

            for (int i = 0; i < NUM_PERCEPCIONES; i++) {
                recibirMensajeDelServidor();
            }

            cont_bateria = 100;
            enviarMensajeAlServidor(Acciones.refuel);

            while ((pisando_objetivo == false) && (map.getAntiguedad() >= -15000) && (this.sin_solucion == false)) {

                for (int i = 0; i < NUM_PERCEPCIONES; i++) {
                    recibirMensajeDelServidor();
                }

                if (comprobarBateria()) {
                    // ESTADO: Poca bateria
                    cont_bateria = 100;
                    enviarMensajeAlServidor(Acciones.refuel);
                } else if (map.pisandoObjetivo(posicion)) {
                    // ESTADO: Pisando objetivo
                    pisando_objetivo = true;
                } else {
                    // ESTADO: Movimiento
                    map.actualizarMapa(posicion);

                    /*
                    Si antiguedad % 100 == 0 se llama a la funcion de
                    comprobarCercos de la heuristica para que compruebe la
                    funcion si esta en un cerco el objetivo y lo asigne a la
                    variable booleana sin solucion
                     */
                    if (map.getPosicionObjetivo() != null && map.getAntiguedad() % 100 == 0) {
                        this.sin_solucion = this.heuristic.comprobarCercos(map, posicion);
                    }

                    if (this.sin_solucion) { // SI no hay solución, finalizar
                        System.out.println("SOLUCIÓN: Este mapa no se puede resolver");
                    } else { // Si sigue habiendo posibilidad de solución, elegimos movimiento
                        Acciones siguiente_accion = heuristic.calcularSiguienteMovimiento(map, posicion);
                        enviarMensajeAlServidor(siguiente_accion);
                        cont_bateria--;
                        map.decrementarAntiguedad();
                    }
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Agente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Método que se ejecutará cuando el agente vaya a finalizar su ejecución
     *
     * @author Juan José Jiménez García
     * @author Gregorio Carvajal Expósito
     */
    @Override
    public void finalize() {

        System.out.println("Finalizando agente...");

        try {
            if (crashed == false) {
                // ESTADO: Error
                enviarMensajeAlServidor(Acciones.logout);
                for (int i = 0; i < NUM_PERCEPCIONES + 1; i++) {
                    recibirMensajeDelServidor();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Agente.class.getName()).log(Level.SEVERE, null, ex);
        }

        super.finalize();
    }

    /**
     * Método que se encarga de actualizar los valores del estado del agente en
     * funcion de la percepcion recibida
     *
     * @param percepcion Cadena JSON recibida desde el servidor
     * @author Gregorio Carvajal Exposito
     * @throws java.io.IOException
     */
    public void parsearPercepcion(String percepcion) throws IOException {
        if (percepcion.equals("CRASHED")) // Recibido desde un sensor
        {
            crashed = true;
        } else {
            JsonObject json = Json.parse(percepcion).asObject();
            List<String> names = json.names();

            switch (names.get(0)) {
                case "result":
                    String result = json.getString("result", "UNKNOW RESPONSE");

                    if (result.startsWith("BAD_") || result.equals("CRASHED")) {
                        crashed = true;
                        System.out.println("ERROR: " + result);
                    } else if (!result.equals("OK")) {
                        server_key = result;
                    }

                    break;

                case "gps":
                    JsonObject gps = json.get("gps").asObject();
                    posicion = new Pair<>(gps.getInt("x", 0), gps.getInt("y", 0));

                    break;

                case "radar":
                    JsonArray radar = json.get("radar").asArray();
                    int[][] radar_percibido = new int[5][5];

                    for (int i = 0; i < 25; i++) {
                        radar_percibido[i / 5][i % 5] = radar.get(i).asInt();
                    }

                    map.setRadar(radar_percibido);
                    break;

                case "scanner":
                    JsonArray scanner = json.get("scanner").asArray();
                    double[][] scanner_percibido = new double[5][5];

                    for (int i = 0; i < 25; i++) {
                        scanner_percibido[i / 5][i % 5] = scanner.get(i).asDouble();
                    }

                    map.setMatrizScanner(scanner_percibido);
                    break;

                case "battery":
                    // No se va a utilizar por el momento
                    break;

                case "trace":
                    procesarTraza(json);
                    break;
            }
        }
    }

    /**
     * Método para el procesamiento de la traza de imagen
     *
     * @author Juan José Jiménez García
     * @author Gregorio Carvajal Expósito
     * @param injson Objeto json que contiene la traza
     * @throws java.io.IOException
     */
    public void procesarTraza(JsonObject injson) throws IOException {

        try {

            JsonArray ja = injson.get("trace").asArray();
            byte data[] = new byte[ja.size()];

            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) ja.get(i).asInt();
            }

            String filename = this.mundo_elegido + " - " + new SimpleDateFormat("yyyy-MM-dd-hh-mm").format(new Date()) + ".png";
            FileOutputStream fos = new FileOutputStream(filename);
            fos.write(data);
            fos.close();
            System.out.println("Traza guardada en el archivo " + filename);

        } catch (IOException ex) {

            System.err.println("Error procesando la traza");
        }
    }

    /**
     * Metodo para generar la cadena JSON adecuada, lista para mandarla al
     * servidor
     *
     * @author Gregorio Carvajal Expósito
     * @param accion Accion que queremos parsear a JSON
     * @return Un String en JSON
     */
    public String parsearAccion(Acciones accion) {
        JsonObject json = new JsonObject();
        json.add("command", accion.toString());

        switch (accion) {
            case login:
                json.add("world", "map" + mundo_elegido);
                json.add("radar", AGENT_NAME);
                json.add("gps", AGENT_NAME);
                json.add("scanner", AGENT_NAME);
                break;

            default:
                json.add("key", server_key);
                break;
        }

        return json.toString();
    }

    /**
     * Método para enviar un mensaje al servidor
     *
     * @author Miguel Angel Torres López
     * @author Antonio Javier Benítez Guijarro
     * @param accion La acción que queremos enviar al servidor
     */
    public void enviarMensajeAlServidor(Acciones accion) {

        ACLMessage outbox = new ACLMessage();
        outbox.setSender(this.getAid());
        outbox.setReceiver(new AgentID("Furud"));
        outbox.setContent(parsearAccion(accion));
        this.send(outbox);
    }

    /**
     * Método para recibir un mensaje del servidor
     *
     * @author Miguel Angel Torres López
     * @author Antonio Javier Benítez Guijarro
     * @throws java.io.IOException
     */
    public void recibirMensajeDelServidor() throws IOException {

        try {
            ACLMessage inbox = this.receiveACLMessage();
            parsearPercepcion(inbox.getContent());
            if (crashed == true) {
                System.out.println("Deslogueando del servidor");
            }
        } catch (InterruptedException ex) {
            System.out.println("No se recibio correctamente el mensaje");
        }
    }

    /**
     * Método para el procesamiento de la traza de imagen
     *
     * @author Miguel Angel Torres López
     * @author Antonio Javier Benítez Guijarro
     * @return Un booleano que indica si la batería requiere de carga
     */
    public boolean comprobarBateria() {

        return (cont_bateria <= 5);
    }
}
