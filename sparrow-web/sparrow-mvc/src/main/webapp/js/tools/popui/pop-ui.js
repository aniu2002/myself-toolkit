function ToolTip() {
}

ToolTip.createIfram = function(w, h) {
	var html = "<iframe src='javascript:false'";
	html += " style='position:absolute; visibility:inherit; left: 0px; top: 0px; width:"
			+ w + "px; height:" + h + "px;";
	html += "z-index: -1; filter:Alpha(Opacity=\"0\");'>";
	var iframe = document.createElement(html);
	iframe.style.border = "none";
	return iframe;
};

ToolTip.calculateOffset = function(field, attr) {
	var offset = 0;
	while (field) {
		offset += field[attr];
		field = field.offsetParent;
	}
	return offset;
};

ToolTip.setPosition = function(bandEle, layer) {
	var end = bandEle.offsetHeight;
	// var width = bandEle.offsetWidth;
	var left = ToolTip.calculateOffset(bandEle, "offsetLeft");
	var top = ToolTip.calculateOffset(bandEle, "offsetTop");
	// width=width+10;
	layer.style.left = left + 2 + "px";
	layer.style.top = top + end + "px";
	// layer.style.width = width + 2 + "px";
	// layer.className = "mainLayer";
};

ToolTip.prototype = {
	create : function(w, h) {
		this.mainLayer = document.createElement("DIV");
		// this.mainLayer.className = "mainLayer";
		// this.mainLayer.style.height =h + "px";
		this.isHide = true;
		this.mainLayer.style.border = '1px solid #6666CC';
		this.mainLayer.style.fontSize = '12px';
		this.mainLayer.style.textAlign = 'center';
		this.mainLayer.style.display = 'none';
		this.mainLayer.style.color = '#FF3300';
		// this.mainLayer.style.margin='0 2px 0 2px';
		this.mainLayer.style.padding = '0px 2px 2px 2px';
		// this.mainLayer.style.overflow='auto'; //overflowY
		this.mainLayer.style.backgroundColor = '#ffffff';
		this.mainLayer.style.position = 'absolute';
		// this.mainLayer.style.width = w + 2 + "px";
		this.mainLayer.style.zIndex = 10000;
		// layer.className = "mainLayer";
		document.body.appendChild(this.mainLayer);
	},
	show : function(ele, title) {
		if (!this.isHide)
			return;
		ToolTip.setPosition(ele, this.mainLayer);
		this.mainLayer.innerHTML = title;
		this.mainLayer.style.display = "block";
		var tp = this;
		window.setTimeout(function() {
			tp.hide();
		}, 3000);

	},
	hide : function() {
		this.mainLayer.style.display = "none";
		this.isHide = true;
	}
};

function ListBox() {
}

ListBox.menuFocusIndex = -1;
ListBox.arrylist = [];
ListBox.oldRow = null;
ListBox.resultlength = 0;
ListBox.mainLayerWidth = 0;
ListBox.bindText = null;
ListBox.bindIds = null;
ListBox.mainLayer = null;
ListBox.height = 150;
ListBox.plusWidth = 200;
ListBox.tbody = null;
ListBox.inputHolder = [];
ListBox.inputIDSHolder = [];
ListBox.dataProviderURL = "data/example.json";
ListBox.datas = []; // 记录当前加载了多少人
ListBox.singal = 0;
ListBox.jsond = null;
ListBox.count = 0;
ListBox.cbfunc = {};
ListBox.isDel = false;

/**
 * 获取 element 的绝对路径
 * 
 */
ListBox.getPosition = function(obj) {
	var top = 0, left = 0;
	do {
		top += obj.offsetTop;
		left += obj.offsetLeft;
	} while (obj = obj.offsetParent);
	var arr = new Array();
	arr[0] = top;
	arr[1] = left;
	return arr;
};

/**
 * 创建下拉菜单
 */
ListBox.createMenu = function() {
	ListBox.mainLayer = document.createElement("DIV");
	ListBox.mainLayer.className = "mainLayer";
	ListBox.mainLayer.style.height = ListBox.height + "px";
	ListBox.mainLayer.style.zIndex = 300;
	ListBox.setPosition();

	document.body.appendChild(ListBox.mainLayer);

	ListBox.mainLayer.onmouseover = function() {
		ListBox.singal = 1;
	};
	ListBox.mainLayer.onmouseout = function() {
		ListBox.singal = 0;
	};
};

/**
 * 获取浏览器的类型
 */
ListBox.isIE = function() {
	if (navigator.userAgent.indexOf("MSIE") > 0)
		return true;
	return false;
}
/**
 * 创建遮挡 select 的iframe
 */
ListBox.createIfram = function() {
	var w = ListBox.width + ListBox.plusWidth + 2;
	var h = ListBox.height + 2;
	var html = "<iframe src='javascript:false'";
	html += " style='position:absolute; visibility:inherit; left: 0px; top: 0px; width:"
			+ w + "px; height:" + h + "px;";
	html += "z-index: -1; filter:Alpha(Opacity=\"0\");'>";
	var iframe = document.createElement(html);
	iframe.style.border = "none";
	return iframe;
}

/**
 * 设置下拉菜单的精确位置
 */
ListBox.setPosition = function() {
	var end = ListBox.bindText.offsetHeight;
	var width = ListBox.bindText.offsetWidth;
	var left = ListBox.calculateOffset(ListBox.bindText, "offsetLeft");
	var top = ListBox.calculateOffset(ListBox.bindText, "offsetTop");

	width = width + ListBox.plusWidth;
	// end=end+5;
	ListBox.mainLayer.style.left = left + 2 + "px";
	ListBox.mainLayer.style.top = top + end + "px";
	ListBox.mainLayer.style.width = width + 2 + "px";
	ListBox.mainLayer.className = "mainLayer";
	ListBox.mainLayerWidth = width;
	// divouter.innerHTML = result;
	ListBox.mainLayer.style.display = "block";
};
/**
 * 计算位移
 */
ListBox.calculateOffset = function(field, attr) {
	var offset = 0;
	while (field) {
		offset += field[attr];
		field = field.offsetParent;
	}
	return offset;
};

// 根据返回数组生成div中行
ListBox.createMenuBody = function(resultlist) {
	var result = "";
	var j = 0;
	var arr = resultlist;
	var arrylist = arr;
	if (arr.length > 20) {
		j = 20;
	} else {
		j = arr.length;
	}

	if (ListBox.mainLayer == null) {
		ListBox.createMenu();
		ListBox.tbody = ListBox.createTbody(ListBox.mainLayer);
	} else {
		ListBox.setPosition();
	}

	ListBox.arrylist = [];
	ListBox.datas = [];
	if (j > 0) {
		for ( var i = 0; i < j; i++) {
			var row = ListBox.createRow(arr[i], i);
			ListBox.tbody.appendChild(row);
			var obj = {};
			obj.name = arr[i].name;
			obj.code = arr[i].code;
			;
			ListBox.datas[i] = obj;
		}
		if (ListBox.isIE()) {
			var w = ListBox.mainLayer.style.width;
			var h = ListBox.mainLayer.style.height;
			var frm = ListBox.createIfram(w, h);
			ListBox.mainLayer.appendChild(frm);
		}
		// createMenu(result);
	} else {
		var div = ListBox.mainLayer;
		if (div) {
			div.parentNode.removeChild(div);
			ListBox.mainLayer = null;
		}
	}
};

