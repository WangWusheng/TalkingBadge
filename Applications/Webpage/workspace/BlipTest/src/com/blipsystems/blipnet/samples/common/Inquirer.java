package com.blipsystems.blipnet.samples.common;

import com.blipsystems.blipnet.api.blipnode.*;
import com.blipsystems.blipnet.api.event.InquiryEventListener;
import com.blipsystems.blipnet.api.event.InquiryEvent;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;

import java.util.HashMap;

public class Inquirer implements InquiryEventListener {
	private BlipNodeHandle blipnode;
	private boolean getFriendlyNames;
	private HashMap inquiryResults = new HashMap();
	private HashMap connectData = new HashMap();

	private boolean done = false;

	public Inquirer(BlipNodeHandle blipnode) {
		this.blipnode = blipnode;
	}

	public HashMap findDevicesInRange(boolean getFriendlyNames) {
		this.getFriendlyNames = getFriendlyNames;
		InquiryData inquiryData = new InquiryData(8);
		inquiryData.setNameLookUpEnabled(getFriendlyNames);

		try {
			blipnode.lock();
			blipnode.getInquiryHandler().addEventListener(this);
			System.out.print("Inquirer is detecting devices        : ");
			blipnode.getInquiryHandler().startInquiry(inquiryData);
		} catch (BlipServerConnectionException e) {
			e.printStackTrace();
		} catch (BlipNodeHandleInUseException e) {
			e.printStackTrace();
		} catch (BlipNodeNotConnectedException e) {
			e.printStackTrace();
		}

		while (!done) {
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
		}

		return inquiryResults;
	}

	public void handleInquiryEvent(InquiryEvent evt) {
		int eventID = evt.getEventID();

		switch (eventID) {
		case InquiryEvent.TERMINAL_DETECTED:
			ConnectData cd = evt.getConnectData();
			cd.setRssiMeasurementOn(true);
			System.out.println("RSSI:"+	evt.getRssiResult()
					+ " PageData:"+ cd.getPageData() 
					);
		
			inquiryResults.put(cd, "Unknown Name");
			connectData.put(evt.getTerminalID(), cd);
			System.out.print(".");
			break;
		case InquiryEvent.INQUIRY_COMPLETE:
			System.out.println();
			if (getFriendlyNames) {
				System.out.print("Inquirer is detecting Friendly names : ");
			} else {
				done();
			}
			break;
		case InquiryEvent.FRIENDLY_NAME_DETECTED:
			System.out.print(".");
			ConnectData cdata = (ConnectData) connectData.get(evt
					.getTerminalID());
			inquiryResults.put(cdata, evt.getTerminalFriendlyName());
			break;
		case InquiryEvent.NAME_LOOKUP_COMPLETE:
			System.out.println();
			System.out.println();
			done();
			break;
		}
	}

	private void done() {
		try {
			blipnode.getInquiryHandler().removeEventListener(this);
			blipnode.release();
			done = true;
			synchronized (this) {
				notify();
			}
		} catch (BlipServerConnectionException e) {
			e.printStackTrace();
		}
	}
}
