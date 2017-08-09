package com.sparrow.transfer.target;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.sparrow.transfer.constant.StatusCode;
import com.sparrow.transfer.exceptions.ProtocolAnalyzerException;
import com.sparrow.transfer.exceptions.TaskException;
import com.sparrow.transfer.protocol.ProtocolAnalyzer;
import com.sparrow.transfer.protocol.ProtocolMessage;
import com.sparrow.transfer.utils.StringUtils;

public class TargetBuilder {
	public static final Map<String, Class<? extends AbstractTarget>> clazCache = new HashMap<String, Class<? extends AbstractTarget>>(
			0);

	public static AbstractTarget getTarget(String url) throws TaskException {
		try {
			ProtocolMessage msg = ProtocolAnalyzer.analyzeSpecialURI(url);
			String protocol = msg.getProtocol();
			Class<? extends AbstractTarget> claz = clazCache.get(protocol);
			if (claz == null) {
				String targetClaz = "com.sparrow.transfer.target."
						+ StringUtils.capitalize(protocol) + "Target";
				Class<?> clz = Class.forName(targetClaz);
				if (AbstractTarget.class.isAssignableFrom(clz))
					claz = clz.asSubclass( AbstractTarget.class);
				clazCache.put(protocol, claz);
			}
			Constructor<? extends AbstractTarget> con = claz
					.getConstructor(new Class[] { ProtocolMessage.class });
			AbstractTarget targetInstantce = (AbstractTarget) con
					.newInstance(new Object[] { msg });
			return targetInstantce;
		} catch (ProtocolAnalyzerException e) {
			throw new TaskException(StatusCode.PROTOCOL_PARSER_ERROR,
					" Protocol analyzer exception URL:" + url);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		throw new TaskException(StatusCode.TARGET_CREATE_ERROR,
				"Can't build this target:" + url);
	}

	public static void main(String args[]) {
		String url = "fIle:///D:/tsd/t.txt";
		try {
			AbstractTarget target = getTarget(url);
			System.out.println(target);
			target.initialize();
			System.out.println(target.isInitiated());
			System.out.println(target.isSupportChannel());
			System.out.println(target.isSupportStream());
			System.out.println(target.getInputStream());
			System.out.println(target.isSupportChannel());
			System.out.println(target.isSupportStream());
		} catch (TaskException e) {
			e.printStackTrace();
		}
	}
}