/**
 * 查找当前加载数据中是否已经存在 name 全匹配
 */
ListBox.findLoadedData = function(name) {
	for ( var i = 0; i < ListBox.datas.length; i++) {
		if (name == ListBox.datas[i].name)
			return ListBox.datas[i];
	}
	return null;
};
/**
 * ====创建tbody==
 */
ListBox.createTbody = function(mainLayer) {
	var tableCon = document.createElement("TABLE");
	var container = document.createElement("TBODY");

	container.className = "";

	tableCon.border = 0;
	tableCon.cellPadding = 0;
	tableCon.cellSpacing = 0;
	tableCon.align = "left";
	tableCon.width = ListBox.mainLayerWidth - 15;
	tableCon.appendChild(container);
	mainLayer.appendChild(tableCon);
	return container;
};
/**
 * 创建列表的一行
 * 
 */
ListBox.createRow = function(rowObjPara, ind) {
	var row = document.createElement("TR");
	var cell = ListBox.createCell(rowObjPara.code, 'codeCss');
	row.index = ind;
	row.onmouseover = function() {
		this.className = "overStyle";
		ListBox.menuFocusIndex = this.index;
		ListBox.oldRow = this;
	};
	row.onmouseout = function() {
		this.className = "defaultRowClass";
		ListBox.menuFocusIndex = -1;
	};
	row.onclick = function() {
		ListBox.givNumber(this.index);
		if (ListBox.selectedFunc) {
			ListBox.isDel = false;
			ListBox.selectedFunc(ListBox.bindText, ListBox.bindIds);
		}
	};

	row.obj = rowObjPara;
	cell.width = '100px';
	// cell.className='codeCss';
	row.appendChild(cell);
	cell = ListBox.createCell(rowObjPara.name, 'nameCss');
	cell.width = '100px';
	// cell.className='nameCss';
	row.appendChild(cell);
	row.height = 17;
	row.className = "defaultRowClass";
	ListBox.arrylist[ListBox.arrylist.length] = row;
	return row;
};
/**
 * 创建一行的一列记录
 * 
 */
ListBox.createCell = function(txt, cls) {
	var cell = document.createElement("TD");
	var dil = document.createElement("SPAN");
	var nodeText = document.createTextNode(txt);

	dil.className = cls;
	dil.appendChild(nodeText);

	cell.cssText = "margin:2px 2px 2px 2px";
	cell.className = "cellStyle";
	cell.appendChild(dil);
	return cell;
};

/**
 * 创建列表
 */
ListBox.clearMenuBody = function() {
	var tbd = ListBox.tbody;
	ListBox.menuFocusIndex = -1;
	if (tbd == null)
		return;
	while (tbd.childNodes.length > 0) {
		tbd.removeChild(tbd.childNodes[0]);
	}
};
/**
 * 打开弹出层
 * 
 */
ListBox.showLayer = function() {
	ListBox.clearMenuBody();
	if (ListBox.mainLayer != null)
		ListBox.mainLayer.style.display = "block";
};
/**
 * 关闭弹出层
 * 
 */
ListBox.hiddenLayer = function() {
	if (ListBox.mainLayer == null)
		return null;
	ListBox.mainLayer.style.display = "none";
	ListBox.arrylist.length = 0;
	ListBox.arrylist = [];
};
/**
 * 使用上下键的时候，使其选中条目颜色更改交替
 */
ListBox.forceMenuItem = function(index) { // div中颜色变化

	var row = ListBox.arrylist[index];

	if (ListBox.oldRow != null) {
		ListBox.oldRow.className = "defaultRowClass";
	}
	if (row != null) {
		row.className = "overStyle";
		ListBox.oldRow = row;
	}
	ListBox.menuFocusIndex = index;
};

/**
 * 通过回车键获取列表选中条目值
 */
ListBox.givNumber = function(index) { // 鼠标事件对应文本框赋值
	var row = ListBox.datas[index];
	if (row == null)
		return;

	ListBox.inputHolder[0] = row.code;
	ListBox.inputIDSHolder[0] = row.code;

	ListBox.hiddenLayer();
	var len = ListBox.setTextValue("");
	ListBox.cursorMove(len);
	ListBox.isSplitor = true;
};

/**
 * 当输入是完整匹配时的效果
 */
ListBox.givNumberEl = function(name, code) { // 鼠标事件对应文本框赋值
	var indx = ListBox.inputHolder.length;

	if (ListBox.checkExsistName(name)) {
		alert(name + " 重复选择！");
	} else {
		ListBox.inputHolder[indx] = name;
		ListBox.inputIDSHolder[indx] = code;
	}
	ListBox.hiddenLayer();
	var len = ListBox.setTextValue("");
	ListBox.cursorMove(len);
	ListBox.isSplitor = true;
};

/**
 * 检测该用户的名儿是否已经被选择
 */
ListBox.checkExsistName = function(name) {
	var arrs = ListBox.inputHolder;
	for ( var i = 0; i < arrs.length; i++) {
		if (arrs[i] == name) {
			return true;
		}
	}
	return false;
};

/**
 * 通过上下键获取列表值
 */
ListBox.givNumberbykey = function(index) { // 按键事件对应文本框赋值
	var row = ListBox.datas[index];
	if (row != null) {
		var len = ListBox.setTextValue(row.code);
	} else {
		ListBox.bindText.value = "";
	}
};
/**
 * 设置输入框的值
 * 
 */
ListBox.setTextValue = function(newSel) {
	var vals = ListBox.inputHolder;
	var str = "";
	if (ListBox.bindText == null)
		return;
	if (ListBox.inputHolder.length > 0)
		str = ListBox.inputHolder[0];
	if (newSel && newSel != "") {
		str = newSel;
	}
	ListBox.bindText.value = str;
	ListBox.setIDSvalue();
	return str.length;
};
/**
 * 捕获键盘事件,对 上、下键和退格键、回车键进行处理
 */
