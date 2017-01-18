/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica3;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 *
 * @author Miguel Ángel Torres López
 * @author Gregorio Carvajal Expósito
 */
public class Agente extends SingleAgent {
    public static final String SERVER_NAME = "Furud";
    public static final String AGENT_CONTROLADOR = "GugelCarRedForest";
    private static final String AGENTES_CONVERSATION_ID = "grupo-6-agentes";
    private String conversationIDServer;
	private String conversationIDControlador;
	private String repyWithServer;
	private EstadoAgente miEstado;
	private boolean percepcionRecibida;
	private boolean percepcionSolicitada;

    public Agente(AgentID aid) throws Exception {
        super(aid);
		miEstado = new EstadoAgente(aid.toString());
    }
    
    /**
     * Inicialización de las variables antes de la ejecución del agente
     * 
     * @author Miguel Ángel Torres López
     */
    @Override
    public void init(){
        System.out.println("\n\nInicializando estado del agente...");
        System.out.flush();
        this.conversationIDServer="";
        this.conversationIDControlador="";
        this.repyWithServer="";
        this.percepcionRecibida=false;
        this.percepcionSolicitada=false;
    }
    
    /**
     * Método que ejecutará el agente cuando se inicie
     * 
     * @author Miguel Ángel Torres López
     */
    @Override
    public void execute(){
        try {
            //Solicito el server id al controlador
            this.solicitarConversationID();
            
            //Llamo al metodo recibir para esperar el conversation id del server
            this.recibir();
            
            //Una vez ya ha recibido el agente el id de conversaicon del server, hago checkin en este
            this.checkin();
            
            //Recibo una respuesta del servidor al checkin con las capabilities
            this.recibir();

			
			
            while(!miEstado.isPisandoObjetivo())
            {
				//Espero a recibir un mensaje del controlador consultando el estado del agente
				//y envio el el estado del agente al controlador
				this.recibir();

				//Solicito al servidor las percepciones del agente
				this.solicitarPercepcion();

				//Recibo las percepciones que el servidor me manda en respuesta a la solicitud
				this.recibir();        

				//Espero a recibir un mensaje del controlador con la acción que realizar
				//y mando esa accion al server
				this.recibir();

				//Espero a recibir un mensaje del servidor con la respuesta a la accion realizada
				//e informo al controlador del resultado
				this.recibir();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Agente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Método que se ejecutará cuando el agente vaya a finalizar su ejecución
     *
     * @author Miguel Ángel Torres López
     */
    @Override
    public void finalize() {
        System.out.println("Finalizando agente...");
        System.out.flush();
        super.finalize();
    }
	
	/**
	 * Ejecua el recieve y actualiza las variables necesarias del controlador
	 * 
	 * @author Gregorio Carvajal Expósito
	 * @throws java.lang.InterruptedException
	 */
	public void recibir() throws InterruptedException {
		ACLMessage resp = receiveACLMessage();
		JsonObject json = Json.parse(resp.getContent()).asObject();
		
		switch (resp.getPerformativeInt()) {
			case ACLMessage.INFORM:
                            String resp_InReplyTo = resp.getInReplyTo();
                            String miEstado_ReplyWith = miEstado.getReplyWithControlador();
				if (resp_InReplyTo!=null&&miEstado_ReplyWith!=null&&resp!=null&&miEstado!=null&&resp_InReplyTo.compareTo(miEstado_ReplyWith)==0) //Respuesta conversationID del server
				{
					conversationIDServer = json.get("serverID").asString();
					conversationIDControlador = resp.getConversationId();
				}
				else if (!json.get("result").isObject()&&json.get("result").asString().compareTo("OK")==0) //Capabilities o Accion ok
				{
					if (json.names().size() > 1) //Capabilities
					{
						JsonObject capabilities = json.get("capabilities").asObject();
						this.repyWithServer = resp.getReplyWith();
						int visibilidad = capabilities.get("range").asInt();
						
						switch (visibilidad)
						{
							case CapacidadesAgentes.VISIBILIDAD_CAMION:
								miEstado.setTipo(TiposAgente.camion);
								break;
								
							case CapacidadesAgentes.VISIBILIDAD_COCHE:
								miEstado.setTipo(TiposAgente.coche);
								break;
								
							case CapacidadesAgentes.VISIBILIDAD_DRON:
								miEstado.setTipo(TiposAgente.dron);
								break;
						}
						
					}
					else //Accion ok
						informarResultadoAccion();
				}
				else //Percepciones
				{
					JsonObject percepcion = json.get("result").asObject();
					JsonArray radar = percepcion.get("sensor").asArray();
                    int[][] radar_percibido = new int[miEstado.getVisibilidad()][miEstado.getVisibilidad()];
					int x, y;
					
					miEstado.setFuelActual(percepcion.get("battery").asInt());
					
					x = percepcion.get("x").asInt();
					y = percepcion.get("y").asInt();
					miEstado.setPosicion(new Pair<>(y, x));

                    for (int i = 0; i < miEstado.getVisibilidad()*miEstado.getVisibilidad(); i++) {
                        radar_percibido[i / miEstado.getVisibilidad()][i % miEstado.getVisibilidad()] = radar.get(i).asInt();
                    }
					miEstado.setPercepcion(radar_percibido);
					
					miEstado.setPisandoObjetivo(percepcion.get("goal").asBoolean());
					
					percepcionRecibida = true;
					
					if (percepcionSolicitada)
						enviarEstado();
					
				}
				
				break;
				
			case ACLMessage.QUERY_REF:
				if (percepcionRecibida)
					enviarEstado();
				else
					percepcionSolicitada = true;
				
				break;
				
			case ACLMessage.REQUEST:
				Acciones accion = Acciones.valueOf(json.get("command").asString());
				miEstado.setNextAction(accion);
				ejecutarAccion(accion);
				break;
			
			default:
				informarError(resp);
				break;
		}
	}
	
	/**
	 * Responde al Controlador (send) enviandole
	 * las variables de estado del agente
	 * 
	 * @author Gregorio Carvajal Expósito
	 */
	public void enviarEstado() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		JsonObject json = new JsonObject();
		JsonObject estado = new JsonObject();
		JsonArray radar = new JsonArray();
		
		estado.add("i", miEstado.getPosicion().getKey());
		estado.add("j", miEstado.getPosicion().getValue());
		estado.add("fuelActual", miEstado.getFuelActual());
		estado.add("crashed", miEstado.isCrashed());
		estado.add("pisandoObjetivo", miEstado.isPisandoObjetivo());
		estado.add("replyWithControlador", miEstado.getReplyWithControlador());
		estado.add("tipo", miEstado.getTipo().toString());
		
		if (miEstado.getNextAction() == null)
			estado.add("nextAction", "");
		else
			estado.add("nextAction", miEstado.getNextAction().toString());
		
		//Meter la matriz en JsonArray
		for (int i = 0; i < miEstado.getVisibilidad(); i++) {
			for (int j = 0; j < miEstado.getVisibilidad(); j++) {
				radar.add(miEstado.getPercepcion()[i][j]);
			}
		}
		estado.add("percepcion", radar);
		
		json.add("estado", estado);
		
		msg.setSender(this.getAid());
		msg.setContent(json.toString());
		msg.setReceiver(new AgentID(AGENT_CONTROLADOR));
		msg.setConversationId(conversationIDControlador);
		msg.setReplyWith(miEstado.getReplyWithControlador());
		
		send(msg);
		percepcionRecibida = false;
		percepcionSolicitada = false;
	}
	
	/**
	 * Manda al Server (send) una peticion para moverse o repostar
	 * 
	 * @author Gregorio Carvajal Expósito
	 */
	public void ejecutarAccion(Acciones accion) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		JsonObject json = new JsonObject();
		
		json.add("command", accion.toString());
		
		msg.setSender(this.getAid());
		msg.setContent(json.toString());
		msg.setReceiver(new AgentID(SERVER_NAME));
		msg.setConversationId(conversationIDServer);
		msg.setInReplyTo(repyWithServer);
		
		send(msg);
	}
	
	/**
	 * Realiza el Checkin en el Server
	 * 
	 * @author Gregorio Carvajal Expósito
	 */
	public void checkin() {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		JsonObject json = new JsonObject();
		
		json.add("command", "checkin");
		
		msg.setSender(this.getAid());
		msg.setContent(json.toString());
		msg.setReceiver(new AgentID(SERVER_NAME));
		msg.setConversationId(conversationIDServer);
		
		send(msg);
	}
	
	/**
	 * Informa al Controlador (send) si se ha ejecutado o no la accion
	 * 
	 * @author Gregorio Carvajal Expósito
	 */
	public void informarResultadoAccion() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		JsonObject json = new JsonObject();
		
		json.add("result", "ok");
		
		msg.setSender(this.getAid());
		msg.setContent(json.toString());
		msg.setReceiver(new AgentID(AGENT_CONTROLADOR));
		msg.setConversationId(conversationIDControlador);
		msg.setReplyWith(miEstado.getReplyWithControlador());
		
		send(msg);
	}
	
