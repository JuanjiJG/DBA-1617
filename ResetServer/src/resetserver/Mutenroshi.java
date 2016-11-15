/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resetserver;

import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 *
 * @author Luis Castillo
 */
public class Mutenroshi extends SingleAgent {
    ACLMessage outbox, inbox;
    JsonObject injson, outjson;
    String controller, user, password;

    public Mutenroshi(AgentID aid, String c, String u, String p) throws Exception {
        super(aid);
        controller = c;
        user = u;
        password = p;
    }
    
    @Override
    public void execute()  {
        System.out.println("Ejecutando a Mutenroshi");
        Reboot();
        System.out.println("Fin de Mutenroshi");
    }
    
    void Reboot()  {
        System.out.println("Reseteando server "+controller);
        outbox = new ACLMessage();
        outbox.setSender(this.getAid());
        outbox.setReceiver(new AgentID("Shenron"));
        outjson = new JsonObject();
        outjson.add("controller",controller);
        outjson.add("user",user);
        outjson.add("password", password);
        outbox.setContent(outjson.toString());
        this.send(outbox);
        try {
            System.out.println("Obteniendo respuesta");
            inbox = this.receiveACLMessage();
            System.out.println("Respuesta recibida de "+inbox.getSender().getLocalName()+" = " +inbox.getContent());
        } catch (InterruptedException ex) {
        }
    }
    
}