ListBox.catchKeyBoard = function(e) { // 按键事件
	e = (e) || window.event;
	var resultlength = ListBox.arrylist.length;
	var keyNumber = e.keyCode;

	if (keyNumber == "8") { // 退格处理
		return;
	}

	if (resultlength == 0)
		return;

	if (keyNumber == "40") { // 向下 
		if (ListBox.menuFocusIndex == -1) {
			ListBox.forceMenuItem(0); // 当焦点在文本框中间时，按向下跳到第一个主体。
			ListBox.givNumberbykey(0);
		} else if (ListBox.menuFocusIndex == resultlength - 1) {// 当焦点超出界限时跳转到第一个
			ListBox.forceMenuItem(0);
			ListBox.givNumberbykey(0);
		} else {
			ListBox.forceMenuItem(ListBox.menuFocusIndex + 1); // 焦点增加1
			ListBox.givNumberbykey(ListBox.menuFocusIndex);
		}
	} else if (keyNumber == "38") { // 向上
		if (ListBox.menuFocusIndex == -1)
			return;
		if (ListBox.menuFocusIndex == 0) {
			ListBox.forceMenuItem(resultlength - 1);// 焦点减少1
			ListBox.givNumberbykey(resultlength - 1); //  forceMenuItem(menuFocusIndex-1);
			// 当焦点在第一个主体时，按向上让它回到文本框。
		} else {
			ListBox.forceMenuItem(ListBox.menuFocusIndex - 1);
			ListBox.givNumberbykey(ListBox.menuFocusIndex);
		}
	} else if (keyNumber == "13") { // 回车
		if (ListBox.menuFocusIndex == -1)
			ListBox.givNumber(0);
		else
			ListBox.givNumber(ListBox.menuFocusIndex);
		if (ListBox.selectedFunc) {
			ListBox.isDel = false;
			ListBox.selectedFunc(ListBox.bindText, ListBox.bindIds);
		}
	}
};
/**
 * 将鼠标移到文本指定位置
 * 
 */
ListBox.cursorMove = function(pos) {
	if (ListBox.bindText == null)
		return;

	var ctrl = ListBox.bindText;

	if (ctrl.setSelectionRange) {
		ctrl.focus();
		ctrl.setSelectionRange(pos, pos);
	} else if (ctrl.createTextRange) {
		var range = ctrl.createTextRange();
		range.collapse(true);
		range.moveEnd('character', pos);
		range.moveStart('character', pos);
		range.select();
	}
};

/**
 * 获取鼠标所在的位置
 */
ListBox.getCursorPosition = function() {
	if (ListBox.bindText == null)
		return 0;

	var txt1 = ListBox.bindText;
	txt1.focus();
	var sel1;
	var pos;
	if (document.selection) {
		sel1 = document.selection.createRange().duplicate();
		var range = txt1.createTextRange();
		var sel2 = sel1.duplicate();
		sel2.setEndPoint("StartToStart", range);
		pos = sel2.text.length;
	} else if (txt1.selectionStart || txt1.selectionStart == '0')
		pos = txt1.selectionStart;
	return (pos);
};
/**
 * 根据鼠标的位置，找到所在选择的数组索引
 * 
 */
ListBox.findArrayIndex = function(pos) {
	var arrs = ListBox.inputHolder;
	var len = 0;

	pos = pos;

	for ( var i = 0; i < arrs.length; i++) {
		var la = arrs[i].length;
		len = len + la + 1;
		if (pos < len)
			return i;
	}

	return -1;
};
/**
 * 移除数组中指定位置的数组值
 * 
 */
ListBox.removeArray = function(pos) {
	var arrs = ListBox.inputHolder;
	var ids = ListBox.inputIDSHolder;

	if (pos >= arrs.length || pos < 0)
		return;

	for ( var i = pos; i < arrs.length - 1; i++) {
		arrs[i] = arrs[i + 1];
		ids[i] = ids[i + 1];
	}
	arrs[arrs.length - 1] = null;
	ids[arrs.length - 1] = null;
	arrs.length = arrs.length - 1;
	ids.length = ids.length - 1;
	ListBox.inputHolder = arrs;
	ListBox.inputIDSHolder = ids;
};

/**
 * 获取当前查询输入 text
 */
ListBox.getCurrentText_bak = function() {
	if (ListBox.bindText == null)
		return "";

	var val = ListBox.bindText.value;
	var arrs = val.split(",");

	if (arrs.length == 0)
		return "";

	return arrs[arrs.length - 1];
};

ListBox.getCurrentText = function() {
	if (ListBox.bindText == null)
		return "";
	var val = ListBox.bindText.value;
	return val;
};
// ajax方法调用
ListBox.showDataList = function(e) {
	e = (e) || window.event;
	var keyNumber = e.keyCode;
	var elementt = e.srcElement;

	// 用户输入符号 ','
	if (keyNumber == "188") {
		return;
	}

	// 用户按击 左右键
	if (keyNumber == "39" || keyNumber == "37") {
		var str = ListBox.bindText.value;
		var pos = ListBox.getCursorPosition();
		if (str.charAt(pos - 1) != ",")
			ListBox.isSplitor = false;
		return;
	}
	if (keyNumber == "40" || keyNumber == "38" || keyNumber == "13") {
		ListBox.catchKeyBoard(e);
	} else {
		if (keyNumber == "8" || keyNumber == "46") {
			if (!ListBox.isDel && ListBox.valueDelFunc) {
				ListBox.isDel = true;
			}
			ListBox.valueDelFunc(ListBox.bindText);
		}
		var url = "";
		var canshu = ListBox.getCurrentText();
		if (canshu != "") {
			if (ListBox.jsond) {
				ListBox.CreateUserInfo('t', ListBox.jsond, 'e')
			} else {
				ListBox.remoteRequest(ListBox.dataProviderURL,
						ListBox.CreateUserInfo, {
							uname : canshu,
							command : 'getProductCode'
						});
			}
		}
	}

	if (keyNumber == "8" && ListBox.isSplitor) {
		return;
	}

	if (keyNumber != "13")
		ListBox.isSplitor = false;
};
/**
 **/
ListBox.clickDown = function() {
	var str = ListBox.bindText.value;
	var pos = ListBox.getCursorPosition();
	if (str.charAt(pos - 1) != ",") {
		ListBox.isSplitor = false;
	}
};
/**
 * 当焦点聚集到输入框时，注册输入框和ids的绑定
 * 
 */
ListBox.clickBind = function(e, ids) {
	e = (e) || window.event;
	var keyNumber = e.keyCode;
	var elementt = e.srcElement || e.target;

	ListBox.bindText = elementt; //
	if (!ids) {
		ids = "";
	}
	// document.getElementById(ids)
	ListBox.bindIds = new Object();
	ListBox.bindIds.bindId = ids;

	var str = ListBox.bindText.value;

	if (str.charAt(str.length - 1) == ",") {
		str = str.substring(0, str.length - 1);
	}

	if (str == "") {
		ListBox.inputHolder = [];
		ListBox.inputIDSHolder = [];
		return;
	}
	ListBox.inputHolder = [ str ];
	// ListBox.inputHolder = str.split(",");
	// str = ListBox.bindIds.value;
	ListBox.inputIDSHolder = [ str ];
};
/**
 * 失去焦点的事件处理
 * 
 */
ListBox.unBlur = function() {
	if (ListBox.singal == 1)
		return;
	if (ListBox.bindText.value != "")
		ListBox.setTextValue(null);
	ListBox.hiddenLayer();
};
/**
 * 设置IDS的值
 * 
 */
ListBox.setIDSvalue = function() {
	var str = "";
	if (ListBox.inputIDSHolder.length > 0)
		str = ListBox.inputIDSHolder[0];

	ListBox.bindIds.value = str;
};

ListBox.CreateUserInfo = function(typ, jsond, evn) { // ajax方法数组接收
	var userinfos = jsond.data;
	if (userinfos != null && userinfos.length > 0) {
		ListBox.showLayer();
		ListBox.createMenuBody(userinfos);
	} else {
		ListBox.setTextValue("");
		ListBox.hiddenLayer();
		ListBox.datas = [];
	}
}

