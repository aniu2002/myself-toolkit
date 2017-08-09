// JavaScript Document
function DemoTree(el) {
	this.defaultChildClass = null;
	this.domNode = (typeof (el) == 'string' ? document.getElementById(el) : el)
			|| document.createElement("DIV");
	this.containerNode = null;
	this.treeId = '';
	this.needNodeIco = false;
	this.needCheckbox = false;

	this.checkProfix = "DemoCheck";
	this.nodeTemplate = null;
	this.expandNodeTemplate = null;
	this.labelNodeTemplate = null;
	this.contentNodeTemplate = null;
	this.parent = null;
	this.children = [];
	this.listeners = [];
	this.selectedNode = null;
	this.isTree = true;
	this.defaultEventNames = {
		onexpand : null,
		oncollapse : null,
		onclick : null,
		oncontextmenu : "oncontextmenu",
		onselect : null
	};
}

// DemoTree.defaultEventNames={onexpand:null,oncollapse:null,onclick:null,oncontextmenu:null,onselect:null};

DemoTree.prototype = {
	createNode : function(data, parent) {
		data.tree = this.treeId;
		if (this.defaultChildClass.createTreeNode) {
			return this.defaultChildClass.createTreeNode(this, parent, data); // tree,parent,data
		}
	},
	makeNodeTemplate : function(isRoot) {
		var domNode = document.createElement("div");
		var expandNode = document.createElement("span");
		var labelNode = document.createElement("span");
		var contentNode = document.createElement("div");
		// 设置class
		this.nodeTemplate = domNode;
		domNode.className = "TreeNode TreeExpandLeaf";
		this.expandNodeTemplate = expandNode;
		expandNode.className = "TreeExpand TreeIEExpand";
		this.labelNodeTemplate = labelNode;
		labelNode.className = "TreeLabel";
		this.contentNodeTemplate = contentNode;
		contentNode.className = "TreeContent TreeIEContent";

		domNode.appendChild(expandNode);
		domNode.appendChild(contentNode);
		contentNode.appendChild(labelNode);
	},
	makeContainerNodeTemplate : function() {
		var div = document.createElement("div");
		div.style.display = "none";
		this.containerNodeTemplate = div;
	},
	initialize : function(args) {
		this.domNode.treeId = this.treeId;
		this.defaultChildClass = DemoTreeNodeUtil;
		this.makeNodeTemplate(true);
		this.makeContainerNodeTemplate();
		this.containerNode = this.domNode;
	},
	removeChildren : function() {
		while (this.containerNode.childNodes.length > 0) {
			this.containerNode.removeChild(this.containerNode.childNodes[0]);
		}
	},
	clear : function() {
		while (this.containerNode.childNodes.length > 0) {
			this.containerNode.removeChild(this.containerNode.childNodes[0]);
		}
		this.children = undefined;
	},
	hasChild : function() {
		return typeof (this.children) != 'undefined'
				&& this.children.length > 0;
	},
	setChildren : function(childrenArray) {
		var f = this.hasChild();
		if (f && childrenArray.length > 0)
			this.removeChildren();
		if (childrenArray)
			this.children = childrenArray;
		f = this.hasChild();
		if (!f)
			return;
		var lastIndex = this.children.length - 1;

		for ( var i = 0; i < this.children.length; i++) {
			var child = this.children[i];
			if (i == lastIndex)
				child.isLastChild = true;
			else
				child.isLastChild = false;
			child.isRoot = true;
			if (typeof (child) == "object") {
				child = this.children[i] = this.createNode(child, this);
			}

			if (!child.parent) {
				child.parent = this;
				// child.viewAddLayout();
			}
			this.containerNode.appendChild(child.domNode); //
		}
	},
	listenTree : function(tree) {
		if (typeof (tree) != "object")
			return;
	},
	registryEventHandle : function(eventName, handleObj) // {listener,handlemethod}
	{
		if (handleObj.listener == null)
			handleObj.listener = this;
		handleObj.handle = null;

		this.defaultEventNames[eventName] = handleObj;
	},
	addListener : function(listener) {
		if (listener.init)
			listener.init();

		listener.tree = this;

		this.listeners[this.listeners.length] = listener;
	},
	toString : function() {
		return 'DemoTree--' + this.treeId;
	}
}

function DemoTreeNodeUtil() {
}

