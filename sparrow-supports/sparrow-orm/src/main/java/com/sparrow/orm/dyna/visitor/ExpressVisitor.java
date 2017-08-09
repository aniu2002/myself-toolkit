package com.sparrow.orm.dyna.visitor;

public interface ExpressVisitor {
	void visitExpress(String express);

	void visitCommand(String command);

	void visitEntity(String entity);

	void visitWhere(String where);

	void visitParam(String param);

	void visitFilter(String filter);

	void visitOperate(String operate);
}