ListBox.serializeObject = function(obj) {
	if (obj == null)
		return "";
	if (typeof (obj) != 'object')
		return "";

	var tmp = "";
	for ( var name in obj) {
		tmp = tmp + "&" + escape(name) + "=" + obj[name];
	}
	if (tmp != "")
		tmp = tmp.substring(1);
	return tmp;
};
ListBox.remoteRequest = function(url, reqhandle, args) {
	var handle = reqhandle;
	var req = null;

	if (window.XMLHttpRequest) { // Non-IE browsers
		req = new XMLHttpRequest();
	} else if (window.ActiveXObject) { // IE
		req = new ActiveXObject("Microsoft.XMLHTTP");
	}

	if (req) {
		var callBack = function() {
			if (req.readyState == 4) { // Complete
				if (req.status == 200) { // OK response
					try {
						var jsond = eval("(" + req.responseText + ")");
					} catch (e) {
						alert("error");
					}
					handle("type", jsond, "event");
				} else {
					alert("Problem with server response:\n " + req.statusText);
				}
			}
		};
		req.onreadystatechange = callBack;
		req.open("POST", url, true);
		req.setRequestHeader('Content-Type',
				'application/x-www-form-urlencoded;charset=utf-8');
		var queryStr = ListBox.serializeObject(args);
		// alert(queryStr);
		req.send(queryStr);
	}
};
function PopDialog(wid, hei, lef, topp) {
	this.renderId = '';
	this.moveObject = null;
	this.eventSource = null;
	this.popDialog = null;

	this.mainLayer = null;
	this.titleLayer = null;
	this.backLayer = null;
	this.contentLayer = null;
	this.addedPane = null;

	this.title = null;
	this.titleText = "";
	this.minButton = null;
	this.maxButton = null;
	this.closeBtn = null;

	this.normal = 'slategray';
	this.hover = 'orange';
	this.offx = 7;
	this.offy = 10;
	this.moveable = false;
	this.z_index = 200;

	this.width = wid;
	this.height = hei;
	this.left = lef;
	this.top = topp;

	this.titleText = "";
	this.message = "";
	this.model = false;
	this.dragEnable = false;
	this.isHidden = true;
}
PopDialog.index = 100;
PopDialog.mainLayerCss = "mainLayerL";
PopDialog.titleLayerCss = "titleLayer";
PopDialog.btnCss = "minButton";
PopDialog.maxButtonCss = "minButton";
PopDialog.normalButtonCss = "minButton";
PopDialog.closeButtonCss = "width:12;border-width:0px;color:white;font-family:webdings;";
PopDialog.titleCss = "titleText";
PopDialog.contentCss = "contentLayer";
PopDialog.backLayerCss = "backLayer";
PopDialog.ie = navigator.userAgent.indexOf("MSIE") > 0;
PopDialog.cancelEvent = function(evt) {
	if (window.event) {
		evt.cancelBubble = true;
		evt.returnValue = false;
	} else {
		evt.preventDefault();
		evt.stopPropagation();
	}
};
PopDialog.prototype = {
	renderTo : function(id) {
		var contextEle = null;
		if (typeof (id) != "undefined") {
			if (typeof (id) == "string")
				contextEle = document.getElementById(id);
			else
				contextEle = id;
		}
		if (!contextEle)
			contextEle = document.getElementById('DIV');
		if (this.model)
			this.createBgLayer();
		var dialogHeight = this.height + 70;
		this.popDialog = document.createElement("DIV");
		this.popDialog.className = "popLogLayerL";
		this.popDialog.style.zIndex = this.z_index;
		this.popDialog.style.width = this.width + "px";
		this.popDialog.style.height = dialogHeight + "px";
		this.popDialog.style.left = this.left + "px";
		this.popDialog.style.top = this.top + "px";

		this.mainLayer = document.createElement("DIV");
		this.mainLayer.className = PopDialog.mainLayerCss;
		this.mainLayer.style.width = this.width + "px";
		this.mainLayer.style.height = dialogHeight + "px";
		this.mainLayer.style.left = this.left + "px";
		this.mainLayer.style.top = this.top + "px";

		this.titleLayer = document.createElement("DIV");
		this.titleLayer.className = PopDialog.titleLayerCss;

		this.contentLayer = document.createElement("DIV");
		this.contentLayer.className = PopDialog.contentCss;
		this.contentLayer.style.display = "block";
		this.contentLayer.style.height = this.height + "px";
		this.contentLayer.style.scroll = "auto";
		this.contentLayer.style.overflow = "auto";

		this.backLayer = document.createElement("DIV");
		this.backLayer.className = PopDialog.backLayerCss;
		this.backLayer.style.position = "absolute";
		this.backLayer.style.width = this.width + "px";
		this.backLayer.style.height = dialogHeight + "px";
		var atop = this.top + this.offy, aleft = this.left + this.offx;
		this.backLayer.style.top = atop + 'px';
		this.backLayer.style.left = aleft + 'px';
		this.backLayer.style.zIndex = this.z_index - 1;
		/**
		 * 设置标题值
		 */
		this.title = document.createElement("DIV");
		this.title.className = PopDialog.titleCss;
		this.title.style.width = (this.width - 43) + 'px';
		if (this.dragEnable) {
			PopDialog._mouseDownFunc = this.fireElEvent(this, this.startDrag,
					[]);
			PopDialog._mouseUpFunc = this.fireElEvent(this, this.stopDrag, []);
			PopDialog._mouseMoveFunc = this.fireElEvent(this, this.drag, []);

			GxTool.addEvent(this.title, 'mousedown', PopDialog._mouseDownFunc);
			GxTool.addEvent(this.title, 'mouseup', PopDialog._mouseUpFunc);
			// this.title.onmousedown = this.fireElEvent(this, this.startDrag,
			// []);
			// this.title.onmouseup = this.fireElEvent(this, this.stopDrag, []);
			// document.onmousemove = this.fireElEvent(this, this.drag, []);
		}

		this.closeBtn = document.createElement("SPAN");
		this.closeBtn.className = PopDialog.btnCss;
		this.closeBtn.onclick = this.fireElEvent(this, "hide", [ 'cancel' ]);
		var slf = this;
		this.closeBtn.onmouseover = function(evt) {
			slf.nbackC = this.style.backgroundColor;
			this.style.cursor = 'hand';
			// this.style.border = 'solid 1px red';
			this.style.backgroundColor = slf.hover;
		};
		this.closeBtn.onmouseout = function(evt) {
			this.style.cursor = 'default';
			this.style.backgroundColor = slf.nbackC;
			// this.style.border = '';
		};

		this.closeBtn.innerHTML = "&nbsp;X&nbsp;";
		/**
		 * 获取document的容器
		 */
		var body1 = document.body;
		this.titleLayer.appendChild(this.title);
		this.titleLayer.appendChild(this.closeBtn);
		this.mainLayer.appendChild(this.titleLayer);
		this.mainLayer.appendChild(this.contentLayer);

		this.bottom = document.createElement("DIV");
		this.bottom.style.cssText = 'height:30px;width:100%;text-align:center;float:left;';
		this.bottom.className = 'bottomLayer';
		var btn = document.createElement("INPUT");
		btn.setAttribute('type', 'button', 0);
		btn.setAttribute('value', '确定', 0);
		btn.onclick = this.fireElEvent(this, "hide", [ 'ok' ]);

		var cbtn = document.createElement("INPUT");
		cbtn.setAttribute('type', 'button', 0);
		cbtn.setAttribute('value', '取消', 0);
		cbtn.onclick = this.fireElEvent(this, "hide", [ 'cancel' ]);
		this.bottom.appendChild(btn);
		this.bottom.appendChild(document.createTextNode('　'));
		this.bottom.appendChild(cbtn);
		this.mainLayer.appendChild(this.bottom);

		if (contextEle != null) {
			contextEle.className = "contentEle";
			this.addedPane = contextEle;
			this.contentLayer.appendChild(contextEle);
		}
		this.popDialog.appendChild(this.mainLayer);
		body1.appendChild(this.popDialog);
		body1.appendChild(this.backLayer);
		this.title.innerHTML = this.titleText;
		if (this.isIE()) {
			var frm = this.createIfram(this.width + this.offx, dialogHeight);
			this.popDialog.appendChild(frm);
		}
		this.popDialog.style.display = "none";
		this.mainLayer.style.display = "none";
		this.backLayer.style.display = "none";
		// this.isHide = false;
	},
	destroy:function(){
		this.clearAllChilds(this.popDialog);
	},
	setTitle : function(ti) {
		this.title.innerHTML = ti;
		this.titleText = ti;
	},
	isIE : function() {
		if (navigator.userAgent.indexOf("MSIE") > 0)
			return true;
		return false;
	},
	createIfram : function(w, h) {
		h += this.offy;
		var html = "<iframe src='javascript:false'";
		html += " style='position:absolute; visibility:inherit; left: 0px; top: 0px; width:"
				+ w + "px; height:" + h + "px;";
		html += "z-index: -1; filter:Alpha(Opacity=\"0\");' oncontextmenu='return false;'>";
		var iframe = document.createElement(html);
		iframe.style.border = "none";
		return iframe;
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
	createBgLayer : function() {
		var bordercolor = "#336699";
		var titlecolor = "#99CCFF";
		var s = this.getBrowseSize();
		var sWidth = s.w;
		var sHeight = s.h;
		this.bgObj = document.createElement("div");
		this.bgObj.style.position = "absolute";
		this.bgObj.style.top = "0";
		this.bgObj.style.background = "#000000";
		this.bgObj.style.opacity = 0.3;
		this.bgObj.style.filter = "alpha(opacity=25)";
		this.bgObj.style.left = "0";
		this.bgObj.style.zIndex = this.z_index - 5;
		this.bgObj.oncontextmenu = function() {
			return false;
		};
		this.bgObj.style.width = sWidth + "px";
		this.bgObj.style.height = sHeight + "px";
		this.bgObj.style.display = 'none';
		this.bgObj.style.margin = "0 0 0 0";
		if (this.isIE()) {
			var frm = this.createIfram(sWidth, sHeight);
			this.bgObj.appendChild(frm);
		}
		document.body.appendChild(this.bgObj);
	},
	bgShow : function() {
		if (this.bgObj) {
			this.bgObj.style.display = 'block';
		}
	},
	bgHide : function() {
		if (this.bgObj) {
			this.bgObj.style.display = 'none';
		}
	},
	show : function(x, y) {
		if (this.popDialog == null)
			this.renderTo(this.renderId);
		if (this.model)
			this.bgShow();
		if (x) {
			this.popDialog.style.left = x + "px";
			this.backLayer.style.left = (x + this.offx) + "px";
		}
		if (y) {
			this.popDialog.style.top = y + "px";
			this.backLayer.style.top = (y + this.offy) + "px";
		}
		this.popDialog.style.display = "block";
		this.mainLayer.style.display = "block";
		this.backLayer.style.display = "block";
		this.normal = this.titleLayer.style.backgroundColor;
		if (this.addedPane != null) {
			this.addedPane.style.display = "block";
		}
		this.isHidden = false;
	},
	hook : function(evt) {
		if (window.event) {
			evt.cancelBubble = true;
			evt.returnValue = false;
		} else {
			evt.preventDefault();
			evt.stopPropagation();
		}
	},
	preHide:function(){
		return true;
	},
	hide : function(evt, f) {
		if (evt)
			this.hook(evt);
		if(!this.preHide()) 
			return;
		if (this.model)
			this.bgHide();
		this.popDialog.style.display = "none";
		this.mainLayer.style.display = "none";
		this.backLayer.style.display = "none";
		if (this.addedPane != null) {
			this.addedPane.style.display = "none";
		}
		if (f == 'ok' && this.callback)
			this.callback();
		this.isHidden = true;
	},
	clearAllChilds : function(element) {
		var obj = element;
		while (obj.childNodes.length > 0) {
			obj.removeChild(obj.childNodes[0]);
		}
	},
	startDrag : function(e) {
		PopDialog.cancelEvent(e);
		var b = e.button || e.which;
		if (b == 1) {
			if (PopDialog.ie)
				this.title.setCapture();
			else if (window.captureEvents)
				window.captureEvents(Event.MOUSEMOVE | Event.MOUSEUP);
			var win = this.popDialog;
			this.x0 = e.clientX || e.pageX;
			this.y0 = e.clientY || e.pageY;
			this.x1 = parseInt(win.style.left);
			this.y1 = parseInt(win.style.top);
			this.titleLayer.style.backgroundColor = this.hover;
			this.title.style.cursor = "move";
			this.mainLayer.style.borderColor = this.hover;
			this.moveable = true;
			GxTool.addEvent(document, 'mousemove', PopDialog._mouseMoveFunc);
		}
	},
	drag : function(e) {
		PopDialog.cancelEvent(e);
		if (this.moveable) {
			var win = this.popDialog;
			var sha = this.backLayer;
			var xc = e.clientX || e.pageX || 0, yc = e.clientY || e.pageY || 0;
			var left = this.x1 + xc - this.x0;
			var top = this.y1 + yc - this.y0;

			if (left < 0)
				left = 0;
			if (top < 0)
				top = 0;
			win.style.left = left + 'px';
			win.style.top = top + 'px';
			left += this.offx;
			top += this.offy;
			sha.style.left = left + 'px';
			sha.style.top = top + 'px';
		}
	},
	stopDrag : function(e) {
		PopDialog.cancelEvent(e);
		if (this.moveable) {
			var win = this.popDialog;
			var sha = this.backLayer;
			var msg = this.contentLayer;
			this.mainLayer.style.borderColor = this.normal;
			this.titleLayer.style.backgroundColor = this.normal;
			this.title.style.cursor = "default";
			if (PopDialog.ie)
				this.title.releaseCapture();
			else if (window.captureEvents)
				window.captureEvents(Event.MOUSEMOVE | Event.MOUSEUP);
			this.moveable = false;
			GxTool.removeEvent(document, 'mousemove', PopDialog._mouseMoveFunc);
		}
	},
	getFocus : function(obj) {
		if (obj.style.zIndex != PopDialog.index) {
			PopDialog.index = PopDialog.index + 2;
			var idx = PopDialog.index;
			obj.style.zIndex = idx;
			obj.nextSibling.style.zIndex = idx - 1;
		}
	},
	createFunction : function(obj, func, args) {
		if (!obj)
			obj = window;
		if (typeof (func) == "string") {
			func = obj[func];
		}
		return function() {
			func.apply(obj, args);
		}
	},
	fireElEvent : function(obj, func, args) {
		if (!obj)
			obj = window;
		if (args && args.length) {
			for ( var i = args.length; i > 0; i--)
				args[i] = args[i - 1];
		}
		var tim = (new Date()).getTime();
		return function(e) {
			e = (e) || window.event;
			if (typeof (func) == "string") {
				func = obj[func];
			}
			if (args)
				args[0] = e;
			var newTim = (new Date()).getTime();
			var interval = newTim - tim;
			if (interval > 20) {
				tim = newTim;
				func.apply(obj, args);
			}
		}
	}
}

var base64EncodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
var base64DecodeChars = new Array(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1,
		63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1,
		0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
		20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31,
		32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
		50, 51, -1, -1, -1, -1, -1);
function utf16to8(str) {
	var out, i, len, c;
	out = "";
	len = str.length;
	for (i = 0; i < len; i++) {
		c = str.charCodeAt(i);
		if ((c >= 0x0001) && (c <= 0x007F)) {
			out += str.charAt(i);
		} else if (c > 0x07FF) {
			out += String.fromCharCode(0xE0 | ((c >> 12) & 0x0F));
			out += String.fromCharCode(0x80 | ((c >> 6) & 0x3F));
			out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));
		} else {
			out += String.fromCharCode(0xC0 | ((c >> 6) & 0x1F));
			out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));
		}
	}
	return out;
}
function base64encode(strs) {
	var str = utf16to8(strs)
	var out, i, len;
	var c1, c2, c3;

	len = str.length;
	i = 0;
	out = "";
	while (i < len) {
		c1 = str.charCodeAt(i++) & 0xff;
		if (i == len) {
			out += base64EncodeChars.charAt(c1 >> 2);
			out += base64EncodeChars.charAt((c1 & 0x3) << 4);
			out += "==";
			break;
		}
		c2 = str.charCodeAt(i++);
		if (i == len) {
			out += base64EncodeChars.charAt(c1 >> 2);
			out += base64EncodeChars.charAt(((c1 & 0x3) << 4)
					| ((c2 & 0xF0) >> 4));
			out += base64EncodeChars.charAt((c2 & 0xF) << 2);
			out += "=";
			break;
		}
		c3 = str.charCodeAt(i++);
		out += base64EncodeChars.charAt(c1 >> 2);
		out += base64EncodeChars.charAt(((c1 & 0x3) << 4) | ((c2 & 0xF0) >> 4));
		out += base64EncodeChars.charAt(((c2 & 0xF) << 2) | ((c3 & 0xC0) >> 6));
		out += base64EncodeChars.charAt(c3 & 0x3F);
	}
	return out;
}

