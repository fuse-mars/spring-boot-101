package com.nshimiye.cqrs.reader.akka;

import akka.actor.Props;
import akka.actor.UntypedActor;
import scala.collection.mutable.ArraySeq;

import com.nshimiye.cprs.reader.domain.Spending;

/**
 * In charge of factorial calculation 
 * The result is to the parent (Master object)
 */
public class ReadWorker extends UntypedActor {

    private Spending spending = null;
    public Spending getValue() {
        return spending;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof Long) {
        
            //1. accomplish the task in hand   
            
            //in this example, we read the requested value 
            // saves it into a static variable
            spending = Database.read((Long) message);
            if(spending == null){
            	spending = new Spending((Long)message, null, 0);
            }
            
            //2. After the task is "completely" done,
            //   send notification to kafka
            System.out.println("[ ReadWorker ] done reading from db");
            getSender().tell(spending, getSelf());
        
        } else if (message instanceof com.nshimiye.cqrs.writer.domain.Spending) { 
        	// 
        	System.out.println("[ ReadWorker ] done writing to db");
        	
        } else
            unhandled(message);
    }

    public static Props createWorker() {
        return Props.create(ReadWorker.class, new ArraySeq<Object>(0));
    }
}