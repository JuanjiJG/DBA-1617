package shenron;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 * Clase que contiene el agente Mutenroshi que contactará con el agente Shenron
 *
 * @author Miguel Ángel Torres López
 */
public class Mutenroshi extends SingleAgent {

    ACLMessage outbox, inbox;
    JsonObject injson, outjson;
    String controller, user, password;
    int accion;

    /**
     * Método constructor del agente Mutenroshi
     *
     * @param aid Contiene el identificador del agente
     * @param c Contiene el controlador asignado a nuestro grupo
     * @param u Contiene el usuario asignado a nuestro grupo
     * @param p Contiene la contraseña asignada a nuestro grupo
     * @param accion Contiene la accion que va a solicitar el agente Mutenroshi
     * a Shenron
     * @throws Exception
     * @author Miguel Ángel Torres López
     */
    public Mutenroshi(AgentID aid, String c, String u, String p, int accion) throws Exception {
        super(aid);
        controller = c;
        user = u;
        password = p;
        this.accion = accion;
    }

    /**
     * Método que ejecutará el agente cuando se inicie
     *
     * @author Miguel Ángel Torres López
     */
    @Override
    public void execute() {
        System.out.println("Ejecutando a Mutenroshi");
        if (accion == 1) {
            Reboot();
        } else if (accion == 2) {
            consultarRegistro();
        }
        System.out.println("Fin de Mutenroshi");
    }

    /**
     * Método que comunica a Shenron que reinicie el servidor
     *
     * @author Miguel Ángel Torres López
     */
    void Reboot() {
        System.out.println("Reseteando Shenron");
        outbox = new ACLMessage(ACLMessage.REQUEST);
        outbox.setSender(this.getAid());
        outbox.setReceiver(new AgentID("Shenron"));
        outjson = new JsonObject();
        outjson.add("user", user);
        outjson.add("password", password);
        outbox.setContent(outjson.toString());
        this.send(outbox);
        try {
            System.out.println("Obteniendo respuesta");
            inbox = this.receiveACLMessage();

            if (inbox.getPerformativeInt() == ACLMessage.INFORM) {
                System.out.println("El servidor se ha reiniciado correctamente");
            } else if (inbox.getPerformativeInt() == ACLMessage.FAILURE) {
                System.out.println("La operación de reinicio ha fallado");
            } else if (inbox.getPerformativeInt() == ACLMessage.NOT_UNDERSTOOD) {
                System.out.println("El servidor no nos ha entendido");
            }

        } catch (InterruptedException ex) {
            System.err.println("Ha habido un error al intentar ejecutar la operación de reinicio");
            System.err.println(ex);
        }
    }

    /**
     * Metodo que comunica a Shenron que nos informe del ultimo registro
     *
     * @author Miguel Ángel Torres López
     */
    void consultarRegistro() {
        System.out.println("Consultando registro de Shenron");
        outbox = new ACLMessage(ACLMessage.QUERY_REF);
        outbox.setSender(this.getAid());
        outbox.setReceiver(new AgentID("Shenron"));
        outjson = new JsonObject();
        outjson.add("user", user);
        outjson.add("password", password);
        outbox.setContent(outjson.toString());
        this.send(outbox);
        try {
            System.out.println("Obteniendo respuesta");
            inbox = this.receiveACLMessage();
            injson = Json.parse(inbox.getContent()).asObject();
            JsonObject value = injson.get("value").asObject();
            JsonObject content = value.get("content").asObject();

            if (inbox.getPerformativeInt() == ACLMessage.INFORM) {
                System.out.println("Último registro de actividad del servidor:");
                System.out.println("\tFecha de conexión: " + injson.get("date").asString());
                System.out.println("\tNombre del agente controlador privado: " + value.get("agent").asString());
                System.out.println("\tConversation ID de la ultima sesión: " + value.get("conversation").asString());
                System.out.println("\tDescripción de estado del controlador: " + content.get("status").asString());

            } else if (inbox.getPerformativeInt() == ACLMessage.FAILURE) {
                System.out.println("La operación de consultar registro ha fallado");
            } else if (inbox.getPerformativeInt() == ACLMessage.NOT_UNDERSTOOD) {
                System.out.println("El servidor ha contestado que no nos entiende");
            }

        } catch (InterruptedException ex) {
            System.err.println("Ha habido un error al intentar ejecutar la operación de consultar registro");
            System.err.println(ex);
        }
    }
}
