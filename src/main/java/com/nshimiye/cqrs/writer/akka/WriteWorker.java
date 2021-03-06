package com.nshimiye.cqrs.writer.akka;

import akka.actor.Props;
import akka.actor.UntypedActor;
import scala.collection.mutable.ArraySeq;

import com.nshimiye.cqrs.writer.domain.Spending;
import com.nshimiye.messaging.Envelope;

/**
 * In charge of factorial calculation 
 * The result is to the parent (Master object)
 * Use of Untypedctor: 
 * “This is due to the fact its quite difficult to implement a Scala PartialFunction in Java 7 and below”
 *     Excerpt From: Duncan K. DeVore. “Reactive Application Development MEAP V03.” iBooks. 
 */
public class WriteWorker extends UntypedActor {

    @Override
    public void onReceive(Object message) {
    	
    	if (message instanceof Spending) {
        
            //1. accomplish the task in hand   
            
            //in this example, we save food and amount to db (list)
    		Spending savedEntity = Database.write( (Spending) message );
            
            //2. After the task is "completely" done,
            //   send notification to kafka
            System.out.println("[ WriteWorker ] done writing to db");
            Envelope envelope = new Envelope("NewEntry", savedEntity);
            getContext().system().eventStream().publish(envelope);
        
        } else
            unhandled(message);
    }

    public static Props createWorker() {
        return Props.create(WriteWorker.class, new ArraySeq<Object>(0));
    }
}