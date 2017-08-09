package com.sparrow.collect.utils.uuid;

import java.net.InetAddress;

/**
 * @author S
 */
public class UUIDGenerator {
    // ip地址
    private static final int IP;
    // 计数器
    private static short counter = (short) 0;
    // jvm 虚拟机
    private static final int JVM = (int) (System.currentTimeMillis() >>> 8);
    private static final String IP_STR;
    private static final String JVM_STR = format(JVM);

    // 获取当前地址
    static {
        int ipAdd;
        try {
            ipAdd = toInt(InetAddress.getLocalHost().getAddress());
        } catch (Exception e) {
            ipAdd = 0;
        }
        IP = ipAdd;
        IP_STR = format(IP);
    }

    /**
     * Convert a byte array IPv4 address into an int.
     *
     * @param bytes
     * @return
     */
    public static int toInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) - Byte.MIN_VALUE + (int) bytes[i];
        }
        return result;
    }


    public UUIDGenerator() {
    }

    /**
     * Unique across JVMs on this machine (unless they load this class in the
     * same quater second - very unlikely)
     */
    protected static int getJVM() {
        return JVM;
    }

    /**
     * Unique in a millisecond for this JVM instance (unless there are >
     * Short.MAX_VALUE instances created in a millisecond)
     */
    protected static short getCount() {
        if (counter < 0)
            counter = 0;
        return counter++;
    }

    /**
     * Unique in a local network
     */
    protected static int getIP() {
        return IP;
    }

    /**
     * Unique down to millisecond
     */
    protected static short getHiTime() {
        return (short) (System.currentTimeMillis() >>> 32);
    }

    protected static int getLoTime() {
        return (int) System.currentTimeMillis();
    }

    private final static String sep = "-";

    protected static String format(int val) {
        String formatted = Integer.toHexString(val);
        StringBuffer buf = new StringBuffer("00000000");
        buf.replace(8 - formatted.length(), 8, formatted);
        return buf.toString();
    }

    protected static String format(short val) {
        String formatted = Integer.toHexString(val);
        StringBuffer buf = new StringBuffer("0000");
        buf.replace(4 - formatted.length(), 4, formatted);
        return buf.toString();
    }

    public static String generate() {
        return new StringBuffer(36).append(IP_STR).append(JVM_STR).append(format(getHiTime())).append(
                format(getLoTime())).append(format(getCount())).toString();
    }

    public static String generateGuid() {
        return new StringBuffer(36).append(IP_STR).append(sep).append(JVM_STR).append(sep).append(format(getHiTime()))
                .append(sep).append(format(getLoTime())).append(sep).append(
                        format(getCount())).toString();
    }
}
