function GxTool() {
}
GxTool.gisIE = navigator.userAgent.indexOf("MSIE") > 0;
GxTool.defaultClassName = "nocked";
GxTool.onSelectItem = function () {
    var el = this;
    var obj = this._obj;
    var idf = this._idref;
    if (el.checked)
        obj.hidden = undefined;
    else
        obj.hidden = true;
    // if (GxTool.notifyFunc) {
    // var arr = GxTool.selectValues(GxTool.holder[idf], 'id');
    // GxTool.notifyFunc(arr);
    // }
};
GxTool.selectValues = function (data, feild) {
    var arr = [];
    for (var i = 0; i < data.length; i++) {
        if (data[i].checked) {
            var obj = data[i]._obj;
            arr[arr.length] = obj;
        }
    }
    return arr;
};
GxTool.holder = {};
GxTool.createProcess = function (mask, cfg) {
    var el = document.createElement('DIV');
    document.body.appendChild(el);
    // GxTool.clearNode(el);
    cfg = cfg || {
        w: 560
    };
    var left = cfg.w / 2 - 20;
    el.style.cssText = 'position:absolute;background:#fff;border:1px solid #5a667b;width:'
        + cfg.w + 'px;z-index:260;';
    // el.style.zIndex = 260;
    // el.className = "popLogLayerL";
    // -------
    var n = document.createElement('DIV');
    n.style.cssText = 'margin:0 0 8px 10px;height:20px;background:#e4e7eb;width:97%;';
    var gn = document.createElement('DIV');
    gn.style.cssText = "background:#090;position:relative;height:20px;width:0%;";
    var gm = document.createElement('DIV');
    gm.style.cssText = "margin:10px 0 0 10px;height:30px;width:97%;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;";
    var span = document.createElement('SPAN');
    span.style.cssText = 'position:absolute;text-align:center;left:' + left
        + 'px';
    n.appendChild(gn);
    gn.appendChild(span);
    el.appendChild(gm);
    el.appendChild(n);
    // -------
    var s = GxTool.getBrowseSize();
    var bg = mask ? GxTool.createBg(s.w, s.h) : false;
    var p = {
        show: function (x, y) {
            x = x || (s.w / 2 - left - 20);
            y = y || s.h / 2;
            if (y > 100)
                y = 100;
            el.style.display = 'block';
            el.style.left = x + 'px';
            el.style.top = y + 'px';
            if (bg)
                bg.style.display = 'block';
        },
        hide: function () {
            if (bg)
                GxTool.removeBg(bg);
            GxTool.removeBg(el);
        },
        msgx: function (msg) {
            gm.innerHTML = msg;
        },
        per: function (p) {
            if (p > 100)
                return;
            span.innerHTML = p + '%';
            gn.style.width = p + '%';
        },
        process: function (p, msg) {
            span.innerHTML = p + '%';
            gn.style.width = p + '%';
            gm.innerHTML = msg;
            if (p == 100) {
                var _s = this;
                window.setTimeout(function () {
                    _s.hide()
                }, 2000);
            }
        }
    };
    return p;
};
GxTool.clearNode = function (el) {
    while (el.childNodes.length > 0) {
        el.removeChild(el.childNodes[0]);
    }
};
GxTool.create = function (el, data, sid, notifyFunc, cols) {
    if (typeof (cols) == 'undefined')
        cols = 2;
    GxTool.notifyFunc = notifyFunc;
    var ele;
    if (typeof (el) == 'string')
        ele = document.getElementById(el);
    else
        ele = el;
    var func = GxTool.onSelectItem;
    var checkboxes = {};
    var itm;
    for (var i = 0; i < data.length; i++) {
        var d = data[i];
        itm = GxTool.createCheckItem(d, el, func);
        ele.appendChild(itm.row);
        checkboxes[d.field] = itm.checkbox;
    }
    GxTool.holder[sid] = checkboxes;
};
GxTool.setChecked = function (sid, arr) {
    var c = GxTool.holder[sid];
    for (var a in c) {
        var n = c[a];
        n.removeAttribute('checked');
        n.checked = undefined;
        n.defaultChecked = false;
    }
    for (var i = 0; i < arr.length; i++) {
        var cell = c[arr[i]];
        if (cell) {
            cell.setAttribute('checked', 'true', 0);
            cell.checked = "checked";
            cell.defaultChecked = true;
        }
    }
};
GxTool.createCheckItem = function (para, idref, func) {
    var name = para.name || para.header;
    var row = document.createElement("DIV");
    var cell = document.createElement("INPUT");
    var txts = document.createTextNode(" " + name);

    cell.type = "checkbox";
    cell.className = "checkbox";
    cell._obj = para;
    cell._idref = idref;
    cell.onclick = func;
    if (para.fixed) {
        para.hidden = undefined;
        cell.setAttribute('disabled', 'true', 0);
    }
    if (!para.hidden) {
        cell.setAttribute('checked', 'true', 0);
        cell.checked = "checked";
        cell.defaultChecked = true;
    }
    row.appendChild(cell);
    row.appendChild(txts);
    row.style.cssText = 'width:130px;height:25px;float:left;';
    // row.className = GxTool.defaultClassName;
    return {
        row: row,
        checkbox: cell
    };
};
GxTool.createFunction = function (obj, strFunc, args) {
    if (!obj)
        obj = window;
    return function () {
        obj[strFunc].apply(obj, args);
    }
};

