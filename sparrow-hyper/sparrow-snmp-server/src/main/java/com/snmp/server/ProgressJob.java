package com.snmp.server;

import java.util.Vector;

import com.snmp.data.SNMPMessage;
import com.snmp.data.SNMPObject;
import com.snmp.data.SNMPObjectIdentifier;
import com.snmp.data.SNMPv1TrapPDU;
import com.snmp.data.SNMPv2InformRequestPDU;
import com.snmp.data.SNMPv2TrapPDU;
import com.snmp.exceptions.SNMPBadValueException;
import com.snmp.pdu.SNMPSequence;
import com.snmp.tools.SNMPBERCodec;

/**
 * 
 * <p>
 * Title: ProgressJob
 * </p>
 * <p>
 * Description: com.snmp.server
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Sobey
 * </p>
 * 
 * @author Yzc
 * @version 3.0
 * @date 2009-8-7
 */
public class ProgressJob implements Runnable {
	/** define OID */
	public static final String OID_EMB_JOBID = "1.3.6.1.4.1.83.3.1.1.2.1";
	public static final String OID_EMB_PERCENTAGE = "1.3.6.1.4.1.83.3.1.1.2.2";
	private UDPServer server;
	/** message notice interface */
	private ProgressNotice notice;
	private byte[] udpData;

	ProgressJob(UDPServer server, ProgressNotice notice, byte[] data) {
		this.server = server;
		this.udpData = data;
		this.notice = notice;
	}

	public void run() {
		this.handle(this.udpData);
		// the job finished , minus 1
		this.server.minus();
		this.udpData = null;
	}

	private void handle(byte[] udpData) {
		try {
			if (udpData[0] == 'z')
				return;
			SNMPMessage receivedMessage = new SNMPMessage(SNMPBERCodec
					.extractNextTLV(udpData, 0).value);
			Object receivedPDU = receivedMessage.getPDUAsObject();
			if (receivedPDU instanceof SNMPv1TrapPDU) {
				SNMPv1TrapPDU trap1pdu = (SNMPv1TrapPDU) receivedPDU;
				SNMPSequence seq = trap1pdu.getVarBindList();
				handBindList(seq);
				//
			} else if (receivedPDU instanceof SNMPv2TrapPDU) {
				SNMPv2TrapPDU trap2pdu = (SNMPv2TrapPDU) receivedPDU;
				SNMPSequence seq = trap2pdu.getVarBindList();
				handBindList(seq);
			} else if (receivedPDU instanceof SNMPv2InformRequestPDU) {

			} else {
				throw new SNMPBadValueException(
						"PDU received that's not a v1 or v2 trap or inform request; message payload of type "
								+ receivedPDU.getClass().toString());
			}

		} catch (SNMPBadValueException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * <p>
	 * Description: dd
	 * </p>
	 * 
	 * @param seq
	 * @author Yzc
	 */
	private void handBindList(SNMPSequence seq) {
		Vector v = (Vector) seq.getValue();
		String oidstr = null, tskid = null, percent = null;
		for (int i = 0; i < v.size(); i++) {
			SNMPSequence variableBind = (SNMPSequence) v.elementAt(i);
			SNMPObjectIdentifier oid = (SNMPObjectIdentifier) variableBind
					.getSNMPObjectAt(0);
			SNMPObject val = variableBind.getSNMPObjectAt(1);
			oidstr = oid.toString();
			if (oidstr.equals(OID_EMB_JOBID)) {
				tskid = val.toString();
			} else if (oidstr.equals(OID_EMB_PERCENTAGE)) {
				percent = val.toString();
			}
			if (tskid != null && percent != null)
				break;
		}
		if (this.notice != null)
			this.notice.messageNotice(tskid, percent);
	}
}
