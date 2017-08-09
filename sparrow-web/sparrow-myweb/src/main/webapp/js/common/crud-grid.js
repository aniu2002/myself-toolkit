var _Render = {
	__checkNull : function(v) {
		if (v == null || v == '' || v == undefined || v == 'null')
			return true;
		return false;
	},
	time : function(v, data, idx, row) {
		if (v)
			return (new Date(v)).format();
	},
	_idx : function(v, data, idx, row, cell) {
		if (cell)
			cell.attr('align', 'center');
		return idx;
	},
	cbox : function(v, data) {
		var el = $('<input type="checkbox" name="_cgroup" >');
		var id = data.id;
		el.attr('_id', id);
		return el;
	},
	radio : function(v, data) {
		var el = $('<input type="radio" name="_cgroup" >');
		var id = data.id;
		el.attr('_id', id);
		return el;
	},
	bool : function(v) {
		if (v == 1)
			return 'Y';
		else
			return 'N'
	},
	status : function(v) {
		var tx = v;
		if (v == 1)
			tx = "启用";
		else
			tx = "禁用";
		return tx;
	},
	state : function(v) {
		var nv = '/app/images';
		var tx = v;
		if (v == 1) {
			nv = '/app/images/run.png';
			tx = "运行";
		} else if (v == 3) {
			nv = '/app/images/success.png';
			tx = "完成";
		} else if (v < 1) {
			nv = '/app/images/error.gif';
			tx = "异常";
		} else {
			nv = '/app/images/stop.png';
			tx = "停止";
		}
		return '<span><img src="' + nv + '"/>-' + tx + '</span>';
	},
	taskType : function(v) {
		var s = Common.getDic('type');
		if (s)
			return s[v].name;
		else
			return v;
	},
	strategy : function(v) {
		var s = Common.getDic('strategy');
		if (s)
			return s[v];
		else
			return v;
	}
};
var _Editor = {
	'text' : function(name, cfg) {
		var el = $('<input type="text" name="' + name + '" >');
		return el;
	},
	'textarea' : function(name, cfg) {
		var el = $('<textarea name="' + name + '"/>');
		return el;
	},
	'pwd' : function(name, cfg) {
		var el = $('<input type="password" name="' + name + '" >');
		return el;
	},
	'date' : function(name, cfg) {
		var el = $('<input type="text" name="' + name + '" >');
		return el;
	},
	'time' : function(name, cfg) {
		var el = $('<input type="text" name="' + name + '" >');
		return el;
	},
	'read' : function(name, cfg) {
		var el = $('<input type="text" name="' + name
				+ '" readonly="readonly">');
		return el;
	},
	'check' : function(name, cfg) {

	},
	'source' : function(name, cfg) {
		var width = '150px';
		if (cfg.width) {
			var n = Common.getPxNum(cfg.width);
			n = n - 80;
			width = n + 'px';
		}
		var el = $('<div></div>');
		var tel = $('<input type="text" name="' + name
				+ '" readonly="readonly">');
		tel.css({
			width : width
		});
		var btn = $('<input type="button" value="选择" />')
		if (cfg.btnClick)
			btn.click(function() {
				cfg.btnClick(tel.val());
			});
		el.append(tel);
		el.append('&nbsp;');
		el.append(btn);
		return el;
	},
	'select' : function(name, cfg, form) {
		var el = $('<select name="' + name + '" ></select>');
		var v = el[0];
		cfg.__fm = form;
		if (cfg.relField)
			Common.renderSelect(cfg.src, v, null, function(sel) {
				var iv = sel.options[sel.selectedIndex].text;
				var g = {};
				g[cfg.relField] = iv;
				form.setParameters(g);
				if (cfg.onSet)
					cfg.onSet(sel.options[sel.selectedIndex].value, sel);
			}, cfg);
		else
			Common.renderSelect(cfg.src, v, null, null, cfg);
		return el;
	},
	'bool' : function(name, cfg) {
		var el = $('<input type="text" name="' + name + '" >');
		return el;
	},
	'checkbox' : function(name, cfg) {
		var el = $('<input type="checkbox" name="' + name + '" >');
		return el;
	}
};
var _ToolTip = function(msg) {
	this.autoHide = true;
};
_ToolTip.prototype = {
	_tip : null,
	_fg : false,
	_create : function() {
		var tip = $('<div class="ToolTip"></div>');
		var icon = $('<span class="icon-tag"></span>');
		var msg = $('<span class="msg-tag"></span>');
		tip.hide();
		tip.append(icon);
		tip.append(msg);
		this._tip = tip;
		this._msg = msg;
		$(document.body).append(tip);
	},
	show : function(el) {
		/** position 相对，offset 绝对 */
		var pos = el.offset();
		var w = pos.left + el.width();
		if (this._tip == null)
			this._create();
		this._tip.css({
			left : w + 'px',
			top : pos.top + 'px'
		});
		this._tip.show();
		if (this.autoHide) {
			var tp = this;
			if (tp._fg)
				return;
			tp._fg = true;
			window.setTimeout(function() {
				tp.hide();
				tp._fg = false;
			}, 3000);
		}
	},
	hide : function() {
		this._tip.hide();
	},
	message : function(msg) {
		this._msg.text(msg);
	}
};
var _Form = function(id, cols, url, search) {
	if (typeof (id) == 'string')
		this.el = $(id);
	else
		this.el = id;
	this.cols = cols;
	this.url = url;
	this.search = search;
	this.formEl = null;
};
_Form.createEditor = function(type, name, value, col, form) {
	if (type == '' || type == null || type == undefined)
		type = 'text';
	var idx = type.indexOf('|');
	var ty = type, width = '150px', height = undefined;
	if (idx != -1) {
		ty = type.substring(0, idx);
		width = type.substring(idx + 1);
	} else
		width = col.width;
	if (col.height)
		height = col.height;
	var el;
	if (_Editor[type])
		el = _Editor[type](name, col, form);
	if (el && value)
		el.val(value);
	if (el && width)
		el.css('width', width);
	if (el && height)
		el.css('height', height);
	return el;
};
_Form.getFormDom = function(ele) {
	var form = undefined;
	if (ele && ele.length != 0)
		form = ele[0];
	return form;
};
_Form.getFormFields = function(ele) {
	var form = _Form.getFormDom(ele);
	if (form) {
		var els = form.elements;
		var fields = {};
		for ( var i = 0; i < els.length; i++) {
			var item = els[i];
			if (item.name != '') {
				var tag = item.tagName;
				tag = tag.toLowerCase();
                if (item.type == 'text' || item.type == 'hidden'
                    || tag == 'select' || item.type == "textarea"
                    || item.type == "password") {
                    fields[item.name] = $(item);
                } else if (item.type == "checkbox") {
                    var exl = $(item);
                    if (exl.is(':checked'))
                        exl.val('1');
                    else
                        exl.val('0');
                    fields[item.name] = exl;
                }
			}
		}
		return fields;
	}
	return undefined;
};
_Form.prototype = {
	_actEl : undefined,
	_editActive : false,
	_editRowId : undefined,
	_validator : undefined,
	_postHandler : undefined,
	_hideEvt : function(fm) {
		var _sf = this;
		fm.keydown(function(e) {
			var k = e.keyCode || e.which;
			if (k == 13) {
				e.stopPropagation();
				e.preventDefault();
				// if (_sf._postHandler)
				// _sf._postHandler();
				return false;
			}
		});
	},
	draw : function() {
		if (this.el == null || this.el == undefined || this.el.length == 0)
			this.el = $('<div></div>');
		else
			this.el.empty();
		// var method
		var form = $('<form action="' + this.url
				+ '" class="breadcrumb form-search" method="POST"></form>');
		var tb;
		if (this.search) {
			tb = this.getSearchTable();
			this._hideEvt(form);
		} else
			tb = this.getFormTable(form);
		form.append(tb);
		this.tableEl = tb;
		this.el.append(form);
		this.formEl = form;
	},
	getSearchTable : function() {
		var table = $('<table></table>');
		this.painSearchRow(table, this.cols);
		return table;
	},
	getFormTable : function(form) {
		var table = $('<table></table>');
		if (this.title)
			this.addRow(this.title, table);
		this.painFormRow(table, this.cols, form);
		return table;
	},
	skipCol : function(col) {
		var t = false;
		if (this.search) {
			t = col.search;
			if (typeof (t) == 'undefined')
				t = false;
			t = !t;
		} else {
			t = col.hidden;
			if (typeof (t) == 'undefined')
				t = false;
		}
		return t;
	},
	clearSet : function() {
		this._editActive = false;
		this._editRowId = undefined;
		var fields = _Form.getFormFields(this.formEl);
		if (fields) {
			for ( var name in fields) {
				var itm = fields[name];
				itm.val('');
			}
		}
	},
	editActive : function(rowid) {
		this._editActive = true;
		this._editRowId = rowid;
	},
	painSearchRow : function(table, cols) {
		var row = undefined;
		var lastCell = undefined;
		var n = 0;
		for ( var i = 0; i < cols.length; i++) {
			var col = cols[i];
			if (this.skipCol(col))
				continue;
			if (n % 3 == 0) {
				if (row)
					table.append(row);
				row = $('<tr></tr>');
			}
			lastCell = this.painItem(row, col);
			n++;
		}
		if (row) {
			table.append(row);
			var search = $('<a class="btn btn-primary">&nbsp;<i class="icon-search icon-white"></i>查询</a>');
			this._actEl = search;
			if (n % 3 !== 0) {
				var colspan = (3 - (n % 3)) * 2;
				row.append($('<td colspan="' + colspan + '"></td>').append(
						search));
			} else if (lastCell)
				lastCell.append(search);
		}
	},
	colspan : 2,
	painFormRow : function(table, cols, form) {
		var row = undefined;
		var lastCell = undefined;
		var n = 0;
		var c = this.colspan;
		var cc = c;
		for ( var i = 0; i < cols.length; i++) {
			var col = cols[i];
			// this.skipCol(col))
			if (col.name == 'id')
				continue;
			else if (col.noe && form) {
				var el = $('<input type="hidden" name="' + col.name + '" />');
				form.append(el);
				continue;
			}
			if (cc >= c) {
				if (row)
					table.append(row);
				row = $('<tr></tr>');
				cc = 0;
			}
			var sp = 1;
			if (col.colspan)
				sp = col.colspan;
			var jx = c - cc;
			// 不够
			if (sp > jx) {
				// 补足行
				if (jx > 0)
					this.appendTd(row, jx);
				table.append(row);
				// 创建新row
				row = $('<tr></tr>');
				cc = 0;
			}
			lastCell = this.painItem(row, col);
			if (col.colspan)
				cc = cc + col.colspan;
			else
				cc = cc + 1;
			n++;
		}
		if (row) {
			table.append(row);
			cc = c - cc;
			if (cc > 0) {
				var colspan = cc * 2;
				lastCell = $('<td colspan="' + colspan + '">&nbsp;</td>');
				row.append(lastCell);
			}
		}
		return lastCell;
	},
	appendTd : function(row, colspan) {
		colspan = colspan * 2;
		var cel = $('<td align="left" colspan="' + colspan + '">&nbsp;</td>');
		row.append(cel);
	},
	addRow : function(desc, table) {
		table = table || this.tableEl;
		var c = this.colspan;
		var colspan = c * 2;
		var row = $('<tr height="50"></tr>');
		row
				.append($('<td align="left" colspan="' + colspan
						+ '"><span style="font-weight:bold;">' + desc
						+ '</span></td>'));
		table.append(row);
	},
	createProw : function() {
		var c = this.colspan;
		var colspan = c * 2;
		var row = $('<tr height="50"></tr>');
		var cel = $('<td align="left" colspan="' + colspan + '"></td>');

		row.append(cel);
		this.tableEl.append(row);
		return cel;
	},
	drawx : function(fn) {
		if (this.el == null || this.el == undefined || this.el.length == 0)
			this.el = $('<div></div>');
		else
			this.el.empty();
		// var method
		var form = $('<form class="breadcrumb form-search" method="POST" onsubmit="return false"></form>');
		var tb = $('<table></table>');
		form.append(tb);
		this.tableEl = tb;
		this.el.append(form);
		this.formEl = form;
		this.appendEditor(this.cols, tb, form, fn);
	},
	appendEditor : function(cols, tb, form, fn) {
		var nTable = tb;
		if (nTable)
			nTable.empty();
		else
			nTable = $('<table></table>');
		var lastCell = this.painFormRow(nTable, cols, form);
		if (lastCell) {
			var gbtn = $('<a class="btn btn-primary">保存</a>');
			gbtn.click(fn);
			lastCell.append(gbtn);
		}
		this._validator = new _Validator(form);
		this._validator._bindCols(cols);
	},
	appendCols : function(cols) {
		var nTable = this._extraTable;
		if (nTable)
			nTable.empty();
		else {
			nTable = $('<table></table>');
			var ncel = this.createProw();
			ncel.append(nTable);
			this._extraTable = nTable;
		}
		this.painFormRow(nTable, cols, this.formEl);
		if (this._validator)
			this._validator._bindCols(cols);
	},
	painItem : function(row, col, ed) {
		var label = $('<td><label>' + col.label + '：</label></td>');
		var editor;
		if (col.colspan) {
			var sp = col.colspan * 2 - 1;
			editor = $('<td colspan="' + sp + '"></td>');
		} else
			editor = $('<td></td>');
		var el = _Form.createEditor(col.editor, col.name, undefined, col, this);
		if (col.vtype)
			el.attr('vtype', col.vtype);
		if (col.max)
			el.attr('max', col.max);
		editor.append(el)
		row.append(label);
		row.append(editor);
		return editor;
	},
	renderTo : function(rto) {
		if (typeof (rto) == 'string')
			rto = $(rto);
		rto.append(this.el);
	},
	setParameters : function(paras) {
		var fields = _Form.getFormFields(this.formEl);
        if (fields) {
            for ( var name in paras) {
                var itm = fields[name];
                if (itm) {
                    var v = paras[name];
                    if (itm.get(0).type == 'checkbox') {
                        if (v == '1' || v == 1) {
                            itm.val('1');
                            itm.attr('checked', true);
                        } else {
                            itm.val('0');
                            itm.attr('checked', false);
                        }
                    } else {
                        itm.val(v);
                    }
                }
            }
        }
	},
	getFields : function() {
		return _Form.getFormFields(this.formEl);
	},
	getParameters : function() {
		var fields = _Form.getFormFields(this.formEl);
		if (fields) {
			var paras = {};
			for ( var name in fields) {
				var itm = fields[name];
				var vals = itm.val();
				if (vals == '' || vals == null || vals == undefined)
					continue;
				paras[name] = vals;
			}
			return paras;
		}
		return undefined;
	},
	bindHandler : function(func) {
		this._postHandler = func;
		/** for search */
		if (this._actEl) {
			this._actEl.click(function(evt) {
				evt.stopPropagation();
				evt.preventDefault();
				func();
			});
		}
	},
	submit : function() {
		var formDom = _Form.getFormDom(this.formEl);
		if (formDom)
			formDom.submit();
	},
	reset : function() {
		var formDom = _Form.getFormDom(this.formEl);
		if (formDom)
			formDom.reset();
	},
	ajaxSubmit : function(func) {
		var url = this.url;
		var eFn = this.eventFn;
		var data = this.getParameters();
		if (this._editActive) {
			url = url + '/' + this._editRowId;
			// data.id = this._editRowId;
			data._method = 'PUT';
		}
		if (this._editActive) {
			_CRUD_.openAlert({
				confirm : '确认更新么?',
				ok : function() {
					if (eFn)
						eFn(data);
					Common.post(url, data, func);
				}
			});
		} else {
			if (eFn)
				eFn(data);
			Common.post(url, data, func);
		}
	}
};

