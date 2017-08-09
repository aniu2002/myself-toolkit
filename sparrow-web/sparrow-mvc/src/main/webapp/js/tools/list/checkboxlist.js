function CheckboxList() {
	this.bindSource = null;
	this.dataProvider = null;
	this.mainLayer = null;
	this.container = null;
	this.checkboxes = [];
	this.titleBar = null;
	// this.tableCon=null;
	this.lastClickRow = null;
	this.defaultClassName = "nocked";
	this.clickClassName = "clickStyle";
	this.overClassName = "overStyle";
	this.outClassName = "defaultRowClass";
	this.cellClassName = "cellStyle";
	this.mainLayerWidth = 0;
	this.hiddenFeild = null;
	this.singal = 0;
}

CheckboxList.mainLayerClassName = "mainLayer";
// CheckboxList.mainTableClassName="";
CheckboxList.containerClassName = "container";
/*******************************************************************************
 * this.dataProvider=[{title:'df'}] 必须字段
 * 
 * setDataProvider(); bind(); create();
 * 
 ******************************************************************************/
CheckboxList.calculateOffset = function(field, attr) {
	var offset = 0;
	while (field) {
		offset += field[attr];
		field = field.offsetParent;
	}
	return offset;
}

CheckboxList.createFunction = function(obj, strFunc, args) {
	if (!obj)
		obj = window;
	return function() {
		obj[strFunc].apply(obj, args);
	}
}