function base64decode(str) {
	var c1, c2, c3, c4;
	var i, len, out;
	len = str.length;
	i = 0;
	out = "";
	while (i < len) {
		/* c1 */
		do {
			c1 = base64DecodeChars[str.charCodeAt(i++) & 0xff];
		} while (i < len && c1 == -1);
		if (c1 == -1)
			break;
		/* c2 */
		do {
			c2 = base64DecodeChars[str.charCodeAt(i++) & 0xff];
		} while (i < len && c2 == -1);
		if (c2 == -1)
			break;
		out += String.fromCharCode((c1 << 2) | ((c2 & 0x30) >> 4));
		/* c3 */
		do {
			c3 = str.charCodeAt(i++) & 0xff;
			if (c3 == 61)
				return out;
			c3 = base64DecodeChars[c3];
		} while (i < len && c3 == -1);
		if (c3 == -1)
			break;

		out += String.fromCharCode(((c2 & 0XF) << 4) | ((c3 & 0x3C) >> 2));

		/* c4 */
		do {
			c4 = str.charCodeAt(i++) & 0xff;
			if (c4 == 61)
				return out;
			c4 = base64DecodeChars[c4];
		} while (i < len && c4 == -1);
		if (c4 == -1)
			break;
		out += String.fromCharCode(((c3 & 0x03) << 6) | c4);
	}
	return out;
}