var _Validator = function(form) {
	this._form = form;
	this._feilds = _Form.getFormFields(form);
	this._bind();
};
_Validator.vTypes = {
	requiredMsg : "输入的值不能为空",
	required : function(value, field) {
		var fg = (value != '');
		return fg;
	},
	maxMsg : "输入的值不能超过长度限制:{0}",
	maxArg : 'max',
	max : function(value, el) {
		var fg = true;
		var mx = parseInt(el.attr('max'));
		if (mx) {
			fg = (value.length <= mx);
		}
		return fg;
	},
	numMsg : "输入的值不是数字",
	num : function(value, el) {
		if (/^(0|[1-9][0-9]*)$/.test(value)) {
			return true;
		}
		return false;
	},
	dateMsg : "输入的值不能为空",
	date : function(value, el) {
		return value != '';
	},
	pwdMsg : "两次密码输入不对！",
	pwd : function(value, el) {
		var ref = el.attr('ref');
		if (ref)
			ref = $(ref);
		if (ref)
			return value == ref.val();
		return false;
	},
	emailMsg : "邮箱输入错误！例如：al@163.com",
	email : function(value, el) {
		var reg = /^(?:[a-z\d]+[_\-\+\.]?)*[a-z\d]+@(?:([a-z\d]+\-?)*[a-z\d]+\.)+([a-z]{2,})+$/i;
		return reg.test(value);
	}
};
_Validator.prototype = {
	_tip : null,
	err : function(k, msg) {
		var fs = this._feilds;
		var el = fs[k];
		this.error(el, msg);
		el[0].focus();
	},
	error : function(el, msg) {
		if (el.length == 0)
			return;
		el.css('border-color', 'red');
		el.attr('_er', 't');
		if (!this._tip) {
			this._tip = new _ToolTip();
			this._tip._create();
		}
		this._tip.show(el);
		this._tip.message(msg);
		this._tip.oEl = el;
	},
	reset : function(el) {
		el.css('border-color', '');
		el.attr('_er', undefined);
		if (this._tip) {
			this._tip.oEl.attr('_er', undefined);
			this._tip.hide();
		}
	},
	check : function(e) {
		e.stopPropagation();
		e.preventDefault();
		var key = e.keyCode;
		var ele = $(e.target);
		var _s = this;
		var typ = ele.attr('vtype');
		if (typ) {
			if (_s.doCheck(typ, ele)) {
				_s.reset(ele);
				return true;
			} else {
				var bl = el.attr('_er');
				if (bl === undefined) {
					var msg = _Validator.vTypes[typ + "Msg"];
					var ags = _Validator.vTypes[typ + "Arg"];
					msg = Common.fetch(msg, el, ags)
					_s.error(ele, msg);
				}
				return false;
			}
		}
		return false;
	},
	_split : function(s) {
		if (s) {
			var args = [];
			var idx = s.indexOf(' ');
			if (idx == -1)
				args[args.length] = s;
			else {
				while (idx != -1) {
					args[args.length] = s.substring(0, idx);
					s = s.substring(idx + 1);
					idx = s.indexOf(' ');
				}
				if (s)
					args[args.length] = s;
			}
			return args;
		}
		return null;
	},
	doCheck : function(type, ele) {
		var ts = this._split(type);
		var vts = _Validator.vTypes;
		var _slf = this;

		var val = ele.val();
		for ( var i = 0; i < ts.length; i++) {
			var typ = ts[i];
			var reg = vts[typ];
			if (!reg)
				continue;
			if (reg(val, ele)) {
				continue;
			} else {
				var msg = vts[typ + "Msg"];
				var ags = vts[typ + "Arg"];
				msg = Common.fetch(msg, ele, ags)
				_slf.error(ele, msg);
				return false;
			}
		}
		return true;
	},
	checkField : function(ele) {
		var typ = ele.attr('vtype');
		if (typ === undefined)
			return true;
		var vts = _Validator.vTypes;
		var _slf = this;
		if (_slf.doCheck(typ, ele)) {
			_slf.reset(ele);
			return true;
		} else {
			// var msg = vts[typ + "Msg"];
			// var ags = vts[typ + "Arg"];
			// msg = Common.fetch(msg, ele, ags)
			// _slf.error(ele, msg);
			return false;
		}
	},
	checkForm : function() {
		var fs = this._feilds;
		for ( var k in fs) {
			if (!this.checkField(fs[k]))
				return false;
		}
		return true;
	},
	checkFields : function(fields) {
		var fs = fields;
		for ( var k in fs) {
			if (!this.checkField(fs[k]))
				return false;
		}
		return true;
	},
	_bindCols : function(cols) {
		if (cols && cols.length) {
			var els = _Form.getFormFields(this._form);
			var _s = this;
			var ck = function(e) {
				e.stopPropagation();
				e.preventDefault();
				_s.checkField($(this));
			};
			for ( var i = 0; i < cols.length; i++) {
				var col = cols[i];
				var im = els[col.name];
				if (im) {
					var typ = im.attr('vtype');
					if (typ)
						im.blur(ck);
				}
			}
		}
	},
	_bind : function() {
		var fs = this._feilds;
		var fm = this._form;
		var im;
		var _s = this;
		var ck = function(e) {
			e.stopPropagation();
			e.preventDefault();
			_s.checkField($(this));
		};
		for ( var k in fs) {
			im = fs[k];
			var typ = im.attr('vtype');
			if (typ)
				im.blur(ck);
		}
		// _s.submit();
	},
	submit : function() {
		if (this.checkForm())
			this._form.submit();
	}
};
var _Create = function(o) {
	var no = function() {
		this.init.apply(this, arguments);
	};
	var _proto = no.prototype;
	for ( var k in o)
		_proto[k] = o[k];
	return no;
};
var _Constructor = {
	init : function(cfg) {
		cfg = cfg || {};
		this.el = $(cfg.id);
		this.closed = cfg.closed;
		this.cfg = cfg;
		this._ui = cfg._ui;
		if (this.postInit)
			this.postInit(cfg);
	}
};
var _BaseDlg = _Create(_Constructor);
_BaseDlg.prototype = {
	drawHeader : function(el) {
		var title
		if (this.cfg.title)
			title = cfg.title;
		else
			title = '详情信息';
		var head = $('<div class="modal-header"></div>');
		var closeBtn = $('<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>');
		this.titleEl = $('<h3 id="modal_header">' + title + '</h3>');
		head.append(closeBtn).append(this.titleEl);
		el.append(head);
		var _this = this;
		closeBtn.click(function() {
			if (_this.closed)
				_this.close();
			else
				_this.hide();
		});
		this.headerEl = head;
	},
	drawBody : function(el) {
		var bd = $('<div class="modal-body"></div>');
		this.modelBody = $('<div></div>');
		bd.append(this.modelBody);
		el.append(bd);
	},
	drawFooter : function(el) {
		var footer = $('<div class="modal-footer"></div>');
		var btns = this.getButtons();
		btns = btns || {};
		// btn-primary
		var okBtn = $('<button class="btn btn-primary">确定</button>');
		var cancelBtn = $('<button class="btn">取消</button>');
		var _this = this;
		var _hide = function() {
			if (_this.closed)
				_this.close();
			else
				_this.hide();
		};
		if (btns.ok)
			okBtn.click(function() {
				btns.ok(_this);
			});
		else
			okBtn.click(_hide);
		if (btns.cancel)
			cancelBtn.click(function() {
				btns.cancel(_this);
			});
		else
			cancelBtn.click(_hide);

		footer.append(cancelBtn).append(okBtn);
		el.append(footer);
	},
	setTitle : function(t) {
		if (this.titleEl)
			this.titleEl.text(t);
	},
	getButtons : function() {
		return undefined;
	},
	draw : function() {
		if (this.el.length == 0) {
			if (this.cfg.id)
				this.el = $('<div id="'
						+ this.cfg.id
						+ '" class="modal hide" role="dialog" data-backdrop="static"></div>');
			else
				this.el = $('<div class="modal hide" role="dialog" data-backdrop="static"></div>');
			$(document.body).append(this.el);
		} else
			this.el.empty();
		this.drawHeader(this.el);
		this.drawBody(this.el);
		this.drawFooter(this.el);
	},
	render : function(html) {
		this.modelBody.empty();
		this.modelBody.append(html);
	},
	show : function() {
		this.el.modal({
			backdrop : true,
			keyboard : true,
			show : true
		});
		if (this.afterShow)
			this.afterShow();
	},
	hide : function() {
		this.el.modal("hide");
	},
	close : function() {
		this.el.modal("hide");
		this.remove();
	},
	remove : function() {
		this.el.remove();
	}
};

