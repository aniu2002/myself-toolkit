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
PopDialog._cache = {};
PopDialog.open = function (id, cfg) {
    if (typeof (id) == 'undefined')
        return;
    var _cfg;
    if (typeof (cfg) == 'string')
        _cfg = {title: cfg};
    else
        _cfg = cfg || {};
    var title = _cfg.title || '弹出框';
    var cache = PopDialog._cache;
    var dialog = cache[id];
    if (dialog) {
        dialog.callback = _cfg.cb;
        dialog.show(150, 50);
        return;
    }
    dialog = PopDialog.pop({
        title: title,
        renderTo: id,
        model: true,
        dragEnable: true,
        width: cfg.w || 530,
        height: cfg.h || 150
    });
    dialog.callback = _cfg.cb;
    if (_cfg.before)
        dialog.beforeOk = _cfg.before;
    else
        dialog.beforeOk = function () {
            return true;
        };
    dialog.show(150, 20);
    PopDialog._cache[id] = dialog;
};
PopDialog.openFrame = function (url, t, cb) {
    if (typeof (url) == 'undefined')
        return;
    var title = t || '弹出框';
    var cache = PopDialog._cache;
    var _pov = cache[url];
    if (_pov == null) {
        var frame = $('<iframe frameborder="no" border="0" marginwidth="0" style="visibility:inherit;left:0px;z-index:-1;top:0px;width:97%;height:98%;"></iframe>');
        var dialog = PopDialog.pop({
            title: title,
            width: 600,
            height: 300,
            hideBtn: true,
            renderTo: frame[0],
            dragEnable: true
        });
        var cbName = 'cb_' + new Date().getTime();
        _pov = {frame: frame, dialog: dialog, callback: cbName}
        dialog.callback = function () {
            return true;
        };
        window[cbName] = function (data) {
            dialog.hide();
            if (cb)
                cb(data);
        };
        PopDialog._cache[url] = _pov;
    }
    var nUrl;
    if (url.indexOf('?') != -1)
        nUrl = url + '&_cb=' + _pov.callback;
    else
        nUrl = url + '?_cb=' + _pov.callback;
    _pov.dialog.show(150, 60);
    _pov.frame.attr('src', nUrl);
};
PopDialog.openEl = function (id, title, cb, before) {
    if (typeof (id) == 'undefined')
        return;
    if (typeof (title) == 'undefined')
        title = '弹出框';
    var cache = PopDialog._cache;
    var dialog = cache[id];
    if (dialog) {
        if (cb)
            dialog.callback = cb;
        dialog.show(150, 50);
        return;
    }
    dialog = PopDialog.pop({
        title: title,
        renderTo: id,
        model: true,
        dragEnable: true,
        width: 530,
        height: 150
    });
    dialog.callback = cb;
    if (before)
        dialog.beforeOk = before;
    else
        dialog.beforeOk = function () {
            return true;
        };
    dialog.show(150, 20);
    PopDialog._cache[id] = dialog;
};
PopDialog.addEvent = function (ele, type, handler) {
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
PopDialog.removeEvent = function (ele, type, handler) {
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
PopDialog.pop = function (cfg) {
    cfg = cfg || {};
    var w, h;
    if (typeof (cfg.width) == 'undefined')
        w = 800;
    else
        w = cfg.width;
    if (typeof (cfg.height) == 'undefined')
        h = 350;
    else
        h = cfg.height;
    var pov = new PopDialog(w, h, 20, 25);
    pov.hideBtn = cfg.hideBtn;
    pov.titleText = cfg.title;
    pov.model = typeof (cfg.model) == 'undefined' ? true : cfg.model;
    pov.dragEnable = cfg.dragEnable;
    if (typeof (cfg.renderTo) == 'undefined')
        pov.renderId = "form";
    else
        pov.renderId = cfg.renderTo;
    if (cfg.fn)
        cfg.fn(pov);
    return pov;
};
PopDialog.cancelEvent = function (evt) {
    if (window.event) {
        evt.cancelBubble = true;
        evt.returnValue = false;
    } else {
        evt.preventDefault();
        evt.stopPropagation();
    }
};
PopDialog.prototype = {
    renderTo: function (id) {
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
        this.contentLayer.style.scroll = "no";
        this.contentLayer.style.overflowY = "auto";
        // this.contentLayer.style.overflow = "no";
        // auto

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

            PopDialog.addEvent(this.title, 'mousedown',
                PopDialog._mouseDownFunc);
            PopDialog.addEvent(this.title, 'mouseup', PopDialog._mouseUpFunc);
            // this.title.onmousedown = this.fireElEvent(this, this.startDrag,
            // []);
            // this.title.onmouseup = this.fireElEvent(this, this.stopDrag, []);
            // document.onmousemove = this.fireElEvent(this, this.drag, []);
        }

        this.closeBtn = document.createElement("SPAN");
        this.closeBtn.className = PopDialog.btnCss;
        this.closeBtn.onclick = this.fireElEvent(this, "hide", [ 'cancel' ]);
        var slf = this;
        this.closeBtn.onmouseover = function (evt) {
            slf.nbackC = this.style.backgroundColor;
            this.style.cursor = 'hand';
            // this.style.border = 'solid 1px red';
            this.style.backgroundColor = slf.hover;
        };
        this.closeBtn.onmouseout = function (evt) {
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
        // this.titleLayer.style.cssText = 'height:35px;';
        this.mainLayer.appendChild(this.titleLayer);
        this.mainLayer.appendChild(this.contentLayer);

        this.bottom = document.createElement("DIV");
        this.bottom.style.cssText = 'height:30px;width:100%;text-align:center;float:left;';
        this.bottom.className = 'bottomLayer';
        if (!this.hideBtn) {
            var btn = document.createElement("INPUT");
            btn.setAttribute('type', 'button', 0);
            btn.setAttribute('value', '确定', 0);
            btn.onclick = this.fireElEvent(this, "btnClick", [ 'ok' ]);

            var cbtn = document.createElement("INPUT");
            cbtn.setAttribute('type', 'button', 0);
            cbtn.setAttribute('value', '取消', 0);
            cbtn.onclick = this.fireElEvent(this, "btnClick", [ 'h' ]);
            this.bottom.appendChild(btn);
            this.bottom.appendChild(document.createTextNode('　'));
            this.bottom.appendChild(cbtn);
        }
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
    destroy: function () {
        this.clearAllChilds(this.popDialog);
    },
    setTitle: function (ti) {
        this.title.innerHTML = ti;
        this.titleText = ti;
    },
    isIE: function () {
        if (navigator.userAgent.indexOf("MSIE") > 0)
            return true;
        return false;
    },
    btnClick: function (et, f) {
        var g = true;
        if (f == 'ok' && this.beforeOk)
            g = this.beforeOk(this);
        if (g)
            this.hide(et, f);
        else if (et)
            this.hook(et);
    },
    createIfram: function (w, h) {
        h += this.offy;
        var html = "<iframe src='javascript:false'";
        html += " style='position:absolute; visibility:inherit; left: 0px; top: 0px; width:"
            + w + "px; height:" + h + "px;";
        html += "z-index: -1; filter:Alpha(Opacity=\"0\");' oncontextmenu='return false;'>";
        var iframe = document.createElement(html);
        iframe.style.border = "none";
        return iframe;
    },
    getBrowseSize: function () {
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
    },
    createBgLayer: function () {
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
        this.bgObj.oncontextmenu = function () {
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
    bgShow: function () {
        if (this.bgObj) {
            this.bgObj.style.display = 'block';
        }
    },
    bgHide: function () {
        if (this.bgObj) {
            this.bgObj.style.display = 'none';
        }
    },
    show: function (x, y) {
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
    hook: function (evt) {
        if (window.event) {
            evt.cancelBubble = true;
            evt.returnValue = false;
        } else {
            evt.preventDefault();
            evt.stopPropagation();
        }
    },
    hide: function (evt, f) {
        if (evt)
            this.hook(evt);
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
        if (this.afterHide) {
            this.afterHide();
        }
        this.isHidden = true;
    },
    clearAllChilds: function (element) {
        var obj = element;
        while (obj.childNodes.length > 0) {
            obj.removeChild(obj.childNodes[0]);
        }
    },
    startDrag: function (e) {
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
            PopDialog.addEvent(document, 'mousemove', PopDialog._mouseMoveFunc);
        }
    },
    drag: function (e) {
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
    stopDrag: function (e) {
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
            PopDialog.removeEvent(document, 'mousemove',
                PopDialog._mouseMoveFunc);
        }
    },
    getFocus: function (obj) {
        if (obj.style.zIndex != PopDialog.index) {
            PopDialog.index = PopDialog.index + 2;
            var idx = PopDialog.index;
            obj.style.zIndex = idx;
            obj.nextSibling.style.zIndex = idx - 1;
        }
    },
    createFunction: function (obj, func, args) {
        if (!obj)
            obj = window;
        if (typeof (func) == "string") {
            func = obj[func];
        }
        return function () {
            func.apply(obj, args);
        }
    },
    fireElEvent: function (obj, func, args) {
        if (!obj)
            obj = window;
        if (args && args.length) {
            for (var i = args.length; i > 0; i--)
                args[i] = args[i - 1];
        }
        var tim = (new Date()).getTime();
        return function (e) {
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