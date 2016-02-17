/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.restfull;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.atmosphere.annotation.Broadcast;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.jersey.Broadcastable;
import org.atmosphere.jersey.SuspendResponse;

/**
 *
 * @author Sandra
 */
@Path("/{topic}")
public class CMTPubSub {
    private
    @PathParam("topic")
    Broadcaster topic;

    @GET
    public SuspendResponse<String> subscribe() {
        return new SuspendResponse.SuspendResponseBuilder<String>()
                .broadcaster(topic)
                .outputComments(true)
                .build();
    }
    
    @Broadcast(writeEntity = false)
    @POST
    @Produces("application/json")
    public Broadcastable publishing(String message){
       
        return new Broadcastable(message, topic);        
    }
    
}
