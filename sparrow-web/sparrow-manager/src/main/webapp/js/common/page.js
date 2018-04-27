var LPage = function (cfg) {
    cfg = cfg || {};

    this.tableEle = undefined;

    if (cfg.id) {
        this.containerEl = $(cfg.id);
        if (this.containerEl.length == 0)
            this.containerEl = $('<div id="' + cfg.id + '"></div>');
    } else
        this.containerEl = $('<div id="gridGx"></div>');

    this.pBarCfg = cfg.pageBar || {};
    this.opBar = cfg.opBar || [];

    if (typeof (this.pBarCfg.limit) == 'number')
        this.limit = this.pBarCfg.limit;
    else
        this.limit = 20;
    if (typeof (this.pBarCfg.page) == 'number')
        this.page = this.pBarCfg.page;
    else
        this.page = 1;
    if (typeof (cfg.total) == 'number')
        this.total = total;
    else
        this.total = -1;
    if (cfg.idFeild)
        this.idFeild = cfg.idFeild;
    else
        this.idFeild = 'id';
    this.url = cfg.url;
    this.maskEnable = cfg.maskEnable;
    this.config = cfg;
    if (cfg.ajaxMethod)
        this._ajaxMethod = cfg.ajaxMethod;
    else
        this._ajaxMethod = 'POST';
    this.topPageBar = null;
    this.bottomPageBar = null;
    if (typeof (cfg.showHeader) == 'undefined')
        this.showHeader = true;
    else
        this.showHeader = cfg.showHeader;
    this.drawPage();
};

LPage.unMask = function () {
    window.setTimeout(function () {
        $('#loadingMask').remove();
    }, 100);
};
LPage.mask1 = function (el, len) {
    el.empty();
    var defImg = '/app/img/ajax-loader.gif';
    var row = $('<tr></tr>');
    var td = $('<td colspan="' + len + '" valign="middle"><img src="' + defImg
        + '" />&nbsp;加载中...</td>');
    row.append(td);
    el.append(row);
};
LPage.mask = function (el) {
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
        + "px;background:#E0ECFF;opacity:0.6;filter:alpha(opacity=60);'>"
        + "<span style='position: relative; left:"
        + c_left
        + "px;top:"
        + c_top
        + "px;width:180px;height:16px;padding:12px 5px 10px 30px;"
        + " background:#fff no-repeat scroll 5px 10px;border:2px solid #ccc;color:#000;'>Loading..."
        + "</span></div>";
    $(document.body).append(_html);
};
LPage.correctXml = function (dom) {
    if ($.browser.mozilla) {
        var oSerializer = new XMLSerializer();
        var sXml = oSerializer.serializeToString(dom, "text/xml");
        return sXml;
    }
    return dom.xml;
};
LPage.correctHtml = function (str) {
    if (str && str.indexOf('<?xml') != -1) {
        var idx = str.indexOf('>');
        if (idx != -1)
            return str.substring(idx + 1);
        else
            return str;
    }
    return str;
};
LPage.cutAClick = function (ele) {
    ele.find('a').each(function () {
        var el = $(this)
        alert(el.attr('class'))
    })
};