GxTool.addEvent = function (ele, type, handler) {
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
};
GxTool.removeEvent = function (ele, type, handler) {
    if (ele.nodeType == 3 || ele.nodeType == 8)
        return;
    if (ele.removeEventListener) {
        ele.removeEventListener(type, handler, false);
    } else if (ele.detachEvent) {
        ele.detachEvent("on" + type, handler);
    } else {
        ele["on" + type] = null;
    }
};
GxTool.createFunctionx = function (func, args, scope) {
    if (!scope)
        scope = window;
    return function () {
        func.apply(scope, args);
    }
};
GxTool.capture = function () {
    return false;
};
GxTool.removeBg = function (el) {
    document.body.removeChild(el);
};
/**
 * 创建遮挡 select 的iframe
 */
GxTool.createIfram = function (w, h) {
    h += 10;
    var html = "<iframe src='javascript:false'";
    html += " style='position:absolute; visibility:inherit; left: 0px; top: 0px; width:"
        + w + "px; height:" + h + "px;";
    html += "z-index: -1; filter:Alpha(Opacity=\"0\");' oncontextmenu='return false;'>";
    var iframe = document.createElement(html);
    iframe.style.border = "none";
    return iframe;
};
GxTool.createBg = function (w, h) {
    var bg = document.createElement("div");
    bg.style.position = "absolute";
    bg.style.top = "0";
    bg.style.background = "#000000";
    bg.style.opacity = 0.3;
    bg.style.filter = "alpha(opacity=25)";
    bg.style.left = "0";
    bg.style.zIndex = 250;
    bg.oncontextmenu = GxTool.capture;
    bg.style.width = w + "px";
    bg.style.height = h + "px";
    bg.style.display = 'none';
    bg.style.margin = "0 0 0 0";
    if (GxTool.gisIE) {
        var frm = GxTool.createIfram(w, h);
        bg.appendChild(frm);
    }
    document.body.appendChild(bg);
    return bg;
};
GxTool.getBrowseSize = function () {
    var x, y;
    if (document.documentElement && document.documentElement.clientHeight) {
        w = document.documentElement.clientWidth;
        h = document.documentElement.clientHeight;
    } else if (document.body) {
        w = document.body.clientWidth;
        h = document.body.clientHeight;
    }
    return {
        w: w,
        h: h - 2
    }
};
GxTool.isIE = function () {
    if (navigator.userAgent.indexOf("MSIE") > 0)
        return true;
    return false;
};
GxTool.cancelEvent = function (evt) {
    if (window.event) {
        evt.cancelBubble = true;
        evt.returnValue = false;
    } else {
        evt.preventDefault();
        evt.stopPropagation();
    }
};