var _Alert = _Create(_Constructor);

$.extend(_Alert.prototype, _BaseDlg.prototype, {
	postInit : function(cfg) {
		if (cfg.ok)
			this.btns = {
				ok : function(dlg) {
					cfg.ok(dlg);
					dlg.close();
				}
			};
	},
	getButtons : function() {
		return this.btns;
	}
});

var _Dialog = _Create(_Constructor);

$.extend(_Dialog.prototype, _BaseDlg.prototype, {
	_form : null,
	title : null,
	colspan : 2,
	_btns : {
		'ok' : function(dlg) {
			if (dlg._form && dlg._validator.checkForm()) {
				dlg._form.ajaxSubmit(function(s) {
					if (s.code != 0) {
						alert(s.msg);
						return;
					}
					dlg.hide();
					if (dlg._ui)
						dlg._ui.grid.reload();
				});
			}
		}
	},
	postInit : function(cfg) {
		this.cols = cfg.cols;
		this.url = cfg.url;
	},
	render : function(data, fn) {
		if (this._form == null) {
			this._form = new _Form('#modalForm', this.cols, this.url, false);
			this._form.colspan = this.colspan;
			this._form.title = this.title;
			this._form.draw();
			if (this.modelBody)
				this._form.renderTo(this.modelBody);
			this._validator = new _Validator(this._form.formEl);
			this._form._validator = this._validator;
		}
		if (data) {
			// if (data._id)
			data._id = data.id;
			if (data._id)
				this._form.editActive(data._id);
			this._form.setParameters(data);
		} else {
			this._form.clearSet();
		}
		if (fn)
			this._form.eventFn = fn;
	},
	afterShow : function() {
		if (this._form) {
			var h = this._form.tableEl.width() + 150;
			this.el.css({
				width : h + 'px'
			});
		}
	},
	getButtons : function() {
		return this._btns;
	}
});

