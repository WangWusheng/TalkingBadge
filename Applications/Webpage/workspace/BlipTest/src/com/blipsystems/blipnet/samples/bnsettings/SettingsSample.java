package com.blipsystems.blipnet.samples.bnsettings;

import com.blipsystems.blipnet.api.blipnode.BlipNodeHandle;
import com.blipsystems.blipnet.api.blipnode.BlipNodeHandleInUseException;
import com.blipsystems.blipnet.api.blipnode.BlipNodeSettings;
import com.blipsystems.blipnet.api.blipnode.ConnectData;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;
import com.blipsystems.blipnet.samples.common.BlipNetSample;

import java.io.IOException;

public class SettingsSample implements BlipNetSample {

    public void startSample(BlipNodeHandle handle, ConnectData connectData)
            throws BlipServerConnectionException {

        try {
            handle.lock();

            BlipNodeSettings nodeSettings = handle.getBlipNodeSettings();
            nodeSettings.setFriendlyName("My BlipNode");
            handle.setBlipNodeSettings(nodeSettings);

            try {
                System.out.println("Friendly name changed. " +
                                   "Press [ENTER] to revert to original friendly name");
                System.in.read();
            } catch (IOException e) {}

            handle.release();

        } catch (BlipNodeHandleInUseException e) {
            System.out.println("BlipNodeHandle is in use by " + e.getMessage());
        }
    }
}
