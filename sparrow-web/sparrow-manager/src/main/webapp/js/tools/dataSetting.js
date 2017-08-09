var module = 'test';
var tabEditor = new TEditor();
var columns = [
		{
			field : "name",
			align : "left",
			fixed : true,
			icon : 'js/images/ico_6.gif',
			render : function(val, obj) {
				var v = val;
				if (obj.size)
					v = v + '(' + obj.size + ')';
				if (obj.primary)
					return '<img src="js/images/ico_12.gif" alt="主键"/>&nbsp;<font color="red">'
							+ v + '</font>';
				else
					return '<img src="js/images/ico_13.gif" alt="字段"/>&nbsp;'
							+ v;
			},
			cellType : 0,
			width : 200,
			header : "字段名"
		}, {
			field : "type",
			cellType : 0,
			align : "left",
			render : function(val, obj) {
				var v = val;
				if (obj.notNull)
					v = v + '-<font color="blue">非空</font>';
				return v;
			},
			width : 150,
			header : "类型"
		}, {
			field : "desc",
			cellType : 0,
			editType : 0,
			align : "left",
			hidden : true,
			width : 200,
			header : "描述信息"
		}, {
			field : "field",
			cellType : 0,
			editType : 1,
			align : "left",
			width : 150,
			header : "对象属性"
		} ];
var dataEditor = null;
var curTable = null;
var columnSetting = {};
var _Plugins = {};
function initGrid(val) {
	if (val == 0)
		return;
	curTable = val;
	module = $('#module').val();
	var fnl = function() {
		if (dataEditor == null)
			dataEditor = createTaData('tdata');
		dataEditor.resetTab({
			force : true,
			url : '/rest/dbmng/tables/' + val + '/data.json'
		});
	};
	_ajax('/rest/dbmng/setting/' + module + '/' + val, function(d) {
		d = d || {}
		var cols = d.cols;
		var cdf = undefined;
		if (cols) {
			columnSetting[val] = cols;
			cdf = cols.split(',');
		}
		if (d.setting && d.setting.length)
			tabEditor.drawGrid(d.setting, cdf);
		else
			tabEditor.gxload('/rest/dbmng/tables/' + val + '.json', cdf);
		// fnl();
	});
}
function createTaData(id) {
	var tabEditor = new TEditor();
	tabEditor.boundTo(id);
	return tabEditor;
}
function editorForPlugin(cellEditor, editRef, editType) {
	var col = editRef.column;
	var rowdata = editRef.data;
	var grid = editRef.grid;
	var field = editRef.field;

	cellEditor.options.length = 0;
	cellEditor.options[0] = new Option("请选择", "");

	var javaType = rowdata['simpleType'];
	var data = col.source;
	if (data == null || typeof (data) === 'undefined')
		return;
	if (data.items)
		data = data.items;
	var j = 1, code, name

	for ( var i = 0; i < data.length; i++) {
		var da = data[i];
		code = da.type;
		if (code == 'all' || (javaType == code)) {
			name = da.name;
			cellEditor.options[j++] = new Option(name, da.id);
		}
	}
}