var _Panel = _Create(_Constructor);

$.extend(_Panel.prototype, _Dialog.prototype, {
	colspan : 3,
	_redirectUrl : undefined,
	_btns : {
		'ok' : function(dlg) {
			var fields = dlg._form.getFields();
			if (dlg._form && dlg._validator.checkFields(fields)) {
				dlg._form.ajaxSubmit(function(s) {
					if (s.code != 0) {
						alert(s.msg);
						return;
					}
					if (dlg._redirectUrl) {
						self.location = dlg._redirectUrl;
					}
					dlg.hide();
					if (dlg._ui)
						dlg._ui.grid.reload();
				});
			}
		}
	},
	draw : function() {
		if (this.el.length == 0) {
			if (this.cfg.id)
				this.el = $('<div id="' + this.cfg.id + '"></div>');
			else
				this.el = $('<div></div>');
			$(document.body).append(this.el);
		} else
			this.el.empty();
		// this.drawHeader(this.el);
		this.drawBody(this.el);
		// this.drawFooter(this.el);
	},
	render : function(data, fn) {
		if (this._form == null) {
			this._form = new _Form('#modalForm', this.cols, this.url, false);
			this._form.colspan = this.colspan;
			this._form.title = this.title;
			this._form.draw();
			if (this.modelBody)
				this._form.renderTo(this.modelBody);
		}
		if (data) {
			data._id = data.id;
			if (data._id)
				this._form.editActive(data._id);
			this._form.setParameters(data);
		} else {
			this._form.clearSet();
		}
		if (fn)
			this._form.eventFn = fn;
	},
	endDraw : function() {
		this.drawFooter(this.el);
		this._validator = new _Validator(this._form.formEl);
		this._form._validator = this._validator;
	},
	addRow : function(d) {
		this._form.addRow(d);
	},
	appendCols : function(cols) {
		this._form.appendCols(cols);
	},
	drawBody : function(el) {
		this.modelBody = $('<div></div>');
		// bd.append(this.modelBody);
		el.append(this.modelBody);
		// this.modelBody = el;
	},
	drawFooter : function(el) {
		// var footer = $('<div class="modal-footer"></div>');
		var btns = this.getButtons();
		btns = btns || {};
		// btn-primary
		var okBtn = $('<button class="btn btn-primary">保存</button>');
		var cancelBtn = $('<button class="btn">清除</button>');
		var _this = this;
		var reset = function() {
			_this._form.reset();
		};
		if (btns.ok)
			okBtn.click(function() {
				btns.ok(_this);
			});
		else
			okBtn.click(reset);
		if (btns.cancel)
			cancelBtn.click(function() {
				btns.cancel(_this);
			});
		else
			cancelBtn.click(reset);

		el.append(cancelBtn).append("&nbsp;&nbsp;").append(okBtn);
		// el.append(footer);
	}
});

