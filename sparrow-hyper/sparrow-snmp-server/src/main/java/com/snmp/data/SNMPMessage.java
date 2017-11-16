/*
 * SNMP Package
 *
 * Copyright (C) 2004, Jonathan Sevy <jsevy@mcs.drexel.edu>
 *
 * This is free software. Redistribution and use in source and binary forms, with
 * or without modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation 
 *     and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products 
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED 
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO 
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.snmp.data;

import java.util.Vector;

import com.snmp.exceptions.SNMPBadValueException;
import com.snmp.log.LogFactory;
import com.snmp.log.Logger;
import com.snmp.pdu.SNMPPDU;
import com.snmp.pdu.SNMPSequence;

/**
 * Defines the SNMPMessage class as a special case of SNMPSequence. Defines a
 * top-level SNMP services, as per the following definitions from RFC 1157 and
 * RFC 1901.
 * 
 * 
 * RFC1157-SNMP DEFINITIONS
 * 
 * IMPORTS FROM RFC1155-SMI; -- top-level services
 * 
 * Message ::= SEQUENCE { version -- version-1 for this RFC INTEGER {
 * version-1(0) },
 * 
 * community -- community name OCTET STRING,
 * 
 * data -- e.g., PDUs if trivial ANY -- authentication is being used } -- From
 * RFC 1901:
 * 
 * COMMUNITY-BASED-SNMPv2 DEFINITIONS ::= BEGIN -- top-level services
 * 
 * Message ::= SEQUENCE { version INTEGER { version(1) -- modified from RFC 1157 },
 * 
 * community -- community name OCTET STRING,
 * 
 * data -- PDUs as defined in [4] ANY } }
 * 
 * END
 * 
 */

public class SNMPMessage extends SNMPSequence {

	public static final Logger logger = LogFactory.getLogger();

	/**
	 * Create an SNMP services with specified version, community, and pdu. Use
	 * version = 0 for SNMP version 1, or version = 1 for enhanced capapbilities
	 * provided through RFC 1157.
	 */

	public SNMPMessage(int version, String community, SNMPPDU pdu) {
		super();
		Vector contents = new Vector();
		contents.insertElementAt(new SNMPInteger(version), 0);
		contents.insertElementAt(new SNMPOctetString(community), 1);
		contents.insertElementAt(pdu, 2);

		try {
			this.setValue(contents);
		} catch (SNMPBadValueException e) {
			// can't happen! all supplied Vector elements are SNMP Object
			// subclasses
		}
	}

	/**
	 * Create an SNMP services with specified version, community, and trap pdu.
	 * Use version = 0 for SNMP version 1, or version = 1 for enhanced
	 * capapbilities provided through RFC 1157.
	 */

	public SNMPMessage(int version, String community, SNMPv1TrapPDU pdu) {
		super();
		Vector contents = new Vector();
		contents.insertElementAt(new SNMPInteger(version), 0);
		contents.insertElementAt(new SNMPOctetString(community), 1);
		contents.insertElementAt(pdu, 2);

		try {
			this.setValue(contents);
		} catch (SNMPBadValueException e) {
			// can't happen! all supplied Vector elements are SNMP Object
			// subclasses
		}
	}

	/**
	 * Create an SNMP services with specified version, community, and v2 trap
	 * pdu. Use version = 1.
	 */

	public SNMPMessage(int version, String community, SNMPv2TrapPDU pdu) {
		super();
		Vector contents = new Vector();
		contents.insertElementAt(new SNMPInteger(version), 0);
		contents.insertElementAt(new SNMPOctetString(community), 1);
		contents.insertElementAt(pdu, 2);

		try {
			this.setValue(contents);
		} catch (SNMPBadValueException e) {
			// can't happen! all supplied Vector elements are SNMP Object
			// subclasses
		}
	}

	/**
	 * Construct an SNMPMessage from a received ASN.1 byte representation.
	 * 
	 * @throws SNMPBadValueException
	 *             Indicates invalid SNMP services encoding supplied.
	 */

