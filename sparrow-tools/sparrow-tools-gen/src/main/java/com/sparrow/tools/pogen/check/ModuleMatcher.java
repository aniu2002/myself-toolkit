package com.sparrow.tools.pogen.check;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

public class ModuleMatcher {
	List<StrCheck> checks;
	List<StrCheck> regChecks;
	StrCheck defaultCheck;

	public String matchModule(String tableStr, String prefix) {
		if (this.checks != null && !this.checks.isEmpty()) {
			for (StrCheck check : checks) {
				if (check.check(tableStr) || check.check(prefix))
					return check.getName();
			}
		}

		if (this.regChecks != null && !this.regChecks.isEmpty()) {
			for (StrCheck check : regChecks) {
				if (check.check(tableStr))
					return check.getName();
			}
		}

		if (defaultCheck != null && defaultCheck.check(prefix))
			return defaultCheck.getName();

		return null;
	}

	public String matchModule(String tableStr) {
		if (this.checks != null && !this.checks.isEmpty()) {
			for (StrCheck check : checks) {
				if (check.check(tableStr))
					return check.getName();
			}
		}

		if (this.regChecks != null && !this.regChecks.isEmpty()) {
			for (StrCheck check : regChecks) {
				if (check.check(tableStr))
					return check.getName();
			}
		}

		if (defaultCheck != null && defaultCheck.check(tableStr))
			return defaultCheck.getName();

		return null;
	}

	public void addModule(String express, String module) {
		if (StringUtils.isEmpty(express))
			return;
		for (StringTokenizer tokenizer = new StringTokenizer(express, ","); tokenizer
				.hasMoreElements();) {
			this.addStrCheck(tokenizer.nextToken(), module);
		}
	}

	public void addRegCheck(StrCheck strCheck) {
		if (this.regChecks == null)
			this.regChecks = new ArrayList();
		this.regChecks.add(strCheck);
	}

	public void addNormalCheck(StrCheck strCheck) {
		if (this.checks == null)
			this.checks = new ArrayList();
		this.checks.add(strCheck);
	}

	void addStrCheck(String string, String name) {
		if (StringUtils.isBlank(string) || "*".equals(string)) {
			if (defaultCheck == null) {
				StrDefaultCheck sc = new StrDefaultCheck();
				sc.setName(name);
				defaultCheck = sc;
			}
		} else if (StringUtils.containsAny(string, '?', '*')) {
			StrRegexCheck sc = new StrRegexCheck(string);
			sc.setName(name);
			this.addRegCheck(sc);
		} else {
			StrEqualsCheck sc = new StrEqualsCheck(string);
			sc.setName(name);
			this.addNormalCheck(sc);
		}
	}
}
