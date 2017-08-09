Date.prototype.format = function(format) {
	format = format || 'yyyy-MM-dd hh:mm:ss';
	var o = {
		"M+" : this.getMonth() + 1,
		"d+" : this.getDate(),
		"h+" : this.getHours(),
		"m+" : this.getMinutes(),
		"s+" : this.getSeconds(),
		"q+" : Math.floor((this.getMonth() + 3) / 3),
		"S" : this.getMilliseconds()
	};
	if (/(y+)/.test(format))
		format = format.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(format))
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
					: ("00" + o[k]).substr(("" + o[k]).length));
	return format;
};
String.prototype.startsWith = function(substring) {
	var reg = new RegExp("^" + substring);
	return reg.test(this);
};
String.prototype.endsWith = function(substring) {
	var reg = new RegExp(substring + "$");
	return reg.test(this);
};
String.prototype.trim = function() {
	var extraSpace = /[\s\n\r]+/g;
	return this.replace(extraSpace, "");
};

var FormCache = {};
function getFormFields(form) {
	var eleS = form.elements;
	var fields = {};
	for ( var i = 0; i < eleS.length; i++) {
		var item = eleS[i];
		if (item.name != '') {
			if (item.type == 'text' || item.type == "textarea"
					|| item.type == "password") {
				fields[item.name] = item;
			}
		}
	}
	return fields;
}
function FormSet(form, paras) {
	var el = null;
	if (typeof (form) == "string")
		el = document.getElementById(form);
	else
		el = form;
	var fields = FormCache[form];
	if (fields == null) {
		fields = getFormFields(el);
		FormCache[form] = fields;
	}
	for ( var name in paras) {
		var itm = fields[name];
		if (itm) {
			itm.value = paras[name];
		}
	}
}

function serializeForm(form) {
	var i, queryString = "", and = "";
	var item; // for each form's object
	var itemValue;// store each form object's value
	if (typeof (form) == "string")
		form = document.getElementById(form);
	var frmID = form.elements();
	for (i = 0; i < frmID.length; i++) {
		item = frmID[i];// get form's each object
		if (item.name != '') {
			if (item.type == 'select-one') {
				itemValue = item.options[item.selectedIndex].value;
			} else if (item.type == 'checkbox' || item.type == 'radio') {
				if (item.checked == false) {
					continue;
				}
				itemValue = item.value;
			} else if (item.type == 'button' || item.type == 'submit'
					|| item.type == 'reset' || item.type == 'image') {// ignore
				// this
				// type
				continue;
			} else {
				itemValue = item.value;
			}
			itemValue = encodeURIComponent(itemValue);
			queryString += and + item.name + '=' + itemValue;
			and = "&";
		}
	}
	return queryString;
}

function BuildHtmlString(data) {
	return BuildHtmlTemp(data, null);
}
function BuildHtmlTemp(data, tmp, renders) {
	var tp = tmp;
	for ( var k in data) {
		var by = '{' + k + '}';
		var v = data[k];
		if (renders && renders[k])
			v = renders[k](v, data)
		if (v == null || typeof (v) == 'undefined')
			v = '';
		tp = tp.replace(new RegExp(by, "gm"), v);
	}
	return tp;
}
function HtmlTemp(tmp, renders) {
	this.template = tmp;
	this.renders = renders;
}