var _DetailDlg = _Create(_Constructor);
var _DPrototype = {
	postInit : function(cfg) {
		this.cols = cfg.cols;
		this.url = cfg.url;
	},
	drawFooter : function(el) {
		var footer = $('<div class="modal-footer"></div>');
		var okBtn = $('<button class="btn btn-primary">确定</button>');
		var _this = this;
		var _hide = function() {
			_this.hide();
		};
		okBtn.click(_hide);
		footer.append(okBtn);
		el.append(footer);
	},
	render : function(data) {
		this.modelBody.empty();
		this.modelBody.append(this.createTable(this.cols, data,
				this.cfg.detailRender));
	},
	load : function(data) {
		this.modelBody.empty();
		if (this.cfg.detailRenderCb && this.url) {
			var u = this.url + '/' + data._id;
			var cb = this.cfg.detailRenderCb;
			var _el = this.modelBody;
			Common.ajax(u, null, function(d) {
				cb(_el, d);
			});
		}
	},
	createTable : function(cols, data, render) {
		var table = $('<table class="table table-striped table-bordered table-condensed table-hover"></table>');
		for ( var i = 0; i < cols.length; i++) {
			var col = cols[i];
			if (col == 'x' || col == 'o')
				continue;
			if (col.noe)
				continue;
			table.append(this.createItem(col, data, render));
		}
		return table;
	},
	createItem : function(col, data, render) {
		var row = $('<tr></tr>');
		var label = $('<td width="80" valign="top"><label>' + col.label
				+ '：</label></td>');
		var cell = $('<td></td>');
		var v = '&nbsp;';
		var cname = col.name
		// if (data[cname])
		v = data[cname];
		if (render && render[cname]) {
			var rnm = render[cname];
			var rd = _Render[rnm];
			if (rd)
				v = rd(v, data);
		} else {
			var _r = col.render;
			if (col._rdr == '_idx')
				v = v + '';
			else if (_r)
				v = _r(v, data);
		}

		if (col.max > 60) {
			var del = $('<div style="word-break:break-all;white-space:normal;width:100%;"></div>');
			del.append(v);
			cell.append(del);
		} else
			cell.append(v)

		row.append(label);
		row.append(cell);
		return row;
	}
};

