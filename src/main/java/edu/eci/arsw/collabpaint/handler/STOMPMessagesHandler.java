package edu.eci.arsw.collabpaint.handler;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import edu.eci.arsw.collabpaint.model.Point;

@Controller
public class STOMPMessagesHandler {
	
	@Autowired
    SimpMessagingTemplate msgt;
    Map<String,ArrayList<Point>> valores =new  ConcurrentHashMap <String,ArrayList<Point>>();
    ArrayList<Point> lisPoligonos;
    @MessageMapping("/newpoint.{numdibujo}")

    public void handlePointEvent(Point pt, @DestinationVariable String numdibujo)  {
        if (!valores.containsKey(numdibujo)){
            ArrayList <Point> temp = new ArrayList<Point>();
            temp.add(pt);
            valores.put(numdibujo,temp);
            msgt.convertAndSend("/topic/newpoint."+numdibujo, pt);
        }
        else{
            lisPoligonos = valores.get(numdibujo);
            lisPoligonos.add(pt);
            valores.replace(numdibujo,lisPoligonos);
            if(lisPoligonos.size() %4 == 0){
                ArrayList<Point> temp = new ArrayList<>();
                for (int i = 1; i<lisPoligonos.size()+1; i++){
                    if(i % 4 == 0 && i != 0){
                        temp.add(lisPoligonos.get(i-1));
                        msgt.convertAndSend("/topic/newpolygon."+numdibujo, temp);
                        //System.out.println("ENTROOOOOOO");
                        temp = new ArrayList<>();
                    }
                    else{
                        temp.add(lisPoligonos.get(i-1));
                    }
                }


            }
            else {
                System.out.println("Nuevo punto recibido en el servidor!:"+pt);
                msgt.convertAndSend("/topic/newpoint."+numdibujo, pt);
            }
        }

    }
}