package practica3;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 *
 * @author Gregorio Carvajal Expósito
 * @author Juan José Jimenez García
 */
public class Controlador extends SingleAgent {

    public static final String SERVER_NAME = "Furud";
    public static final String AGENT_ID = "GugelCarRedForest3";
    public static final String AGENTES_CONVERSATION_ID = "grupo-6-agentes";
    private final int MUNDO_ELEGIDO;
    private boolean quedaFuel;
    private boolean terminado;
    private EstadosEjecucion estadoActual;
    private String conversationID;
    private Map<String, AgentID> agentesMAP;
    private Heuristica heuristica;
    private BaseConocimiento bc;
    private ArrayList<EstadoAgente> agentesEnObjetivo;
    private int fuelMundo;

    /**
     * Constructor de la clase Controlador
     *
     * @param aid El ID del agente
     * @param mundo String que indica el mundo al que se van a conectar los
     * agentes
     * @throws Exception
     * @author Juan José Jiménez García
     */
    public Controlador(AgentID aid, int mundo) throws Exception {
        super(aid);
        this.MUNDO_ELEGIDO = mundo;
    }

    /**
     * Inicialización de las variables antes de la ejecución del Controlador
     *
     * @author Juan José Jiménez García
     */
    @Override
    public void init() {
        System.out.println("Iniciando estado del Controlador...");
        System.out.flush();
        this.agentesMAP = new HashMap<>();
        this.heuristica = new Heuristica();
        this.bc = BaseConocimiento.getInstance();
        this.bc.setMapaElegido(MUNDO_ELEGIDO);
        this.quedaFuel = true;
        this.terminado = false;
        this.estadoActual = EstadosEjecucion.INICIAL;
        this.conversationID = "";
        this.agentesEnObjetivo = new ArrayList();
    }