$.extend(_DetailDlg.prototype, _BaseDlg.prototype, _DPrototype);

var _PopMenu = function(id, items) {
	if (typeof (id) == 'string')
		this.el = $(id);
	else
		this.el = id;
	this.items = items;
};

_PopMenu.findRowEle = function(ele) {
	while (ele != null && ele.length != 0) {
		var s = ele[0].tagName;
		s = s.toLowerCase();
		if (s == 'tr')
			return ele;
		if (s == 'table')
			return null;
		ele = ele.parent();
	}
};

_PopMenu.prototype = {
	clickedRow : undefined,
	draw : function() {
		if (this.el.length == 0) {
			this.el = $('<div class="dropdown clearfix" style="display:none;position:absolute;"></div>');
			$(document.body).append(this.el);
		} else
			this.el.empty();

		this.ulEl = $('<ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu"></div>');
		this.ulEl.css({
			display : 'block',
			position : 'static',
			marginBottom : '5px'
		});
		this.el.append(this.ulEl);
	},
	appendItems : function(items) {
		if (items && items.length)
			for ( var i = 0; i < items.length; i++) {
				var itm = items[i];
				if (itm == '-')
					this.drawDivider(this.ulEl);
				else
					this.drawItem(this.ulEl, itm);
			}
	},
	drawDivider : function(ulEl) {
		ulEl.append('<li class="divider"></li>');
	},
	drawItem : function(ulEl, item) {
		var li = $('<li></li>');
		var link = $('<a name="' + item.name + '">' + item.label + '</a>');
		var _this = this;
		link.click(function(evt) {
			evt.stopPropagation();
			evt.preventDefault();
			if (item.func)
				item.func(item.name, _this.clickedRow);
			_this.el.hide();
		});
		li.append(link);
		ulEl.append(li);
	},
	bind : function(toEl) {
		var _this = this;
		var t = typeof (toEl);
		if (t == 'undefined')
			return;
		else if (t == 'string')
			toEl = $(toEl);
		if (toEl.length == 0)
			return;
		var w = $(window).width();
		var pw = w - this.el.width() - 30;
		var f = function(e) {
			var ele = $(e.target);
			ele = _PopMenu.findRowEle(ele);
			_this.clickedRow = ele;
			var nw = e.pageX;
			if (nw > pw)
				nw = pw;
			_this.el.css({
				display : 'block',
				left : nw,
				top : e.pageY
			});
			_this.el.show();
			return false;
		};
		toEl.children('table:first-child').bind("contextmenu", f);
		$(document).click(function(evt) {
			_this.el.hide();
		});
	}
};

