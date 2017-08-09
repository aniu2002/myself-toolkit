package com.sparrow.orm.dyna.visitor;

public class DefaultExpressVisitor implements ExpressVisitor {
	@Override
	public void visitEntity(String entity) {
		System.out.println("entity:" + entity);
	}

	@Override
	public void visitWhere(String where) {
		System.out.println("where:" + where);
	}

	@Override
	public void visitParam(String param) {
		System.out.println("param:" + param);
	}

	@Override
	public void visitFilter(String filter) {
		System.out.println("filter:" + filter);
	}

	@Override
	public void visitOperate(String operateIdx) {
		System.out.println("operateIdx:" + operateIdx);
	}

	@Override
	public void visitCommand(String command) {
		System.out.println("command:" + command);
	}

	@Override
	public void visitExpress(String express) {
		System.out.println("express:" + express);
	}

}
