package com.blipsystems.blipnet.samples.oppclient;

import com.blipsystems.blipnet.api.blipnode.BlipNodeHandle;
import com.blipsystems.blipnet.api.blipnode.ConnectData;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;
import com.blipsystems.blipnet.api.event.EventCause;
import com.blipsystems.blipnet.api.profile.opp.*;
import com.blipsystems.blipnet.api.profile.opp.pushobjects.ObexFile;
import com.blipsystems.blipnet.api.profile.opp.pushobjects.ObexGenericObject;
import com.blipsystems.blipnet.api.profile.opp.pushobjects.ObexPushObject;
import com.blipsystems.blipnet.samples.common.BlipNetSample;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class OppClientPushSample implements BlipNetSample, OppClientEventListener, ObexProgressListener {
Date start=null, end=null;

    public void startSample(BlipNodeHandle handle, ConnectData connectData) {
        try {
        
            handle.getOppClientHandler().addEventListener(this);
       
            
            ObexPushObject opo = new ObexFile(new File("1.mp3"));
//            ObexGenericObject opo = new ObexGenericObject((new String("Hello")).getBytes());
//            opo.setObexType("text/plain");
            // opo.setObexType("txt");
          //  System.out.println("!!!!opoType:"+opo.getObexType());
            handle.getOppClientHandler().push(connectData, opo, this);


        } catch (BlipServerConnectionException e) {
            System.out.println("Problem communicating with server. Exiting...");
            System.exit(-1);
        } catch (OppException e) {
            System.out.println("Error exchanging businesscards with " + connectData.getTerminalID() + ": " + e.getMessage());
            if (e.getCause() != null) System.out.println("Caused by: " + e.getCause().getMessage());
            System.exit(-1);
        } catch (Exception e) {
            System.out.println("Error loading image.jpg: " + e);
            System.exit(-1);
        }
    }

    public void handleOppClientEvent(OppClientEvent evt) {
        int eventID = evt.getEventID();

        switch (eventID) {
            case OppClientEvent.OBEX_PUSH_COMPLETED:
            	end=new Date();
            	  System.out.println("end:"+end);
                System.out.println(evt.getObexName() + " pushed to " + evt.getTerminalID()+"Time:"+(end.getTime()-start.getTime()));
                System.exit(0);
                break;
            case OppClientEvent.OBEX_PUSH_FAILED:
                System.out.println("Push to '" + evt.getTerminalID() + "' failed. Cause is " + EventCause.causeName(evt.getCause()));
                System.exit(0);
                break;
        }
    }

    public void newProgress(ObexProgressEvent event) {
    	if(start==null){
            start=new Date();
            System.out.println("Start:"+start);
    	}
        System.out.println("Push Progress: " + (event.getBytesCompleted() / event.getObjectSize() * 100) + "% completed");
    }
}
