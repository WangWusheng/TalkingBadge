package com.blipsystems.blipnet.samples.common;

import com.blipsystems.blipnet.api.blipnode.BlipNodeHandle;
import com.blipsystems.blipnet.api.blipnode.ConnectData;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;

public class SampleRunner {
    private BlipNodeHandle blipnode;
    private BlipNetSample sample;

    public SampleRunner(BlipNodeHandle blipnode, String sampleName) throws SampleException {
        this.blipnode = blipnode;

        try {
            Class sampleClass = Class.forName("com.blipsystems.blipnet.samples." + sampleName);
            sample = (BlipNetSample) sampleClass.newInstance();
        } catch (Exception e) {
            throw new SampleException("Error loading sample class", e);
        }
    }

    public void runSample(ConnectData connectData) throws SampleException {
        try {
            sample.startSample(blipnode, connectData);
        } catch (BlipServerConnectionException e) {
            throw new SampleException("Error communicating with server", e);
        }
    }

    public class SampleException extends Exception {
        public SampleException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