function edtorCreateJava(cellEditor, editRef, editType) {
	cellEditor.options.length = 0;
	cellEditor.options[0] = new Option("请选择", "");
	var grid = editRef.grid;
	var rowdata = editRef.data;
	var field = editRef.field;

	var javaType = rowdata["javaType"];
	var data = grid.dataSource[field];
	if (data == null || typeof (data) === 'undefined')
		return;
	if (data.items)
		data = data.items;
	var j = 1, code, name
	for ( var i = 0; i < data.length; i++) {
		if (javaType != data[i].type)
			continue;
		code = name = data[i].name;
		cellEditor.options[j] = new Option(name, code);
		j++;
	}
}
function pluginRender(val, obj, column) {
	if (val) {
		var s = column.source;
		if (s && s.length)
			for ( var i = 0; i < s.length; i++) {
				var im = s[i];
				if (val === im.id)
					return im.name;
			}
	}
	return val;
}
function getHeaders(result) {
	var cols = columns;
	var j = cols.length;
	for ( var i = 0; i < result.length; i++) {
		var rt = result[i];
		var et = 0;
		var ct = 0;
		if (typeof (rt.type) == 'undefined')
			et = 1;
		else if (rt.type == 'checkbox')
			ct = 2;
		else if (rt.type == 'text')
			ct = 1;
		else if (rt.type == 'combox')
			et = 2;
		var cl = {
			field : rt.id,
			cellType : ct,
			editType : et,
			width : 100,
			hidden : true,
			header : rt.name
		};
		if (rt.fd)
			cl.field = rt.fd;
		if (et == 2 && rt.data) {
			cl.source = rt.data;
			cl.render = pluginRender;
			cl.editorCreated = editorForPlugin;
		}
		cl.defVal = rt.defVal;
		cl.extend = true;
		cols[j++] = cl;
		_Plugins[rt.id] = rt;
	}
	cols[j++] = {
		field : "field",
		header : "操作",
		cellType : 0,
		editType : 0,
		align : "left",
		width : 80,
		render : function(v, obj) {
			_CurObj = obj;
			var str = '<button onclick="editDetail(\'' + v
					+ '\');">高级</button>';
			return str;
		}
	};
	return cols;
}
var _CurObj;
function editDetail(v, obj) {
	alert(v);
	editField();
}
function editField() {
	if (_Pox == null) {
		_Pox = new PopDialog(800, 350, 20, 25);
		_Pox.titleText = '任务编辑';
		_Pox.model = true;
		_Pox.renderId = 'editorDiv';
		_Pox.dragEnable = true;
		_Pox.preHide = function() {
			return true;
		};
	}
	_Pox.show();
}
function initTablesMeta() {
	_ajax('plugins/plugins.json', function(d) {
		var hds = getHeaders(d);
		generateTables(hds);
	});
}
function generateTables(hds) {
	tabEditor.dataRoot = "items";
	tabEditor.customDef = true;
	tabEditor.columnSet = function(s) {
		if (curTable == null)
			return;
		columnSetting[curTable] = s;
	};
	tabEditor.columnDef = {
		id : "name",
		columns : hds || columns
	};
	tabEditor.render("test");
	_ajax("/rest/dbmng/tables.json", function(result) {
		var data = result;
		var y = document.getElementById("database");
		y.options.length = 0;
		y.options[0] = new Option("请选择", "0");
		var j = 1, code, name
		for ( var i = 0; i < data.length; i++) {
			code = name = data[i];
			y.options[j] = new Option(name, code);
			j++;
		}
	});
}

function saveConfig() {
	// getChangedRows
	var g = tabEditor.getAllRows();
	var data = {
		table : curTable,
		cols : columnSetting[curTable],
		setting : g
	};
	saveData(curTable, data);
}
function saveData(table, data) {
	var str = JSON.stringify(data);
	module = $('#module').val();
	var re = {
		url : '/rest/dbmng/setting/' + module + '/' + table,
		method : 'POST',
		responseType : "json",
		args : str,
		dataType : 'application/json',
		timeout : 10000,
		success : function(result, type, status) {
			alert(result.msg);
		},
		error : function(errorMsg, responseType, status) {
			alert(errorMsg);
		}
	};
	AjaxReq.sendRequest(re);
}
function clearConfig() {
	module = $('#module').val();
	_ajax("/rest/dbmng/clear/" + module, function(result) {
		alert(result.msg);
	}, {
		table : curTable
	}, 'POST');
}
var _Pov = null;
var _Pox = null;
var _Ps = null;
var _Done = false;
function generateModule() {
	var ps;
	module = $('#module').val();
	_ajax("/rest/task/install/" + module, function(result) {
		_Done = false;
		processInfo('install', _Ps);
	}, {
		label : $('#label').val(),
		token : 'install',
		pack : $('#pack').val(),
		reload : true
	}, 'POST');
	if (_Ps == null)
		_Ps = Common.createProcess({
			el : '#proc'
		});
	;

	if (_Pov == null) {
		_Pov = new PopDialog(800, 350, 20, 25);
		_Pov.titleText = '任务进度';
		_Pov.model = true;
		_Pov.renderId = 'taskProcessDlg';
		_Pov.dragEnable = true;
		_Pov.preHide = function() {
			return _Done;
		};
	}
	_Pov.show();
}
function _ajax(url, func, args, method) {
	method = method || 'GET';
	var re = {
		url : url,
		method : method,
		force : true,
		responseType : "json",
		args : args,
		timeout : 10000,
		success : func,
		error : function(errorMsg, responseType, status) {
			alert(errorMsg);
		}
	};
	AjaxReq.sendRequest(re);
}