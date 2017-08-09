package com.sparrow.orm.dyna.common;

import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.orm.dyna.proxy.ProxyHelper;
import com.sparrow.orm.dyna.sql.SqlBuilder;
import com.sparrow.orm.dyna.visitor.ExpressHelper;
import com.sparrow.orm.dyna.visitor.ExpressVisitor;


public class QueryExpressVisitor implements ExpressVisitor {
	private GrammarMeta meta;
	private Stack<ParamItem> stack = new Stack<ParamItem>();
	private int index;

	public QueryExpressVisitor() {
		meta = new GrammarMeta();
		meta.setCommand(Constants.COMMAND_UNKNOW);
	}

	@Override
	public void visitEntity(String entity) {
		meta.setEntity(entity);
	}

	@Override
	public void visitWhere(String where) {
		meta.setWhere(where);
	}

	@Override
	public void visitCommand(String command) {
		meta.setCommand(ProxyHelper.parseCommand(command));
	}

	@Override
	public void visitParam(String param) {
		ParamItem itm = new ParamItem();
		itm.param = param;
		itm.index = (this.index++);
		stack.push(itm);
	}

	@Override
	public void visitFilter(String filter) {
		ParamItem itm = stack.peek();
		if (StringUtils.equals(filter, ExpressHelper.P_AND))
			itm.filter = SqlBuilder.OP_AND;
		else
			itm.filter = SqlBuilder.OP_OR;
	}

	@Override
	public void visitOperate(String operate) {
		ParamItem itm = stack.peek();
		itm.operate = ProxyHelper.parseOperator(operate);
		// between 需要两个参数项目所以,跳过一个
		if (StringUtils.equals(operate, "Between")) {
			itm.valSigns = 2;
			this.index++;
		}
	}

	ParamItem[] getQueryItems() {
		ParamItem itm[] = new ParamItem[this.stack.size()];
		stack.copyInto(itm);
		return itm;
	}

	public GrammarMeta getGrammarMeta() {
		this.meta.setItems(this.getQueryItems());
		return this.meta;
	}

	@Override
	public void visitExpress(String express) {
		this.meta.setMethodName(express);
	}
}