var FuChart = {
	cxtPath : '/',
	charts : {},
	dialogs : {},
	DataUrl : null,
	ChartArgs : {},
	ColumnFilter : "id,siteId,statisTime,total",
	ColumnsMap : null,
	Columns : null,
	CurrentIds : '',
	count : 0,
	cbfunc : {},
	beforeRequest : undefined,
	refreshChart : function(id, paras, ignore) {
		var layer = id + "Layer";
		var ags = this.ChartArgs[layer];
		if (ags) {
			for ( var p in paras)
				ags[p] = paras[p];
			this.beforeShowChart(ags, ignore);
			FuChart.showChart(layer, ags, this.DataUrl);
		}
	},
	showChartDialog : function(opts) {
		if (typeof (opts) == 'string')
			return;
		var dialog = opts.id + "Dialog";
		var layer = opts.id + "Layer";
		var ags = this.ChartArgs[layer];
		// hold args
		opts.paras['ct'] = opts.type;
		if (typeof (ags) == 'undefined') {
			this.ChartArgs[layer] = ags = opts.paras;
		} else {
			for ( var p in opts.paras)
				ags[p] = opts.paras[p];
		}
		if (opts.beforeOpen)
			opts.beforeOpen();
		// first load
		if (!FuChart.isLoaded(dialog)) {
			// type 1 msLine 2 column 3 area
			this.beforeShowChart(ags, opts.ignore);
			FuChart.popChart({
				id : dialog,
				title : opts.title,
				cid : layer,
				cType : opts.type,
				left : opts.x ? opts.x : 150,
				top : opts.y ? opts.y : 60,
				dataUrl : opts.url,
				paras : ags
			});
			if (opts.init)
				opts.init(ags);
		} else {
			// refresh chart
			FuChart.showDialog(dialog);
			if (opts.reset) {
				opts.reset(ags);
			}
			this.beforeShowChart(ags, opts.ignore);
			FuChart.showChart(layer, ags, opts.url);
		}
	},
	beforeShowChart : function(ags, ignore) {
		if (ignore)
			return;
		if (this.beforeRequest)
			this.beforeRequest(ags);
	},
	getColumnsParas : function() {
		var columns = this.Columns;
		var str = '', lb = '';
		var fg = true;
		for ( var i = 0; i < columns.length; i++) {
			var d = columns[i];
			if (fg)
				fg = false;
			else {
				str += ',';
				lb += ",";
			}
			str += d.code
			lb += d.name
		}
		return {
			fields : str,
			labels : base64encode(lb)
		};
	},
	getColumns : function() {
		return this.Columns;
	},
	getAllColumns : function(da1) {
		var data = [];
		var columns = this.Columns;
		for ( var i = 0; i < columns.length; i++) {
			data[i] = columns[i];
		}
		for ( var i = 0; i < da1.length; i++) {
			data[data.length] = da1[i];
		}
		return data;
	},
	setColumnsDef : function(columns, filters) {
		var data = [];
		this.ColumnsMap = {};
		for ( var i = 0; i < columns.length; i++) {
			var d = columns[i];
			if (filters && filters.indexOf(d.dataIndex) != -1)
				continue;
			data[data.length] = {
				name : d.header,
				code : d.dataIndex
			}
			this.ColumnsMap[d.dataIndex] = d.header
		}
		this.Columns = data;
	},
	setIncludeColumns : function(columns, filters) {
		var data = [];
		this.ColumnsMap = {};
		for ( var i = 0; i < columns.length; i++) {
			var d = columns[i];
			if (filters && filters.indexOf(d.dataIndex) == -1)
				continue;
			data[data.length] = {
				name : d.header,
				code : d.dataIndex
			}
			this.ColumnsMap[d.dataIndex] = d.header
		}
		this.Columns = data;
	},
	getSitesLabel : function(fields) {
		if (this.SitesDic == null)
			return '';
		var tmp = this.SitesDic;
		var s = fields.split(',')
		var str;
		for ( var i = 0; i < s.length; i++) {
			if (i == 0)
				str = tmp[s[i]];
			else
				str += "," + tmp[s[i]];
		}
		return str;
	},
	getSitesData : function(fields) {
		if (this.SitesDic == null)
			return '';
		var tmp = this.SitesDic;
		var s = fields.split(',')
		var data = [];
		data[data.length] = {
			code : '',
			name : '--所有--'
		};
		for ( var i = 0; i < s.length; i++) {
			var k = s[i];
			data[data.length] = {
				code : k,
				name : tmp[k]
			};
		}
		return data;
	},
	getColumnLabels : function(fields) {
		if (this.ColumnsMap == null)
			return '';
		var tmp = this.ColumnsMap;
		var s = fields.split(',')
		var str;
		for ( var i = 0; i < s.length; i++) {
			if (i == 0)
				str = tmp[s[i]];
			else
				str += "," + tmp[s[i]];
		}
		return str;
	},
	renderList : function(id, group, jsond, cb) {
		var str = '';
		FuChart.count++;
		var rid = "Fn" + FuChart.count;
		FuChart.cbfunc[rid] = {
			fn : cb,
			group : group
		};
		for ( var i = 0; i < jsond.length; i++) {
			str += '<input type="checkbox" checked="true" name="' + group
					+ '" value ="' + jsond[i].code + '" >' + jsond[i].name
					+ ' &nbsp;&nbsp;';
		}
		str += "<input type=\"button\" value=\"确定\" onclick=\"FuChart.callFn('"
				+ rid + "')\">"
		document.getElementById(id).innerHTML = "指标:" + str;
	},
	getLatestYears : function(years) {
		var date = new Date(); // 日期对象
		var year = date.getFullYear();
		var data = [];
		var n = 0;
		for (; n < years; n++) {
			var s = year
			var o = {
				name : s,
				code : s
			};
			data[data.length] = o;
			year--
		}
		return data;
	},
	getLatestMonths : function(nums) {
		var date = new Date(); // 日期对象
		var year = date.getFullYear();
		var month = date.getMonth() + 1;
		var data = [];
		var n = 0;
		for (; n < nums; n++, month--) {
			if (month == 0) {
				year--;
				month = 12;
			}
			var s = year + '-' + (month > 9 ? month : '0' + month);
			var o = {
				name : s,
				code : s
			};
			data[data.length] = o;
		}
		return data;
	},
	renderOptions : function(id, data, selected, cb) {
		var sel = document.getElementById(id);
		for ( var i = 0; i < data.length; i++) {
			var op = new Option(data[i].name, data[i].code);
			if (selected && selected == data[i].code)
				op.selected = true;
			sel.options[sel.options.length] = op;
		}
		if (cb) {
			sel.onchange = function(e) {
				cb(sel)
			}
		}
		return sel;
	},
	setComValue : function(id, val) {
		var sel = document.getElementById(id);
		if (sel)
			sel.value = val;
	},
	clearAndRenderOption : function(id, data, selected) {
		var sel = document.getElementById(id);
		sel.options.length = 0;
		for ( var i = 0; i < data.length; i++) {
			var op = new Option(data[i].name, data[i].code);
			if (selected && selected == data[i].code)
				op.selected = true;
			sel.options[sel.options.length] = op;
		}
		return sel;
	},
	getCheckBoxValue : function(group) {
		var items = document.getElementsByName(group);
		var str = '';
		var fg = true
		for ( var i = 0; i < items.length; i++) {
			var imt = items[i]
			if (!imt.checked)
				continue;
			if (fg) {
				str = items[i].value;
				fg = false;
			} else
				str += "," + items[i].value;
		}
		return str;
	},
	callFn : function(rid) {
		var cb = FuChart.cbfunc[rid];
		if (cb) {
			var str = FuChart.getCheckBoxValue(cb.group);
			cb.fn(str)
		}
	},
	isLoaded : function(id) {
		if (this.dialogs[id])
			return true;
		return false;
	},
	initDialog : function(title, renderto, width, height) {
		if (this.dialogs[renderto])
			return;
		var w, h;
		if (typeof (width) == 'undefined')
			w = 950;
		else
			w = width;
		if (typeof (height) == 'undefined')
			h = 530;
		else
			h = height;
		var pov = new PopDialog(w, h, 20, 25);
		pov.titleText = title;
		pov.model = true;
		if (typeof (renderto) == 'undefined')
			pov.renderId = "form";
		else
			pov.renderId = renderto;
		this.dialogs[renderto] = pov;
	},
	hideDialog : function(id) {
		var dialog = this.dialogs[id]
		if (dialog)
			dialog.hide()
	},
	showDialog : function(id, x, y) {
		var dialog = this.dialogs[id]
		if (dialog.isHidden)
			dialog.show(x, y)
	},
	chartShow : function(args, urls) {
		var url = urls + args;
		if (window.chartObj) {
			chartObj.flashvars = "dataURL=" + url;
			return;
		}
		var swf = this.cxtPath + "/swf/Column2D.swf";
		var flashvars = {
			dataURL : url
		};
		var params = {
			flashvars : this.createFlashVars(flashvars),
			WMode : "transparent"
		};
		var attributes = {
			id : "chart1",
			data : swf,
			width : "800",
			height : "500"
		};
		chartObj = swfobject.createSWF(attributes, params, "chartLayer");
	},
	createFlashVars : function(flashvarsObj) {
		var flashvars;
		var isFst = true;
		for ( var k in flashvarsObj) {
			if (isFst) {
				flashvars = k + "=" + flashvarsObj[k];
				isFst = true;
			} else {
				flashvars += "&" + k + "=" + flashvarsObj[k];
			}
		}
		return flashvars;
	},
	gencodeUrlArgs : function(args) {
		if (args == undefined)
			return '';
		var isf = true;
		var str = "";
		for ( var v in args) {
			if (isf) {
				str = v + '=' + args[v];
				isf = false;
			} else
				str = str + '&' + v + '=' + args[v];
		}
		return encodeURIComponent(str);
	},
	showChart : function(id, args, url, swf) {
		var paras = this.gencodeUrlArgs(args);
		var chturl = url + "?" + paras;
		var chart = this.charts[id]
		if (chart) {
			// var chartObj = getChartFromId("ChartId");
			chart.initialDataSet = false;
			chart.setDataURL(chturl);
			chart.args = args;
			chart.urlset = url;
			chart.render(id);
			return;
		}
		var swf = this.cxtPath + "/swf/" + swf; // Area2D.swf
		chart = new FusionCharts(swf, "ChartId", "910", "450", "0", "0");
		chart.addParam("unescapeLinks", "0");
		chart.addParam("registerWithJS", "1");
		chart.setTransparent(true);
		chart.setDataURL(chturl);
		chart.render(id);
		chart.args = args;
		chart.urlset = url;
		this.charts[id] = chart;
	},
	showChartColumn : function(id, paras, dataUrl) {
		this.showChart(id, paras, dataUrl, "Column2D.swf");
	},
	showChartMsLine : function(id, paras, dataUrl) {
		this.showChart(id, paras, dataUrl, "MSLine.swf");
	},
	popChart : function(cfg) { // 1 line 2 column 3 area 4 circle
		if (typeof (cfg.cType) == 'undefined')
			cfg.cType = 1;
		this.initDialog(cfg.title, cfg.id)
		this.showDialog(cfg.id, cfg.left, cfg.top)
		switch (cfg.cType) {
		case 1:
			this.showChartMsLine(cfg.cid, cfg.paras, cfg.dataUrl);
			break;
		case 2:
			this.showChartColumn(cfg.cid, cfg.paras, cfg.dataUrl);
			break;
		case 3:
			this.showChartColumn(cfg.cid, cfg.paras, cfg.dataUrl);
			break;
		}
	},
	popFuChart : function(cfg) {
		this.initDialog(cfg.title, cfg.id)
		this.showDialog(cfg.id, cfg.left, cfg.top)
		this.showChartMsLine(cfg.cid, cfg.paras, cfg.dataUrl);
	},
	SitesDic : {
		"1" : "大众点评",
		"2" : "饭统网",
		"3" : "QQ美食",
		"4" : "百度",
		"5" : "悦乐",
		"9" : "京探网",
		"10" : "胡椒蓓蓓",
		"12" : "号码百事通",
		"15" : "55bbs",
		"22" : "中信银行",
		"23" : "交通银行",
		"26" : "订餐小秘书",
		"27" : "微博美食",
		"43" : "华夏银行",
		"45" : "光大银行",
		"46" : "宁波银行",
		"48" : "兴业银行",
		"49" : "平安银行",
		"50" : "浦发银行",
		"51" : "农业银行",
		"52" : "民生银行",
		"53" : "广发银行",
		"54" : "建设银行",
		"55" : "招商银行",
		"57" : "工商银行",
		"61" : "中国银行",
		"66" : "丁丁优惠",
		"75" : "杭州银行",
		"81" : "钱库网",
		"96" : "维络城",
		"97" : "东莞银行",
		"98" : "邮政储蓄",
		"109" : "爱乐活",
		"110" : "咕嘟妈咪",
		"112" : "12580",
		"114" : "开饭喇",
		"115" : "一点优惠",
		"116" : "上生活",
		"133" : "易淘食",
		"134" : "哗啦啦",
		"135" : "外卖库",
		"137" : "美餐网",
		"141" : "clubzone",
		"108" : "本地搜",
		"136" : "到家美食会",
		"140" : "豆瓣网",
		"142" : "百度地图",
		"139" : "大麦网",
		"143" : "北京ktv",
		"144" : "熊猫打折"
	},
	MonthDef : [ {
		name : '1',
		code : '01'
	}, {
		name : '2',
		code : '02'
	}, {
		name : '3',
		code : '03'
	}, {
		name : '4',
		code : '04'
	}, {
		name : '5',
		code : '05'
	}, {
		name : '6',
		code : '06'
	}, {
		name : '7',
		code : '07'
	}, {
		name : '8',
		code : '08'
	}, {
		name : '9',
		code : '09'
	}, {
		name : '10',
		code : '10'
	}, {
		name : '11',
		code : '11'
	}, {
		name : '12',
		code : '12'
	} ]
}

