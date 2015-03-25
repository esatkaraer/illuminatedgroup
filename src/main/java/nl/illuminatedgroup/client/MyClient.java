/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.illuminatedgroup.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import nl.illuminatedgroup.bankapplicatievoorbeeld.WithdrawRequest;
import nl.illuminatedgroup.bankapplicatievoorbeeld.WithdrawResponse;
import org.glassfish.jersey.jackson.JacksonFeature;

/**
 *
 * @author Esat
 */
public class MyClient
{
    private Client client = ClientBuilder.newClient().register(JacksonFeature.class);
    private String target = "145.24.222.103:8080";
    
    public WithdrawResponse withdraw(WithdrawRequest request)
    {
        WithdrawResponse response = client
                .target(target)
                .path("/withdraw")
                .request()
                .post(Entity.entity(request,MediaType.APPLICATION_JSON),
                        WithdrawResponse.class);
        
        return response;
    }
    
    public WithdrawResponse balance(String IBAN)
    {
         WithdrawResponse response = client
                .target(target)
                .path("/balance/" + IBAN).request().get(WithdrawResponse.class);
         return response;
    }
}
