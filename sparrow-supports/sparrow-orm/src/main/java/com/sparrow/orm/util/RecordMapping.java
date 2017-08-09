package com.sparrow.orm.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sparrow.core.utils.BeanForceUtil;
import com.sparrow.orm.config.TableConfig;
import com.sparrow.orm.config.TableItem;
import com.sparrow.orm.extractor.SingleExtractor;
import com.sparrow.orm.session.BeanCallback;


public class RecordMapping {

	public static List<?> mapping(ResultSet rs) {
		return generate(rs);
	}

	public static List<?> mapping(ResultSet rs, TableConfig cfg) {
		return generate(rs, items2Aarray(cfg.getAllItems()), cfg.getClaz());
	}

	public static TableItem[] items2Aarray(Collection<TableItem> itms) {
		TableItem items[] = itms.toArray(new TableItem[0]);
		return items;
	}

	public static List<?> mapping(ResultSet rs, TableItem[] items,
			String clazName) {
		return generate(rs, items, clazName);
	}

	public static List<?> mapping(ResultSet rs, TableItem[] items, Class<?> claz) {
		return generate(rs, items, claz);
	}

	public static List<?> mapping(ResultSet rs, String alias[], Class<?> claz) {
		return generate(rs, null, alias, claz);
	}

	public static List<?> mapping(ResultSet rs, String columns[],
			String alias[], Class<?> claz) {
		return generate(rs, columns, alias, claz);
	}

	public static int mapping(ResultSet rs, TableItem[] items, Class<?> claz,
			BeanCallback callback) {
		return generate(rs, items, claz, callback);
	}