var _CRUD_ = {
	opBar : [ {
        name: '增加',
        func: function (grid) {
            if (this.url) {
                self.location = this.url;
                return;
            }
            if (grid._ui.modalDlg) {
                grid._ui.modalDlg.render();
                grid._ui.modalDlg.show();
                _CRUD_.afterOp();
            }
        }
    },{
		name : '删除',
		func : function(grid) {
			var ids = _CRUD_.selectIds(grid);
			_CRUD_.openAlert({
				confirm : '确认删除该记录么?',
				ok : function() {
					_CRUD_.deleteRows(grid.url, ids, grid);
				}
			});
		}
	} ],
    getSelectedVal: function (grid) {
        var tb = grid._bodyEl;
        tb.find('[name = _cgroup]:checkbox').each(function () {
            var el = $(this);
            var n = el.attr('checked');
            if (n) {
                var id = el.attr('_id');
                grid.removeSelectVal(id);
                grid.addSelectVal(id);
            }
        });
        return grid._selVal;
    },
	selectIds : function(grid) {
		var tb = grid._bodyEl;
		var ids = '';
		tb.find('[name = _cgroup]:checkbox').each(function() {
			var el = $(this);
			var n = el.attr('checked');
			if (n)
				ids += ',' + el.attr('_id');
		});
		if (ids != '')
			ids = ids.substring(1);
		return ids;
	},
	selectRIds : function(grid) {
		var tb = grid._bodyEl;
		var ids = '';
		tb.find('[name = _cgroup]:radio').each(function() {
			var el = $(this);
			var n = el.attr('checked');
			if (n)
				ids += ',' + el.attr('_id');
		});
		if (ids != '')
			ids = ids.substring(1);
		return ids;
	},
	selectRow : function(grid, fg) {
		var tb = grid._bodyEl;
		if (fg == undefined)
			fg = false;
		else
			fg = true;
		tb.find('[name = _cgroup]:checkbox').each(function() {
			$(this).attr('checked', fg);
		});
	},
	createRadioCol : function(grid, ui) {
		return {
			_create : function() {
				return $('<label>选择</label>');
			},
			noe : true,
			_st : 'o',
			width : '30px',
			render : _Render['radio']
		};
	},
	createCboxCol : function(grid, ui) {
		return {
			_create : function() {
				var el = $('<input type="checkbox" name="_cx" >');
				el.click(function() {
					if (grid)
						_CRUD_.selectRow(grid, el.attr('checked'));
					else
						_CRUD_.selectRow(ui.grid, el.attr('checked'));
				});
				return el;
			},
			noe : true,
			_st : 'x',
			width : '30px',
			render : _Render['cbox']
		};
	},
	renderTable : function(cfg) {
		var cols = cfg.cols, url = cfg.url;
		var _ui = {
			cols : cols,
			url : url
		};
		cfg.opBar = cfg.opBar || undefined;
		for ( var i = 0; i < cols.length; i++) {
			var col = cols[i];
			if (typeof (col) == 'string') {
				switch (col) {
				case 'x':
					col = _CRUD_.createCboxCol(_ui.grid, _ui);
					break;
				case 'o':
					col = _CRUD_.createRadioCol(_ui.grid, _ui);
					break;
				}
				cols[i] = col;
			} else if (typeof (col.render) == 'string') {
				col._rdr = col.render;
				col.render = _Render[col.render];
			}
		}
		var _dlgCfg = {
			cols : cols,
			url : url,
			_ui : _ui
		};
		if (cfg.searchEl)
			_ui.searchForm = _CRUD_.drawSearchForm(cfg.searchEl, cols, url);
		if (cfg.canOp) {
			_dlgCfg.id = '#modal_div';
			_ui.modalDlg = _CRUD_.drawModalDialog(_dlgCfg);
		}
		if (cfg.showMenu)
			_ui.ctxMenu = _CRUD_.drawContextMenu();
		if (cfg.showDetail) {
			_dlgCfg.id = '#modal_detail';
			_dlgCfg.detailRender = cfg.detailRender;
			_ui.detailDlg = _CRUD_.drawDetailDlg(_dlgCfg);
		}
		if (cfg.detailUrl) {
			_dlgCfg.id = '#modal_detail';
			_dlgCfg.url = cfg.detailUrl;
			_dlgCfg.detailRenderCb = cfg.detailRenderCb;
			_ui.detailDlgEx = _CRUD_.drawDetailDlg(_dlgCfg);
		}
		var rowDblclick = function() {
			var rid = $(this).attr('rowid');
			if (cfg.canOp) {
				if (cfg.rowDblClick) {
					var r = _ui.grid.findRecord(rid);
					r._id = rid;
					cfg.rowDblClick(r);
					return;
				} else if (_ui.modalDlg) {
					var r = _ui.grid.findRecord(rid);
					r._id = rid;
					_ui.modalDlg.render(r);
					_ui.modalDlg.show();
					return;
				}
			}
			var dgl = _ui.detailDlgEx || _ui.detailDlg;
			if (dgl) {
				dgl.show();
				var r = page.findRecord(rid);
				r._id = rid;
				if (r) {
					if (cfg.detailUrl)
						dgl.load(r);
					else
						dgl.render(r);
				}
			}
		};
		var mak = true;
		if (typeof (cfg.mask) != 'undefined')
			mak = cfg.mask;
		var lmt = cfg.limit || 10;
		var bbr = {
			limit : lmt,
			stepSize : 5
		};
		if (typeof (cfg.pBar) != 'undefined') {
			if (cfg.pBar > 0) {
				bbr.top = '#pageBar';
				if (cfg.pBar == 2)
					bbr.bottom = '#pageBar2';
			}
		} else {
			bbr.top = '#pageBar';
			bbr.bottom = '#pageBar2';
		}
		var page = new LPage({
			id : '#grid',
			url : url,
            idField : cfg.idField,
			pageBar : bbr,
			maskEnable : mak,
			maskBd : cfg.maskBd,
			ajaxMethod : 'POST',
			cols : cols,
			opBar : cfg.opBar,
			beforeLoad : function(cfg) {
				cfg._method = 'GET';
				if (_ui.searchForm) {
					var paras = _ui.searchForm.getParameters();
					for ( var k in paras)
						cfg[k] = paras[k];
				}
			},
			onLoaded : function(data) {
			},
			rowDblclick : rowDblclick,
            rowSelect: cfg.rowSelect,
			errorLoad : Common.errorLoad
		});
		_ui.grid = page;
		if (_ui.searchForm) {
			var doSearch = function() {
				page.clearLoad(1);
			};
			_ui.searchForm.bindHandler(doSearch);
		}
		if (_ui.ctxMenu) {
			var clk = function(name, row) {
				_CRUD_.ctxMenuClick(name, row, _ui);
			};
			var items = [ {
				name : 'refresh',
				label : '刷新表格',
				func : clk
			}, {
				name : 'detail',
				label : '记录详情',
				func : clk
			}, '-', {
				name : 'add',
				label : '增加记录',
				func : clk
			}, {
				name : 'edit',
				label : '编辑记录',
				func : clk
			}, '-', {
				name : 'delete',
				label : '删除记录',
				func : clk
			} ];
			_ui.ctxMenu.appendItems(items);
			_ui.ctxMenu.bind(page.containerEl);
		}
		return _ui;
	},
	ctxMenuClick : function(name, row, _ui) {
		switch (name) {
		case 'refresh':
			if (_ui.grid) {
				_ui.grid.reload();
			}
			break;
		case 'add':
			if (_ui.modalDlg) {
				_ui.modalDlg.render();
				_ui.modalDlg.show();
			}
			break;
		case 'detail':
			var rid = row.attr('rowid');
			if (rid == undefined || rid == null || rid == '')
				return;
			if (_ui.detailDlg) {
				_ui.detailDlg.show();
				var r = _ui.grid.findRecord(rid);
				r._id = rid;
				if (r)
					_ui.detailDlg.render(r);
			}
			break;
		case 'edit':
			var rid = row.attr('rowid');
			if (rid == undefined || rid == null || rid == '')
				return;
			if (_ui.modalDlg) {
				var r = _ui.grid.findRecord(rid);
				r._id = rid;
				_ui.modalDlg.render(r);
				_ui.modalDlg.show();
			}
			break;
		case 'delete':
			var rid = row.attr('rowid');
			_CRUD_.openAlert({
				confirm : '确认删除该记录么?',
				ok : function() {
					_CRUD_.deleteRow(_ui.url, rid, _ui.grid);
				}
			});
			break;
		}
	},
	deleteRow : function(url, id, grid) {
		Common.post(url + '/' + id, {
			_method : 'DELETE'
		}, function(x) {
			grid.reload();
		});
	},
	deleteRows : function(url, id, grid) {
		Common.post(url + '/delete', {
			_method : 'DELETE',
			id : id
		}, function(x) {
			grid.reload();
		});
	},
	drawModalDialog : function(cfg) {
		var dlg = new _Dialog(cfg);
		dlg.draw();
		return dlg;
	},
	drawSearchForm : function(id, cols, url) {
		var fg = false;
		for ( var i = 0; i < cols.length; i++) {
			if (cols[i].search) {
				fg = true;
				break;
			}
		}
		if (!fg)
			return undefined;
		var form = new _Form(id, cols, url, true);
		form.draw();
		return form;
	},
	drawContextMenu : function() {
		var menu = new _PopMenu('#popMenu');
		menu.draw();
		return menu;
	},
	drawDetailDlg : function(cfg) {
		var dlg = new _DetailDlg(cfg);
		dlg.draw();
		return dlg;
	},
	openAlert : function(opts, fn) {
		var cfg;
		if (typeof (opts) == 'string')
			cfg = {
				confirm : opts,
				ok : fn
			};
		else
			cfg = opts;
		cfg.closed = true;
		var m = new _Alert(cfg);
		m.draw();
		m.render(cfg.confirm);
		m.show();
		return m;
	},
	appendToX : function(el, cols, data) {
		$(el).append(this.painTableX(cols, data));
	},
	appendTdX : function(row, colspan) {
		colspan = colspan * 2;
		var cel = $('<td align="left" colspan="' + colspan + '">&nbsp;</td>');
		row.append(cel);
	},
	painTableX : function(cols, data) {
		var row = undefined;
		var lastCell = undefined;
		var n = 0;
		var c = 3;
		var cc = c;
		// class="table table-striped table-bordered table-condensed
		// table-hover"
		var table = $('<table></table>');
		for ( var i = 0; i < cols.length; i++) {
			var col = cols[i];
			if (cc >= c) {
				if (row)
					table.append(row);
				row = $('<tr></tr>');
				cc = 0;
			}
			var sp = 1;
			if (col.colspan)
				sp = col.colspan;
			var jx = c - cc;
			// 不够
			if (sp > jx) {
				// 补足行
				if (jx > 0)
					this.appendTdX(row, jx);
				table.append(row);
				// 创建新row
				row = $('<tr></tr>');
				cc = 0;
			}
			lastCell = this.painItemX(row, col, data);
			if (col.colspan)
				cc = cc + col.colspan;
			else
				cc = cc + 1;
			n++;
		}
		if (row) {
			table.append(row);
			cc = c - cc;
			if (cc > 0) {
				var colspan = cc * 2;
				row.append($('<td colspan="' + colspan + '">&nbsp;</td>'));
			}
		}
		return table;
	},
	painItemX : function(row, col, data) {
		var label = $('<td><label>' + col.label + '：</label></td>');
		var cname = col.name
		var v = data[cname];

		var editor;
		if (col.colspan) {
			var sp = col.colspan * 2 - 1;
			editor = $('<td colspan="' + sp + '"></td>');
		} else
			editor = $('<td></td>');

		var _r = col.render;
		if (col._rdr == '_idx')
			v = v + '';
		else if (typeof (_r) == 'string') {
			var rd = _Render[_r];
			if (rd)
				v = rd(v, data);
		} else if (typeof (_r) == 'function') {
			v = _r(v, data);
		}

		if (col.max > 60) {
			var del = $('<div style="word-break:break-all;white-space:normal;width:100%;"></div>');
			del.append(v);
			editor.append(del);
		} else
			editor.append(v);

		row.append(label);
		row.append(editor);
		return editor;
	}
};
