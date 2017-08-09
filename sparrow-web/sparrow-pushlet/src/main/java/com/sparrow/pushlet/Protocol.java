package com.sparrow.pushlet;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-3-18 Time: 下午3:04 To change this
 * template use File | Settings | File Templates.
 */
public class Protocol {

	public static final String E_ABORT = "abort";
	public static final String E_LEAVE = "leave";
	public static final String E_SUBSCRIBE = "subscribe";
	public static final String E_UN_SUBSCRIBE = "un_subscribe";
	public static final String E_DATA = "data";
	public static final String E_DONE = "done";
	public static final String E_PUBLISH = "publish";
	public static final String E_JOIN = "join";
	public static final String E_ACK = "ack";
	public static final String E_HB_ACK = "ack-hb";
	public static final String E_LISTEN = "listen";
	public static final String E_ERR = "error";
	public static final String E_FETCH = "fetch";
	public static final String E_SESSIONS = "sessions";

	public static final String P_SUBJECT = "subject";
	public static final String P_EVENT = "event";
	public static final String P_SUBJECT_ID = "subject_id";
	public static final String P_ID = "id";
	public static final String P_REASON = "reason";
	public static final String P_LABEL = "label";
	public static final String P_FROM = "from";
	public static final String P_TO = "to";
	public static final String P_FORMAT = "format";
}
