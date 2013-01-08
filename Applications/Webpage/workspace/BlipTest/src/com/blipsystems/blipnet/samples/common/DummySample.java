package com.blipsystems.blipnet.samples.common;

import com.blipsystems.blipnet.api.blipnode.BlipNodeHandle;
import com.blipsystems.blipnet.api.blipnode.ConnectData;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;

public class DummySample implements BlipNetSample {
    public void startSample(BlipNodeHandle handle, ConnectData connectData) {
        try {
            System.out.println("com.blipsystems.blipnet.samples.common.DummySample got a handle to " + handle.getBlipNodeSettings().getFriendlyName() + " and is supposed to connect to " + connectData.getTerminalID());
            System.out.println();

            StringBuffer sb = new StringBuffer("Specify desired sample app on command line. Available Samples:\n");
            sb.append("bnsettings.SettingsSample\n");
            sb.append("ftpclient.FtpClientSample\n");
            sb.append("oppclient.OppClientPushSample\n");
            sb.append("oppclient.OppClientExchangeSample\n");
            sb.append("oppserver.ImageDisplay\n");
            sb.append("pannap.PanNapSample\n");
            sb.append("sppchatclient.SppChatClient\n");
            sb.append("sppchatserver.SppChatServer\n");
            sb.append("statscollector.StatisticsCollector\n");

            System.out.println(sb.toString());

            System.exit(0);
        } catch (BlipServerConnectionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