	public SNMPMessage(byte[] enc) throws SNMPBadValueException {
		super(enc);

		// validate the services: make sure we have the appropriate pieces
		Vector contents = (Vector) (this.getValue());

		if (contents.size() != 3) {
			throw new SNMPBadValueException("Bad SNMP services");
		}

		if (!(contents.elementAt(0) instanceof SNMPInteger)) {
			throw new SNMPBadValueException("Bad SNMP services: bad version");
		}

		if (!(contents.elementAt(1) instanceof SNMPOctetString)) {
			throw new SNMPBadValueException(
					"Bad SNMP services: bad community name");
		}

		if (!(contents.elementAt(2) instanceof SNMPPDU)
				&& !(contents.elementAt(2) instanceof SNMPv1TrapPDU)
				&& !(contents.elementAt(2) instanceof SNMPv2TrapPDU)) {
			throw new SNMPBadValueException("Bad SNMP services: bad PDU");
		}

	}

	/**
	 * Utility method which returns the PDU contained in the SNMP services as a
	 * plain Java Object. The pdu is the third component of the sequence, after
	 * the version and community name.
	 */

	public Object getPDUAsObject() throws SNMPBadValueException {
		Vector contents = (Vector) (this.getValue());
		StringBuffer snmpStr = new StringBuffer();
		snmpStr.append("  ");
		for (int i = 0; i < contents.size(); i++) {
			if (i != 0)
				snmpStr.append(" ");
			SNMPObject snmpObj = (SNMPObject) contents.elementAt(i);
			snmpStr.append(snmpObj.toString());
		}
		logger.info(snmpStr.toString());
		Object pdu = contents.elementAt(2);
		return pdu;
	}

	/**
	 * Utility method which returns the PDU contained in the SNMP services. The
	 * pdu is the third component of the sequence, after the version and
	 * community name.
	 */

	public SNMPPDU getPDU() throws SNMPBadValueException {
		Vector contents = (Vector) (this.getValue());
		Object pdu = contents.elementAt(2);

		if (!(pdu instanceof SNMPPDU)) {
			throw new SNMPBadValueException(
					"Wrong PDU type in services: expected SNMPPDU, have "
							+ pdu.getClass().toString());
		}

		return (SNMPPDU) pdu;
	}

	/**
	 * Utility method which returns the PDU contained in the SNMP services as an
	 * SNMPv1TrapPDU. The pdu is the third component of the sequence, after the
	 * version and community name.
	 */

	public SNMPv1TrapPDU getv1TrapPDU() throws SNMPBadValueException {
		Vector contents = (Vector) (this.getValue());
		Object pdu = contents.elementAt(2);

		if (!(pdu instanceof SNMPv1TrapPDU)) {
			throw new SNMPBadValueException(
					"Wrong PDU type in services: expected SNMPTrapPDU, have "
							+ pdu.getClass().toString());
		}

		return (SNMPv1TrapPDU) pdu;
	}

	/**
	 * Utility method which returns the PDU contained in the SNMP services as an
	 * SNMPv2TrapPDU. The pdu is the third component of the sequence, after the
	 * version and community name.
	 */

	public SNMPv2TrapPDU getv2TrapPDU() throws SNMPBadValueException {
		Vector contents = (Vector) (this.getValue());
		Object pdu = contents.elementAt(2);

		if (!(pdu instanceof SNMPv2TrapPDU)) {
			throw new SNMPBadValueException(
					"Wrong PDU type in services: expected SNMPv2TrapPDU, have "
							+ pdu.getClass().toString());
		}

		return (SNMPv2TrapPDU) pdu;
	}

	/**
	 * Utility method which returns the community name contained in the SNMP
	 * services. The community name is the second component of the sequence,
	 * after the version.
	 */

	public String getCommunityName() throws SNMPBadValueException {
		Vector contents = (Vector) (this.getValue());
		Object communityName = contents.elementAt(1);

		if (!(communityName instanceof SNMPOctetString)) {
			throw new SNMPBadValueException(
					"Wrong SNMP type for community name in services: expected SNMPOctetString, have "
							+ communityName.getClass().toString());
		}

		return ((SNMPOctetString) communityName).toString();
	}

}