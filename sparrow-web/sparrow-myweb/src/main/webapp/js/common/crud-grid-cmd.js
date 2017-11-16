var _Render = {
    _source: function (s) {
        var ss = Common.getDic(s);
        var f;
        if (ss) {
            f = function (v) {
                v = String(v);
                if (v) {
                    var vl = ss[v];
                    if (vl)
                        return vl;
                    else
                        return v;
                } else
                    return v;
            };
            _Render[s] = f;
            return f;
        } else {
            f = function (v) {
                return v;
            };
            _Render[s] = f;
            return f;
        }
    },
    __checkNull: function (v) {
        if (v == null || v == '' || v == undefined || v == 'null')
            return true;
        return false;
    },
    time: function (v, data, idx, row) {
        if (v)
            return (new Date(v)).format();
    },
    _idx: function (v, data, idx, row, cell) {
        if (cell)
            cell.attr('align', 'center');
        return idx;
    },
    cbox: function (v, data) {
        var el = $('<input type="checkbox" name="_cgroup" >');
        if (data.id)
            el.attr('_id', data.id);
        return el;
    },
    radio: function (v, data) {
        var el = $('<input type="radio" name="_cgroup" >');
        if (data.id)
            el.attr('_id', data.id);
        return el;
    },
    bool: function (v) {
        if (v == 1)
            return 'Y';
        else
            return 'N'
    },
    state: function (v) {
        var tx = v;
        if (v == 1)
            tx = "已提交";
        else
            tx = "草稿";
        return tx;
    },
    reportState: function (v) {
        var s = Common.getDit('reportState');
        v = String(v);
        if (v && s) {
            if (v == -1)
                v = '<font color="red">' + s[v] + '</font>';
            else if (v == 2)
                v = '<font color="blue">' + s[v] + '</font>';
            else if (v == 1)
                v = '<font color="#9AC6FF">' + s[v] + '</font>';
            else
                v = s[v];
        }
        return v;
    },
    applyState: function (v) {
        var s = Common.getDit('applyState');
        v = String(v);
        if (v && s) {
            if (v == -1)
                v = '<font color="red">' + s[v] + '</font>';
            else if (v == 1)
                v = '<font color="blue">' + s[v] + '</font>';
            else if (v == -2)
                v = '<font color="red">' + s[v] + '</font>';
            else
                v = '<font color="#9AC6FF">' + s[v] + '</font>';
        }
        return v;
    },
    status: function (v) {
        var tx = v;
        if (v == 1)
            tx = "可用";
        else
            tx = "不可用";
        return tx;
    },
    chartType: function (v) {
        var s = Common.getDit('chartType');
        if (v && s)
            return s[v];
        else
            return v;
    },
    reportType: function (v) {
        var s = Common.getDit('reportType');
        if (v && s)
            return s[v];
        else
            return v;
    },
    desktopType: function (v) {
        var s = Common.getDit('desktopType');
        if (v && s)
            return s[v];
        else
            return v;
    },
    layout: function (v) {
        var s = Common.getDit('layout');
        if (v && s)
            return s[v];
        else
            return v;
    },
    business: function (v) {
        var s = Common.getDit('business');
        if (v && s)
            return s[v];
        else
            return v;
    },
    img: function (v) {
        var img = $('<img src="/app/images/defaultx.png" style="width:170px;height:80px;"/>');
        if (v) {
            img.attr('ori', v);
            Common.lazyInit(img);
        }
        return img;
    }
};
var _Editor = {
    'text': function (name, cfg) {
        var el = $('<input type="text" name="' + name + '" >');
        if (cfg.ref)
            el.attr('ref', cfg.ref);
        return el;
    },
    'textarea': function (name, cfg) {
        var el = $('<textarea name="' + name + '"/>');
        return el;
    },
    'pwd': function (name, cfg) {
        var el = $('<input type="password" autocomplete="off" name="' + name
            + '">');
        if (cfg.ref)
            el.attr('ref', cfg.ref);
        return el;
    },
    'date': function (name, cfg) {
        var el = $('<input type="text" name="' + name + '" >');
        return el;
    },
    'time': function (name, cfg) {
        var el = $('<input type="text" name="' + name + '" >');
        return el;
    },
    'read': function (name, cfg) {
        var el = $('<input type="text" name="' + name
            + '" readonly="readonly">');
        return el;
    },
    'cur': function (name, cfg) {
        var el = $('<span></span>');
        Common.xhr('/cmd/sys/user?_t=cur', el);
        return el;
    },
    'desktop': function (name, cfg) {
        var width = '150px';
        if (cfg.width) {
            var n = Common.getPxNum(cfg.width);
            n = n - 80;
            width = n + 'px';
        }
        var el = $('<div></div>');
        var tel = $('<input type="text" id="' + name + '_lb" name="' + name
            + '_label" readonly="readonly">');
        tel.css({
            width: width
        });
        var gel = $('<input type="hidden" name="' + name + '">');

        var _$P = PopDialog.pop({
            title: cfg.povTitle,
            renderTo: cfg.povEl,
            dragEnable: true,
            width: 800,
            height: 400,
            modal: true
        });
        _$P.callback = function () {
            if (cfg.func) {
                var v = cfg.func();
                if (v) {
                    tel.val(v.label);
                    gel.val(v.code);
                }
            }
        };
        var btn = $('<input type="button" value="选择" />')
        btn.click(function (e) {
            var h = 30;
            if (e.pageY > 50)
                h = e.pageY - 50;
            if (cfg.prefn)
                cfg.prefn();
            _$P.show(60, h);
        });
        el.append(tel);
        el.append(gel);
        el.append('&nbsp;');
        el.append(btn);
        return el;
    },
    'desktopx': function (name, cfg) {
        var width = '150px';
        if (cfg.width) {
            var n = Common.getPxNum(cfg.width);
            n = n - 80;
            width = n + 'px';
        }
        var el = $('<div></div>');
        var tel = $('<input type="text" id="' + name + '_lb" name="' + name
            + '_label" readonly="readonly">');
        tel.css({
            width: width
        });
        var gel = $('<input type="hidden" name="' + name + '">');
        el.append(tel);
        el.append(gel);
        return el;
    },
    'source': function (name, cfg) {
        var width = '150px';
        if (cfg.width) {
            var n = Common.getPxNum(cfg.width);
            n = n - 80;
            width = n + 'px';
        }
        var el = $('<div></div>');
        var tel;
        if (cfg.et == 'sl' || cfg.et == 'pk')
            tel = $('<input type="text" name="' + name
                + '" readonly="readonly">');
        else if (cfg.et == 'sh')
            tel = $('<textarea name="' + name + '"/>');
        else
            tel = $('<textarea name="' + name + '" readonly="readonly"/>');

        tel.css({
            width: width
        });
        var btn = $('<input type="button" value="选择" />')
        if (cfg.btnClick)
            btn.click(function () {
                cfg.btnClick(tel.val(), tel, cfg);
            });
        el.append(tel);
        el.append('&nbsp;');
        el.append(btn);
        return el;
    },
    'select': function (name, cfg, form) {
        var el = $('<select name="' + name + '" ></select>');
        var v = el[0];
        cfg.__fm = form;
        if (cfg.relField || cfg.onSet)
            Common.renderSelect(cfg.src, v, cfg.defVal, function (sel) {
                var itm = sel.options[sel.selectedIndex];
                var iv = itm.text;
                if (cfg.relField) {
                    var g = {};
                    g[cfg.relField] = iv;
                    form.setParameters(g);
                }
                if (cfg.onSet)
                    cfg.onSet(itm.value, iv, sel, itm);
            }, cfg);
        else
            Common.renderSelect(cfg.src, v, cfg.defVal, null, cfg);
        return el;
    },
    'bool': function (name, cfg) {
        var el = $('<input type="text" name="' + name + '" >');
        return el;
    },
    'checkbox': function (name, cfg) {
        var el = $('<input type="checkbox" name="' + name + '" >');
        return el;
    },
    'label': function (name, cfg) {
        var el = $('<input type="text" name="' + name
            + '" readonly="readonly">');
        return el;
    },
    'img': function (name, cfg) {
        var img = $('<img src="/app/images/defaultx.png" style="width:370px;height:180px;"/>');
        if (cfg.imgEl)
            img.attr('id', cfg.imgEl);
        return {
            _ig: true,
            set: function (v) {
                if (v) {
                    img.attr('ori', v);
                    Common.lazyInit(img);
                }
            },
            _rf: 'x',
            _el: img
        };
    },
    'picBrowse': function (name, cfg) {
        var div = cfg._cl;
        var img = $('<img src="/app/images/defaultx.png" style="width:270px;height:120px;"/>');
        if (cfg.imgEl)
            img.attr('id', cfg.imgEl);
        var el = $('<select name="' + name + '" ></select>');
        var v = el[0];
        if (cfg.onSet)
            Common.renderSelect(cfg.src, v, null, function (sel) {
                var itm = sel.options[sel.selectedIndex];
                var iv = itm.text;
                if (cfg.onSet)
                    cfg.onSet(itm.value, iv, sel, itm);
            }, cfg);
        else
            Common.renderSelect(cfg.src, v, null, null, cfg);
        div.append(el);
        div.append('&nbsp;&nbsp;');
        div.append(img);

        return {
            _ig: false,
            fb: function () {
                el.attr('disabled', true);
            },
            set: function (v) {
                alert(v)
                if (v) {
                    var ev = el[0];
                    var im = ev.options[ev.selectedIndex];
                    var ivv = im.text;
                    alert(ivv)
                    if (cfg.onSet)
                        cfg.onSet(im.value, ivv, ev, im);
                }
            },
            _rf: el,
            _el: div
        };
    },
    __createBtn: function (title, func, gxt) {
        var btn = $('<button class="btn btn-primary">' + title + '</button>');
        if (func) {
            btn.click(function (e) {
                e.stopPropagation();
                e.preventDefault();
                func({
                    x: e.pageX,
                    y: e.pageY
                }, gxt);
            });
        }
        return btn;
    },
    'texta': function (name, cfg) {
        var div = cfg._cl;
        var el = $('<input type="text" name="' + name + '" >');
        if (cfg.ref)
            el.attr('ref', cfg.ref);
        div.append(el);
        div.append('&nbsp;&nbsp;');
        var btn = this.__createBtn(cfg.btnTitle, cfg.onSet);
        div.append(btn);
        if (cfg.btn2Title) {
            var btn2 = this.__createBtn(cfg.btn2Title, cfg.onSet, 1);
            div.append('&nbsp;&nbsp;');
            div.append(btn2);
        }
        return {
            _ig: false,
            fb: function () {
                // btn.attr('disabled', true);
            },
            _rf: el,
            _el: div
        };
    },
    'btnTxt': function (name, cfg) {
        var el = $('<input type="text" name="' + name + '" >');
        return el;
    },
    'imgUp': function (name, cfg) {
        var div = cfg._cl;
        var btn = $('<button class="btn btn-primary">' + cfg.btnTitle
            + '</button>');
        var el = $('<input type="hidden" name="' + name + '" >');
        if (cfg.onSet)
            btn.click(function (e) {
                e.stopPropagation();
                e.preventDefault();
                cfg.onSet(el, {
                    x: e.pageX,
                    y: e.pageY
                });
            });
        var img = $('<img src="/app/images/defaultx.png" style="width:270px;height:120px;"/>');
        if (cfg.imgEl)
            img.attr('id', cfg.imgEl);
        div.append(el);
        div.append(btn);
        div.append('&nbsp;&nbsp;');
        div.append(img);
        return {
            _ig: false,
            _rf: el,
            _el: div
        };
    }
};
var _ToolTip = function (msg) {
    this.autoHide = true;
};
_ToolTip.prototype = {
    _tip: null,
    _fg: false,
    _create: function () {
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
    show: function (el) {
        /** position 相对，offset 绝对 */
        var pos = el.offset();
        var w = pos.left + el.width();
        if (this._tip == null)
            this._create();
        this._tip.css({
            left: w + 'px',
            top: pos.top + 'px'
        });
        this._tip.show();
        if (this.autoHide) {
            var tp = this;
            if (tp._fg)
                return;
            tp._fg = true;
            window.setTimeout(function () {
                tp.hide();
                tp._fg = false;
            }, 3000);
        }
    },
    hide: function () {
        this._tip.hide();
    },
    message: function (msg) {
        this._msg.text(msg);
    }
};
var _Form = function (id, cols, url, search) {
    if (typeof (id) == 'string')
        this.el = $(id);
    else
        this.el = id;
    this.cols = cols;
    this.url = url;
    this.search = search;
    this.formEl = null;
};
_Form.createEditor = function (type, name, value, col, form) {
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
    var nel = el._rf || el;
    if (nel && nel != 'x') {
        if (col.id)
            nel.attr('id', col.id);
        if (value)
            nel.val(value);
        if (!col.search) {
            if (width)
                nel.css('width', width);
            if (height)
                nel.css('height', height);
        }
    }
    return el;
};
_Form.getFormDom = function (ele) {
    var form = undefined;
    if (ele && ele.length != 0)
        form = ele[0];
    return form;
};
_Form.getFormFields = function (ele) {
    var form = _Form.getFormDom(ele);
    if (form) {
        var els = form.elements;
        var fields = {};
        for (var i = 0; i < els.length; i++) {
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
_Form.hasIn = function (arr, str) {
    for (var i = 0; i < arr.length; i++)
        if (arr[i] == str)
            return true;
    return false;
};
_Form.getFormParas = function (ele, strs) {
    var form = _Form.getFormDom(ele);
    if (form) {
        var els = form.elements;
        var fields = {};
        for (var i = 0; i < els.length; i++) {
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
        var gxg = {};
        for (var a in fields) {
            if (_Form.hasIn(strs, a))
                gxg[a] = fields[a];
        }
        return gxg;
    }
    return undefined;
};
_Form.prototype = {
    _actEl: undefined,
    _editActive: false,
    _editRowId: undefined,
    _validator: undefined,
    _postHandler: undefined,
    _hideEvt: function (fm) {
        fm.keydown(function (e) {
            var k = e.keyCode || e.which;
            if (k == 13) {
                e.stopPropagation();
                e.preventDefault();
                return false;
            }
        });
    },
    draw: function () {
        if (this.el == null || this.el == undefined || this.el.length == 0)
            this.el = $('<div></div>');
        else
            this.el.empty();
        // var method
        var form = $('<form action="' + this.url
            + '" class="breadcrumb form-search" method="POST"></form>');
        var tb;
        if (this.search) {
            tb = this.getSearchTable(form);
            this._hideEvt(form);
        } else {
            tb = this.getFormTable(form);
            this._hideEvt(form);
        }
        form.append(tb);
        this.tableEl = tb;
        this.el.append(form);
        this.formEl = form;
    },
    getSearchTable: function (form) {
        var table = $('<table></table>');
        this.painSearchRow(table, this.cols, form);
        return table;
    },
    getFormTable: function (form) {
        var table = $('<table></table>');
        if (this.title)
            this.addRow(this.title, table);
        this.painFormRow(table, this.cols, form);
        return table;
    },
    skipCol: function (col) {
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
    clearSet: function () {
        this._editActive = false;
        this._editRowId = undefined;
        var fields = _Form.getFormFields(this.formEl);
        if (fields) {
            for (var name in fields) {
                var itm = fields[name];
                itm.val('');
            }
        }
    },
    editActive: function (rowid) {
        this._editActive = true;
        this._editRowId = rowid;
    },
    painSearchRow: function (table, cols, form) {
        var row = undefined;
        var lastCell = undefined;
        var n = 0;
        for (var i = 0; i < cols.length; i++) {
            var col = cols[i];
            if (this.skipCol(col))
                continue;
            var ignore = (typeof(col.skipHide) == 'boolean') && col.skipHide;
            var needHide = col.hidden || col.noe;
            if (!ignore && needHide) {
                if (form) {
                    var el = $('<input type="hidden" name="' + col.name
                        + '" />');
                    if (col.id)
                        el.attr('id', col.id);
                    if (typeof (col.defVal) != 'undefined')
                        el.val(col.defVal);
                    form.prepend(el);
                }
                continue;
            }
            if (n % 4 == 0) {
                if (row)
                    table.append(row);
                row = $('<tr></tr>');
            }
            lastCell = this.painSearchItem(row, col);
            n++;
        }
        if (row) {
            table.append(row);
            var search = $('<a class="btn btn-primary">&nbsp;<i class="icon-search icon-white"></i>查询</a>');
            this._actEl = search;
            if (n % 4 !== 0) {
                var colspan = (4 - (n % 4)) * 2;
                row.append($('<td colspan="' + colspan + '"></td>').append(
                    search));
            } else if (lastCell)
                lastCell.append(search);
        }
    },
    colspan: 2,
    painFormRow: function (table, cols, form) {
        var row = undefined;
        var lastCell = undefined;
        var n = 0;
        var c = this.colspan;
        var cc = c;
        for (var i = 0; i < cols.length; i++) {
            var col = cols[i];
            // if (col.name == 'id')
            // continue;
            // else
            if (col.noe && form) {
                if (col.id) {
                    var elx = $('#' + col.id);
                    if (elx.length > 0)
                        continue;
                }
                var el = $('<input type="hidden" name="' + col.name + '" />');
                if (col.id)
                    el.attr('id', col.id);
                form.prepend(el);
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
    appendTd: function (row, colspan) {
        colspan = colspan * 2;
        var cel = $('<td align="left" colspan="' + colspan + '">&nbsp;</td>');
        row.append(cel);
    },
    addRow: function (desc, table) {
        table = table || this.tableEl;
        var c = this.colspan;
        var colspan = c * 2;
        var row = $('<tr height="50"></tr>');
        row
            .append($('<td align="left" colspan="' + colspan
                + '"><span style="font-weight:bold;">' + desc
                + '</span></td>'));
        table.append(row);
        return row;
    },
    addRowx: function (table) {
        table = table || this.tableEl;
        var c = this.colspan;
        var colspan = c * 2;
        var row = $('<tr height="50"></tr>');
        var td = $('<td align="left" colspan="' + colspan + '"></td>');
        var ul = $('<ul class="nav nav-tabs"></ul>');
        var div = $('<div style="margin-top:5px;"></div>');
        div.append(ul);
        td.append(div);
        row.append(td);
        table.append(row);
        return ul;
    },
    createProw: function (title) {
        var c = this.colspan;
        var colspan = c * 2;
        var row = $('<tr height="50"></tr>');
        var cel = $('<td align="left" colspan="' + colspan + '"></td>');
        if (title) {
            var tel = $('<span style="font-weight:bold;"></span>');
            tel.text(title);
            cel.append(tel);
        }
        row.append(cel);
        this.tableEl.append(row);
        return cel;
    },
    drawx: function (fn) {
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
    appendEditor: function (cols, tb, form, fn) {
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
    selectedIdx: 0,
    divs: [],
    _tbEls: [],
    _tabUL: undefined,
    _lastEl: undefined,
    _lastLi: undefined,
    _tabCon: undefined,
    _extraTable: undefined,
    _extTbl: undefined,
    _tblItems: {},
    clearExtra: function () {
        if (this._tabUL)
            this._tabUL.empty();
        if (this._tabCon)
            this._tabCon.empty();
        this.selectedIdx = 0;
        this.divs = [];
        this._lastEl = undefined;
    },
    appendTabItms: function (idx, cols, cs) {
        var sp = this.colspan;
        if (cs)
            this.colspan = cs;
        var nTable = this._tbEls[idx];
        if (nTable)
            nTable.empty();
        this.painFormRow(nTable, cols, this.formEl);
        this.colspan = sp;
        if (this._validator)
            this._validator._bindCols(cols);
    },
    bindClick: function (btn, f, room, form) {
        btn.click(function (e) {
            e.stopPropagation();
            e.preventDefault();
            f($(this), room, form);
        });
    },
    drawExBar: function (col, cel, room) {
        var bars = col.bars;
        var form = this;
        for (var j = 0; j < bars.length; j++) {
            var bar = bars[j];
            if (bar.title) {
                cel.append('&nbsp;&nbsp;');
                var btn = $('<button class="btn btn-primary">' + bar.title
                    + '</button>');
                if (bar.id)
                    btn.attr('id', bar.id);
                if (bar.func)
                    this.bindClick(btn, bar.func, room, form);
                cel.append(btn);
            }
        }
    },
    appendOtItem: function (cols, title, cs) {
        var nTable = this._tblItems[cs];
        if (nTable)
            nTable.empty();
        else {
            nTable = $('<table></table>');
            var ncel = this.createProw(title);
            ncel.append(nTable);
            this._tblItems[cs] = nTable;
        }
        // this.painFormRow(nTable, cols, this.formEl);
        this.painFormRow(nTable, cols, this.formEl);
        if (this._validator)
            this._validator._bindCols(cols);
    },
    _rooms: {},
    appendExItem: function (cols, title, cs) {
        var nTable = this._tblItems[cs];
        if (nTable)
            nTable.empty();
        else {
            nTable = $('<table style="margin-left:10px;"></table>');
            var ncel = this.createProw(title);
            ncel.append(nTable);
            this._tblItems[cs] = nTable;
        }
        // this.painFormRow(nTable, cols, this.formEl);
        for (var i = 0; i < cols.length; i++) {
            var col = cols[i];
            var row = $('<tr></tr>');
            var cel = $('<td></td>');

            var room = undefined;
            if (col.create)
                room = col.create(col, cel, row, nTable);
            this._rooms[cs] = room;
            if (col.title) {
                var tel = $('<span></span>')
                tel.text(col.title);
                cel.append(tel);
            }
            if (col.bars)
                this.drawExBar(col, cel, room);
            row.append(cel);
            nTable.append(row);

            row = $('<tr></tr>');
            cel = $('<td></td>');

            if (room)
                cel.append(room);
            row.append(cel);
            nTable.append(row);
        }
        if (this._validator)
            this._validator._bindCols(cols);
        return this._rooms;
    },
    appendTab: function (cols, title, cs) {
        var len = this.divs.length;
        var _s = this;
        var _f = (len == 0);
        if (this._tabUL === undefined)
            this._tabUL = this.addRowx(this.tableEl);
        // class="btn btn-primary"
        var s;
        if (_f) {
            s = $('<li idx="' + len + '" class="active"><a href="#">' + title
                + '</a></li>');
            this._lastLi = s;
        } else
            s = $('<li style="margin-left:10px;" idx="' + len
                + '"><a href="#">' + title + '</a></li>');
        s.click(function (e) {
            e.stopPropagation();
            e.preventDefault();
            if (_s._lastLi)
                _s._lastLi.removeClass('active');
            if (_s._lastEl)
                _s._lastEl.hide();

            var ecl = $(this);
            _s._lastLi = ecl;
            ecl.addClass('active');

            var idx = parseInt(ecl.attr('idx'));
            ecl = _s.divs[idx];
            ecl.show();
            _s.selectedIdx = idx;
            _s._lastEl = ecl;
        });

        this._tabUL.append(s);

        if (this._tabCon === undefined)
            this._tabCon = this.createProw();
        var div = $('<div style="widht:100%"></div>');
        this._tabCon.append(div);
        if (_f)
            this._lastEl = div;
        else
            div.hide();
        this.divs[len] = div;
        var nTable = $('<table></table>');
        this._tbEls[len] = nTable;
        div.append(nTable);
        var sp = this.colspan;
        if (cs)
            this.colspan = cs;
        this.painFormRow(nTable, cols, this.formEl);
        this.colspan = sp;
        if (this._validator)
            this._validator._bindCols(cols);
    },
    appendCols: function (cols) {
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
    painSearchItem: function (row, col, ed) {
        var label = undefined;
        if (col.label)
            label = $('<td><label>' + col.label + '：</label></td>');
        else
            label = $('<td>&nbsp;</td>');
        var editor;
        if (col.colspan) {
            var sp = col.colspan * 2 - 1;
            editor = $('<td colspan="' + sp + '"></td>');
        } else
            editor = $('<td></td>');
        var wd;
        if (col.editor == 'select')
            wd = '120px';
        else
            wd = '150px';
        col._cl = editor;
        var el = _Form.createEditor(col.editor, col.name, undefined, col, this);
        var nel = el._rf || el;
        el = el._el || el;

        if (nel && nel != 'x') {
            if (col.vtype)
                nel.attr('vtype', col.vtype);
            if (col.max)
                nel.attr('max', col.max);
            nel.css({
                width: wd
            });
        }
        editor.append(el)
        row.append(label);
        row.append(editor);
        return editor;
    },
    _editors: {},
    painItem: function (row, col, ed) {
        var label = null;
        if (col.label)
            label = $('<td><label>' + col.label + '：</label></td>');
        else
            label = $('<td><label>&nbsp;</label></td>');
        var editor;
        if (col.colspan) {
            var sp = col.colspan * 2 - 1;
            editor = $('<td colspan="' + sp + '"></td>');
        } else {
            editor = $('<td></td>');
        }
        col._cl = editor;
        var el = _Form.createEditor(col.editor, col.name, undefined, col, this);
        this._editors[col.name] = el;
        var ign = typeof (el._ig) == 'undefined' ? true : el._ig;
        var nel = el._rf || el;
        el = el._el || el;
        if (nel && nel != 'x') {
            if (col.vtype)
                nel.attr('vtype', col.vtype);
            if (col.max)
                nel.attr('max', col.max);
            if (col.disabled)
                nel.attr('disabled', true);
            if (col.readOnly)
                nel.attr('readOnly', true);
            if (col.defVal)
                nel.val(col.defVal);
        }

        if (col.lw)
            label.attr('width', col.lw);
        if (col.ew)
            editor.attr('width', col.ew);
        if (col.valign) {
            label.attr('valign', col.valign);
            editor.attr('valign', col.valign);
        }
        if (ign)
            editor.append(el)
        row.append(label);
        row.append(editor);
        return editor;
    },
    renderTo: function (rto) {
        if (typeof (rto) == 'string')
            rto = $(rto);
        rto.append(this.el);
    },
    replaceToSel: function (itm) {
        var el = itm[0];
        var tag = el.tagName.toLowerCase();
        itm = this.reToSelect(itm)
        el = itm[0];
        return el;
    },
    resetOptions: function (el, d, cb) {
        el.options.length = 0;
        for (var i = 0; i < d.length; i++) {
            var dd = d[i];
            var c, n;
            if (typeof (dd) == 'string') {
                c = n = dd;
            } else {
                c = dd.code;
                n = dd.name;
            }
            var op = new Option(n, c);
            el.options[el.options.length] = op;
        }
        if (cb)
            el.onchange = function (e) {
                cb(el)
            };
    },
    reToSelect: function (el) {
        var p = el.parent();
        var name = el.attr('name');
        var id = el.attr('id');
        var vtype = el.attr('vtype');
        var vl = el.val();
        p.empty();
        var col = {
            editor: 'select',
            max: 256,
            name: name,
            width: '450px'
        };
        if (vtype)
            col.vtype = vtype;
        if (vl)
            col.defVal = vl;
        if (id)
            col.id = id;
        var el = _Form.createEditor(col.editor, col.name, undefined, col, this);
        if (col.vtype) {
            el.attr('vtype', col.vtype);
            if (col.max)
                el.attr('max', col.max);
        }
        p.append(el);
        return el;
    },
    setReadOnly: function () {
        var fields = _Form.getFormFields(this.formEl);
        if (fields) {
            for (var name in fields) {
                var itm = fields[name];
                if (itm) {
                    var p = itm.parent();
                    var vl = itm.val();
                    // p.text(vl);
                    itm.remove();
                    p.append(vl);
                }
            }
        }
    },
    setReadOnlyx: function () {
        var fields = _Form.getFormFields(this.formEl);
        if (fields) {
            for (var name in fields) {
                var itm = fields[name];
                if (itm)
                    itm.attr('disabled', true);
            }
        }
    },
    setReadOnlyXY: function (nx) {
        if (typeof (nx) == 'undefined')
            return;
        var fields = _Form.getFormFields(this.formEl);
        var ar = nx.split(',');
        if (fields) {
            for (var i = 0; i < ar.length; i++) {
                var nm = ar[i];
                var itm = fields[nm];
                if (itm)
                    itm.attr('disabled', true);
            }
        }
    },
    resetEditorUi: function (paras, cfg) {
        cfg = cfg || {};
        var fields = _Form.getFormFields(this.formEl);
        for (var name in paras) {
            var itm = fields[name];
            var v = paras[name];
            if (itm) {
                var el = itm[0];
                var tag = el.tagName.toLowerCase();
                var tpo = typeof (v);
                if (tpo == 'undefined')
                    continue;
                if (tag == 'select') {
                    if (tpo == 'string')
                        v = [ v ];
                    this.resetOptions(el, v, cfg[name]);
                } else {
                    if (tpo == 'string')
                        itm.val(v);
                    else if (v.length > 0) {
                        if (v.length > 1) {
                            el = this.replaceToSel(itm);
                            this.resetOptions(el, v, cfg[name]);
                        } else
                            itm.val(v[0]);
                    } else
                        itm.val('');
                }
            }
        }
    },
    setParameters: function (paras) {
        var fields = _Form.getFormFields(this.formEl);
        if (fields) {
            for (var name in paras) {
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
                    } else
                        itm.val(v);
                }
            }
        }
    },
    setParas: function (paras) {
        var fields = this._editors;
        if (fields) {
            for (var name in paras) {
                var itm = fields[name];
                if (itm) {
                    var v = paras[name];
                    if (itm.set) {
                        itm.set(v);
                        return;
                    }
                    var el = itm;
                    if (itm._rf)
                        el = itm._rf;
                    if (el.get(0).type == 'checkbox') {
                        if (v == '1' || v == 1) {
                            el.val('1');
                            el.attr('checked', true);
                        } else {
                            el.val('0');
                            el.attr('checked', false);
                        }
                    } else
                        el.val(v);
                }
            }
        }
    },
    disableEditor: function () {
        var fields = this._editors;
        if (fields) {
            for (var name in fields) {
                var itm = fields[name];
                if (itm.fb)
                    itm.fb();
                else if (itm.attr)
                    itm.attr('disabled', true);
            }
        }
    },
    getFields: function () {
        return _Form.getFormFields(this.formEl);
    },
    getParas: function (strs) {
        return _Form.getFormParas(this.formEl, strs);
    },
    getParameters: function () {
        var fields = _Form.getFormFields(this.formEl);
        if (fields) {
            var paras = {};
            for (var name in fields) {
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
    bindHandler: function (func) {
        this._postHandler = func;
        /** for search */
        if (this._actEl) {
            this._actEl.click(function (evt) {
                evt.stopPropagation();
                evt.preventDefault();
                func();
            });
        }
    },
    submit: function () {
        var formDom = _Form.getFormDom(this.formEl);
        if (formDom)
            formDom.submit();
    },
    reset: function () {
        var formDom = _Form.getFormDom(this.formEl);
        if (formDom)
            formDom.reset();
    },
    ajaxSubmit: function (func) {
        var url = this.url;
        var eFn = this.eventFn;
        var data = this.getParameters();
        if (this._editActive) {
            // url = url + '/' + this._editRowId;
            data._id = this._editRowId;
            data._method = 'PUT';
        }
        if (this._editActive) {
            if (eFn)
                eFn(data);
            Common.post(url, data, func);
        } else {
            if (eFn)
                eFn(data);
            Common.post(url, data, func);
        }
    }
};

var _Validator = function (form) {
    this._form = form;
    this._feilds = _Form.getFormFields(form);
    this._bind();
};
_Validator.vTypes = {
    requiredMsg: "输入的值不能为空",
    required: function (value, field) {
        var fg = (value != '');
        return fg;
    },
    maxMsg: "输入的值不能超过长度限制:{0}",
    maxArg: 'max',
    max: function (value, el) {
        var fg = true;
        var mx = parseInt(el.attr('max'));
        if (mx) {
            fg = (value.length <= mx);
        }
        return fg;
    },
    numMsg: "输入的值不是数字",
    num: function (value, el) {
        if (value == '')
            return true;
        if (/^(-|\+)?\d+$/.test(value)) {
            return true;
        }
        return false;
    },
    numxMsg: "输入的值不是数字",
    numx: function (value, el) {
        if (/^(0|[1-9][0-9]*)$/.test(value)) {
            return true;
        }
        return false;
    },
    chinMsg: '不能有汉字',
    chin: function (value, el) {
        var reg = /[^\x00-\xff]/g;
        if (reg.test(value) == true)
            return false;
        return true;
    },
    mailMsg: 'mail邮箱不正确',
    mail: function (value, el) {
        var reg = /^[\w\-\.]+@[\w\-\.]+(\.\w+)+$/;
        return reg.test(value);
    },
    phoneMsg: '电话格式不正确',
    phone: function (value, el) {
        var mobile = /^(((13[0-9]{1})|(15[0-9]{1}))+\d{8})$/;
        var tel = /^\d{3,4}-?\d{7,9}$/;
        return (tel.test(value) || mobile.test(value));
    },
    mobileMsg: '手机号码格式不正确',
    mobile: function (value, el) {
        var mobile = /^(((13[0-9]{1})|(15[0-9]{1}))+\d{8})$/;
        return mobile.test(value);
    },
    dateMsg: "输入的值不能为空",
    date: function (value, el) {
        return value != '';
    },
    pwdMsg: "两次密码输入不对！",
    pwd: function (value, el) {
        var ref = el.attr('ref');
        if (ref)
            ref = $(ref);
        if (ref)
            return value == ref.val();
        return false;
    },
    periodMsg: "设置的数字不合理！",
    period: function (value, el) {
        var ref = el.attr('ref');
        if (ref)
            ref = $(ref);
        if (ref) {
            return value == ref.val();
            var vx = ref.val();
            if (vx < 3)
                return parseInt(value) <= 60;
            else if (vx == 3)
                return parseInt(value) <= 24;
        }
        return true;
    },
    emailMsg: "邮箱输入错误！例如：al@163.com",
    email: function (value, el) {
        var reg = /^(?:[a-z\d]+[_\-\+\.]?)*[a-z\d]+@(?:([a-z\d]+\-?)*[a-z\d]+\.)+([a-z]{2,})+$/i;
        return reg.test(value);
    }
};
_Validator.prototype = {
    _tip: null,
    err: function (k, msg) {
        var fs = this._feilds;
        var el = fs[k];
        this.error(el, msg);
        el[0].focus();
    },
    error: function (el, msg) {
        if (el.length == 0)
            return;
        el.css('border-color', 'red')
        el.attr('_er', 't');
        if (!this._tip) {
            this._tip = new _ToolTip();
            this._tip._create();
        }
        this._tip.show(el);
        this._tip.message(msg);
        // alert(msg);
        this._tip.oEl = el;
    },
    reset: function (el) {
        el.css('border-color', '');
        el.attr('_er', undefined);
        if (this._tip) {
            this._tip.oEl.attr('_er', undefined);
            this._tip.hide();
        }
    },
    check: function (e) {
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
    _split: function (s) {
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
    doCheck: function (type, ele) {
        var ts = this._split(type);
        var vts = _Validator.vTypes;
        var _slf = this;

        var val = ele.val();
        for (var i = 0; i < ts.length; i++) {
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
    checkField: function (ele) {
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
    checkForm: function () {
        var fs = this._feilds;
        for (var k in fs) {
            if (!this.checkField(fs[k]))
                return false;
        }
        return true;
    },
    checkFields: function (fields) {
        var fs = fields;
        for (var k in fs) {
            if (!this.checkField(fs[k]))
                return false;
        }
        return true;
    },
    _bindCols: function (cols) {
        if (cols && cols.length) {
            var els = _Form.getFormFields(this._form);
            var _s = this;
            var ck = function (e) {
                e.stopPropagation();
                e.preventDefault();
                _s.checkField($(this));
            };
            for (var i = 0; i < cols.length; i++) {
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
    _bind: function () {
        var fs = this._feilds;
        var fm = this._form;
        var im;
        var _s = this;
        var ck = function (e) {
            e.stopPropagation();
            e.preventDefault();
            _s.checkField($(this));
        };
        for (var k in fs) {
            im = fs[k];
            var typ = im.attr('vtype');
            if (typ)
                im.blur(ck);
        }
        // _s.submit();
    },
    submit: function () {
        if (this.checkForm())
            this._form.submit();
    }
};
var _Create = function (o) {
    var no = function () {
        this.init.apply(this, arguments);
    };
    var _proto = no.prototype;
    for (var k in o)
        _proto[k] = o[k];
    return no;
};
var _Constructor = {
    init: function (cfg) {
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
    drawHeader: function (el) {
        var title
        if (this.cfg.title)
            title = this.cfg.title;
        else
            title = '详情信息';
        var head = $('<div class="modal-header"></div>');
        var closeBtn = $('<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>');
        this.titleEl = $('<h3 id="modal_header">' + title + '</h3>');
        head.append(closeBtn).append(this.titleEl);
        el.append(head);
        var _this = this;
        closeBtn.click(function () {
            if (_this.closed)
                _this.close();
            else
                _this.hide();
        });
        this.headerEl = head;
    },
    drawBody: function (el) {
        var bd = $('<div class="modal-body"></div>');
        this.modelBody = $('<div></div>');
        bd.append(this.modelBody);
        el.append(bd);
    },
    drawFooter: function (el) {
        var footer = $('<div class="modal-footer"></div>');
        var btns = this.getButtons();
        btns = btns || {};
        // btn-primary
        var okBtn = $('<button class="btn btn-primary">确定</button>');
        var cancelBtn = $('<button class="btn">取消</button>');
        var _this = this;
        var _hide = function () {
            if (_this.closed)
                _this.close();
            else
                _this.hide();
        };
        if (btns.ok)
            okBtn.click(function () {
                btns.ok(_this);
            });
        else
            okBtn.click(_hide);
        if (btns.cancel)
            cancelBtn.click(function () {
                btns.cancel(_this);
            });
        else
            cancelBtn.click(_hide);

        footer.append(cancelBtn).append(okBtn);
        el.append(footer);
    },
    setTitle: function (t) {
        if (this.titleEl)
            this.titleEl.text(t);
    },
    getButtons: function () {
        return undefined;
    },
    draw: function () {
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
        if (this.drawOther)
            this.drawOther(this.el);
        this.drawFooter(this.el);
    },
    render: function (html) {
        this.modelBody.empty();
        this.modelBody.append(html);
    },
    show: function () {
        this.el.modal({
            backdrop: true,
            keyboard: true,
            show: true
        });
        if (this.afterShow)
            this.afterShow();
    },
    hide: function () {
        this.el.modal("hide");
    },
    close: function () {
        this.el.modal("hide");
        this.remove();
    },
    remove: function () {
        this.el.remove();
    }
};

var _Alert = _Create(_Constructor);

$.extend(_Alert.prototype, _BaseDlg.prototype, {
    postInit: function (cfg) {
        if (cfg.ok)
            this.btns = {
                ok: function (dlg) {
                    cfg.ok(dlg);
                    dlg.close();
                }
            };
    },
    getButtons: function () {
        return this.btns;
    }
});

var _Dialog = _Create(_Constructor);

$.extend(_Dialog.prototype, _BaseDlg.prototype, {
    _form: null,
    title: null,
    _idf: 'id',
    colspan: 2,
    _btns: {
        'ok': function (dlg) {
            if (dlg._form && dlg._validator.checkForm()) {
                dlg._form.ajaxSubmit(function (s) {
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
    postInit: function (cfg) {
        this.cols = cfg.cols;
        this.url = cfg.url;
        if (cfg.colspan)
            this.colspan = cfg.colspan;
        this._btns.cancel = cfg.cancel;
        if (cfg.submit)
            this._btns.ok = cfg.submit;
        if (cfg.doSub)
            this._btns.doSub = cfg.doSub;
        if (cfg.doAuth)
            this._btns.doAuth = cfg.doAuth;
        if (cfg.doSyn)
            this._btns.doSyn = cfg.doSyn;
        if (cfg._idf)
            this._idf = cfg._idf;
        if (cfg.footers)
            this.footers = cfg.footers;
    },
    resetUi: function (da, cb) {
        if (da)
            this._form.resetEditorUi(da, cb);
    },
    render: function (data, fn) {
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
            data._id = data[this._idf];
            if (data._id)
                this._form.editActive(data._id);
            this._form.setParameters(data);
        } else {
            this._form.clearSet();
        }
        if (fn)
            this._form.eventFn = fn;
    },
    afterShow: function () {
        if (this._form) {
            var h = this._form.tableEl.width() + 150;
            this.el.css({
                width: h + 'px'
            });
        }
    },
    getButtons: function () {
        return this._btns;
    }
});

var _Panel = _Create(_Constructor);

$.extend(_Panel.prototype, _Dialog.prototype, {
    colspan: 2,
    _redirectUrl: undefined,
    _footerEl: undefined,
    footers: {},
    _btns: {
        'ok': function (dlg) {
            var fields = dlg._form.getFields();
            if (dlg._form && dlg._validator.checkFields(fields)) {
                dlg._form.ajaxSubmit(function (s) {
                    if (s.code != 0) {
                        alert(s.msg);
                        return;
                    }
                    if (dlg._redirectUrl) {
                        self.location = dlg._redirectUrl;
                        return;
                    } else if (dlg.afterSubmit) {
                        dlg.afterSubmit();
                        return;
                    } else {
                        alert(s.msg);
                    }
                    dlg.hide();
                    if (dlg._ui)
                        dlg._ui.grid.reload();
                });
            }
        }
    },
    draw: function () {
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
    render: function (data, fn) {
        if (this._form == null) {
            this._form = new _Form('#modalForm', this.cols, this.url, false);
            this._form.colspan = this.colspan;
            this._form.title = this.title;
            this._form.draw();
            if (this.modelBody)
                this._form.renderTo(this.modelBody);
        }
        if (data) {
            data._id = data[this._idf];
            if (data._id)
                this._form.editActive(data._id);
            if (this._disable)
                this._form.setParas(data);
            else
                this._form.setParameters(data);
        } else {
            this._form.clearSet();
        }
        if (fn)
            this._form.eventFn = fn;
    },
    endDraw: function () {
        if (this.drawOther)
            this.drawOther(this.el);
        this.drawFooter(this.el);
        this._validator = new _Validator(this._form.formEl);
        this._form._validator = this._validator;
    },
    addRow: function (d) {
        this._form.addRow(d);
    },
    appendCols: function (cols) {
        this._form.appendCols(cols);
    },
    setReadOnly: function (nx) {
        // this._form.setReadOnlyx();
        if (nx)
            this._form.setReadOnlyXY(nx);
        else
            this._form.disableEditor();
    },
    clearFooter: function () {
        if (this._footerEl)
            this._footerEl.remove();
    },
    clearExtra: function () {
        this._form.clearExtra();
    },
    appendTab: function (cols, title, cs) {
        this._form.appendTab(cols, title, cs);
    },
    _rooms: {},
    appendExItem: function (cols, title, cs) {
        this._rooms = this._form.appendExItem(cols, title, cs);
    },
    appendOtItem: function (cols, title, cs) {
        this._form.appendOtItem(cols, title, cs);
    },
    appendTabItms: function (idx, cols, cs) {
        this._form.appendTabItms(idx, cols);
    },
    drawBody: function (el) {
        this.modelBody = $('<div></div>');
        // bd.append(this.modelBody);
        el.append(this.modelBody);
        // this.modelBody = el;
    },
    drawFooter: function (gel) {
        // var footer = $('<div class="modal-footer"></div>');
        var btns = this.getButtons();
        var cfg = this.footers;
        btns = btns || {};
        if (btns.ok == 'x')
            return;
        var str = '保存';
        if (cfg.okLabel)
            str = cfg.okLabel;
        // btn-primary
        var okBtn = $('<button class="btn btn-primary">' + str + '</button>');
        var cancelBtn = $('<button class="btn">清除</button>');
        var _this = this;
        var reset = function () {
            _this._form.reset();
        };
        if (cfg.ok)
            btns.ok = cfg.ok;
        if (btns.ok)
            okBtn.click(function () {
                btns.ok(_this);
            });
        else
            okBtn.click(reset);
        if (btns.cancel)
            cancelBtn.click(function () {
                btns.cancel(_this);
            });
        else
            cancelBtn.click(reset);

        var el = $('<div></div>');
        this._footerEl = el;
        el.append(cancelBtn).append("&nbsp;&nbsp;").append(okBtn);
        gel.append(el);

        if (cfg.btns) {
            var bns = cfg.btns;
            for (var i = 0; i < bns.length; i++) {
                var bn = bns[i];
                var sbBtn = $('<button class="btn btn-primary">' + bn.label + '</button>');
                el.append("&nbsp;&nbsp;").append(sbBtn);
                if (bn.func) {
                    sbBtn.click(function () {
                        bn.func(_this);
                    });
                }
            }
        }
        // el.append(footer);
    }
});

var _DetailDlg = _Create(_Constructor);
var _DPrototype = {
    postInit: function (cfg) {
        this.cols = cfg.cols;
        this.url = cfg.url;
    },
    drawFooter: function (el) {
        var footer = $('<div class="modal-footer"></div>');
        var okBtn = $('<button class="btn btn-primary">确定</button>');
        var _this = this;
        var _hide = function () {
            _this.hide();
        };
        okBtn.click(_hide);
        footer.append(okBtn);
        el.append(footer);
    },
    render: function (data) {
        this.modelBody.empty();
        this.modelBody.append(this.createTable(this.cols, data,
            this.cfg.detailRender));
    },
    loadx: function (url) {
        this.modelBody.empty();
        this.modelBody.append(Common.loadingHtml);
        if (url) {
            var _this = this;
            Common.ajax(url, null, function (d) {
                if (d) {
                    _this.modelBody.empty();
                    _this.modelBody.append(_this.createTable(_this.cols, d,
                        _this.cfg.detailRender));
                }
            });
        }
    },
    drawDirect: function (url, el, colspan) {
        if (typeof (el) == 'string')
            el = $(el);
        el.append(Common.loadingHtml);
        if (url) {
            var _this = this;
            Common.ajax(url, null, function (d) {
                if (d) {
                    el.empty();
                    el.append(_this.createTableRv(_this.cols, d,
                        _this.cfg.detailRender, colspan));
                }
            });
        }
    },
    load: function (data) {
        this.modelBody.empty();
        if (this.cfg.detailRenderCb && this.url) {
            var u = this.url + '/' + data[this._idf];
            var cb = this.cfg.detailRenderCb;
            var _el = this.modelBody;
            Common.ajax(u, null, function (d) {
                cb(_el, d);
            });
        }
    },
    createTable: function (cols, data, render) {
        var table = $('<table class="table table-striped table-bordered table-condensed table-hover"></table>');
        for (var i = 0; i < cols.length; i++) {
            var col = cols[i];
            if (col == 'x' || col == 'o')
                continue;
            if (col.noe)
                continue;
            table.append(this.createItem(col, data, render));
        }
        return table;
    },
    createTableRv: function (cols, data, render, colspan) {
        var table = $('<table class="table table-striped table-bordered table-condensed table-hover"></table>');
        var mx = typeof(colspan) == 'undefined' ? 1 : 3;
        var n = 0;
        var row;
        for (var i = 0; i < cols.length; i++) {
            var col = cols[i];
            if (col == 'x' || col == 'o')
                continue;
            if (col.noe)
                continue;
            if (n % mx == 0) {
                row = $('<tr></tr>');
                table.append(row);
            }
            this.createImx(col, data, render, row)
            n++;
        }
        if (n % mx !== 0) {
            var colspan = (mx - (n % mx)) * 2;
            if (row)
                row.append($('<td colspan="' + colspan + '">&nbsp;</td>'));
        }
        return table;
    },
    createImx: function (col, data, render, row) {
        var label = $('<td width="100" valign="top"><label>' + col.label
            + '：</label></td>');
        var cell = $('<td></td>');
        var v = '&nbsp;';
        var cname = col.name
        v = data[cname];
        if (render && render[cname]) {
            var rnm = render[cname];
            var rd = _Render[rnm];
            if (rd)
                v = rd(v, data);
        } else {
            var _r = col.render;
            if (typeof (_r) == 'string') {
                if (_r == '$')
                    _r = _Render._source(col.src);
                else
                    _r = _Render[_r];
            }
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
    },
    createItem: function (col, data, render) {
        var row = $('<tr></tr>');
        var label = $('<td width="100" valign="top"><label>' + col.label
            + '：</label></td>');
        var cell = $('<td></td>');
        var v = '&nbsp;';
        var cname = col.name
        v = data[cname];
        if (render && render[cname]) {
            var rnm = render[cname];
            var rd = _Render[rnm];
            if (rd)
                v = rd(v, data);
        } else {
            var _r = col.render;
            if (typeof (_r) == 'string') {
                if (_r == '$')
                    _r = _Render._source(col.src);
                else
                    _r = _Render[_r];
            }
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

var _PopMenu = function (id, items) {
    if (typeof (id) == 'string')
        this.el = $(id);
    else
        this.el = id;
    this.items = items;
};

_PopMenu.findRowEle = function (ele) {
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
    clickedRow: undefined,
    draw: function () {
        if (this.el.length == 0) {
            this.el = $('<div class="dropdown clearfix" style="display:none;position:absolute;"></div>');
            $(document.body).append(this.el);
        } else
            this.el.empty();

        this.ulEl = $('<ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu"></div>');
        this.ulEl.css({
            display: 'block',
            position: 'static',
            marginBottom: '5px'
        });
        this.el.append(this.ulEl);
    },
    appendItems: function (items) {
        if (items && items.length)
            for (var i = 0; i < items.length; i++) {
                var itm = items[i];
                if (itm == '-')
                    this.drawDivider(this.ulEl);
                else
                    this.drawItem(this.ulEl, itm);
            }
    },
    drawDivider: function (ulEl) {
        ulEl.append('<li class="divider"></li>');
    },
    drawItem: function (ulEl, item) {
        var li = $('<li></li>');
        var link = $('<a name="' + item.name + '">' + item.label + '</a>');
        var _this = this;
        link.click(function (evt) {
            evt.stopPropagation();
            evt.preventDefault();
            if (item.func)
                item.func(item.name, _this.clickedRow);
            _this.el.hide();
        });
        li.append(link);
        ulEl.append(li);
    },
    evtBind: function (el, f) {
        el.children('table:first-child').bind("contextmenu", f);
    },
    findEle: function (ele) {
        return _PopMenu.findRowEle(ele)
    },
    bind: function (toEl) {
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
        var f = function (e) {
            var ele = $(e.target);
            ele = _this.findEle(ele);
            if (ele == null)
                return;
            _this.clickedRow = ele;
            var nw = e.pageX;
            if (nw > pw)
                nw = pw;
            _this.el.css({
                display: 'block',
                left: nw,
                top: e.pageY
            });
            _this.el.show();
            return false;
        };
        this.evtBind(toEl, f);
        $(document).click(function (evt) {
            _this.el.hide();
        });
    }
};

var _CRUD_ = {
    opBar: [
        {
            name: '刷新',
            func: function (grid) {
                grid.reload();
            }
        },
//        {
//            name: '增加',
//            func: function (grid) {
//                if (this.url) {
//                    self.location = this.url;
//                    return;
//                }
//                if (grid._ui.modalDlg) {
//                    grid._ui.modalDlg.render();
//                    grid._ui.modalDlg.show();
//                    _CRUD_.afterOp();
//                }
//            }
//        },
        {
            name: '删除',
            func: function (grid) {
                var ids = _CRUD_.selectIds(grid);
                if (ids == '') {
                    _CRUD_.openAlert('未选取任何元素');
                    return;
                }
                _CRUD_.openAlert({
                    confirm: '确认删除该记录么?',
                    ok: function () {
                        _CRUD_.deleteRow(grid.url, ids, grid);
                    }
                });
            }
        }
    ],
    editRow: function (_ui, rid) {
        if (_ui.modalDlg) {
            var r = _ui.grid.findRecord(rid);
            r._id = rid;
            if (_ui.preEdit)
                _ui.preEdit(r);
            _ui.modalDlg.render(r);
            _ui.modalDlg.show();
            this.afterOp();
        }
    },
    selectFd: function (grid, f) {
        var tb = grid._bodyEl;
        var ids = [];
        tb.find('[name = _cgroup]:checkbox').each(function () {
            var el = $(this);
            var n = el.attr('checked');
            if (n)
                ids[ids.length] = el.attr('_id');
        });
        return grid.getRows(ids, f);
    },
    selectRows: function (grid) {
        var tb = grid._bodyEl;
        var ids = [];
        tb.find('[name = _cgroup]:checkbox').each(function () {
            var el = $(this);
            var n = el.attr('checked');
            if (n)
                ids[ids.length] = el.attr('_id');
        });
        return grid.getSelectedRows(ids);
    },
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
    selectIds: function (grid) {
        var tb = grid._bodyEl;
        var ids = '';
        tb.find('[name = _cgroup]:checkbox').each(function () {
            var el = $(this);
            var n = el.attr('checked');
            if (n)
                ids += ',' + el.attr('_id');
        });
        if (ids != '')
            ids = ids.substring(1);
        return ids;
    },
    selectRadioRows: function (grid) {
        var tb = grid._bodyEl;
        var ids = [];
        tb.find('[name = _cgroup]:radio').each(function () {
            var el = $(this);
            var n = el.attr('checked');
            if (n)
                ids[ids.length] = el.attr('_id');
        });
        return grid.getSelectedRows(ids);
    },
    selectRIds: function (grid) {
        var tb = grid._bodyEl;
        var ids = '';
        tb.find('[name = _cgroup]:radio').each(function () {
            var el = $(this);
            var n = el.attr('checked');
            if (n)
                ids += ',' + el.attr('_id');
        });
        if (ids != '')
            ids = ids.substring(1);
        return ids;
    },
    selectRow: function (grid, fg) {
        var tb = grid._bodyEl;
        if (fg == undefined)
            fg = false;
        else
            fg = true;
        tb.find('[name = _cgroup]:checkbox').each(function () {
            $(this).attr('checked', fg);
        });
    },
    createRadioCol: function (grid, ui, f) {
        return {
            _create: function () {
                return $('<label>选择</label>');
            },
            noe: true,
            _st: 'o',
            width: '30px',
            disabled: f,
            idf: true,
            render: _Render['radio']
        };
    },
    createCboxCol: function (grid, ui, f) {
        return {
            _create: function () {
                var el = $('<input type="checkbox" name="_cx" >');
                if (f)
                    el.attr('disabled', true);
                el.click(function () {
                    if (grid)
                        _CRUD_.selectRow(grid, el.attr('checked'));
                    else
                        _CRUD_.selectRow(ui.grid, el.attr('checked'));
                });
                return el;
            },
            noe: true,
            _st: 'x',
            width: '30px',
            disabled: f,
            idf: true,
            render: _Render['cbox']
        };
    },
    buildRender: function (cols, _ui) {
        for (var i = 0; i < cols.length; i++) {
            var col = cols[i];
            if (typeof (col) == 'string' && _ui) {
                switch (col) {
                    case 'x':
                        col = _CRUD_.createCboxCol(_ui.grid, _ui, false);
                        break;
                    case 'X':
                        col = _CRUD_.createCboxCol(_ui.grid, _ui, true);
                        break;
                    case 'o':
                        col = _CRUD_.createRadioCol(_ui.grid, _ui, false);
                        break;
                    case 'O':
                        col = _CRUD_.createRadioCol(_ui.grid, _ui, true);
                        break;
                }
                cols[i] = col;
            } else if (typeof (col.render) == 'string') {
                col._rdr = col.render;
                if (col.render == '$')
                    col.render = _Render._source(col.src);
                else
                    col.render = _Render[col.render];
            }
        }
        return cols;
    },
    renderTable: function (cfg) {
        var cols = cfg.cols, url = cfg.url;
        var _ui = {
            cols: cols,
            url: url,
            preEdit: cfg.preEdit
        };
        cfg.opBar = cfg.opBar || undefined;
        var _dlgCfg = {
            cols: this.buildRender(cols, _ui),
            url: url,
            _idf: cfg.idField,
            cancel: cfg.cancel,
            _ui: _ui
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
        var rowDblclick = function () {
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
                    if (_ui.preEdit)
                        _ui.preEdit(r);
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
        var lmt = cfg.limit || 20;
        var bbr = {
            limit: lmt,
            stepSize: 5
        };
        if (typeof (cfg.stepSize) == 'number')
            bbr.stepSize = cfg.stepSize;
        var bar = typeof (cfg.pBar);
        if (bar != 'undefined') {
            if (bar == 'string')
                bbr.top = cfg.pBar;
            else if (cfg.pBar > 0) {
                if (cfg.pBar == 1)
                    bbr.top = '#pageBar';
                else if (cfg.pBar == 2) {
                    bbr.top = '#pageBar';
                    bbr.bottom = '#pageBar2';
                } else
                    bbr.bottom = '#pageBar2';
            }
        } else {
            bbr.top = '#pageBar';
            bbr.bottom = '#pageBar2';
        }
        var tid = cfg.id || '#grid';
        var page = new LPage({
            id: tid,
            url: url,
            idField: cfg.idField,
            pageBar: bbr,
            maskEnable: mak,
            maskBd: cfg.maskBd,
            ajaxMethod: 'POST',
            cols: cols,
            opBar: cfg.opBar,
            beforeLoad: function (cfg) {
                cfg._method = 'GET';
                if (_ui.searchForm) {
                    var paras = _ui.searchForm.getParameters();
                    for (var k in paras)
                        cfg[k] = paras[k];
                }
            },
            onLoaded: function (data) {
            },
            rowDblclick: rowDblclick,
            rowSelect: cfg.rowSelect,
            errorLoad: Common.errorLoad
        });
        _ui.grid = page;
        page._ui = _ui;
        if (_ui.searchForm) {
            var doSearch = function () {
                page.clearLoad(1);
            };
            _ui.searchForm.bindHandler(doSearch);
        }
        if (_ui.ctxMenu) {
            var clk = function (name, row) {
                _CRUD_.ctxMenuClick(name, row, _ui);
            };
            var items = [
                {
                    name: 'refresh',
                    label: '刷新表格',
                    func: clk
                },
                {
                    name: 'detail',
                    label: '记录详情',
                    func: clk
                },
                '-',
                {
                    name: 'add',
                    label: '增加记录',
                    func: clk
                },
                {
                    name: 'edit',
                    label: '编辑记录',
                    func: clk
                },
                '-',
                {
                    name: 'delete',
                    label: '删除记录',
                    func: clk
                }
            ];
            _ui.ctxMenu.appendItems(items);
            _ui.ctxMenu.bind(page.containerEl);
        }
        return _ui;
    },
    ctxMenuClick: function (name, row, _ui) {
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
                    this.afterOp();
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
                    if (_ui.preEdit)
                        _ui.preEdit(r);
                    _ui.modalDlg.render(r);
                    _ui.modalDlg.show();
                    this.afterOp();
                }
                break;
            case 'delete':
                var rid = row.attr('rowid');
                _CRUD_.openAlert({
                    confirm: '确认删除该记录么?',
                    ok: function () {
                        _CRUD_.deleteRow(_ui.url, rid, _ui.grid);
                    }
                });
                break;
        }
    },
    afterOp: function () {

    },
    deleteRow: function (url, id, grid) {
        Common.post(url, {
            id: id,
            _method: 'DELETE'
        }, function (d) {
            if (d.code == 0)
                grid.reload();
            else
                _CRUD_.openAlert('&nbsp;&nbsp;<font color="red">' + d.msg
                    + '</font>');
        });
    },
    drawModalDialog: function (cfg) {
        var dlg = new _Dialog(cfg);
        dlg.draw();
        return dlg;
    },
    drawSearchForm: function (id, cols, url) {
        var fg = false;
        for (var i = 0; i < cols.length; i++) {
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
    drawContextMenu: function () {
        var menu = new _PopMenu('#popMenu');
        menu.draw();
        return menu;
    },
    drawDetailDlg: function (cfg) {
        var dlg = new _DetailDlg(cfg);
        dlg.draw();
        return dlg;
    },
    openAlert: function (opts, fn) {
        var cfg;
        if (typeof (opts) == 'string')
            cfg = {
                confirm: opts,
                ok: fn
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
    appendToX: function (el, cols, data) {
        $(el).append(this.painTableX(cols, data));
    },
    appendTdX: function (row, colspan) {
        colspan = colspan * 2;
        var cel = $('<td align="left" colspan="' + colspan + '">&nbsp;</td>');
        row.append(cel);
    },
    painTableX: function (cols, data) {
        var row = undefined;
        var lastCell = undefined;
        var n = 0;
        var c = 3;
        var cc = c;
        // class="table table-striped table-bordered table-condensed
        // table-hover"
        var table = $('<table></table>');
        for (var i = 0; i < cols.length; i++) {
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
    painItemX: function (row, col, data) {
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
            var rd;
            if (_r == '$')
                rd = _Render._source(col.src);
            else
                rd = _Render[_r];
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
