/**  
 * Project Name:http-server  
 * File Name:Base64.java  
 * Package Name:com.sparrow.core.utils.codec  
 * Date:2013-12-30下午6:45:58  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.netty.codec;


/**
 * ClassName:Base64 <br/>
 * Date: 2013-12-30 下午6:45:58 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class Base64 {
	static final int CHUNK_SIZE = 76;
	static final byte[] CHUNK_SEPARATOR = "\r\n".getBytes();
	private static final int BASELENGTH = 255;
	private static final int LOOKUPLENGTH = 64;
	private static final int EIGHTBIT = 8;
	private static final int SIXTEENBIT = 16;
	private static final int TWENTYFOURBITGROUP = 24;
	private static final int FOURBYTE = 4;
	private static final int SIGN = -128;
	private static final byte PAD = (byte) '=';
	private static final byte[] base64Alphabet = new byte[BASELENGTH];
	private static final byte[] lookUpBase64Alphabet = new byte[LOOKUPLENGTH];

	static {
		for (int i = 0; i < BASELENGTH; i++) {
			base64Alphabet[i] = (byte) -1;
		}
		for (int i = 'Z'; i >= 'A'; i--) {
			base64Alphabet[i] = (byte) (i - 'A');
		}
		for (int i = 'z'; i >= 'a'; i--) {
			base64Alphabet[i] = (byte) (i - 'a' + 26);
		}
		for (int i = '9'; i >= '0'; i--) {
			base64Alphabet[i] = (byte) (i - '0' + 52);
		}

		base64Alphabet['+'] = 62;
		base64Alphabet['/'] = 63;

		for (int i = 0; i <= 25; i++) {
			lookUpBase64Alphabet[i] = (byte) ('A' + i);
		}

		for (int i = 26, j = 0; i <= 51; i++, j++) {
			lookUpBase64Alphabet[i] = (byte) ('a' + j);
		}

		for (int i = 52, j = 0; i <= 61; i++, j++) {
			lookUpBase64Alphabet[i] = (byte) ('0' + j);
		}

		lookUpBase64Alphabet[62] = (byte) '+';
		lookUpBase64Alphabet[63] = (byte) '/';
	}

	private static boolean isBase64(byte octect) {
		if (octect == PAD) {
			return true;
		} else // noinspection RedundantIfStatement
		if (octect < 0 || base64Alphabet[octect] == -1) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean isBase64(byte[] arrayOctect) {

		arrayOctect = discardWhitespace(arrayOctect);

		int length = arrayOctect.length;
		if (length == 0) {
			// shouldn't a 0 length array be valid base64 data?
			// return false;
			return true;
		}
		for (int i = 0; i < length; i++) {
			if (!isBase64(arrayOctect[i])) {
				return false;
			}
		}
		return true;
	}

	static byte[] discardWhitespace(byte[] data) {
		byte groomedData[] = new byte[data.length];
		int bytesCopied = 0;

		for (byte aByte : data) {
			switch (aByte) {
			case (byte) ' ':
			case (byte) '\n':
			case (byte) '\r':
			case (byte) '\t':
				break;
			default:
				groomedData[bytesCopied++] = aByte;
			}
		}

		byte packedData[] = new byte[bytesCopied];

		System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);

		return packedData;
	}

	public static String encodeToString(byte[] bytes) {
		byte[] encoded = encode(bytes);
		return CodecSupport.toString(encoded);
	}

	public static byte[] encodeChunked(byte[] binaryData) {
		return encode(binaryData, true);
	}

	public static byte[] encode(byte[] pArray) {
		return encode(pArray, false);
	}

	public static byte[] encode(byte[] binaryData, boolean isChunked) {
		long binaryDataLength = binaryData.length;
		long lengthDataBits = binaryDataLength * EIGHTBIT;
		long fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
		long tripletCount = lengthDataBits / TWENTYFOURBITGROUP;
		long encodedDataLengthLong;
		int chunckCount = 0;

		if (fewerThan24bits != 0) {
			// data not divisible by 24 bit
			encodedDataLengthLong = (tripletCount + 1) * 4;
		} else {
			// 16 or 8 bit
			encodedDataLengthLong = tripletCount * 4;
		}

		// If the output is to be "chunked" into 76 character sections,
		// for compliance with RFC 2045 MIME, then it is important to
		// allow for extra length to account for the separator(s)
		if (isChunked) {

			chunckCount = (CHUNK_SEPARATOR.length == 0 ? 0 : (int) Math
					.ceil((float) encodedDataLengthLong / CHUNK_SIZE));
			encodedDataLengthLong += chunckCount * CHUNK_SEPARATOR.length;
		}

		if (encodedDataLengthLong > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Input array too big, output array would be bigger than Integer.MAX_VALUE="
							+ Integer.MAX_VALUE);
		}
		int encodedDataLength = (int) encodedDataLengthLong;
		byte encodedData[] = new byte[encodedDataLength];

		byte k, l, b1, b2, b3;

		int encodedIndex = 0;
		int dataIndex;
		int i;
		int nextSeparatorIndex = CHUNK_SIZE;
		int chunksSoFar = 0;

		// log.debug("number of triplets = " + numberTriplets);
		for (i = 0; i < tripletCount; i++) {
			dataIndex = i * 3;
			b1 = binaryData[dataIndex];
			b2 = binaryData[dataIndex + 1];
			b3 = binaryData[dataIndex + 2];

			// log.debug("b1= " + b1 +", b2= " + b2 + ", b3= " + b3);

			l = (byte) (b2 & 0x0f);
			k = (byte) (b1 & 0x03);

			byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
					: (byte) ((b1) >> 2 ^ 0xc0);
			byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4)
					: (byte) ((b2) >> 4 ^ 0xf0);
			byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6)
					: (byte) ((b3) >> 6 ^ 0xfc);

			encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
			// log.debug( "val2 = " + val2 );
			// log.debug( "k4 = " + (k<<4) );
			// log.debug( "vak = " + (val2 | (k<<4)) );
			encodedData[encodedIndex + 1] = lookUpBase64Alphabet[val2
					| (k << 4)];
			encodedData[encodedIndex + 2] = lookUpBase64Alphabet[(l << 2)
					| val3];
			encodedData[encodedIndex + 3] = lookUpBase64Alphabet[b3 & 0x3f];

			encodedIndex += 4;

			// If we are chunking, let's put a chunk separator down.
			if (isChunked) {
				// this assumes that CHUNK_SIZE % 4 == 0
				if (encodedIndex == nextSeparatorIndex) {
					System.arraycopy(CHUNK_SEPARATOR, 0, encodedData,
							encodedIndex, CHUNK_SEPARATOR.length);
					chunksSoFar++;
					nextSeparatorIndex = (CHUNK_SIZE * (chunksSoFar + 1))
							+ (chunksSoFar * CHUNK_SEPARATOR.length);
					encodedIndex += CHUNK_SEPARATOR.length;
				}
			}
		}

		// form integral number of 6-bit groups
		dataIndex = i * 3;

		if (fewerThan24bits == EIGHTBIT) {
			b1 = binaryData[dataIndex];
			k = (byte) (b1 & 0x03);
			// log.debug("b1=" + b1);
			// log.debug("b1<<2 = " + (b1>>2) );
			byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
					: (byte) ((b1) >> 2 ^ 0xc0);
			encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
			encodedData[encodedIndex + 1] = lookUpBase64Alphabet[k << 4];
			encodedData[encodedIndex + 2] = PAD;
			encodedData[encodedIndex + 3] = PAD;
		} else if (fewerThan24bits == SIXTEENBIT) {

			b1 = binaryData[dataIndex];
			b2 = binaryData[dataIndex + 1];
			l = (byte) (b2 & 0x0f);
			k = (byte) (b1 & 0x03);

			byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
					: (byte) ((b1) >> 2 ^ 0xc0);
			byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4)
					: (byte) ((b2) >> 4 ^ 0xf0);

			encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
			encodedData[encodedIndex + 1] = lookUpBase64Alphabet[val2
					| (k << 4)];
			encodedData[encodedIndex + 2] = lookUpBase64Alphabet[l << 2];
			encodedData[encodedIndex + 3] = PAD;
		}

		if (isChunked) {
			// we also add a separator to the end of the final chunk.
			if (chunksSoFar < chunckCount) {
				System.arraycopy(CHUNK_SEPARATOR, 0, encodedData,
						encodedDataLength - CHUNK_SEPARATOR.length,
						CHUNK_SEPARATOR.length);
			}
		}

		return encodedData;
	}

	public static String decodeToString(String base64Encoded) {
		byte[] encodedBytes = CodecSupport.toBytes(base64Encoded);
		return decodeToString(encodedBytes);
	}

	public static String decodeToString(byte[] base64Encoded) {
		byte[] decoded = decode(base64Encoded);
		return CodecSupport.toString(decoded);
	}

	public static byte[] decode(String base64Encoded) {
		byte[] bytes = CodecSupport.toBytes(base64Encoded);
		return decode(bytes);
	}

	public static byte[] decode(byte[] base64Data) {
		// RFC 2045 requires that we discard ALL non-Base64 characters
		base64Data = discardNonBase64(base64Data);

		// handle the edge case, so we don't have to worry about it later
		if (base64Data.length == 0) {
			return new byte[0];
		}

		int numberQuadruple = base64Data.length / FOURBYTE;
		byte decodedData[];
		byte b1, b2, b3, b4, marker0, marker1;

		// Throw away anything not in base64Data

		int encodedIndex = 0;
		int dataIndex;
		{
			// this sizes the output array properly - rlw
			int lastData = base64Data.length;
			// ignore the '=' padding
			while (base64Data[lastData - 1] == PAD) {
				if (--lastData == 0) {
					return new byte[0];
				}
			}
			decodedData = new byte[lastData - numberQuadruple];
		}

		for (int i = 0; i < numberQuadruple; i++) {
			dataIndex = i * 4;
			marker0 = base64Data[dataIndex + 2];
			marker1 = base64Data[dataIndex + 3];

			b1 = base64Alphabet[base64Data[dataIndex]];
			b2 = base64Alphabet[base64Data[dataIndex + 1]];

			if (marker0 != PAD && marker1 != PAD) {
				// No PAD e.g 3cQl
				b3 = base64Alphabet[marker0];
				b4 = base64Alphabet[marker1];

				decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
				decodedData[encodedIndex + 1] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
				decodedData[encodedIndex + 2] = (byte) (b3 << 6 | b4);
			} else if (marker0 == PAD) {
				// Two PAD e.g. 3c[Pad][Pad]
				decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
			} else {
				// One PAD e.g. 3cQ[Pad]
				b3 = base64Alphabet[marker0];
				decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
				decodedData[encodedIndex + 1] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
			}
			encodedIndex += 3;
		}
		return decodedData;
	}

	static byte[] discardNonBase64(byte[] data) {
		byte groomedData[] = new byte[data.length];
		int bytesCopied = 0;
		for (byte aByte : data) {
			if (isBase64(aByte)) {
				groomedData[bytesCopied++] = aByte;
			}
		}
		byte packedData[] = new byte[bytesCopied];
		System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);
		return packedData;
	}
}
