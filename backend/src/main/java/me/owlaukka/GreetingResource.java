package me.owlaukka;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import me.owlaukka.api.GreetingApi;
import me.owlaukka.model.Greeting;

import java.time.OffsetDateTime;

@ApplicationScoped
public class GreetingResource implements GreetingApi {

    @Override
    public Response getGreeting(String name) {
        Greeting greeting = new Greeting();
        greeting.setMessage("Hello " + (name != null ? name : "World") + "!");
        greeting.setTimestamp(OffsetDateTime.now());
        return Response.ok(greeting).build();
    }
}
