package com.blipsystems.blipnet.samples.common;

import com.blipsystems.blipnet.api.blipnode.BlipNodeHandle;
import com.blipsystems.blipnet.api.blipnode.ConnectData;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;

public interface BlipNetSample {
    public void startSample(BlipNodeHandle handle, ConnectData connectData) throws BlipServerConnectionException;
}