	private static <T> List<T> generate(ResultSet rs, TableItem items[],
			Class<T> claz) {
		if (items == null || claz == null)
			return null;
		List<T> list = new ArrayList<T>();
		T obj;
		try {
			Map<String, Object> props;
			while (rs.next()) {
				props = new HashMap<String, Object>();
				for (int i = 0; i < items.length; i++) {
					Object nObj = rs.getObject(items[i].getAlia());
					if (nObj != null)
						props.put(items[i].getProperty(), nObj);
				}
				obj = createObject(claz, props);
				list.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static Collection<TableItem> generateMapItem(ResultSet rs,
			TableConfig cfg) {
		ResultSetMetaData rsmd;
		Collection<TableItem> items = new ArrayList<TableItem>();
		Map<String, TableItem> regItemMap = cfg.getItemMap();
		TableItem item;
		try {
			rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			String columnName, label;
			for (int i = 1; i <= columnCount; i++) {
				columnName = rsmd.getColumnName(i);
				label = rsmd.getColumnLabel(i);
				System.out.println("label:" + label);
				columnName = columnName.toLowerCase();
				item = regItemMap.get(columnName);
				if (item != null)
					items.add(item);
			}
			return items;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<?> mapping(ResultSet rs, String columns[],
			String alias[], String clazName) {
		Class<?> claz;
		try {
			claz = BeanForceUtil.loadClass(clazName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return generate(rs, columns, alias, claz);
	}

	private static List<?> generate(ResultSet rs) {
		ResultSetMetaData rsmd;
		try {
			rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			int dType[] = new int[columnCount];
			int dataType, dataScale, dataPrecision;
			for (int i = 1; i <= columnCount; i++) {
				dataType = rsmd.getColumnType(i);
				if (dataType == Types.NUMERIC) {
					dataScale = rsmd.getScale(i);
					dataPrecision = rsmd.getPrecision(i);
					if (dataScale == 0) {
						if (dataPrecision == 0) {
							dType[i - 1] = Types.NUMERIC;
						} else {
							dType[i - 1] = Types.NUMERIC;
						}
					} else {
						dType[i - 1] = Types.NUMERIC;
					}
				} else {
					dType[i - 1] = localColumnType(dataType);
				}
			}
			List<Object[]> list = new ArrayList<Object[]>();
			while (rs.next()) {
				Object[] t = new Object[columnCount];
				for (int j = 1; j <= columnCount; j++) {
					if (dType[j - 1] == Types.CLOB) {
						t[j - 1] = LobUtil.clobToString(rs.getClob(j));
					} else if (dType[j - 1] == Types.BLOB) {
						t[j - 1] = LobUtil.blobToBytes(rs.getBlob(j));
					} else {
						t[j - 1] = rs.getObject(j);
					}
				}
				list.add(t);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static List<?> generate(ResultSet rs, TableItem items[],
			String className) {
		try {
			Class<?> claz = BeanForceUtil.loadClass(className);
			return generate(rs, items, claz);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<?> generate(ResultSet rs, TableItem items[]) {
		return generate(rs);
	}

	public static <T> int mapping(ResultSet rs, SingleExtractor<T> extractor,
			BeanCallback callback) {
		return generate(rs, extractor, callback);
	}

	private static <T> int generate(ResultSet rs, SingleExtractor<T> extractor,
			BeanCallback callback) {
		if (extractor == null || extractor == null)
			return 0;
		int n = 0;
		try {
			while (rs.next()) {
				n++;
				callback.callback(extractor.singleExtract(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n;
	}

	private static <T> int generate(ResultSet rs, TableItem items[],
			Class<T> claz, BeanCallback callback) {
		if (items == null || claz == null)
			return 0;
		int n = 0;
		try {
			while (rs.next()) {
				T bean = BeanForceUtil.createInstance(claz);
				for (int i = 0; i < items.length; i++) {
					BeanForceUtil.forceSetProperty(bean,
							items[i].getProperty(),
							rs.getObject(items[i].getAlia()));
				}
				n++;
				callback.callback(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return n;
	}

	static <T> void generate(ResultSet rs, String columns[], String alias[],
			Class<T> claz, BeanCallback callback) {
		if (columns == null)
			columns = alias;
		try {
			while (rs.next()) {
				T bean = BeanForceUtil.createInstance(claz);
				for (int i = 0; i < alias.length; i++) {
					BeanForceUtil.forceSetProperty(bean, alias[i],
							rs.getObject(columns[i]));
				}
				callback.callback(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	private static <T> List<T> generate(ResultSet rs, String columns[],
			String alias[], Class<T> claz) {
		List<T> list = new ArrayList<T>();
		if (columns == null)
			columns = alias;
		T obj;
		try {
			Map<String, Object> props;
			while (rs.next()) {
				props = new HashMap<String, Object>();
				for (int i = 0; i < alias.length; i++) {
					props.put(alias[i], rs.getObject(columns[i]));
				}
				obj = createObject(claz, props);
				list.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static TableItem[] getTableItems(TableConfig config,
			String columns[], String alias[]) {
		if (columns == null || alias == null)
			return items2Aarray(config.getAllItems());
		List<TableItem> list = new ArrayList<TableItem>();
		TableItem[] items = items2Aarray(config.getAllItems());
		String tmp;
		for (int i = 0; i < items.length; i++) {
			tmp = items[i].getColumn();
			for (int j = 0; j < columns.length; j++) {
				if (columns[j].equalsIgnoreCase(tmp)) {
					items[i].setAlia(alias[j]);
					list.add(items[i]);
					break;
				}
			}
		}
		return (TableItem[]) list.toArray(new TableItem[0]);
	}

	public static Object createObject(String clazName,
			Map<String, Object> paramtes) {
		Object obj = BeanForceUtil.createInstance(clazName);
		BeanForceUtil.setBeanProperties(obj, paramtes);
		return obj;
	}

	public static <T> T createObject(Class<T> claz, Map<String, Object> paramtes) {
		T obj = BeanForceUtil.createInstance(claz);
		BeanForceUtil.setBeanProperties(obj, paramtes);
		return obj;
	}

	private static int localColumnType(int dataType) {
		return dataType;
	}
}
