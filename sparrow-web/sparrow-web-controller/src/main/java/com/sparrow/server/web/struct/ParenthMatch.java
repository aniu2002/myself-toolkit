package com.sparrow.server.web.struct;

public class ParenthMatch {
	public static boolean parse(String exp) {
		Stack stack = new Stack();
		for (int i = 0; i < exp.length(); i++) {
			switch (exp.charAt(i)) {
			case '(':
			case '[':
			case '<':
				stack.push(exp.charAt(i));
				break;
			case ')':
			case ']':
			case '>':
				try {
					if (stack.isEmpty())
						return false;
					if (!match(stack.pop().toString(), exp.charAt(i)))
						return false;
				} catch (Exception e1) {
				}
				break;
			}
		}
		if (!stack.isEmpty())
			return false;
		return true;
	}

	public static boolean match(String ch1, char ch2) {
		switch (ch1.charAt(0)) {
		case '(':
			if (ch2 == ')')
				return true;
			break;
		case '[':
			if (ch2 == ']')
				return true;
			break;
		case '<':
			if (ch2 == '>')
				return true;
			break;
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println(ParenthMatch.parse("(4+9)*<>()"));
	}

}