LPage.prototype = {
    _headerEl: null,
    _bodyEl: null,
    _initialized: false,
    _curData: undefined,
    _selData: [],
    _ldData: undefined,
    _selVal: undefined,
    reload: function (page) {
        if (typeof (page) == "number")
            this.page = page;
        this.load(this.url);
    },
    getRawData: function (id) {
        var fs = this._ldData;
        for (var i = 0; i < fs.length; i++) {
            var d = fs[i];
            if (d.id == id)
                return d;
        }
        return null;
    },
    createPageBar: function (pageBarEl) {
        var lp = this;
        var pageCut = function (p, limit) {
            lp.loadPage(p, limit);
        };
        var s = 6;
        if (typeof (this.pBarCfg.stepSize) == 'number')
            s = this.pBarCfg.stepSize;
        var pageBar = new LPageBar({
            el: pageBarEl,
            limit: this.limit,
            stepSize: s,
            onPageCut: pageCut
        });
        return pageBar;
    },
    createIm: function (it) {
        var en = $('<a class="btn btn-primary">' + it.name + '</a>');
        var grid = this;
        if (it.func)
            en.click(function () {
                it.func(grid);
            });
        return en;
    },
    createTableCell: function (row, el) {
        var cel = $('<td valign="top"></td>');
        row.append(cel);
        cel.append(el);
    },
    createTableRow: function (el1, el2) {
        var tb = $('<table border="0" cellpadding="0" cellspacing="0"  style="font-size:10;"></table>');
        var tbd = $('<tbody></tbody>');
        tb.append(tbd);
        var row = $('<tr></tr>');
        tbd.append(row);
        this.createTableCell(row, el1);
        this.createTableCell(row, el2);
        return tb;
    },
    drawPageBar: function (total, page) {
        if (total == -1)
            return;
        if (this.topPageBar == null) {
            if (this.pBarCfg.top) {
                this.topPageBar = this.createPageBar(this.pBarCfg.top);
                if (this.opBar && this.opBar.length) {
                    var el = $('<div style="margin-top:0px;padding-top:0px;"></div>');
                    var obarEl = $('<div></div>');
                    el.append(this.createTableRow(this.topPageBar.pagerEl, obarEl));
                    obarEl.append('&nbsp;');
                    for (var i = 0; i < this.opBar.length; i++) {
                        obarEl.append('&nbsp;');
                        obarEl.append(this.createIm(this.opBar[i]));
                    }
                    this.containerEl.before(el);
                } else {
                    this.containerEl.before(this.topPageBar.pagerEl);
                }
            }
        }
        if (this.bottomPageBar == null) {
            if (this.pBarCfg.bottom) {
                this.bottomPageBar = this.createPageBar(this.pBarCfg.bottom);
                this.containerEl.after(this.bottomPageBar.pagerEl);
            }
        }
        if (this.topPageBar)
            this.topPageBar.drawPageBar(total, page);
        if (this.bottomPageBar)
            this.bottomPageBar.drawPageBar(total, page);
    },
    loadPage: function (p, limit) {
        this.page = p;
        this.limit = limit;
        this.load(this.url);
    },
    skipCol: function (col) {
        var t = col.hidden;
        if (typeof (t) == 'undefined')
            t = false;
        return t;
    },
    resetHeader: function (idx) {
        var n = this.config.cols[idx];
        var t = n._st;
        var el = n._hl;
        switch (t) {
            case 'x':
                el.attr('checked', false);
                break;
        }
    },
    drawPageHeader: function () {
        if (this._initialized)
            return;
        if (!this.showHeader)
            return;
        var head = this._headerEl.children().eq(0);
        if (this.config.cols) {
            var cols = this.config.cols;
            for (var i = 0; i < cols.length; i++) {
                var col = cols[i];
                if (this.skipCol(col))
                    continue;
                var th;
                if (col._create) {
                    th = $('<th></th>');
                    var erg = col._create();
                    col._hl = erg;
                    this.config.cols[i] = col;
                    th.append(erg);
                } else {
                    th = '<th>' + col.label + '</th>';
                    th = $(th);
                }
                // if (typeof (col.width) == 'number')
                // th.attr('width', col.width);
                if (col.width)
                    th.attr('width', col.width);
                col._el = th;
                head.append(th);
            }
        }
    },
    createEmptyRow: function (msg) {
        if (this.config.cols) {
            var l = this.config.cols.length;
            var row = $('<tr></tr>');
            var td = $('<td colspan="' + l + '">' + msg + '</td>');
            row.append(td);
            return row;
        }
    },
    drawPageBody: function (data) {
        if (!this._initialized)
            return;
        this._selData = [];
        this._ldData = data;
        this.resetHeader(0);
        var body = this._bodyEl;
        body.empty();
        if (data == null || data == undefined || data.length == 0) {
            var r = this.createEmptyRow('数据为空');
            if (r)
                body.append(r);
            return;
        }
        if (data.length) {
            for (var i = 0; i < data.length; i++) {
                var d = data[i];
                var idx = i + 1;
                var row = this.createRow(d, idx);
                if (row)
                    body.append(row);
            }
        }
    },
    setDefVal: function (v) {
        v = parseInt(v);
        this._selVal = v;
        var fs = this._selData;
        for (var i = 0; i < fs.length; i++) {
            var d = fs[i];
            if (d._id == v) {
                d.el.attr('checked', true);
                break;
            }
        }
    },
    createRow: function (data, idx) {
        if (this.config.cols) {
            var cols = this.config.cols;
            var row = $('<tr></tr>');
            for (var i = 0; i < cols.length; i++) {
                var col = cols[i];
                if (this.skipCol(col))
                    continue;
                var td = $('<td></td>');
                var v = data[col.name];
                if (col.render) {
                    var vl = v;
                    v = col.render(vl, data, idx, row, td);
                    if (col.wrap)
                        v = col.wrap(v, vl);
                    if (col._st) {
                        vl = data.id;
                        if (vl != undefined && vl == this._selVal)
                            v.attr('checked', true);
                        this._selData[this._selData.length] = {
                            _id: vl,
                            el: v
                        };
                    }
                }
                if (v == 0)
                    v = '0';
                if (v == undefined || v == '')
                    v = '&nbsp;'
                td.append(v);
                row.append(td);
            }
            if (this.config.rowDblclick) {
                row.attr('rowid', data[this.idFeild]);
                row.dblclick(this.config.rowDblclick);
            }
            return row;
        }
        return undefined;
    },
    drawPage: function () {
        if (this._initialized)
            return;
        if (this.tableEle == undefined || this.tableEle.length == 0) {
            this.tableEle = $('<table class="table table-striped table-bordered table-condensed table-hover"></table>');
            this.containerEl.append(this.tableEle);
        }
        if (this.showHeader) {
            var thead = this.tableEle.has('thead');
            if (thead.length == 0) {
                thead = $('<thead><tr></tr></thead>');
                this.tableEle.prepend(thead);
            }

            this._headerEl = thead;
            this.drawPageHeader();
        }
        var tbody = this.tableEle.has('tbody');
        if (tbody.length == 0) {
            tbody = $('<tbody></tbody>');
            this.tableEle.append(tbody);
        }
        this._bodyEl = tbody;
        if (this.url)
            this.load(this.url);
        this._initialized = true;
    },
    findRecord: function (id) {
        var d = this._curData;
        if (d && d.length) {
            for (var i = 0; i < d.length; i++) {
                var dl = d[i];
                if (dl[this.idFeild] == id)
                    return dl;
            }
        }
        return undefined;
    },
    dataLoad: function (data, cfg) {
        if (this.config.onLoaded)
            this.config.onLoaded(data);
        var total = data.total;
        // if (data.rows == null || data.rows == undefined
        // || data.rows.length < this.limit)
        // total = (cfg.page - 1) * this.limit + data.rows.length;
        this._curData = data.rows;
        if (data.total == -1)
            return;
        if (data.total == 0)
            cfg.page = 1;
        this.drawPageBar(total, cfg.page);
    },
    clearLoad: function () {
        this.page = 1;
        this.load(this.url);
    },
    appendRow: function (d) {
        var body = this._bodyEl;
        if (this._empMark) {
            this._empMark = false;
            body.empty();
        }
        var l = this._curData.length;
        var row = this.createRow(d, l);
        this._curData[l] = d;
        if (row)
            body.append(row);
    },
    _empMark: false,
    loadData: function (data) {
        this._curData = data;
        var body = this._bodyEl;
        body.empty();
        if (data == null || data == undefined || data.length == 0) {
            var r = this.createEmptyRow('数据为空');
            if (r)
                body.append(r);
            this._empMark = true;
            return;
        }
        if (data.length) {
            for (var i = 0; i < data.length; i++) {
                var d = data[i];
                var idx = i + 1;
                var row = this.createRow(d, idx);
                if (row)
                    body.append(row);
            }
        }
    },
    load: function (url) {
        var cfg = {};
        if (this.config.beforeLoad)
            this.config.beforeLoad(cfg);
        cfg.limit = this.limit;
        cfg.page = this.page;
        if (url) {
            var lp = this;
            if (lp.maskEnable) {
                if (lp.config.maskBd)
                    LPage.mask1(this._bodyEl, lp.config.cols.length);
                else
                    LPage.mask();
            }
            var reqCfg = {
                type: lp._ajaxMethod,
                url: url,
                cache: false,
                datatype: "json",
                data: cfg,
                error: function (xhr, status, err) {
                    if (lp.maskEnable)
                        LPage.unMask();
                    if (lp.config.errorLoad)
                        lp.config.errorLoad(xhr, status, err);
                    if (lp.onLoaded)
                        lp.onLoaded({
                            fg: true
                        });
                },
                success: function (data, status) {
                    try {
                        if (status == 'success') {
                            lp.drawPageBody(data.rows);
                            lp.dataLoad(data, cfg);
                        }
                    } catch (e) {
                        alert(e)
                    }
                    if (lp.maskEnable) {
                        if (lp.config.maskBd)
                            return;
                        else
                            LPage.unMask();
                    }
                }
            };
            if (lp._ajaxMethod == 'POST')
                reqCfg.contentType = "application/x-www-form-urlencoded;charset=UTF-8";
            $.ajax(reqCfg);
        }
    }
}