	/**
	 * Envia al Server (send) una peticion para que le
	 * responda con las percepciones del agente
	 * 
	 * @author Gregorio Carvajal Expósito
	 */
	public void solicitarPercepcion() {
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
		
		msg.setSender(this.getAid());
		msg.setReceiver(new AgentID(SERVER_NAME));
		msg.setConversationId(conversationIDServer);
		msg.setInReplyTo(repyWithServer);
		
		send(msg);
	}
	
	/**
	 * Manda un mensaje FAILURE al Controlador
	 * indicando los motivos del error
	 * 
	 * @author Gregorio Carvajal Expósito
	 * @param msg Mensaje recibido del Server
	 */
	public void informarError(ACLMessage msg) {
		JsonObject jsonErr = new JsonObject();
		JsonObject jsonMsg = Json.parse(msg.getContent()).asObject();
		ACLMessage err = new ACLMessage(ACLMessage.FAILURE);
		String str = msg.getPerformative().toString() + " -- ";
		
		str += jsonMsg.getString("details", "");
		jsonErr.add("details", str);
		
		msg.setSender(this.getAid());
		msg.setReceiver(new AgentID(AGENT_CONTROLADOR));
		msg.setContent(jsonErr.toString());
		msg.setConversationId(conversationIDControlador);
		msg.setReplyWith(miEstado.getReplyWithControlador());
		
		send(msg);
	}
	
	/**
	 * Solicita (send) al Controlador que le informe de
	 * los ConversationID que se van a usar en las comunicaciones
	 * 
	 * @author Gregorio Carvajal Expósito
	 */
	public void solicitarConversationID() {
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
		JsonObject json = new JsonObject();
		
		json.add("query", "server-conversation-id");
		
		msg.setSender(this.getAid());
		msg.setContent(json.toString());
		msg.setReceiver(new AgentID(AGENT_CONTROLADOR));
		msg.setReplyWith(miEstado.getReplyWithControlador());
                msg.setConversationId(AGENTES_CONVERSATION_ID);
                System.out.println("Respondeme con: "+miEstado.getReplyWithControlador());
                System.out.flush();
                System.out.println("Se lo envio a: "+msg.getReceiver());
                System.out.flush();
		
		send(msg);
	}
}