DemoTreeNodeUtil.createTreeNode = function(tree, pare, para) {
	var treeNode = new DemoTreeNode();
	for ( var ar in para) {
		treeNode[ar] = para[ar];
	}

	treeNode.tree = tree;
	treeNode.parent = pare;
	treeNode.title = para.title;

	if (para.folder)
		para.nodeId = "p_" + para.nodeId;

	if (typeof (para.nodeId) != "undefined") {
		treeNode.nodeId = para.nodeId;
	} else if (typeof (para.cid) != "undefined") {
		treeNode.nodeId = para.cid;
	} else if (typeof (para.serialid) != "undefined") {
		treeNode.nodeId = para.serialid; // 应是nodeId
	}

	if (typeof (para.pid) != "undefined") {
		treeNode.pid = para.pid;
	}

	treeNode.isLastChild = para.isLastChild;

	if (typeof (para.folder) == "undefined")
		treeNode.isFolder = false;
	else
		treeNode.isFolder = para.folder;
	if (para.children && para.children.length)
		treeNode.children = para.children;

	if (treeNode.children && treeNode.children.length > 0) {
		treeNode.isFolder = true;
	}
	if (treeNode.isRoot)
		treeNode.initTemplate(true);
	else
		treeNode.initTemplate();
	treeNode.initContainer();

	return treeNode;
}

DemoTreeNodeUtil.createFunction = function(obj, strFunc, args) {
	if (!obj)
		obj = window;

	return function() {
		obj[strFunc].apply(obj, args);
	}
}
DemoTreeNodeUtil.removeArray = function(arry, index) {
	if (arry.length) {
		var len = arry.length;
		if (arry.length < index + 1)
			return;

		for ( var i = index; i < arry.length - 1; i++) {
			arry[i] = arry[i + 1];
			arry[i].index = i;
		}

		arry[len - 1] = null;
		arry.length = len - 1;
	}
}

DemoTreeNodeUtil.doHandleCheckbox = function(node) {
	var state = node.checkNode.checked;
	DemoTreeNodeUtil.doSelectedSubs(node, state);
	DemoTreeNodeUtil.doSelectedParent(node.parent);

	if (DemoTreeNodeUtil.afterCheckHandle) {
		DemoTreeNodeUtil.checkedNodes = [];
		DemoTreeNodeUtil.selectCheckedNodes(node.tree);
		DemoTreeNodeUtil.afterCheckHandle(DemoTreeNodeUtil.checkedNodes);
	}
}

DemoTreeNodeUtil.doSelectedSubs = function(node, state) {
	if (node.children == null)
		return;
	if (typeof (node.children.length) == "undefined")
		return;
	for ( var i = 0; i < node.children.length; i++) {
		var treeNode = node.children[i];
		if (treeNode.checkNode != null) {
			treeNode.checkNode.checked = state;
			DemoTreeNodeUtil.doSelectedSubs(treeNode, state);
		}
	}
}

DemoTreeNodeUtil.doSelectedParent = function(node) {
	if (node == null)
		return;
	if (node.isTree)
		return;
	if (node.children == null)
		return;

	if (DemoTreeNodeUtil.searchBrother(node.children)) {
		if (node.checkNode != null)
			node.checkNode.checked = true;
	} else {
		if (node.checkNode != null)
			node.checkNode.checked = false;
	}
	if (node.parent == null)
		return;
	else
		DemoTreeNodeUtil.doSelectedParent(node.parent);
}

DemoTreeNodeUtil.searchBrother = function(children) {
	if (children == null)
		return false;
	if (typeof (children.length) == "undefined")
		return false;

	for ( var i = 0; i < children.length; i++) {
		var node = children[i];
		if (node.checkNode != null) {
			if (node.checkNode.checked == false)
				return false;
		}
	}

	return true;
}

DemoTreeNodeUtil.getCheckedNodes = function(node) {
	var str = "";
	if (node == null)
		return null;
	if (node.children == null)
		return null;

	for ( var i = 0; i < node.children.length; i++) {
		var tnod = node.children[i];
		if (tnod.children == null || tnod.children.length < 1) {
			if (tnod.checkNode != null && tnod.checkNode.checked == true) {
				str += "," + tnod.nodeId;
			}
		} else {
			/*
			 * if(tnod.checkNode!=null&&tnod.checkNode.checked==true){
			 * str+=","+tnod.nodeId; }
			 */

			var temp = DemoTreeNodeUtil.getCheckedNodes(tnod);
			if (temp != null && temp != "") {
				str += "," + temp;
			}
		}
	}

	if (str != "") {
		str = str.substring(1);
	}

	return str;
};
/**
 * 查询树的某个接点 返回treeNode对象
 */
