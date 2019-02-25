package com.sparrow.collect.index.format;

import com.sparrow.collect.utils.CommonSpliter;
import com.sparrow.collect.utils.Spliter;
import com.sparrow.collect.utils.StringKit;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class WordSplitter implements Splitter {
	private static Hashtable<String, WordSplitter> splitterTable = new Hashtable<String, WordSplitter>();
	public Hashtable<String, Integer> dic = new Hashtable<String, Integer>();
	private static final String PATH = "song.dic";

	public WordSplitter() {
		initializeDic(PATH);
	}

	private WordSplitter(String fileName) {
		initializeDic(fileName);
	}

	private void initializeDic(String fileName) {
		try {
			InputStream stream = this.getClass().getResourceAsStream(fileName);
			if (stream == null) {
				stream = new FileInputStream(fileName);
			}
			BufferedReader bfReader = new BufferedReader(new InputStreamReader(
					stream, "GBK"));
			while (bfReader.ready()) {
				String line = bfReader.readLine();
				if (!StringKit.isNullOrEmpty(line) && line.contains(",")) {
					String[] names = line.split(",");
					if (!names[0].trim().equals("")) {
						if (dic.containsKey(names[0])) {
							dic.put(names[0], Integer.valueOf(names[1])
									+ dic.get(names[0]));
						} else {
							dic.put(names[0], Integer.valueOf(names[1]));
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	public synchronized static WordSplitter getInstance(String fileName) {
		if (StringKit.isNullOrEmpty(fileName)) {
			fileName = PATH;
		}
		if (!splitterTable.containsKey(fileName)) {
			splitterTable.put(fileName, new WordSplitter(fileName));
		}

		return splitterTable.get(fileName);
	}

	public synchronized static WordSplitter getInstance() {
		return getInstance(null);
	}

	@Override
	public String[] split(String sentence) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			String[] subsens = CommonSpliter.filter(sentence);
			for (int i = 0; i < subsens.length; i++) {
				if (CommonSpliter.needSplit(subsens[i])) {
					String[] words = getFinalWords(subsens[i]);
					for (int j = 0; j < words.length; j++) {
						list.add(words[j]);
					}
				} else {
					list.add(subsens[i]);
				}
			}
		} catch (Exception e) {
		}

		return list.toArray(new String[] {});
	}

	private String[] getFinalWords(String string) {
		// 从词典中得到所有命中的词语
		Hashtable<String, Integer> matchTables = getMatchWords(dic, string);
		// 计算命中词的位置信息
		Hashtable<Position, String> posTable = new Hashtable<Position, String>();
		List<Position> posList = new ArrayList<Position>();
		for (String key : matchTables.keySet()) {
			int[] ids = StringKit.getAllIndex(string, key);
			for (int i = 0; i < ids.length; i++) {
				Position pos = new Position(ids[i] + 1, ids[i] + key.length());
				posTable.put(pos, key);
				posList.add(pos);
			}
		}

		Collections.sort(posList, new PositionComparator());
		List<List<Position>> slist = new ArrayList<List<Position>>();
		List<Position> plist = new ArrayList<Position>();
		Position lastP = new Position();
		boolean toolong = false;
		for (int i = 0; i < posList.size(); i++) {
			if (lastP.getEnd() < posList.get(i).getStart()) {
				if (plist.size() > 0) {
					slist.add(plist);
				}
				plist = new ArrayList<Position>();
				toolong = false;
			}
			if (plist.size() > 50 || getAllPosibility(plist).size() > 1000) {
				if (plist.size() > 0) {
					slist.add(plist);
				}
				plist = new ArrayList<Position>();
				toolong = true;
			}
			if (!toolong) {
				plist.add(posList.get(i));
				lastP = posList.get(i);
				if (i == posList.size() - 1) {
					if (plist.size() > 0) {
						slist.add(plist);
					}
				}
			}
		}

		List<Position> resultList = new ArrayList<Position>();
		for (List<Position> sectionList : slist) {
			resultList.addAll(getMaxList(getAllPosibility(sectionList),
					matchTables, posTable));
		}

		return getMaxString(matchTables, resultList, posTable, string);
	}

	private List<Position> getMaxList(List<List<Position>> llist,
			Hashtable<String, Integer> matchTables,
			Hashtable<Position, String> posTable) {
		// 返回值最大的
		int max = 0;
		List<Position> maxList = new ArrayList<Position>();
		for (List<Position> list : llist) {
			int value = 0;
			for (Position pos : list) {
				value = value + matchTables.get(posTable.get(pos));
			}
			if (value > max) {
				max = value;
				maxList = list;
			}
		}

		return maxList;
	}

	private String[] getMaxString(Hashtable<String, Integer> matchTables,
			List<Position> maxList, Hashtable<Position, String> posTable,
			String string) {

		// 拼接字符串
		if (maxList.size() > 0) {
			List<Position> newList = new ArrayList<Position>();
			newList.add(new Position(0, maxList.get(0).getStart() - 1));
			for (Position pos : maxList) {
				newList.add(new Position(pos.getStart() - 1, pos.getEnd()));
			}
			if (maxList.get(maxList.size() - 1).getEnd() < string.length()) {
				newList.add(new Position(maxList.get(maxList.size() - 1)
						.getEnd(), string.length()));
			}
			List<String> result = new ArrayList<String>();
			for (int i = 0; i < newList.size(); i++) {
				String value = string.substring(newList.get(i).getStart(),
						newList.get(i).getEnd());
				if (!StringKit.isNullOrEmpty(value)) {
					result.add(value);
				}
				if (i != newList.size() - 1) {
					if (newList.get(i + 1).getStart() > newList.get(i).getEnd()) {
						result.add(string.substring(newList.get(i).getEnd(),
								newList.get(i + 1).getStart()));
					}
				}
			}

			// 如果不是字典中的字则分成一个一个汉字
			// List<String> sresult = new ArrayList<String>();
			// for (String s : result) {
			// sresult.addAll(Arrays.asList(spitNotWord(s, matchTables)));
			// }

			return result.toArray(new String[] {});
		} else {
			return new String[] { string };
		}
	}

	// private String[] spitNotWord(String s,
	// Hashtable<String, Integer> matchTables) {
	// List<String> result = new ArrayList<String>();
	// if (!matchTables.containsKey(s) && StringKit.isAllChineseCharacter(s)) {
	// for (char c : s.toCharArray()) {
	// result.add(String.valueOf(c));
	// }
	// } else {
	// result.add(s + matchTables.get(s));
	// }
	//
	// return result.toArray(new String[] {});
	// }

	private List<List<Position>> getAllPosibility(List<Position> posList) {
		List<List<Position>> llist = new ArrayList<List<Position>>();
		for (Position pos : posList) {
			boolean added = false;
			for (int i = 0; i < llist.size(); i++) {
				List<Position> list = llist.get(i);
				if (list.size() > 0) {
					if (!pos.isInclusivePosition(list.get(list.size() - 1))) {
						List<Position> newlist = new ArrayList<Position>();
						for (Position pp : list) {
							newlist.add(pp);
						}
						newlist.add(pos);
						llist.add(newlist);
						added = true;
					}
				}
			}
			if (!added) {
				List<Position> newlist = new ArrayList<Position>();
				newlist.add(pos);
				llist.add(newlist);
			}
		}

		return llist;
	}

	private Hashtable<String, Integer> getMatchWords(
			Hashtable<String, Integer> dic, String sentence) {
		Hashtable<String, Integer> table = new Hashtable<String, Integer>();
		for (int i = 1; i < 5; i++) {
			String[] words = Spliter.split(sentence, i);
			for (int j = 0; j < words.length; j++) {
				if (dic.containsKey(words[j])) {
					table.put(words[j], (Integer) dic.get(words[j]));
				}
			}
		}

		return table;
	}

}

class PositionComparator implements Comparator<Position> {

	@Override
	public int compare(Position o1, Position o2) {
		if (o1.getStart() > o2.getStart()) {
			return 1;
		} else if (o1.getStart() == o2.getStart()) {
			if (o1.getEnd() > o2.getEnd()) {
				return 1;
			} else if (o1.getEnd() == o2.getEnd()) {
				return 0;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

}

class Position {
	private int start;
	private int end;

	public Position() {

	}

	public Position(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public boolean beforePosition(Position pos) {
		return this.start < pos.start;
	}

	public boolean isIncludePosition(Position pos) {
		return this.start <= pos.start && this.end >= pos.end;
	}

	public boolean isIncludeByPosition(Position pos) {
		return this.start >= pos.start && this.end <= pos.end;
	}

	public boolean isInclusivePosition(Position pos) {
		return (this.start <= pos.start && this.end >= pos.start)
				|| (pos.start <= this.start && pos.end >= this.start);
	}

	public int hashCode() {
		return (String.valueOf(start) + String.valueOf(end)).hashCode();
	}

	public boolean equals(Object obj) {
		Position pos = (Position) obj;
		return this.start == pos.start && this.end == pos.end;
	}

	public String toString() {
		return "start:" + start + "end:" + end;
	}
}
