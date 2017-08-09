package com.sparrow.app.system.controller;

import java.util.Map;

import com.sparrow.core.config.FileMnger;
import com.sparrow.app.services.meta.MetaInterface;
import com.sparrow.core.utils.JsonFormat;
import com.sparrow.app.system.service.pojo.PojoInterface;
import com.sparrow.server.web.OpResult;
import com.sparrow.server.web.annotation.PathVariable;
import com.sparrow.server.web.annotation.ReqMapping;
import com.sparrow.server.web.annotation.ReqParameter;
import com.sparrow.server.web.annotation.RequestBody;
import com.sparrow.server.web.annotation.ResponseBody;
import com.sparrow.server.web.annotation.WebController;
import com.sparrow.service.annotation.Autowired;


@WebController(value = "/dbmng")
public class DatabaseManageController {
	@Autowired(value = "objectMetaService")
	private PojoInterface objMetaSrv;
	@Autowired(value = "databaseMetaService")
	private MetaInterface dbMetaSrv;

	@ReqMapping(method = ReqMapping.POST, value = "/setting/{module}/{table}")
	public Object saveTableSetting(@PathVariable("module") String module,
			@PathVariable("table") String table, @RequestBody String string) {
		// TableColumn column
        FileMnger.writeText(module, table, JsonFormat.format(string));
		return OpResult.OK;
	}

	@ReqMapping(method = ReqMapping.POST, value = "/clear/{module}")
	public Object clearTableSetting(@PathVariable("module") String module,
			@ReqParameter("table") String table) {
		// TableColumn column
        FileMnger.clearModule(module, table);
		return OpResult.OK;
	}

	@ReqMapping(value = "/setting/{module}/{table}")
	@ResponseBody
	public String getTableSetting(@PathVariable("module") String module,
			@PathVariable("table") String table) {
		return FileMnger.readText(module, table);
	}

	@ReqMapping(method = ReqMapping.POST, value = "/conf/{module}")
	public Object saveColumnDef(@PathVariable("module") String module,
			Map<String, String> map) {
        FileMnger.writeMap(module, map);
		return OpResult.OK;
	}

	@ReqMapping(value = "/conf/{module}")
	@ResponseBody
	public Object getColumnDef(@PathVariable("module") String module) {
		return FileMnger.readMap(module);
	}

	@ReqMapping(value = "/beans")
	public Object getClassName(Map<String, String> in) {
		return objMetaSrv.getClassNames(in.get("clazzPath"));
	}

	@ReqMapping(value = "/beans/{clazz}")
	public Object getClassInfo(@PathVariable("clazz") String clazz) {
		return objMetaSrv.getClassInfo(clazz);
	}

	@ReqMapping(value = "/tables")
	public Object getTables(Map<String, String> in) {
		return dbMetaSrv.getTableNames();
	}

	@ReqMapping(value = "/tables/{table}")
	public Object getTableInfo(@PathVariable("table") String table) {
		return dbMetaSrv.getTable(table);
	}

	@ReqMapping(value = "/tables/{table}/data")
	public Object getTableData(@PathVariable("table") String table) {
		return dbMetaSrv.getTableData(table, 1, 100);
	}

	@ReqMapping(value = "/tables/{table}/column")
	public Object getTableInfoEl(@PathVariable("table") String table) {
		return dbMetaSrv.getColumnMetaData(table);
	}
}