HtmlTemp.prototype = {
	render : function(data) {
		return BuildHtmlTemp(data, this.template, this.renders);
	}
}
var Common = {
	ditionary : {},
	store : {
		systemsx : [ '搜索', '抓取', '数据分析', '数据处理', '算法', '指数系统' ],
		times : [ '1', '2', '3', '4', '5' ],
		env : [ 'hadoop', 'hive', 'hbase', 'spark', 'storm' ],
		pos : [ '1', '2', '3', '4', '5', '6', '7', '8', '9' ],
		cancel : [ {
			name : '是',
			code : '1'
		}, {
			name : '否',
			code : '0'
		} ],
		message : [ {
			name : '邮箱',
			code : '1'
		}, {
			name : '手机',
			code : '2'
		} ],
		disType : [ {
			name : '选择框',
			code : '1'
		} ],
		state : [ {
			name : '禁用',
			code : '0'
		}, {
			name : '可用',
			code : '1'
		} ],
		rptype : [ {
			name : 'SQL参数',
			code : '0'
		}, {
			name : '图表参数',
			code : '1'
		} ],
		vltype : [ {
			name : '字符',
			code : '0'
		}, {
			name : '数字',
			code : '1'
		} ],
		status : [ {
			name : '可用',
			code : '1'
		}, {
			name : '不可用',
			code : '0'
		} ],
		desktopType : [ {
			name : '系统桌面',
			code : '1'
		}, {
			name : '自定义桌面',
			code : '2'
		} ],
		reportState : [ {
			name : '驳回',
			code : '-1'
		}, {
			name : '草稿',
			code : '0'
		}, {
			name : '待审核',
			code : '1'
		}, {
			name : '审核通过',
			code : '2'
		} ],
		applyState : [ {
			name : '申请撤销',
			code : '-2'
		}, {
			name : '申请失败',
			code : '-1'
		}, {
			name : '申请中',
			code : '0'
		}, {
			name : '申请成功',
			code : '1'
		} ]
	},
	errorLoad : function(xhr, status, err) {
		if (xhr.status == 555) {
			top.location.href = xhr.responseText;
			return;
		} else {
			// alert(xhr.responseText);
		}
	},
	post : function(url, data, func, type) {
		type = type || 'application/x-www-form-urlencoded;charset=UTF-8';
		$.ajax({
			type : 'POST',
			url : url,
			cache : false,
			contentType : type,
			dataType : "json",
			ifModified : true,
			data : data,
			statusCode : {
				302 : function() {
					alert('page not found');
				}
			},
			error : Common.errorLoad,
			success : function(text, status, jqXHR) {
				if (func)
					func(text, status)
			}
		});
	},
	load : function(url, func, c) {
		if (typeof (c) == 'undefined')
			c = false;
		var xhr = $.ajax({
			type : "GET",
			url : url,
			cache : c,
			dataType : "text",
			ifModified : true,
			error : Common.errorLoad,
			success : func
		});
	},
	loadingHtml : '<div><img src="/app/images/ajax-loader.gif" />&nbsp;加载中...</div>',
	xhr : function(url, ele) {
		ele.empty();
		ele.append(this.loadingHtml);
		Common.load(url, function(d) {
			if (d) {
				ele.empty();
				ele.append(d);
			}
		}, false);
	},
	ajax : function(url, data, func, c) {
		var cfg = {
			type : "GET",
			url : url,
			cache : false,
			dataType : "json",
			ifModified : true,
			// ajaxStart : waitingQuery,
			data : data,
			statusCode : {
				302 : function() {
					alert('page not found');
				}
			},
			error : Common.errorLoad,
			success : function(text, status, jqXHR) {
				if (func)
					func(text, status)
			}
		};
		if (c)
			cfg.cache = true;
		var xhr = $.ajax(cfg);
		return xhr;
	},
	lockgx : {},
	_cb : {},
	batchLoad : function(s, fn) {
		var handleResp = function(d) {
			for ( var k in d) {
				Common.store[k] = d[k];
			}
			fn(d);
		}
		Common.ajax('/cmd/sys/source?_s=' + s, null, handleResp, true);
	},
	batchLoadx : function(s, fn) {
		var handleResp = function(d) {
			for ( var k in d) {
				Common.store[k] = d[k];
			}
			fn(d);
		}
		Common.ajax('/cmd/sys/source?_s=' + s, null, handleResp, true);
	},
	loadSource : function(s, fn) {
		var idx = s.indexOf('-');
		var gs = s;
		var g = '';
		if (idx != -1) {
			g = s.substring(idx + 1);
			s = s.substring(0, idx);
		}
		if (Common.store[s]) {
			if (fn)
				fn(Common.store[s]);
			return;
		}
		var vf = this._cb[s];
		if (vf == undefined) {
			vf = [];
			this._cb[s] = vf;
		}
		vf[vf.length] = fn;
		if (this.lockgx[s])
			return;
		var _sf = this;
		_sf.lockgx[s] = true;
		var handleResp = function(d) {
			Common.store[s] = d;
			var cb = _sf._cb[s];
			if (cb) {
				for ( var i = 0; i < cb.length; i++) {
					var fc = cb[i];
					if (fc)
						fc(d);
				}
				_sf._cb[s] = undefined;
			}
			_sf.lockgx[s] = false;
		}
		if (g == 'f')
			Common.ajax('/app/json/' + s + '.json', null, handleResp, true);
		else
			Common.ajax('/cmd/sys/source?_s=' + s, null, handleResp, true);

	},
	getDit : function(s) {
		if (Common.ditionary[s])
			return Common.ditionary[s];
		var d = Common.store[s];
		if (!d)
			return null;
		var sv = {};
		for ( var i = 0; i < d.length; i++) {
			var ld = d[i];
			var k = String(ld.code);
			sv[k] = ld.name;
		}
		Common.ditionary[s] = sv;
		return sv;
	},
	setting : function(el, url) {
		if (typeof (el) == 'string')
			el = $(el);
		var _self = this;
		Common.ajax(url, null, function(d) {
			el.append(_self.createTable(d));
		});
	},
	createTable : function(p) {
		var table = $('<table class="table table-striped table-bordered table-condensed table-hover"></table>');
		var _s = this;
		var fn = function(t, d) {
			for ( var k in d) {
				t.append(_s.createItem(k, d[k]));
			}
		};
		if (p.length) {
			for ( var i = 0; i < p.length; i++) {
				fn(table, p[i])
			}
		} else
			fn(table, p)
		return table;
	},
	createItem : function(k, v) {
		var row = $('<tr></tr>');
		var label = $('<td width="80" valign="top"><label>' + k
				+ '：</label></td>');
		var cell = $('<td></td>');
		if (!v)
			v = '&nbsp;';
		cell.append(v)
		row.append(label);
		row.append(cell);
		return row;
	},
	renderSelect : function(s, el, v, fn, cfg) {
		var xcb = function(d) {
			var sel = Common.renderOptions(el, d, v, fn, cfg);
			if (cfg && cfg.onSet) {
				if (sel.selectedIndex == -1)
					return;
				var form = cfg.__fm;
				var opt = sel.options[sel.selectedIndex];
				var iv = opt.text;
				var g = {};
				g[cfg.relField] = iv;
				if (form)
					form.setParameters(g);
				cfg.onSet(sel.options[sel.selectedIndex].value, iv, sel, opt);
			}
		};
		if (typeof (s) == 'string')
			Common.loadSource(s, xcb);
		else if (typeof (s) == 'undefined')
			return;
		else
			xcb(s);
	},
	renderOptions : function(id, data, selected, cb, cfg) {
		var sel = typeof (id) == 'string' ? document.getElementById(id) : id;
		var v = null;
		if (cfg) {
			if (typeof (cfg.defVal) != 'undefined' || cfg.search) {
				v = cfg.defVal || '';
				var op = new Option('请选择', '');
				if (cfg.search)
					op.selected = true;
				sel.options[sel.options.length] = op;
			}
		}
		if (data && data.length > 0) {
			for ( var i = 0; i < data.length; i++) {
				var d = data[i];
				var c, n;
				if (typeof (d) == 'string') {
					c = n = d;
				} else {
					c = d.code;
					n = d.name;
				}
				var op = new Option(n, c);
				if (selected && selected == c)
					op.selected = true;
				if (d.extra)
					$(op).attr('ex', d.extra);
				sel.options[sel.options.length] = op;
			}
		}
		if (cb) {
			sel.onchange = function(e) {
				cb(sel)
			};
		}
		return sel;
	},
	getBrowseSize : function() {
		var x, y;
		if (document.documentElement && document.documentElement.clientHeight) {
			w = document.documentElement.clientWidth;
			h = document.documentElement.clientHeight;
		} else if (document.body) {
			w = document.body.clientWidth;
			h = document.body.clientHeight;
		}
		return {
			w : w,
			h : h - 2
		}
	},
	imgLazyLoad : function(el) {
		if (typeof (el) == 'string')
			el = $(el);
		el.find('img').each(function(i, ele) {
			Common.lazyInit(el, $(ele));
		});
	},
	lazyInit : function(imgEl) {
		var ori = imgEl.attr('ori');
		if (ori) {
			var img = new Image();
			img.src = ori;
			$(img).bind('load', function(e) {
				imgEl.attr('src', ori);
			});
		}
	},
	createProcess : function(cfg) {
		cfg = cfg || {
			w : 100
		};
		var el;
		if (cfg.el)
			el = $(cfg.el);
		else {
			el = $('<div></div>');
			$(document.body).append(el);
		}
		var left = 100;
		var w = el.width() - 2;
		el[0].style.cssText = 'background:#fff;width:' + cfg.w + 'px;';
		var n = document.createElement('DIV');
		n.style.cssText = 'position:relative;left:0px;margin:0 0 0 0;height:20px;background:#e4e7eb;width:'
				+ w + 'px';
		var gn = document.createElement('DIV');
		gn.style.cssText = "background:#090;position:relative;height:20px;width:2%;";
		var span = document.createElement('SPAN');
		span.style.cssText = 'position:absolute;text-align:center;left:' + left
				+ 'px';
		n.appendChild(gn);
		gn.appendChild(span);
		el.append($(n));
		// -------
		var p = {
			process : function(p, m) {
				span.innerHTML = p + '%';
				gn.style.width = p + '%';
			}
		};
		return p;
	},
	addEvent : function(ele, type, handler) {
		if (ele.nodeType == 3 || ele.nodeType == 8)
			return;
		if (ele.setInterval && ele != window)
			ele = window;
		if (ele.addEventListener) {
			ele.addEventListener(type, handler, false);
		} else if (ele.attachEvent) {
			ele.attachEvent("on" + type, handler);
		} else {
			ele["on" + type] = handler;
		}
	},
	getPxNum : function(w) {
		if (typeof (w) == 'number')
			return w;
		var idx = w.indexOf('px');
		if (idx != -1)
			w = w.substring(0, idx);
		return parseInt(w);
	},
	fetch : function(msg, el, vars) {
		var args = [ msg ];
		if (vars) {
			if (typeof (vars) == 'array') {
				for ( var i = 0; i < vars.length; i++)
					args[args.length] = el.attr(vars[i]);
			} else
				args[args.length] = el.attr(vars);
		}
		return Common.replace.apply(this, args);
	},
	replace : function() {
		var args = arguments;
		var s;
		if (args && args.length) {
			s = args[0];
			if (s && args.length > 1) {
				for ( var i = 1; i < args.length; i++) {
					var n = i - 1;
					var by = '\\{' + n + '\\}';
					var v = args[i];
					s = s.replace(new RegExp(by, "gm"), v);
				}
			}
		}
		return s;
	},
	mask : function(el, msg) {
		var cf = true;
		if (el)
			cf = false;
		el = el || document.body;
		var offset = $(el).offset();
		var height = $(el).height();
		if (cf) {
			var winH = $(window).height();
			if (height < winH)
				height = winH;
		}
		if (typeof (msg) == 'undefined')
			msg = "加载中...";
		var width = $(el).width();
		var c_left = (width / 2) - 100;
		var c_top = height / 2 - 5;
		var _html = "<div id='loadingMask' style='position:absolute;cursor:wait;top:"
				+ offset.top
				+ "px;left:"
				+ offset.left
				+ "px;width:"
				+ width
				+ "px;height:"
				+ height
				+ "px;background:#E0ECFF;opacity:0.9;filter:alpha(opacity=90);'>"
				+ "<div style='position: relative; left:"
				+ c_left
				+ "px;top:"
				+ c_top
				+ "px;width:180px;height:36px;padding:12px 5px 10px 30px;"
				+ " background:#fff no-repeat scroll 5px 10px;border:2px solid #ccc;color:#000;'><img src=\"/app/images/ajax-loader.gif\" />&nbsp;"
				+ msg + "</div></div>";
		$(document.body).append(_html);
	},
	unMask : function() {
		window.setTimeout(function() {
			$('#loadingMask').remove();
		}, 100);
	}
};