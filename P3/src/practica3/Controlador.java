/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
	
	private static final String SERVER_NAME = "Furud";
	private static final String AGENTES_CONVERSATION_ID = "grupo-6-agentes";
	private final int MUNDO_ELEGIDO; //Debe asignarse en el constructor, por parametro
	private String conversationID;
	private Map<String, AgentID> agentesMAP = new HashMap<>(); //Inicializar asi en constructor
    
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
				//Del servidor
				if (! resp.getConversationId().equals(AGENTES_CONVERSATION_ID))
				{
					if (json.get("result") != null) //OK to subscribe
					{
						conversationID = resp.getConversationId();
					}
					else //Trace
					{
						//PROCESAR TRAZA
					}
				}
				else //De un agente
				{
					//Respuesta al QUERY_REF pidiendo el estado
					if (json.get("estado") != null)
					{
						JsonObject jsonEstado = json.get("estado").asObject();
						//Aun no se como construir el objeto EstadoAgente
					}
					else //Respuesta al REQUEST de la accion escogida
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
	 * Informa (send)Solo del  al agente seleccionado su siguiente accion
	 * 
	 * @author Gregorio Carvajal Expósito
	 * @param agenteElegido Clase en la que tenemos el agente elegido
	 * y la accion siguiente
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
	 * Manda (send) a todos los agentes el ConversationID necesario
	 * para comunicarse con el Servidor
	 * 
	 * @author Gregorio Carvajal Expósito
	 */
	public void compartirConversationID() {
		JsonObject json = new JsonObject();
		json.add("result", conversationID);
		
		for (Map.Entry<String, AgentID> agente : agentesMAP.entrySet())
		{
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
		
		for (Map.Entry<String, AgentID> agente : agentesMAP.entrySet())
		{
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
	 * Realiza el SUBSCRIBE (send) al Servidor
	 * JsonObject json = new JsonObject();
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
	 * Realiza un subscribe y despues un logout para obtener una traza vacia
	 * y asi poder obtener el tamaño del mapa
	 * 
	 * @author Gregorio Carvajal Expósito
	 */
	public void obtenerTamanoMapa() {
		
	}
}
