package practica3;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Gregorio Carvajal Expósito
 * @author Juan José Jimenez García
 */
public class Controlador extends SingleAgent {

    public static final String SERVER_NAME = "Furud";
    public static final String AGENT_ID = "controlador";
    private static final String AGENTES_CONVERSATION_ID = "grupo-6-agentes";
    private final int MUNDO_ELEGIDO;
    private String conversationID;
    private Map<String, AgentID> agentesMAP;
    private boolean fuelMundoAcabado = false;
    private Heuristica heuristica;

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
        this.agentesMAP = new HashMap<>();
        this.heuristica = new Heuristica();
    }

    /**
     * Método que contiene la lógica que ejecutará el Controlador cuando se
     * inicie
     *
     * @author Juan José Jiménez García
     */
    @Override
    public void execute() {

    }

    /**
     * Método que se ejecutará cuando el Controlador vaya a finalizar su
     * ejecución
     *
     * @author Juan José Jiménez García
     */
    @Override
    public void finalize() {

    }

    /**
     * Ejecuta el receive y actualiza las variables necesarias del controlador
     *
     * @author Gregorio Carvajal Expósito
     * @throws java.lang.InterruptedException
     */
    public void recibir() throws InterruptedException {
        ACLMessage resp = receiveACLMessage();
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
                        //PROCESAR TRAZA
                    }
                } else //De un agente
                {
                    //Respuesta al QUERY_REF pidiendo el estado
                    if (json.get("estado") != null) {
                        JsonObject jsonEstado = json.get("estado").asObject();
                        //Aun no se como construir el objeto EstadoAgente
                    } else //Respuesta al REQUEST de la accion escogida
                    {
                        //Do nothing
                    }
                }

                break;

            case ACLMessage.QUERY_REF:
                agentesMAP.put(resp.getReplyWith(), resp.getSender());
                break;

            default:
                System.err.println("ERROR: El controlador ha recibido " + json.get("details"));
                //Hacer algo para detener la ejecucion de los agentes
                break;
        }
    }

    /**
     * Informa (send)Solo del al agente seleccionado su siguiente accion
     *
     * @author Gregorio Carvajal Expósito
     * @param agenteElegido Clase en la que tenemos el agente elegido y la
     * accion siguiente
     */
    public void asignarAccion(EstadoAgente agenteElegido) {
        JsonObject json = new JsonObject();
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

        json.add("command", agenteElegido.getNextAction());

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

        send(cancel);
    }

    /**
     * Realiza un subscribe y despues un logout para obtener una traza vacia y
     * asi poder obtener el tamaño del mapa
     *
     * @author Gregorio Carvajal Expósito
     */
    public void obtenerTamanoMapa() {

    }
}
