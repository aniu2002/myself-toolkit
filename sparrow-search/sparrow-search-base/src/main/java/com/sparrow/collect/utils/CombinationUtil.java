package com.dili.dd.searcher.basesearch.common.util;

import java.util.*;

/**
 * 实现组合的工具类
 * 
 * @author Dave
 * 
 */
public class CombinationUtil {

	/**
	 * @param word
	 *            字符串
	 * @return 更多长度权重的值
	 */
	public static int getCountOfCombinationByWeight(String word) {
		int a = word.length();
		int count = 0;
		for (int i = 0; i < a + 1; i++) {
			count = i * (a + 1 - i) + count;
		}

		return count;
	}

	/**
	 * 有N个口袋，每个口袋有若干个球，每个口袋只能选一个球，列出选length个球的所有可能
	 * 
	 * @param llist
	 *            可供组合的列表
	 * @param length
	 *            选择的个数
	 * @return 所有可能的选择情况
	 */
	@SuppressWarnings("unchecked")
	public static List<List> getLimitedCombination(List<List> llist, int length) {
		List<List> result = new ArrayList<List>();
		List<List> rlist = getCombination(llist, length);
		for (List list : rlist) {
			List<List> rrlist = getCombination(list);
			result.addAll(rrlist);
		}

		return result;
	}

	/**
	 * 有N个口袋，每个口袋有若干个球，每个口袋只能选一个球，列出最多选length个球的所有可能
	 * 
	 * @param llist
	 *            可供组合的列表
	 * @param max
	 *            选择的最大个数
	 * @return 所有可能的选择情况
	 */
	@SuppressWarnings("unchecked")
	public static List<List> getLimitedCombinationWithinMax(List<List> llist,
			int max) {
		List<List> result = new ArrayList<List>();
		for (int i = 1; i <= max; i++) {
			result.addAll(getLimitedCombination(llist, i));
		}

		return result;
	}

	
	/** 返回元素的排列组合
	 * @param list 元素集合
	 * @return
	 */
	public static List<List<?>> getPermutation(List<?> list) {
		List<List<?>> llist = new ArrayList<List<?>>();
		if (list.size() > 0) {
			List<Object> newList = new ArrayList<Object>();
			newList.add(list.get(0));
			llist.add(newList);
			for (int i = 1; i < list.size(); i++) {
				llist = buildPermutation(llist, list.get(i));
			}
		}

		return llist;
	}

	private static List<List<?>> buildPermutation(List<List<?>> llist,
			Object element) {
		List<List<?>> newLList = new ArrayList<List<?>>();
		for (List<?> list : llist) {
			// 把element插入原列表的从0到size的位置
			for (int i = 0; i <= list.size(); i++) {
				List<Object> newList = new ArrayList<Object>();
				for (Object obj : list) {
					newList.add(obj);
				}
				newList.add(i, element);
				newLList.add(newList);
			}
		}

		return newLList;
	}

	/**
	 * 在一个列表中任选length个成员的所有情况
	 * 
	 * @param list
	 *            列表
	 * @param length
	 *            选择的个数
	 * @return 所有可能的选择情况
	 */
	@SuppressWarnings("unchecked")
	public static List<List> getCombination(List list, int length) {
		List<List> resultwList = new ArrayList<List>();
		if (list.size() <= length) {
			resultwList.add(list);
		} else {
			boolean reverse = false;
			if (list.size() - length < length) {
				length = list.size() - length;
				reverse = true;
			}
			List<List> newList = new ArrayList<List>();
			List wholeList = new ArrayList();
			for (int j = 0; j < list.size(); j++) {
				wholeList.add(new Entry(list.get(j), j));
			}
			for (int i = 0; i < length; i++) {
				List entryList = new ArrayList();
				for (int j = 0; j < list.size(); j++) {
					entryList.add(new Entry(list.get(j), j));
				}
				newList.add(entryList);
			}
			newList = getCombination(newList);

			Iterator<List> it = newList.iterator();
			HashSet<HashSet<Integer>> resultSet = new HashSet<HashSet<Integer>>();
			while (it.hasNext()) {
				List lo = it.next();
				HashSet<Integer> hs = new HashSet<Integer>();
				for (Object entry : lo) {
					hs.add(((Entry) entry).getPosition());
				}

				if (hs.size() < lo.size()) {
					it.remove();
				} else if (resultSet.contains(hs)) {
					it.remove();
				} else {
					resultSet.add(hs);
				}
			}

			if (reverse) {
				List<List> nnList = new ArrayList<List>();
				for (List newchildList : newList) {
					nnList.add(getLeftPart(wholeList, newchildList));
				}
				newList = nnList;
			}

			for (int i = 0; i < newList.size(); i++) {
				List vList = new ArrayList();
				for (int j = 0; j < newList.get(i).size(); j++) {
					vList.add(((Entry) newList.get(i).get(j)).getObject());
				}
				resultwList.add(vList);
			}
		}

		return resultwList;
	}

