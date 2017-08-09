package com.au.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.au.cache.exceptions.CacheException;

public class Element implements Serializable, Cloneable {
	private final Object key;
	private final Object value;

	/**
	 * Constructor.
	 * 
	 * @param key
	 * @param value
	 */
	public Element(final Serializable key, final Serializable value) {
		this((Object) key, (Object) value);
	}

	/**
	 * 
	 * <p>
	 * Title: getKey
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return
	 * @throws CacheException
	 * @author Yzc
	 */
	public final Serializable getKey() {
		Serializable keyAsSerializable;
		try {
			keyAsSerializable = (Serializable) key;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return keyAsSerializable;
	}

	/**
	 * 
	 * <p>
	 * Title: getObjectKey
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return
	 * @author Yzc
	 */
	public final Object getObjectKey() {
		return key;
	}

	/**
	 * 
	 * <p>
	 * Title: getValue
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return
	 * @throws CacheException
	 * @author Yzc
	 */
	public final Serializable getValue() {
		Serializable valueAsSerializable;
		try {
			valueAsSerializable = (Serializable) value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return valueAsSerializable;
	}

	/**
	 * 
	 * <p>
	 * Title: getSerializedSize
	 * </p>
	 * <p>
	 * Description: get element bytes size
	 * </p>
	 * 
	 * @return
	 * @author Yzc
	 */
	public final long getSerializedSize() {
		if (!isSerializable()) {
			return 0;
		}
		long size = 0;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(bout);
			oos.writeObject(this);
			size = bout.size();
			return size;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (Exception e) {
			}
		}

		return size;
	}

	/**
	 * 
	 * <p>
	 * Title: getObjectValue
	 * </p>
	 * <p>
	 * Description: get serializable value
	 * </p>
	 * 
	 * @return
	 * @author Yzc
	 */
	public final Object getObjectValue() {
		return value;
	}

	/**
	 * Constructor.
	 * 
	 * @param key
	 * @param value
	 * @since 1.2
	 */
	public Element(final Object key, final Object value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * 
	 * <p>
	 * Title: isSerializable
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return
	 * @author Yzc
	 */
	public boolean isSerializable() {
		return (key instanceof Serializable)
				&& (value == null || value instanceof Serializable);
	}

	@Override
	public final boolean equals(final Object object) {
		if (object == null || !(object instanceof Element)) {
			return false;
		}

		Element element = (Element) object;
		if (key == null || element.getObjectKey() == null) {
			return false;
		}

		return key.equals(element.getObjectKey());
	}

	@Override
	public final int hashCode() {
		return key.hashCode();
	}

	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[ key = ").append(key).append(", value=").append(value)
				.append(" ]");
		return sb.toString();
	}

	@Override
	public final Object clone() throws CloneNotSupportedException {
		// Not used. Just to get code inspectors to shut up
		super.clone();

		try {
			Element element = new Element(deepCopy(key), deepCopy(value));
			return element;
		} catch (IOException e) {
			e.printStackTrace();
			throw new CloneNotSupportedException();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new CloneNotSupportedException();
		}
	}

	private static Object deepCopy(final Object oldValue) throws IOException,
			ClassNotFoundException {
		Serializable newValue = null;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			oos = new ObjectOutputStream(bout);
			oos.writeObject(oldValue);
			ByteArrayInputStream bin = new ByteArrayInputStream(bout
					.toByteArray());
			ois = new ObjectInputStream(bin);
			newValue = (Serializable) ois.readObject();
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
				if (ois != null) {
					ois.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return newValue;
	}
}