DemoTreeNodeUtil.searchNodeById = function(node, nid) {
	if (node == null)
		return null;
	if (node.children == null)
		return null;
	// alert(node.children.length);
	for ( var i = 0; i < node.children.length; i++) {
		var tnod = node.children[i];

		var neid = "" + tnod.nodeId;
		if (neid == nid) {
			return tnod;
		} else if (tnod.isFolder && tnod.children != null
				&& tnod.children.length > 0) {
			var nod = DemoTreeNodeUtil.searchNodeById(tnod, nid);
			if (nod != null)
				return nod;
		}
	}

	return null;
};

function DemoTreeNode() {
	this.nodeId = "";
	this.srcTreeId = "";
	this.domNode = null;
	this.expandNode = null;
	this.contentNode = null;
	this.labelNode = null;
	this.parent = null;
	this.title = null;
	this.tree = null;
	this.children = [];
	this.isFolder = false;
	this.isTreeNode = true;
	this.state = false;
	this.loadStates = {
		LOADED : true,
		UNLOADED : false
	};
	this.containerNode = null;
	this.isLastChild = false;
	this.isExpanded = false;
	this.isLoaded = false;
	this.domNodeClassName = "TreeNode";
}

DemoTreeNode.prototype = {
	initTemplate : function(isRoot) {
		this.domNode = this.tree.nodeTemplate.cloneNode(true);
		this.expandNode = this.domNode.firstChild;
		this.contentNode = this.domNode.childNodes[1];
		this.labelNode = this.contentNode.firstChild;

		var isRt = (typeof (isRoot) == "undefined") ? false : true;

		if (this.nodeId == null || this.nodeId == "")
			this.domNode.nodeId = this.domNode.uniqueID;

		this.domNode.nodeId = this.nodeId;
		this.labelNode.innerHTML = this.title;

		var clasName = "TreeNode";

		if (isRt)
			clasName = "TreeIsRoot";

		if (this.isFolder) {
			if (this.isExpanded)
				clasName += " TreeExpandOpen";
			else
				clasName += " TreeExpandClosed";
		} else
			clasName += " TreeExpandLeaf";

		// this.labelNode.onmousedown = DemoTreeNodeUtil.createFunction(this,
		// "registryListener", [ "draglistener", "onmousedown", this ]);
		// this.contentNode.onmouseover = DemoTreeNodeUtil.createFunction(this,
		// "registryListener", [ "draglistener", "onmouseover", this ]);
		// this.contentNode.onmouseout = DemoTreeNodeUtil.createFunction(this,
		// "registryListener", [ "draglistener", "onmouseout", this ]);
		// this.labelNode.onmouseup=DemoTreeNodeUtil.createFunction(this,"registryListener",["draglistener","onmouseup",this]);

		this.expandNode.onclick = DemoTreeNodeUtil.createFunction(this,
				"processExpandClick", [ this ]);
		// this.labelNode.oncontextmenu = DemoTreeNodeUtil.createFunction(this,
		// "processLabelNodeEvent", [ "oncontextmenu", this ]);
		// this.labelNode.onfocus = DemoTreeNodeUtil.createFunction(this,
		// "processLabelNodeEvent", [ "onlabelfocus", this ]);
		// this.labelNode.onclick=DemoTreeNodeUtil.createFunction(this,"processLabelSelected",[this]);
		// //"onlabelclick",
		this.labelNode.onclick = DemoTreeNodeUtil.createFunction(this,
				"processLabelNodeEvent", [ "onlabelclick", this ]);

		if (this.isLastChild)
			clasName += " TreeIsLast";
		this.domNodeClassName = clasName;
		this.domNode.className = clasName;
		this.domNode.descname = "container";
		// this.domNode.style.zIndex = 5;
		if (!this.isFolder) {
			Common.addEvent(this.labelNode, 'mouseover', this.mouseOver);
			Common.addEvent(this.labelNode, 'mouseout', this.mouseOut);
		}
		this.createIconNode(this);
	},
	mouseOver : function() {
		//$(this).css({cursor:'pointer'});
	    this.style.cssText = "cursor:pointer;";
		//this.style.cursor = 'hand';
	},
	mouseOut : function() {
		this.style.cssText = "cursor:default;";
		//$(this).css({cursor:'default'});
	},
	createIconNode : function(node) {
		var needIco = node.tree.needNodeIco;
		var needCheck = node.tree.needCheckbox;
		var prefix = "Tree";

		if (needIco) {
			node.contentIconNode = document.createElement("div");
			var clazz = prefix + "IconContent";
			var ftype = "";

			if (node.isFolder)
				ftype = "Folder";
			else if(node.icon)
				ftype = "Folderx";
			else
				ftype = "Document";

			//if (node.icon)
			//	ftype = "v";

			node.contentIconNode.className = clazz;
			node.contentNode.parentNode.replaceChild(node.contentIconNode,
					node.expandNode);
			node.iconNode = document.createElement("div");

			var iconclass = prefix + "Icon" + " " + prefix + "Icon" + ftype;

			node.iconNode.className = iconclass;

			node.contentIconNode.appendChild(node.expandNode);
			node.contentIconNode.appendChild(node.iconNode);
		}

		if (needCheck) {
			var tex = document.createTextNode(node.title);

			node.checkNode = document.createElement("input");
			// node.checkNode.srcNode=node;
			node.checkNode.setAttribute("type", "checkbox");
			var lid = this.tree.checkProfix + node.nodeId;
			node.checkNode.setAttribute("id", lid, 1);
			node.checkNode.onclick = DemoTreeNodeUtil.createFunction(
					DemoTreeNodeUtil, 'doHandleCheckbox', [ node ]);
			// node.checkNode.setAttribute("checked",true,1);

			node.labelNode.innerHTML = "";
			node.labelNode.appendChild(node.checkNode);
			node.labelNode.appendChild(tex);
			// node.checkNode.checked=true;
		}
	},
	updateIconNodeView : function(node) {
		var prefix = "Tree";
		var clazz = prefix + "IconContent";
		var ftype = "";
		if (node.isFolder)
			ftype = "Folder";
		else
			ftype = "Document";

		if (node.contentIconNode)
			node.contentIconNode.className = clazz;

		var iconclass = prefix + "Icon" + " " + prefix + "Icon" + ftype;

		if (node.iconNode)
			node.iconNode.className = iconclass;
	},
	initContainer : function() {
		this.containerNode = this.tree.containerNodeTemplate.cloneNode(true);
		this.domNode.appendChild(this.containerNode);
	},
	setFolder : function() {
		this.isFolder = true;
	},
	removeChildren : function() {
		while (this.containerNode.childNodes.length > 0) {
			this.containerNode.removeChild(this.containerNode.childNodes[0]);
		}
		this.children = [];
	},
	clear : function() {
		while (this.containerNode.childNodes.length > 0) {
			this.containerNode.removeChild(this.containerNode.childNodes[0]);
		}
		this.children = [];
	},
	showChildren : function() {
		if (this.state == false)
			this.updateView();
		this.domNodeClassName = this.domNodeClassName.replace(
				"TreeExpandClosed", "TreeExpandOpen");
		this.domNode.className = this.domNodeClassName;
		this.containerNode.style.display = "block";
		this.isExpanded = true;
	},
	collapse : function() {
		this.domNodeClassName = this.domNodeClassName.replace("TreeExpandOpen",
				"TreeExpandClosed");
		this.domNode.className = this.domNodeClassName;
		this.containerNode.style.display = "none";
		this.isExpanded = false;
	},
	expand : function() {
		this.showChildren();
	},
	hidden : function() {
		this.collapse();
	},
	updateNodeView : function(treeNode) {
		var clasName = "TreeNode";

		if (treeNode == null)
			return;

		if (treeNode.isFolder) {
			if (treeNode.isExpanded)
				clasName += " TreeExpandOpen";
			else
				clasName += " TreeExpandClosed";
		} else
			clasName += " TreeExpandLeaf";

		if (treeNode.isLastChild)
			clasName += " TreeIsLast";

		treeNode.domNodeClassName = clasName;
		treeNode.domNode.className = clasName;
		this.updateIconNodeView(treeNode);
	},
	hasChild : function() {
		return this.children && this.children.length > 0;
	},
	updateView : function() {
		if (!this.hasChild())
			return;
		var lastIndex = this.children.length - 1;
		for ( var i = 0; i < this.children.length; i++) {
			var child = this.children[i];

			if (child.parent && child.parent == this) {
				if (child.isLastChild && i < lastIndex) {
					child.isLastChild = false;
					this.updateNodeView(child);
				}

				if (child.isLastChild == false && i == lastIndex) {
					child.isLastChild = true;
					this.updateNodeView(child);
				}
				continue;
			}

			if (i == lastIndex)
				child.isLastChild = true;
			else
				child.isLastChild = false;

			if (typeof (child) == "object") {
				child = this.children[i] = this.tree.createNode(child, this);
			}

			if (!child.parent) {
				child.parent = this;
			}
			this.children[i].index = i;
			this.containerNode.appendChild(child.domNode); //
			// 添加了后才能设置checkbox的长度
			if (child.parent != null && child.parent.checkNode != null) {
				child.checkNode.checked = child.parent.checkNode.checked;
			}
		}

		this.state = true;
	},
	setChildren : function(childrenArray) {
		if (this.isTreeNode && !this.isFolder) {
			this.setFolder();
		} else {
			if (this.isTreeNode)
				this.state = this.loadStates.LOADED;
		}

		var hadChildren = this.children && this.children.length > 0;

		if (hadChildren && childrenArray) {
			this.removeChildren();
		}
		if (childrenArray) {
			this.children = childrenArray;
		}
		var hasChildren = this.children && this.children.length > 0;
		if (!hasChildren)
			return;
		var lastIndex = this.children.length - 1;

		for ( var i = 0; i < this.children.length; i++) {
			var child = this.children[i];

			if (i == lastIndex)
				child.isLastChild = true;
			else
				child.isLashChild = false;

			if (typeof (child) == "object") {
				child = this.children[i] = this.tree.createNode(child, this);
			}

			this.children[i].index = i;

			if (!child.parent) {
				child.parent = this;
			}
			this.containerNode.appendChild(child.domNode); //
		}

		this.state = true;
	},
	addChild : function(data) // var data={title:""};
	{
		var child = this.tree.createNode(data, this);

		if (data.title == null || data.title == "")
			return;
		if (this.isFolder == false) {
			this.domNodeClassName = "TreeNode TreeExpandClosed";

			if (this.isLastChild)
				this.domNodeClassName += " TreeIsLast";

			this.domNode.className = this.domNodeClassName;
			this.isFolder = true;
		}

		this.updateIconNodeView(this);
		this.state = false;
		this.children[this.children.length] = data;
		this.showChildren();
	},
	removeChild : function(index) {
		if (index >= this.children.length || index < 0)
			return;

		this.containerNode.removeChild(this.children[index].domNode);
		DemoTreeNodeUtil.removeArray(this.children, index);
		if (this.children.length == 0) {
			this.isFolder = false;
			this.updateNodeView(this);
		} else {
			var snode = this.children[this.children.length - 1];
			snode.isLastChild = true;
			this.updateNodeView(snode);
		}
	},
	processLabelSelected : function(treeNode) {

		if (this.tree.selectedNode)
			this.tree.selectedNode.labelNode.className = "TreeLabelDefault";

		treeNode.labelNode.className = "TreeLabelFocused";
		this.tree.selectedNode = treeNode;
	},
	processLabelNodeEvent : function(eventName, treeNode) {
		this.bindListener(eventName, treeNode);
	},
	processExpandClick : function(treeNode) {
		if (treeNode.isFolder == false)
			return;

		var eventName = "onexpand";

		if (treeNode.isExpanded)
			eventName = "oncollapse";
		this.bindListener(eventName, treeNode);
	},
	registryListener : function(listentype, eventName, node) {
		this.bindListener(eventName, node, listentype);
	},
	bindListener : function(eventName, node, listentype) {
		var treeListener = this.tree.listeners;
		var listenType = null;

		if (typeof (listentype) != "undefined") {
			listenType = listentype;
		}

		if (listenType != null && listenType != "undefined") {
			for ( var i = 0; i < treeListener.length; i++) {
				if (treeListener[i].type == listenType) {
					treeListener[i].fireEvent(eventName, node);
				}
			}

			return;
		}
		for ( var i = 0; i < treeListener.length; i++) {
			treeListener[i].fireEvent(eventName, node);
		}

	},
	toString : function() {
		return "[tree_node]--" + this.title;
	}
};
function BaseController() {
	this.tree = null;
	this.type = "actionlistener";
}

