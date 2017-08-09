package com.sparrow.server.web.struct;

public class UrlPatternCheck {
	public static boolean parse(String exp) {
		Stack stack = new Stack();
		boolean urlpattern = false;
		for (int i = 0; i < exp.length(); i++) {
			switch (exp.charAt(i)) {
			case '{':
				urlpattern = true;
				stack.push(exp.charAt(i));
				break;
			case '}':
				try {
					if (stack.isEmpty())
						return false;
					if (!match((Character) stack.pop(), exp.charAt(i)))
						return false;
				} catch (Exception e1) {
				}
				break;
			}
		}
		if (!stack.isEmpty())
			return false;
		if (urlpattern)
			return true;
		else
			return false;
	}

	public static boolean match(char ch1, char ch2) {
		if (ch1 == '{' && ch2 == '}')
			return true;
		return false;
	}

	public static void main(String[] args) {
		System.out.println(UrlPatternCheck.parse("(4+9)*<>()"));
	}

}
