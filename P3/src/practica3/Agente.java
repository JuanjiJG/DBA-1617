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

/**
 *
 * @author m_ang
 * @author Gregorio Carvajal Expósito
 */
public class Agente extends SingleAgent {
    private String conversationIDServer;
	private String conversationIDControlador;
	private String repyWithServer;
	private EstadoAgente miEstado;
	private boolean percepcionRecibida = false;
	private boolean percepcionSolicitada = false;
	
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
				if (resp.getInReplyTo().equals(miEstado.getReplyWithControlador())) //Respuesta conversationID del server
				{
					conversationIDServer = json.get("serverID").asString();
					conversationIDControlador = resp.getConversationId();
				}
				else if (json.get("result").asString().equals("ok")) //Capabilities o Accion ok
				{
					if (json.names().size() > 1) //Capabilities
					{
						//Meter tipo agente en EstadoAgente
					}
					else //Accion ok
						informarResultadoAccion();
				}
				else //Percepciones
				{
					//todo
					
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
				ejecutarAccion(accion);
				break;
			
			default:
				break; //Redirigirlo al controlador
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
		
		json.add("estado", ""); //Aun no se como encapsular EstadoAgente
		
		msg.setSender(this.getAid());
		msg.setContent(json.toString());
		msg.setReceiver(new AgentID(Controlador.AGENT_ID));
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
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
		JsonObject json = new JsonObject();
		
		json.add("command", accion.toString());
		
		msg.setSender(this.getAid());
		msg.setContent(json.toString());
		msg.setReceiver(new AgentID(Controlador.SERVER_NAME));
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
		
		json.add("command", "checking");
		
		msg.setSender(this.getAid());
		msg.setContent(json.toString());
		msg.setReceiver(new AgentID(Controlador.SERVER_NAME));
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
		msg.setReceiver(new AgentID(Controlador.AGENT_ID));
		msg.setConversationId(conversationIDServer);
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
		msg.setReceiver(new AgentID(Controlador.SERVER_NAME));
		msg.setConversationId(conversationIDServer);
		msg.setInReplyTo(repyWithServer);
		
		send(msg);
	}
}