/**
 * PageBar
 */
var LPageBar = function (cfg) {
    cfg = cfg || {};
    var pageset = cfg.el || '#pageSet';
    if (typeof (pageset) == 'string')
        this.pagerEl = $(pageset);
    else {
        this.pagerEl = pageset;
        pageset = pageset.attr('id');
    }
    if (pageset.charAt(0) == '#')
        pageset = pageset.substring(1);
    if (this.pagerEl.length == 0)
        this.pagerEl = $('<div id="' + pageset
            + '" class="pagination" style="margin:10px 0;"></div>');
    if (typeof (cfg.stepSize) == 'number')
        this.stepSize = cfg.stepSize;
    else
        this.stepSize = 6;
    if (typeof (cfg.limit) == 'number')
        this.limit = cfg.limit;
    else
        this.limit = 20;
    if (typeof (cfg.page) == 'number')
        this.page = page;
    else
        this.page = 1;
    if (typeof (cfg.total) == 'number')
        this.total = total;
    else
        this.total = -1;
    this.records = 0;
    if (typeof (cfg.refreshPager) == 'boolean')
        this.refreshPager = cfg.refreshPager;
    else
        this.refreshPager = true;
    this.pageSteps = null;
    this.config = cfg;
};
LPageBar.prototype = {
    initialized: false,
    _firstEl: null,
    _lastEl: null,
    initPageBar: function () {
        if (this.initialized)
            return;
        var lp = this;
        var func = function (evt) {
            lp.handlePageCut(evt, $(this));
        };
        var pBar = this.pagerEl.find('ul');
        if (pBar.length == 0) {
            pBar = $('<ul></ul>');
            this.pagerEl.append(pBar);
        } else {
            this.initPageSteps();
            this.initialized = true;
            return;
        }
        var steps = [];
        var idx = 0;
        this._firstEl = this.drawBtn(pBar, "|<", 'f', func);
        var el = $('<li class="disabled"><a href="#" no="p">&lt;</a></li>');
        pBar.append(el);
        el = el.children('a');
        el.click(func);
        steps[idx] = el;
        for (var i = 0; i < this.stepSize; i++) {
            idx++;
            var nel = null;
            nel = $('<li class="disabled"><a href="#" no="' + idx + '">' + idx
                + '</a></li>');
            pBar.append(nel);
            nel = nel.children('a');
            nel.click(func);
            steps[idx] = nel;
        }
        idx++;
        el = $('<li class="disabled"><a href="#" no="n">&gt;</a></li>');
        pBar.append(el);
        el = el.children('a');
        el.click(func);
        steps[idx] = el;
        this._lastEl = this.drawBtn(pBar, ">|", 'l', func);
        this.pageSteps = steps;
        this.stepSize = this.pageSteps.length - 2;
        this.initialized = true;
    },
    drawBtn: function (pBar, text, v, fn) {
        var el = $('<li class="active"><a href="#" no="' + v + '">' + text
            + '</a></li>');
        pBar.append(el);
        el = el.children('a');
        el.click(fn);
        return el;
    },
    drawPageBar: function (rows, page) {
        if (!this.initialized)
            this.initPageBar();
        var lim = this.limit;
        if (typeof (rows) == 'number') {
            var t = parseInt(rows / lim);
            if (rows % lim != 0)
                t = t + 1;
            this.total = t;
            this.records = rows;
        }
        if (typeof (page) == 'number')
            this.page = page;
        // alert(rows+'-'+this.total+ ' - '+this.page)
        if (this.records < 0)
            return;
        this.renderPagination();
    },
    initPageSteps: function () {
        var lp = this;
        var func = function (evt) {
            lp.handlePageCut(evt, $(this));
        };
        var steps = [];
        $(".pagination ul>li>a").each(function () {
            var el = $(this);
            el.click([ el ], func);
            steps[steps.length] = el;
        })
        this.pageSteps = steps;
        this.stepSize = this.pageSteps.length - 2;
    },
    handlePageCut: function (evt, el) {
        evt.stopPropagation();
        evt.preventDefault();
        var liEl = el.parent();
        if (liEl.hasClass("disabled") || liEl.hasClass("active")) {
            return;
        } else {
            var p = el.text();
            var t = el.attr('no');
            if (t == 'p')
                p = this.page - 1;
            else if (t == "n")
                p = this.page + 1;
            else if (t == "f")
                p = 1;
            else if (t == "l")
                p = this.total;
            else
                p = parseInt(p)
            this.page = p;
            if (this.config.onPageCut)
                this.config.onPageCut(p, this.limit);
            // if (this.refreshPager)
            // this.renderPagination();
        }
    },
    setState: function (itm, active, disabled) {
        if (itm) {
            var p = itm.parent();
            p.removeClass("active disabled");
            p.removeClass("active")
            if (active)
                p.addClass("active");
            if (disabled)
                p.addClass('disabled');
        }
    },
    renderPagination: function () {
        var cur = this.page;
        var total = this.total;
        var bound = Math.floor(cur / this.stepSize);
        var from = 0, to = 0;
        var md = cur % this.stepSize;
        if (md > 0) {
            from = bound * this.stepSize + 1;
            to = from + this.stepSize - 1;
        } else if (md == 0) {
            from = cur - this.stepSize + 1;
            to = cur;
        } else {
            to = from * this.stepSize;
            from = to - this.stepSize + 1;
        }
        if (from < 0)
            from = 1;
        var max = to > total ? total : to;
        var j = 1;
        this.setState(this._firstEl, false, cur == 1);
        this.setState(this.pageSteps[0], false, cur == 1);
        this._lastEl.text();
        for (var i = 0; i < this.stepSize; i++) {
            var pItm = this.pageSteps[i + 1];
            pItm.text(from + i);
        }
        for (var ii = from; ii <= max; ii++) {
            var pItm = this.pageSteps[j];
            this.setState(pItm, cur == ii, false);
            j++;
        }
        if (to > max) {
            var ij = (max < 0) ? 0 : max;
            for (; ij < to; ij++) {
                var pItm = this.pageSteps[j];
                this.setState(pItm, false, true);
                j++;
            }
        }
        this.setState(this.pageSteps[j], false, cur >= total);
        this.setState(this._lastEl, false, cur >= total);
    }
}