Date.prototype.format = function (format) {
    format = format || 'yyyy-MM-dd hh:mm:ss';
    var o = {
        "M+": this.getMonth() + 1, // month
        "d+": this.getDate(), // day
        "h+": this.getHours(), // hour
        "m+": this.getMinutes(), // minute
        "s+": this.getSeconds(), // second
        "q+": Math.floor((this.getMonth() + 3) / 3), // quarter
        "S": this.getMilliseconds()
    };
    if (/(y+)/.test(format))
        format = format.replace(RegExp.$1, (this.getFullYear() + "")
            .substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(format))
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
                : ("00" + o[k]).substr(("" + o[k]).length));
    return format;
};
// 给字符串对象添加一个startsWith()方法
String.prototype.startsWith = function (substring) {
    var reg = new RegExp("^" + substring);
    return reg.test(this);
};
// 给字符串对象添加一个endsWith()方法
String.prototype.endsWith = function (substring) {
    var reg = new RegExp(substring + "$");
    return reg.test(this);
};
// 删除所有空白字符
String.prototype.deleteWhiteSpaces = function () {
    var extraSpace = /[\s\n\r]+/g;
    return this.replace(extraSpace, "");
};
var Utils = {
    _formCache: {},
    formFields: function (form) {
        var eleS = form.elements;
        var fields = {};
        for (var i = 0; i < eleS.length; i++) {
            var item = eleS[i];
            if (item.name != '') {
                if (item.type == 'text' || item.type == "textarea"
                    || item.type == "password") {
                    fields[item.name] = item;
                }
            }
        }
        return fields;
    },
    formSet: function (form, paras) {
        var el = null;
        if (typeof (form) == "string")
            el = document.getElementById(form);
        else
            el = form;
        var fields = this._formCache[form];
        if (fields == null) {
            fields = getFormFields(el);
            this._formCache[form] = fields;
        }
        for (var name in paras) {
            var itm = fields[name];
            if (itm) {
                itm.value = paras[name];
            }
        }
    },
    serializeForm: function (form) {
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
                    || item.type == 'reset' || item.type == 'image') {
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
    },
    renderHtml: function (data, tmp, renders) {
        var tp = tmp;
        for (var k in data) {
            var by = '{' + k + '}';
            var v = data[k];
            if (renders && renders[k])
                v = renders[k](v, data)
            if (v == null || typeof (v) == 'undefined')
                v = '';
            tp = tp.replace(new RegExp(by, "gm"), v);
        }
        return tp;
    },
    createTemplate: function (tmp, renders) {
        return {
            template: tmp,
            renders: renders,
            render: function (data) {
                return this.renderHtml(data, this.template, this.renders);
            }
        }
    },
    addEvent: function (ele, type, handler) {
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
    getPixNum: function (w) {
        if (typeof (w) == 'number')
            return w;
        var idx = w.indexOf('px');
        if (idx != -1)
            w = w.substring(0, idx);
        return parseInt(w);
    },
    fetch: function (msg, el, vars) {
        var args = [ msg ];
        if (vars) {
            if (typeof (vars) == 'array') {
                for (var i = 0; i < vars.length; i++)
                    args[args.length] = el.attr(vars[i]);
            } else
                args[args.length] = el.attr(vars);
        }
        return Utils.replace.apply(this, args);
    },
    replace: function () {
        var args = arguments;
        var s;
        if (args && args.length) {
            s = args[0];
            if (s && args.length > 1) {
                for (var i = 1; i < args.length; i++) {
                    var n = i - 1;
                    var by = '\\{' + n + '\\}';
                    var v = args[i];
                    s = s.replace(new RegExp(by, "gm"), v);
                }
            }
        }
        return s;
    },
    createItm: function (k, v) {
        var row = $('<tr></tr>');
        var label = $('<td width="80" valign="top">' + k + '：</td>');
        var cell = $('<td></td>');
        if (!v)
            v = '&nbsp;';
        if (k.indexOf('password') != -1)
            v = '*******';
        cell.append(v)
        row.append(label);
        row.append(cell);
        return row;
    }, createTable: function () {
        var table = $('<table class="table table-striped table-bordered table-condensed table-hover"></table>');
        var _f = Utils.createItm;
        var fn = function (t, d) {
            for (var k in d) {
                t.append(_f(k, d[k]));
            }
        };
        if (p.length) {
            for (var i = 0; i < p.length; i++) {
                fn(table, p[i])
            }
        } else
            fn(table, p)
        return table;
    },
    setting: function (el, url) {
        if (typeof (el) == 'string')
            el = $(el);
        Common.get(url, function (d) {
            el.append(Utils.createTable(d));
        });
    }
};

var Common = {
    loadHtml: '<div><img src="/app/images/ajax-loader.gif" />&nbsp;加载中...</div>',
    loadImg: '<img src="/app/images/ajax-loader.gif" />',
    _locks: {},
    _cb: {},
    _dic: {},
    store: {
        sqlType: [ 'mysql', 'oracle' ]
    },
    errorLoad: function (xhr, status, err) {
        if (xhr.status == 555) {
            top.location.href = xhr.responseText;
            return;
        } else {
            // alert(xhr.responseText);
        }
    },
    get: function (url, func, data, c) {
        var cfg = {
            type: "GET",
            url: url,
            cache: false,
            dataType: "json",
            ifModified: true,
            // ajaxStart : waitingQuery,
            data: data,
            statusCode: {
                302: function () {
                    alert('page not found');
                }
            },
            error: Common.errorLoad,
            success: function (text, status, jqXHR) {
                if (func)
                    func(text, status)
            }
        };
        if (c)
            cfg.cache = true;
        $.ajax(cfg);
    },
    post: function (url, data, func, type) {
        type = type || 'application/x-www-form-urlencoded;charset=UTF-8';
        $.ajax({
            type: 'POST',
            url: url,
            cache: false,
            contentType: type,
            dataType: "json",
            ifModified: true,
            data: data,
            statusCode: {
                302: function () {
                    alert('page not found');
                }
            },
            error: Common.errorLoad,
            success: function (text, status, jqXHR) {
                if (func)
                    func(text, status)
            }
        });
    },
    load: function (url, func, c) {
        if (typeof (c) == 'undefined')
            c = false;
        $.ajax({
            type: "GET",
            url: url,
            cache: c,
            dataType: "text",
            ifModified: true,
            error: Common.errorLoad,
            success: func
        });
    },
    xhr: function (url, ele) {
        ele.empty();
        ele.append(this.loadHtml);
        Common.load(url, function (d) {
            if (d) {
                ele.empty();
                ele.append(d);
            }
        }, false);
    },
    batchLoad: function (s, fn) {
        var handleResp = function (d) {
            for (var k in d) {
                Common.store[k] = d[k];
            }
            fn(d);
        }
        Common.get('/cmd/source?_s=' + s, handleResp, null, true);
    },
    loadSource: function (s, fn) {
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
        if (this._locks[s])
            return;
        var _sf = this;
        _sf._locks[s] = true;
        var handleResp = function (d) {
            Common.store[s] = d;
            var cb = _sf._cb[s];
            if (cb) {
                for (var i = 0; i < cb.length; i++) {
                    var fc = cb[i];
                    if (fc)
                        fc(d);
                }
                _sf._cb[s] = undefined;
            }
            _sf._locks[s] = false;
        }
        if (g == 'f')
            Common.get('/app/json/' + s + '.json', handleResp, null, true);
        else
            Common.get('/cmd/source?_s=' + s, handleResp, null, true);

    },
    getDic: function (s) {
        if (Common._dic[s])
            return Common._dic[s];
        var d = Common.store[s];
        if (!d)
            return null;
        var sv = {};
        for (var i = 0; i < d.length; i++) {
            var ld = d[i];
            sv[ld.code] = ld.name;
        }
        Common._dic[s] = sv;
        return sv;
    },
    renderSelect: function (s, el, v, fn, cfg) {
        var xcb = function (d) {
            var sel = Common.renderOptions(el, d, v, fn, cfg);
            if (cfg && cfg.onSet) {
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
    bindSelect: function (id, data, cb) {
        var sel = typeof (id) == 'string' ? document.getElementById(id) : id;
        sel.options.length = 0;
        var op = new Option('请选择', '');
        sel.options[sel.options.length] = op;
        for (var i = 0; i < data.length; i++) {
            var d = data[i];
            var c, n;
            if (typeof (d) == 'string') {
                c = n = d;
            } else {
                c = d.code;
                n = d.name;
            }
            var op = new Option(n, c);
            sel.options[sel.options.length] = op;
        }
        if (cb) {
            sel.onchange = function (e) {
                cb(sel)
            };
        }
    },
    /**
     * 过滤掉source里的extra值=type的条目
     */
    bindSelectExtra: function (id, alias, type, defVal) {
        var _bx = true;
        if (typeof (defv) == 'undefined')
            _bx = false;
        var data = Common.store[alias];
        var sel = typeof (id) == 'string' ? document.getElementById(id) : id;
        var op = new Option('请选择', '');
        sel.options.length = 0;
        sel.options[sel.options.length] = op;
        for (var i = 0; i < data.length; i++) {
            var d = data[i];
            if (d.extra && type != '*') {
                if (d.extra != '*' && d.extra.indexOf(type) == -1)
                    continue;
            }
            var c, n;
            if (typeof (d) == 'string') {
                c = n = d;
            } else {
                c = d.code;
                n = d.name;
            }
            var op = new Option(n, c);
            if (_bx && c == defVal) {
                _bx = false;
            }
            sel.options[sel.options.length] = op;
        }
        if (_bx && defVal) {
            var opx = new Option(defVal, defVal);
            opx.selected = true;
            sel.options[sel.options.length] = opx;
        }
        if (defVal)
            $(sel).val(defVal);
    },
    renderOptions: function (id, data, selected, cb, cfg) {
        var sel = typeof (id) == 'string' ? document.getElementById(id) : id;
        if (!sel)
            return;
        sel.options.length = 0;
        if (cfg) {
            if (typeof (cfg.defVal) != 'undefined' || cfg.search) {
                var v = cfg.defVal || '';
                var op = new Option('请选择', v);
                if (cfg.search)
                    op.selected = true;
                sel.options[sel.options.length] = op;
            }
        }
        for (var i = 0; i < data.length; i++) {
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
        if (cb) {
            sel.onchange = function (e) {
                cb(sel)
            };
        }
        return sel;
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
    imgLazyLoad: function (el) {
        if (typeof (el) == 'string')
            el = $(el);
        el.find('img').each(function (i, ele) {
            Common.lazyInit(el, $(ele));
        });
    },
    lazyInit: function (imgEl) {
        var ori = imgEl.attr('ori');
        if (ori) {
            var img = new Image();
            img.src = ori;
            $(img).bind('load', function (e) {
                imgEl.attr('src', ori);
            });
        }
    },
    mask: function (el, msg) {
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
        var _html = $('<div id="loadingMask" style="position:absolute;cursor:wait;background:#E0ECFF;opacity:0.9;filter:alpha(opacity=90);"></div>');
        var _inner = $('<div style="position: relative;width:180px;height:16px;padding:12px 5px 10px 30px;background:#fff no-repeat scroll 5px 10px;border:2px solid #ccc;color:#000;"><img src="/app/images/ajax-loader.gif" />&nbsp;' + msg + '</div>');
        _html.append(_inner);
        _html.css({
            top: offset.top + "px",
            left: offset.left + "px",
            width: width + "px",
            height: height + "px"
        });
        _inner.css({
            top: c_top + "px",
            left: c_left + "px"
        });
        $(document.body).append(_html);
    },
    unMask: function () {
        window.setTimeout(function () {
            $('#loadingMask').remove();
        }, 100);
    }
};