BaseController.defaultEventHandle = {
	onexpand : "expand",
	oncollapse : "collapse",
	onlabelfocus : "labelFocus",
	onlabelclick : "labelClick",
	onmousedown : "onmousedown",
	onmouseover : "onmouseover",
	onmouseout : "onmouseout"
};
// ,oncontextmenu:'oncontextmenu'
BaseController.prototype = {
	fireEvent : function(eventName, treeNode) {
		var funname = BaseController.defaultEventHandle[eventName];
		if (!funname)
			return;

		if (this[funname])
			this[funname](eventName, treeNode);
	},
	expand : function(eventName, treeNode) {
		treeNode.expand();
	},
	collapse : function(eventName, treeNode) {
		treeNode.collapse();
	},
	oncontextmenu : function(eventName, treeNode) {
		this.selectNode(treeNode);
	},
	labelFocus : function(eventName, treeNode) {
		treeNode.labelNode.className = 'TreeLabelFocused';
	},
	labelClick : function(eventName, treeNode) {
		if (this.tree.needCheckbox)
			return;
		else
			this.selectNode(treeNode); // alert('ok');
		DemoTreeNodeUtil.handleLabelClick(treeNode);
		// alert(treeNode.nodeId);
		// treeNode.labelNode.className='TreeLabelFocused';
	},
	expandAll : function(node) {
		this._expandAll(node);
	},
	_expandAll : function(node) {
		if (node.children == null)
			return;
		if (typeof (node.children.length) == "undefined")
			return;

		for ( var i = 0; i < node.children.length; i++) {
			var treeNode = node.children[i];

			if (treeNode.isTreeNode) {
				treeNode.expand();
				this._expandAll(treeNode);
			}
		}
	},
	expandToLeval : function(node, deep) {
		this._expandToLeval(node, 1, deep);
	},
	_expandToLeval : function(node, leval, final) {
		if (leval > final)
			return;
		leval = leval + 1;
		if (node.children == null)
			return;
		if (typeof (node.children.length) == "undefined")
			return;

		for ( var i = 0; i < node.children.length; i++) {
			var treeNode = node.children[i];

			if (treeNode.isTreeNode) {
				treeNode.expand();
				this._expandToLeval(treeNode, leval, final);
			}
		}
	},
	selectNode : function(treeNode) {
		if (this.tree.selectedNode) {
			var clas = this.tree.selectedNode.labelNode.className;
			clas = clas.replace("TreeLabelFocused", "");
			this.tree.selectedNode.labelNode.className = clas;
		}

		treeNode.labelNode.className = "TreeLabelFocused";
		this.tree.selectedNode = treeNode;
	},
	onmousedown : function() {
	},
	getSelectedNode : function() {
		return this.tree.selectedNode;
	},
	onmouseover : function() {

	},
	onmouseout : function() {
	},
	getCheckedItems : function() {
		return DemoTreeNodeUtil.getCheckedNodes(this.tree);
	},
	toString : function() {
		return " BaseController : ";
	}
};
DemoTreeNodeUtil.checkedNodes = [];
DemoTreeNodeUtil.selectCheckedNodes = function(node) {
	if (node == null)
		return null;
	if (node.children == null)
		return null;

	for ( var i = 0; i < node.children.length; i++) {
		var tnod = node.children[i];

		if (tnod.checkNode != null && tnod.checkNode.checked == true)
			if (tnod.isFolder)
				DemoTreeNodeUtil.featchCheckedNodes(tnod); // folder
			// 其下的子接点未展开所以无checkedNode
			else {
				DemoTreeNodeUtil.checkedNodes.push(tnod);
			}
		else if (tnod.isFolder) {
			DemoTreeNodeUtil.selectCheckedNodes(tnod);
		}
	}
};
DemoTreeNodeUtil.featchCheckedNodes = function(node) {
	if (node == null)
		return null;
	if (node.children == null)
		return null;

	for ( var i = 0; i < node.children.length; i++) {
		var tnod = node.children[i];
		if (tnod.children == null || tnod.children.length < 1) {
			if (!tnod.isFolder)
				DemoTreeNodeUtil.checkedNodes.push(tnod);
		}
		DemoTreeNodeUtil.featchCheckedNodes(tnod);
	}
};
DemoTreeNodeUtil.handleLabelClick = function() {
};