	@SuppressWarnings("unchecked")
	private static List getLeftPart(List sourceList, List valueList) {
		List leftList = new ArrayList();
		for (Object o : sourceList) {
			Entry entry = (Entry) o;
			boolean contains = false;
			for (Object ob : valueList) {
				Entry ventry = (Entry) ob;
				if (ventry.getPosition() == entry.getPosition()) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				leftList.add(o);
			}
		}

		return leftList;
	}

	/**
	 * 有N个列表，每个列表有若干成语，在每个列表中各选一个成员的所有可能情况
	 * 
	 * @param list
	 *            N个列表
	 * @return 所有的组合情况
	 */
	@SuppressWarnings("unchecked")
	public static List<List> getCombination(List<List> list) {
		// record the position
		Hashtable<Integer, Integer> recorder = new Hashtable<Integer, Integer>();
		Hashtable<Integer, Integer> done = new Hashtable<Integer, Integer>();
		int changePosition = 0;
		int currentPosition = 0;
		List<List> result = new ArrayList<List>();

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).size() == 0) {
				list.remove(i);
			}
		}

		// Initial position
		for (int i = 0; i < list.size(); i++) {
			recorder.put(i, 0);
		}
		// Initial done
		for (int i = 0; i < list.size(); i++) {
			done.put(i, list.get(i).size() - 1);
		}

		while (recorder.size() <= done.size() && recorder.size() != 0) {
			List oneList = new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				oneList.add(list.get(i).get(recorder.get(i)));
			}
			result.add(oneList);
			adjustValue(list, recorder, done, currentPosition, changePosition);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private static void adjustValue(List<List> list,
			Hashtable<Integer, Integer> recorder,
			Hashtable<Integer, Integer> done, int currentPosition,
			int changePosition) {
		for (int i = 0; i < list.size(); i++) {
			if (i < changePosition) {
				if (recorder.get(i) < done.get(i)) {
					if (currentPosition == i) {
						recorder.put(i, recorder.get(i) + 1);
						currentPosition = i;
						break;
					}
				} else {
					recorder.put(i, 0);
					for (int j = i + 1; j < changePosition; j++) {
						if (recorder.get(j) < done.get(j)) {
							recorder.put(j, recorder.get(j) + 1);
							break;
						} else {
							recorder.put(j, 0);
						}
					}
					currentPosition = 0;
				}
			} else if (i == changePosition) {
				if (currentPosition == changePosition) {
					if (recorder.get(i) < done.get(i)) {
						recorder.put(i, recorder.get(i) + 1);
						currentPosition = i;
					} else {
						recorder.put(i, 0);
						for (int j = i + 1; j < done.size() + 1; j++) {
							if (j < done.size()) {
								if (recorder.get(j) < done.get(j)) {
									recorder.put(j, recorder.get(j) + 1);
									break;
								} else {
									recorder.put(j, 0);
								}
							} else {
								recorder.put(j, 0);
							}
						}
						currentPosition = 0;
						changePosition = changePosition + 1;
					}
					break;
				}
			}
		}
	}
}

/**
 * 值和位置信息
 * 
 * @author Dave
 * 
 */
class Entry {
	/**
	 * 值
	 */
	private Object object;
	/**
	 * 所处位置
	 */
	private int position;

	public Entry(Object object, int position) {
		this.object = object;
		this.position = position;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean eqauls(Object obj) {
		Entry entry = (Entry) obj;
		return entry.getObject().equals(object) && entry.position == position;
	}

	public int hashCode() {
		return position;
	}

	public String toString() {
		return "[" + object + ", " + position + "]";
	}
}
