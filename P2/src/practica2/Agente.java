/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 */
public class Agente extends SingleAgent {

    private static final int NUM_PERCEPCIONES = 3;
    private static final String AGENT_NAME = "GugelCarRedForest";

    private Pair<Integer, Integer> posicion;
    private String percepcion;
    private boolean pisando_objetivo;
    private boolean crashed;
    private int cont_bateria;
    private String server_key;
    private int mundo_elegido;
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

        System.out.println("Inicializando estado del agente...");

        this.map = new Mapa();
        this.map.inicializarMapa();

        this.heuristic = new Heuristica();

        this.pisando_objetivo = false;
        this.crashed = false;
        this.posicion = new Pair(0, 0);
        this.percepcion = "";
        this.server_key = "";
        this.cont_bateria = 0;
    }

    /**
     * Método que ejecutará el agente cuando se inicie
     *
     * @author Juan José Jiménez García
     */
    @Override
    public void execute() {

        try {
            System.out.println("Ejecutando el agente...");
            enviarMensajeAlServidor(Acciones.login);

            for (int i = 0; i < NUM_PERCEPCIONES; i++) {
                recibirMensajeDelServidor();
            }

            cont_bateria = 100;
            enviarMensajeAlServidor(Acciones.refuel);

            while (pisando_objetivo == false && map.getAntiguedad() >= -1000000) { //-1610612736 es 3/4 del valor minimo de una variable tipo entero. Indica que se ha recorrido el mapa varias veces sin encotrar la solucion

                for (int i = 0; i < NUM_PERCEPCIONES; i++) {
                    recibirMensajeDelServidor();
                }
                //System.out.println("Debug: Antes de comprobar bateria");
                if (comprobarBateria()) {
                    System.out.println("Debug: voy a recargar la bateria");
                    cont_bateria = 100;
                    enviarMensajeAlServidor(Acciones.refuel);
                } else //System.out.println("Debug: antes de if pisando objetivo");
                if (map.pisandoObjetivo(posicion)) {
                    //System.out.println("Debug: se supone que está en el objetivo");
                    pisando_objetivo = true;
                } else {
                    //System.out.println("Debug: antes de actualizar mapa");
                    map.actualizarMapa(posicion);
                    //Si antigueda%100 == 0 se llama a la funcion de comprobarCercos de la heuristica
                    //para que compruebe la funcion si esta en un cerco el objetivo y lo asigne a la variable 
                    //booleana sin solucion

                    Acciones siguiente_accion = heuristic.calcularSiguienteMovimiento(map, posicion);
                    System.out.println("Debug: esta es la accion que voy a hacer: " + siguiente_accion.toString());
                    enviarMensajeAlServidor(siguiente_accion);
                    cont_bateria--;
                    map.decrementarAntiguedad();
                }
            }

            this.finalize();

            // Aquí hay que implementar el funcionamiento del agente y todo el grueso del sistema
            // Este método puede llamar a otros métodos que formen parte del comportamiento
        } catch (IOException ex) {
            Logger.getLogger(Agente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Método que se ejecutará cuando el agente vaya a finalizar su ejecución
     *
     * @author Juan José Jiménez García
     */
    @Override
    public void finalize() {

        System.out.println("Finalizando ejecución y estado del agente...");

        try {
            if (crashed == false) {
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
        if (percepcion.equals("CRASHED")) //Recibido desde un sensor
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
                    JsonArray scanner = json.get("radar").asArray();
                    double[][] scanner_percibido = new double[5][5];

                    for (int i = 0; i < 25; i++) {
                        scanner_percibido[i / 5][i % 5] = scanner.get(i).asDouble();
                    }

                    map.setMatriz_scanner(scanner_percibido);
                    break;

                case "battery":
                    //No se usa por el momento
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
            System.out.println("Recibiendo la traza");

            //ACLMessage inbox = this.receiveACLMessage();
            //JsonObject injson = Json.parse(inbox.getContent()).asObject();
            JsonArray ja = injson.get("trace").asArray();
            byte data[] = new byte[ja.size()];

            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) ja.get(i).asInt();
            }

            FileOutputStream fos = new FileOutputStream(this.mundo_elegido + " - " + new SimpleDateFormat("yyyy-MM-dd-hh-mm").format(new Date()) + ".png");
            fos.write(data);
            fos.close();
            System.out.println("Traza guardada");

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
                break;

            default:
                json.add("key", server_key);
                break;
        }

        return json.toString();
    }

    /**
     * Método para el procesamiento de la traza de imagen
     *
     * @author Miguel Angel Torres López
     * @author Antonio Javier Benítez Guijarro
     */
    public void enviarMensajeAlServidor(Acciones accion) {

        ACLMessage outbox = new ACLMessage();
        outbox.setSender(this.getAid());
        outbox.setReceiver(new AgentID("Furud"));
        outbox.setContent(parsearAccion(accion));
        this.send(outbox);
    }

    /**
     * Método para el procesamiento de la traza de imagen
     *
     * @author Miguel Angel Torres López
     * @author Antonio Javier Benítez Guijarro
     * @progress PENDIENTE
     */
    public void recibirMensajeDelServidor() throws IOException {
        ACLMessage inbox = new ACLMessage();
        try {
            inbox = this.receiveACLMessage();
            System.out.println("\nDebug: " + inbox.getContent());
            parsearPercepcion(inbox.getContent());
            if (crashed == true) {
                System.out.println("Deslogueando del servidor");
                this.finalize();
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
     */
    public boolean comprobarBateria() {

        if (cont_bateria <= 5) {
            return true;
        } else {
            return false;
        }
    }

}
