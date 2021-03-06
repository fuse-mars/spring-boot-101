package com.nshimiye.cqrs.reader.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.pattern.Patterns;
import akka.util.Timeout;

import com.nshimiye.akka.AkkaFactory;
import com.nshimiye.cprs.reader.domain.Spending;
import com.nshimiye.cqrs.reader.akka.ReadWorker;

@RestController
public class SpendingRController {


    private ActorRef readWorker = AkkaFactory.getActorSystem()
                .actorOf(ReadWorker.createWorker(), "readWorker");

    @RequestMapping("api/expenses/read")
    public Spending recordExpense(
        @RequestParam(value="id") long id
        ) throws Exception {

        System.out.println("read route called");
        System.out.println("Actor reference: " + readWorker.toString());
        System.out.println("Actor path: " + readWorker.path().toString());
        System.out.println("Actor path address: " + readWorker.path().address().toString());
        System.out.println("Actor path name: " + readWorker.path().name());
        ActorSelection readWorker1 = AkkaFactory.getActorSystem()
        		.actorSelection("akka://AKKASystem/user/readWorker");
        
        System.out.println("Actor reference: [] - " + readWorker1.toString());
        System.out.println("Actor path: [] - " + readWorker1.path());
        System.out.println("Actor path anchor: [] - " + readWorker1.anchor());
        System.out.println("Actor path name: [] - " + readWorker1.pathString());
        System.out.println("Actor path serial: [] - " + readWorker1.toSerializationFormat());
        
        // send a command to read the requested value
        readWorker.tell(new Long(id), ActorRef.noSender());
        
        // testing actorSelection
        readWorker1.tell(new Long(id), ActorRef.noSender());
        
        //blocking call uses ask
        FiniteDuration duration = FiniteDuration.apply(10, "seconds");
        
        Future<Object> answer = Patterns
        		.ask(readWorker1, new Long(id), 
        				Timeout.durationToTimeout(duration));
        Object result = Await.result(answer, duration);
        //this would usually be a push request because
        // this function would have to wait for a notification from "readWorker"
        // saying that it has finished writing the value.
        return (Spending) result;
    }
}