CheckboxList.prototype = {
    columns:1,
	init : function() {
		var body1 = document.getElementsByTagName("body")[0];
		this.mainLayer = document.createElement("DIV");
		this.mainLayer.className = CheckboxList.mainLayerClassName;

		this.titleBar = document.createElement("DIV");
		this.titleBar.className = "titlebar";
		//this.container = document.createElement("DIV");
		// this.tableCon.className=CheckboxList.mainTableClassName;
		//this.container.className = CheckboxList.containerClassName;

		this.mainLayer.appendChild(this.titleBar);
		var div= document.createElement("DIV") ;
		div.className = CheckboxList.containerClassName;
		var tbl=document.createElement("TABLE");
		tbl.border = 0;
        tbl.cellPadding = 0;
        tbl.cellSpacing = 0;
        tbl.style.cssText = 'font-size:12px;';
        tbl.align = "left";

        this.container=tbl;

        div.appendChild(tbl);

		this.mainLayer.appendChild(div);
		this.mainLayer.onmouseover = CheckboxList.createFunction(this,
				'setSingal', [ 1 ]);
		this.mainLayer.onmouseout = CheckboxList.createFunction(this,
				'setSingal', [ 0 ]);
		// this.mainLayer.className="mainLayer";
		body1.appendChild(this.mainLayer);

	},
	setTitle : function() {
		var span = document.createElement("SPAN");
		var txtnode = document.createTextNode("确定");
		var span1 = document.createElement("SPAN");
		var txtnode1 = document.createTextNode("取消");

		span.appendChild(txtnode);
		span.className = "spanbar";
		span.onclick = CheckboxList.createFunction(this, 'selectCheckbox',
				[ "id" ]); // 选取id列值赋给hiddenval需要提交的字段值
		span.onmouseover = function() {
			this.style.cursor = "hand";
			this.style.backgroundColor = "#000066";
			this.style.color = "#FFFFFF";
		}
		span.onmouseout = function() {
			this.style.cursor = "default";
			this.style.backgroundColor = "";
			this.style.color = "#000000";
		}
		this.titleBar.appendChild(span);

		span1.appendChild(txtnode1);
		span1.className = "spanbar1";
		span1.onclick = CheckboxList.createFunction(this, 'resetCheckboxes',
				[ "id" ]);
		span1.onmouseover = function() {
			this.style.cursor = "hand";
			this.style.backgroundColor = "#000066";
			this.style.color = "#FFFFFF";
		}
		span1.onmouseout = function() {
			this.style.cursor = "default";
			this.style.backgroundColor = "";
			this.style.color = "#000000";
		}
		this.titleBar.appendChild(span1);
	},
	resetCheckboxes : function(feild) { // 根据feild列名确定是否取checkbox.obj的feild值与hiddenval比较，如果匹配则选上这些接点
		var selects = this.checkboxes;
		var val = "";
		var str = "";
		var defaultval = [];

		if (this.hiddenFeild == null)
			return;
		val = this.hiddenFeild.value;
		defaultval = val.split(",");
		if (defaultval.length < 1)
			return;

		for ( var i = 0; i < this.checkboxes.length; i++) {
			var obj = this.checkboxes[i].obj;
			var flag = false;

			for ( var j = 0; j < defaultval.length; j++) {
				if (obj[feild] == defaultval[j]) {
					flag = true;
					str += "," + obj["title"];
					break;
				}
			}
			if (flag)
				this.checkboxes[i].checked = true;
			else
				this.checkboxes[i].checked = false;
		}
		if (this.bindSource != null)
			this.bindSource.value = str.substring(1);
		this.singal = 0;
		this.hideLayer();

	},
	selectCheckbox : function(feild) { // 点击确定后选择给hiddenval赋予选择的属性值，一般是id
		this.singal = 0;
		this.hideLayer();
		if (this.hiddenFeild != null) {
			this.hiddenFeild.value = this.selectValues(feild);
		}

	},
	setSingal : function(si) {
		// if(si==0) alert(si);
		this.singal = si;
	},
	setDataProvider : function(data) {
		this.dataProvider = data;
	},
	bind : function(sourceid, hiddenfeild) {
		if (typeof (sourceid) == "string") {
			this.bindSource = document.getElementById(sourceid);
			// return ;
		} else if (typeof (sourceid) == "object")
			this.bindSource = sourceid;
		else {
			alert("Bind source error!");
			return;
		}
		if (typeof (hiddenfeild) != "undefined") {
			this.hiddenFeild = document.getElementById(hiddenfeild);
		}
		// this.initMainLayerOffset(this.bindSource);

	},
	initMainLayerOffset : function(srcElement) {
		var end = srcElement.offsetHeight;
		var width = srcElement.offsetWidth;
		var left = CheckboxList.calculateOffset(srcElement, "offsetLeft");
		var top = CheckboxList.calculateOffset(srcElement, "offsetTop");
		this.bindSource.onclick = CheckboxList.createFunction(this,'showLayer', []);
		this.bindSource.onkeypress = CheckboxList.createFunction(this,
				'hideLayer', []);
		this.bindSource.onblur = CheckboxList.createFunction(this, 'hideLayer',
				[]);
		// this.menu_div.style.border="black 1px solid";
		if (this.mainLayer == null) {
			this.init();
		}

		this.mainLayer.style.display = "none";
		this.mainLayer.style.left = left + "px";
		this.mainLayer.style.top = top + end + "px";
		this.mainLayer.style.width = width + "px";
		this.mainLayerWidth = width;
		// this.container.style.width= this.mainLayerWidth-20;
	},
	initData : function(container) {
		if (this.dataProvider.length > 0) {
		    var cols=this.columns;
		    var data=this.dataProvider;
		    var j=0;
			for ( var i = 0; i < data.length; i++) {
				var row = this.createRow();
				for(j=0;j<cols&&i<data.length;j++)
				  row.appendChild(this.createItem(data[i++]));
				container.appendChild(row);
			}
		} else {
			return;
		}
	},
	createRow:function(){
	    var  row=document.createElement('TR');
	    row.setAttribute('height',30,0);
	    return row;
	},
	propertychange : function(row, cell) {
		if (cell.checked) {
			row.className = "cked";
		} else {
			row.className = "nocked";
		}
	},
	selectValues : function(feild) {
		var str = "";
		for ( var i = 0; i < this.checkboxes.length; i++) {
			if (this.checkboxes[i].checked) {
				var obj = this.checkboxes[i].obj;
				str += "," + obj[feild];
			}
		}
		return str.substring(1);
	},
	setSourceValue : function(feild) { // 点机checkbox时设置显示给客户看的text
		var value = this.selectValues(feild);
		this.bindSource.value = value;
	},
	createItem : function(rowObjPara) {
		var row = document.createElement("TD");
		var cell = document.createElement("INPUT");
		var txts = document.createTextNode(" " + rowObjPara.title);

		cell.type = "checkbox";
		cell.className = "checkbox";
		// row.className="nocked";
		// cell.onpropertychange=CheckboxList.createFunction(this,'propertychange',[row,cell]);
		cell.onclick = CheckboxList.createFunction(this, 'setSourceValue',
				[ "title" ]);
		cell.obj = rowObjPara;
		//cell.onmouseover=CheckboxList.createFunction(this,'onmouseover',[row]);
		//cell.onmouseout=CheckboxList.createFunction(this,'onmouseout',[row]);
		// cell.onclick=CheckboxList.createFunction(this,'onclick',[row,rowObjPara.title]);
		// row.oncdblick=CheckboxList.createFunction(this,'ondbclick',[row]);
		row.appendChild(cell);
		row.appendChild(txts);
		row.height = 15;
		row.className = this.defaultClassName;

		this.checkboxes[this.checkboxes.length] = cell;
		return row;
	},
	createCell : function(txt) {
		var cell = document.createElement("TD");
		var span = document.createElement("SPAN");
		var nodeText = document.createTextNode(txt);
		span.appendChild(nodeText);
		cell.className = this.cellClassName;
		cell.appendChild(span);
		return cell;
	},
	create : function() { 
		// for people apply
		this.checkboxes = [];
		this.init(); // 初始化各个控件 div table tbody
		this.setTitle();
		this.initData(this.container); // 在容器中装载数据 tr td textnode
		this.initMainLayerOffset(this.bindSource); // 根据bindsource设置div显示位置

	},
	onmouseover : function(row) {
		row.className = this.overClassName;
		// if(this.lastOverRow!=null)
		// this.lastOverRow=this.defaultClassName;
	},
	onmouseout : function(row) {
		row.className = this.outClassName;
	},
	onclick : function(row, value) {
		this.lastClickRow = row;
		this.singal = 0;
		this.bindSource.value = value;
		this.hideLayer();
	},
	ondbclick : function(row) {
	},
	showLayer : function() {
		if (this.mainLayer != null) {
			this.mainLayer.style.display = "block";
		}
	},
	hideLayer : function() {
		if (this.singal == 1)
			return;
		if (this.mainLayer != null)
			this.mainLayer.style.display = "none";
	},
	toString : function() {
		return " CheckboxList - > ";
	}
}