    /**
     * Método que contiene la lógica que ejecutará el Controlador cuando
     * finalice su ejecución
     *
     * @author Juan José Jiménez García
     */
    @Override
    public void execute() {

        ArrayList<EstadoAgente> estadosAgentes;

        while (!terminado) {
            switch (this.estadoActual) {
                case INICIAL:
                    // Realizamos orden subscribe al servidor
                    this.suscribirse();

                    //Recibimos el conversationID del server y las peticiones de los 4 agentes
                    for (int i = 0; i < 5; i++) {
                        try {
                            recibir();
                        } catch (InterruptedException | IOException ex) {
                            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    // Mandar conversationID a los demás agentes
                    this.compartirConversationID();

                    // Estamos subcritos
                    int tamMapa = this.obtenerTamanoMapa();
                    heuristica.setTamMapa(tamMapa);

                    // Cargar el mapa
                    boolean resultado = bc.cargarMapa(this.MUNDO_ELEGIDO, tamMapa);

                    if (resultado) {
                        this.estadoActual = EstadosEjecucion.ENCONTRADO;
                    } else {
                        this.estadoActual = EstadosEjecucion.BUSCANDO;
                    }
                    break;

                case BUSCANDO:
                    // Obtener un array de EstadoAgente
                    this.bc.limpiarConjuntoEstados();
                    this.pedirEstadoAgente();

                    // Recopilar los estados de agente
                    for (int i = 0; i < this.agentesMAP.size(); i++) {
                        try {
                            this.recibir();
                        } catch (InterruptedException | IOException ex) {
                            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    // Una vez recopilados, los pedimos a la base de conocimiento
                    estadosAgentes = bc.getConjuntoEstados();

                    // Si las percepciones han localizado el objetivo, pasaremos a otro estado
                    if (this.bc.getPosicionObjetivo() == null) {
                        // Pasar el array a la heurística y obtener el agente seleccionado
                        EstadoAgente agenteSeleccionado = this.heuristica.buscandoObjetivo(estadosAgentes, this.quedaFuel);
                        this.bc.limpiarConjuntoEstados();

                        if (agenteSeleccionado == null) {
                            this.estadoActual = EstadosEjecucion.TERMINADO;
                            break;
                        }
                        // Si la siguiente accion es null, significa que nos hemos quedad sin fuel
                        if (agenteSeleccionado.getNextAction() == null) {
                            this.estadoActual = EstadosEjecucion.TERMINADO;
                        } else {
                            // Mandamos la accion al agente seleccionado
                            this.asignarAccion(agenteSeleccionado);
                            try {
                                this.recibir();
                            } catch (InterruptedException | IOException ex) {
                                Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } else {
                        this.estadoActual = EstadosEjecucion.ENCONTRADO;
                    }

                    break;

                case ENCONTRADO:
                    // Obtener un array de EstadoAgente
                    this.bc.limpiarConjuntoEstados();
                    this.pedirEstadoAgente();

                    // Recopilar los estados de agente
                    for (int i = 0; i < this.agentesMAP.size(); i++) {
                        try {
                            this.recibir();
                        } catch (InterruptedException | IOException ex) {
                            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    // Una vez recopilados, los pedimos a la base de conocimiento
                    estadosAgentes = bc.getConjuntoEstados();

                    // Si alguno de los agentes está pisando el objetivo, pasamos al estado ALCANZADO
                    boolean unoPisando = false;

                    for (int i = 0; i < estadosAgentes.size() && !unoPisando; i++) {
                        if (estadosAgentes.get(i).isPisandoObjetivo()) {
                            unoPisando = true;
                            agentesEnObjetivo.add(estadosAgentes.get(i));
                            agentesMAP.remove(estadosAgentes.get(i).getReplyWithControlador());
                        }
                    }

                    if (unoPisando) {
                        this.estadoActual = EstadosEjecucion.ALCANZADO;
                        //CAPADOR
                        //this.estadoActual = EstadosEjecucion.TERMINADO;
                    } else {
                        // Pasar el array a la heurística y obtener el agente seleccionado
                        EstadoAgente agenteSeleccionado = this.heuristica.objetivoEncontrado(estadosAgentes, bc.getPosicionObjetivo(), this.quedaFuel);
                        this.bc.limpiarConjuntoEstados();

                        // Si la siguiente accion es null, significa que nos hemos quedad sin fuel
                        if (agenteSeleccionado.getNextAction() == null) {
                            this.estadoActual = EstadosEjecucion.TERMINADO;
                        } else {
                            // Mandamos la accion al agente seleccionado
                            this.asignarAccion(agenteSeleccionado);
                            try {
                                this.recibir();
                            } catch (InterruptedException | IOException ex) {
                                Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    break;

                case ALCANZADO:
                    // Obtener un array de EstadoAgente
                    this.bc.limpiarConjuntoEstados();
                    this.pedirEstadoAgente();

                    // Recopilar los estados de agente
                    for (int i = 0; i < this.agentesMAP.size(); i++) {
                        try {
                            this.recibir();
                        } catch (InterruptedException | IOException ex) {
                            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    // Una vez recopilados, los pedimos a la base de conocimiento
                    estadosAgentes = bc.getConjuntoEstados();

                    for (int i = 0; i < estadosAgentes.size(); i++) {
                        if (estadosAgentes.get(i).isPisandoObjetivo()) {
                            agentesEnObjetivo.add(estadosAgentes.get(i));
                            agentesMAP.remove(estadosAgentes.get(i).getReplyWithControlador());

                        }
                    }

                    // Si todos los agentes está pisando el objetivo, pasamos al estado TERMINADO
                    if (agentesMAP.isEmpty()) {
                        this.estadoActual = EstadosEjecucion.TERMINADO;
                    } else {
                        // Pasar el array a la heurística y obtener el agente seleccionado
                        EstadoAgente agenteSeleccionado = this.heuristica.objetivoAlcanzado(estadosAgentes, bc.getPosicionObjetivo(), this.quedaFuel);
                        this.bc.limpiarConjuntoEstados();

                        // Si la siguiente accion es null, significa que nos hemos quedad sin fuel
                        if (agenteSeleccionado == null) {
                            this.estadoActual = EstadosEjecucion.TERMINADO;
                        } else if (agenteSeleccionado.getNextAction() == null) {
                            this.estadoActual = EstadosEjecucion.TERMINADO;
                        } else {
                            // Mandamos la accion al agente seleccionado
                            this.asignarAccion(agenteSeleccionado);
                            try {
                                this.recibir();
                            } catch (InterruptedException | IOException ex) {
                                Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    break;

                case TERMINADO:
                    this.terminado = true;
                    this.logout();
                    try {
                        this.recibir();
                        this.recibir();
                    } catch (InterruptedException | IOException ex) {
                        Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;

                case ERROR:
                    this.estadoActual = EstadosEjecucion.TERMINADO;
                    break;
            }
        }
    }

    /**
     * Método que se ejecutará cuando el Controlador vaya a finalizar su
     * ejecución
     *
     * @author Juan José Jiménez García
     */
    @Override
    public void finalize() {

        System.out.println("Finalizando Controlador...");
        super.finalize();
    }

    /**
     * Método que obtiene el tamaño del mapa a usar según el nº de mapa
     *
     * @author Juan José Jiménez García
     */
    public int obtenerTamanoMapa() {

        int[] tamaniosMapa = {110, 110, 110, 110, 110, 160, 110, 110, 160, 510};

        if (this.MUNDO_ELEGIDO <= 10) {
            return tamaniosMapa[this.MUNDO_ELEGIDO - 1];
        } else {
            return tamaniosMapa[(this.MUNDO_ELEGIDO - 1) / 100];
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

            String filename = this.MUNDO_ELEGIDO + " - " + new SimpleDateFormat("yyyy-MM-dd-hh-mm").format(new Date()) + " - " + this.conversationID + ".png";
            FileOutputStream fos = new FileOutputStream(filename);
            fos.write(data);
            fos.close();
            System.out.println("Traza guardada en el archivo " + filename);
            System.out.flush();

        } catch (IOException ex) {
            System.err.println("Error procesando la traza");
            System.out.flush();
            System.err.println(ex.toString());
            System.out.flush();
        }
    }

    /**
     * Ejecuta el receive y actualiza las variables necesarias del controlador
     *
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     * @author Gregorio Carvajal Expósito
     */
    public void recibir() throws InterruptedException, IOException {

        ACLMessage resp = receiveACLMessage();
        System.out.println("Controlador mensaje recibido");
        System.out.flush();
        JsonObject json = Json.parse(resp.getContent()).asObject();
        switch (resp.getPerformativeInt()) {
            case ACLMessage.INFORM:
                //Del servidor
                if (!resp.getConversationId().equals(AGENTES_CONVERSATION_ID)) {
                    if (json.get("result") != null) //OK to subscribe
                    {
                        conversationID = resp.getConversationId();
                    } else //Trace
                    {
                        this.procesarTraza(json);
                    }
                } else //De un agente
                //Respuesta al QUERY_REF pidiendo el estado
                if (json.get("estado") != null) {
                    JsonObject jsonEstado = json.get("estado").asObject();
                    JsonArray jsonRadar = jsonEstado.get("percepcion").asArray();
                    Acciones accion;

                    if (jsonEstado.get("nextAction").asString().equals("")) {
                        accion = null;
                    } else {
                        accion = Acciones.valueOf(jsonEstado.get("nextAction").asString());
                    }

                    EstadoAgente estado = new EstadoAgente(
                            new int[1][1],
                            new Pair<>(jsonEstado.get("i").asInt(), jsonEstado.get("j").asInt()),
                            jsonEstado.get("fuelActual").asInt(),
                            jsonEstado.get("crashed").asBoolean(),
                            jsonEstado.get("pisandoObjetivo").asBoolean(),
                            jsonEstado.get("replyWithControlador").asString(),
                            TiposAgente.valueOf(jsonEstado.get("tipo").asString()),
                            accion
                    );

                    int[][] radar = new int[estado.getVisibilidad()][estado.getVisibilidad()];

                    for (int i = 0; i < estado.getVisibilidad() * estado.getVisibilidad(); i++) {
                        radar[i / estado.getVisibilidad()][i % estado.getVisibilidad()] = jsonRadar.get(i).asInt();
                    }

                    estado.setPercepcion(radar);
                    bc.actualizarMapa(estado, agentesEnObjetivo);
                    fuelMundo = json.get("fuelmundo").asInt();
                    quedaFuel = fuelMundo > 0;
                } else //Respuesta al REQUEST de la accion escogida
                {
                    //Do nothing
                }

                break;

            case ACLMessage.QUERY_REF:
                agentesMAP.put(resp.getReplyWith(), resp.getSender());
                break;
            case ACLMessage.AGREE:
                break;
            default:
                if (resp.getConversationId().equals(AGENTES_CONVERSATION_ID)) //Recibido de un Agente
                {
                    if (json.get("details").asString().contains("BAD ENERGY")) {
                        this.quedaFuel = false;
                        break;
                    } else //Recibido directamente del Server
                    {
                        System.err.println("ERROR: Un agente ha recibido " + json.get("details").asString());
                        logout();
                        recibir();
                        recibir();
                    }
                } else {
                    System.err.println("ERROR: El controlador ha recibido " + json.get("details").asString());
                }

                break;
        }
    }

    /**
     * Informa (send) Solo del al agente seleccionado su siguiente accion
     *
     * @author Gregorio Carvajal Expósito
     * @param agenteElegido Clase en la que tenemos el agente elegido y la
     * accion siguiente
     */
    public void asignarAccion(EstadoAgente agenteElegido) {
        JsonObject json = new JsonObject();
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

        json.add("command", agenteElegido.getNextAction().toString());

        msg.setSender(this.getAid());
        msg.setContent(json.toString());
        msg.setInReplyTo(agenteElegido.getReplyWithControlador());
        msg.setReceiver(agentesMAP.get(agenteElegido.getReplyWithControlador()));
        msg.setConversationId(AGENTES_CONVERSATION_ID);

        send(msg);
    }

    /**
     * Manda (send) a todos los agentes el ConversationID necesario para
     * comunicarse con el Servidor
     *
     * @author Gregorio Carvajal Expósito
     */
    public void compartirConversationID() {
        JsonObject json = new JsonObject();
        json.add("serverID", conversationID);

        for (Map.Entry<String, AgentID> agente : agentesMAP.entrySet()) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

            msg.setSender(this.getAid());
            msg.setContent(json.toString());
            msg.setInReplyTo(agente.getKey());
            msg.setReceiver(agente.getValue());
            msg.setConversationId(AGENTES_CONVERSATION_ID);

            send(msg);
        }
    }

    /**
     * Solicita (send) a todos los agentes su estado
     *
     * @author Gregorio Carvajal Expósito
     */
    public void pedirEstadoAgente() {
        JsonObject json = new JsonObject();
        json.add("query", "estado");

        for (Map.Entry<String, AgentID> agente : agentesMAP.entrySet()) {
            ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);

            msg.setSender(this.getAid());
            msg.setContent(json.toString());
            msg.setInReplyTo(agente.getKey());
            msg.setReceiver(agente.getValue());
            msg.setConversationId(AGENTES_CONVERSATION_ID);

            send(msg);
        }
    }

    /**
     * Realiza el SUBSCRIBE (send) al Servidor JsonObject json = new
     * JsonObject();
     *
     * @author Gregorio Carvajal Expósito
     */
    public void suscribirse() {
        ACLMessage subs = new ACLMessage(ACLMessage.SUBSCRIBE);
        JsonObject json = new JsonObject();

        subs.setSender(this.getAid());
        subs.setReceiver(new AgentID(SERVER_NAME));
        json.add("world", "map" + MUNDO_ELEGIDO);
        subs.setContent(json.toString());

        send(subs);
    }

    /**
     * Realiza el logout (send) al Servidor
     *
     * @author Gregorio Carvajal Expósito
     */
    public void logout() {
        ACLMessage cancel = new ACLMessage(ACLMessage.CANCEL);

        cancel.setSender(this.getAid());
        cancel.setReceiver(new AgentID(SERVER_NAME));

        for (Map.Entry<String, AgentID> agente : agentesMAP.entrySet()) {
            cancel.addReceiver(agente.getValue());
        }

        send(cancel);
    }
}
