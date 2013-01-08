package com.blipsystems.blipnet.samples.oppclient;

import com.blipsystems.blipnet.api.blipnode.BlipNodeHandle;
import com.blipsystems.blipnet.api.blipnode.ConnectData;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;
import com.blipsystems.blipnet.api.event.EventCause;
import com.blipsystems.blipnet.api.profile.opp.OppClientEvent;
import com.blipsystems.blipnet.api.profile.opp.OppClientEventListener;
import com.blipsystems.blipnet.api.profile.opp.OppException;
import com.blipsystems.blipnet.samples.common.BlipNetSample;

import java.nio.ByteBuffer;

public class OppClientExchangeSample implements BlipNetSample, OppClientEventListener {

    public void startSample(BlipNodeHandle handle, ConnectData connectData) {
        try {
            handle.getOppClientHandler().addEventListener(this);
            handle.getOppClientHandler().exchangeBusinessCards(connectData);
        } catch (BlipServerConnectionException e) {
            System.out.println("Problem communicating with server. Exiting...");

            e.printStackTrace();

            System.exit(-1);
        } catch (OppException e) {
            System.out.println("Error exchanging businesscards with " + connectData.getTerminalID() + ": " + e.getMessage());
            if (e.getCause() != null) System.out.println("Caused by: " + e.getCause().getMessage());
            System.exit(-1);
        }
    }

    private ByteBuffer businessCard = ByteBuffer.allocateDirect(10000); // 10KB should be enough for most business cards

    public void handleOppClientEvent(OppClientEvent evt) {
        int eventID = evt.getEventID();

        switch (eventID) {
            case OppClientEvent.OBEX_BUSINESS_CARD_EXCHANGE_COMPLETED:
                businessCard.flip();
                byte[] bytes = new byte[businessCard.remaining()];
                businessCard.get(bytes);
                String cardData = new String(bytes);

                System.out.println("Business Cards exchanged with " + evt.getTerminalID());
                System.out.println("Business card data:");
                System.out.println(cardData);
                System.exit(0);
                break;

            case OppClientEvent.OBEX_BUSINESS_CARD_EXCHANGE_FAILED:
                System.out.println("Business Card Exchange with '" + evt.getTerminalID() + "' failed. Cause is " + EventCause.causeName(evt.getCause()));
                System.exit(0);
                break;

            case OppClientEvent.OBJECT_FRAGMENT_RECEIVED:
                businessCard.put(evt.getObjectFragment());
                break;
        }
    }
}
