package com.sparrow.orm.config;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

public class TableCfgRuleSet extends RuleSetBase {

	public void addRuleInstances(Digester digester) {
		digester.addObjectCreate("cfg/table", TableConfig.class);
		digester.addSetProperties("cfg/table");
		digester.addSetNext("cfg/table", "addTableCfg",
				"com.sparrow.orm.conf.TableConfig");

		digester.addObjectCreate("cfg/table/item", TableItem.class);
		digester.addSetProperties("cfg/table/item");
		digester.addSetNext("cfg/table/item", "addItem",
				"com.sparrow.orm.conf.TableItem");
		digester.addObjectCreate("cfg/table/key", TableItem.class);
		digester.addSetProperties("cfg/table/key");
		digester.addSetNext("cfg/table/key", "addKey",
				"com.sparrow.orm.conf.TableItem");
		digester.addObjectCreate("cfg/sql-map", SqlMapConfig.class);
		digester.addSetProperties("cfg/sql-map");
		digester.addBeanPropertySetter("cfg/sql-map/sql", "sql");
		digester.addBeanPropertySetter("cfg/sql-map/fileds", "fileds");
		digester.addSetNext("cfg/sql-map", "addSqlMap",
				"com.sparrow.orm.conf.SqlMapConfig");
	}
}