Date.prototype.format = function(mask) {
	var d = this;
	var zeroize = function(value, length) {
		if (!length)
			length = 2;
		value = String(value);
		for ( var i = 0, zeros = ''; i < (length - value.length); i++) {
			zeros += '0';
		}
		return zeros + value;
	};
	return mask
			.replace(
					/"[^"]*"|'[^']*'|\b(?:d{1,4}|m{1,4}|yy(?:yy)?|([hHMstT])\1?|[lLZ])\b/g,
					function($0) {
						switch ($0) {
						case 'd':
							return d.getDate();
						case 'dd':
							return zeroize(d.getDate());
						case 'ddd':
							return [ 'Sun', 'Mon', 'Tue', 'Wed', 'Thr', 'Fri',
									'Sat' ][d.getDay()];
						case 'dddd':
							return [ 'Sunday', 'Monday', 'Tuesday',
									'Wednesday', 'Thursday', 'Friday',
									'Saturday' ][d.getDay()];
						case 'M':
							return d.getMonth() + 1;
						case 'MM':
							return zeroize(d.getMonth() + 1);
						case 'MMM':
							return [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
									'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ][d
									.getMonth()];
						case 'MMMM':
							return [ 'January', 'February', 'March', 'April',
									'May', 'June', 'July', 'August',
									'September', 'October', 'November',
									'December' ][d.getMonth()];
						case 'yy':
							return String(d.getFullYear()).substr(2);
						case 'yyyy':
							return d.getFullYear();
						case 'h':
							return d.getHours() % 12 || 12;
						case 'hh':
							return zeroize(d.getHours() % 12 || 12);
						case 'H':
							return d.getHours();
						case 'HH':
							return zeroize(d.getHours());
						case 'm':
							return d.getMinutes();
						case 'mm':
							return zeroize(d.getMinutes());
						case 's':
							return d.getSeconds();
						case 'ss':
							return zeroize(d.getSeconds());
						case 'l':
							return zeroize(d.getMilliseconds(), 3);
						case 'L':
							var m = d.getMilliseconds();
							if (m > 99)
								m = Math.round(m / 10);
							return zeroize(m);
						case 'tt':
							return d.getHours() < 12 ? 'am' : 'pm';
						case 'TT':
							return d.getHours() < 12 ? 'AM' : 'PM';
						case 'Z':
							return d.toUTCString().match(/[A-Z]+$/);
						default:
							return $0.substr(1, $0.length - 2);
						}
					});
}