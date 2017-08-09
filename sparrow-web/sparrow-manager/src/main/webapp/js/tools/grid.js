function TEditor() {
    this.url = "";
    this.dataRoot = "data";
    this.tableREF = null;
    this.columnDef = null;
    this.columns = null;
    this.haderShow = true;
    this.data = [];
    this.idField = "id";
    this._table = null;
    this._rendered = false;
    this._inited = false;
    this._desc = "Table Editor " + (TEditor.counter++);
};

TEditor._editRow = null;
TEditor._editCell = null;
TEditor.counter = 0;
TEditor._inputEditor = null;
TEditor._selectEditor = null;

TEditor.defaultStyle = "background-color: #F0EEE1;font-size: 12px;color: #363544;text-align:center;line-height:22px;";
TEditor.overStyle = "background-color:#D1C6A7;font-size: 12px;color:#FF6600;text-align:center;line-height:22px;cursor:hand;";
TEditor.outStyle = "background-color: #F0EEE1;font-size: 12px;color: #363544;text-align:center;line-height:22px;";
TEditor.clickStyle = "background-color:#477BC4;font-size: 12px;	color: #ffffff;font-family:\"宋体\";font-style: normal;	text-align:center;line-height:22px;";

TEditor.DEAULT_CELL_STYLE = "padding-right:3px;padding-left:3px;border-right-width:1px;border-bottom-width:1px;border-top-width:2px;border-left-width:2px;border-style: solid;border-top-color: #FFFFFF;border-right-color: #999900;border-bottom-color: #000000;border-left-color: #FFFFFF;";
TEditor.ROW_STYLE = "font-family:\"宋体\";font-size:12px;text-align:center;line-height:22px;background-color:#F0EEE1;";
TEditor.HEADER_CELL_STYLE = "font-size:13px;padding-right:2px;padding-left:2px;border-top-width:1px;border-right-width:1px;border-bottom-width:2px;border-left-width:1px;border-style:solid;border-top-color:#FFFFFF;border-right-color:#999900;border-bottom-color:#333333;border-left-color:#FFFFFF;font-weight:bold;margin-bottom:3px;margin-top:3px;background-color:#D5E6FA;line-height:27px;";

TEditor.createFunc = function (obj, strFunc, args) {
    if (!obj)
        obj = window;
    return function () {
        if (!args)
            args = arguments;
        obj[strFunc].apply(obj, args);
    }
};
TEditor.createEventFunc = function (obj, strFunc, args) {
    if (!obj)
        obj = window;
    return function (e) {
        e = (e) || window.event;
        newArgs = [ e ];
        if (args) {
            for (var i = 0; i < args.length; i++)
                newArgs[i + 1] = args[i];
        }
        obj[strFunc].apply(obj, newArgs);
    }
};
TEditor.renderCell = function (row, cell, column, obj, fn, gridRf) {
    if (cell == null)
        return;
    if (typeof (column.cellType) == 'undefined')
        column.cellType = 0;
    if (typeof (column.align) !== 'undefined')
        cell.align = column.align;
    if (typeof (column.valign) !== 'undefined')
        cell.valign = column.valign;
    if (column.cssText)
        cell.style.cssText = column.cssText;
    // if (typeof (column.cssText) == 'undefined')
    // column.cssText = TEditor.DEAULT_CELL_STYLE;
    // cell.style.cssText = column.cssText;

    var val = obj[column.field];
    var type = column.cellType;
    if (val === undefined) {
        val = column.defVal;
        obj[column.field] = val;
    }
    if (column.render)
        val = column.render(val, obj, column);
    if (val == '' || val == null || typeof (val) == 'undefined') {
        if (type == 0)
            val = "&nbsp;";
        else
            val = "";
    }
    if (column.extend)
        cell.setAttribute('class', 'ext-cell', 0);
    else if (column.className)
        cell.className = column.className;
    else
        cell.setAttribute('class', 'cell', 0);

    if (type == 0) {
        cell.innerHTML = val;
        if (typeof (column.editType) !== 'undefined') {
            var editRef = {
                grid: gridRf,
                data: obj,
                column: column,
                field: column.field,
                fn: fn
            };
            TEditor.fireCellEditor(cell, column.editType, editRef);
        }
        return;
    }

    var ele;
    if (type == 1) {
        ele = document.createElement("input");
        ele.type = "text";
        ele.onblur = function () {
            var v = ele.value;
            var _dat = obj;
            var fd = column.field;
            if (v === _dat[fd])
                return;
            _dat[fd] = v;
            obj._changed = true;
            // _dat._changed = true;
        };
        if (typeof (column.className) !== 'undefined')
            cell.className = column.className;
        ele.value = val;
    } else if (type == 2) {
        ele = document.createElement("input");
        ele.onclick = function () {
            var v = ele.checked;
            var _dat = obj;
            var fd = column.field;
            var ov = _dat[fd];
            if (typeof (ov) != 'undefined')
                if (v == ov)
                    return;
            _dat[fd] = v;
            obj._changed = true;
        };
        ele.type = "checkbox";
        ele.tabindex = "-1";

        if (val || column.field != 'search') {
            ele.setAttribute('checked', 'true', 0);
            ele.checked = "checked";
            ele.defaultChecked = true;
        }
        // ele.value = val;
        row.combox = ele;
        cell.appendChild(ele);
        return;
    } else if (type == 3) {
        ele = document.createElement("select");
        if (typeof (column.className) !== 'undefined')
            cell.className = column.className;
        ele.options[0] = new Option("请选择", "0");
    }
    if (typeof (column.disabled) !== 'undefined')
        ele.disabled = column.disabled;
    if (ele) {
        var w = column.width - 10;
        ele.style.cssText = 'width:' + w + 'px';
        cell.appendChild(ele);
    }
};
TEditor.fireCellEditor = function (cell, type, editRef, beforeFunc, validateFunc) {
    if (cell == null)
        return;
    if (type == 1) {
        cell.ondblclick = TEditor.createEventFunc(TEditor, "textEditCell",
            [ editRef ]);
    } else if (type == 2) {
        cell.ondblclick = TEditor.createEventFunc(TEditor, "optionsEditCell",
            [ editRef ]);
    }
};
TEditor.getCellValue = function (editRef) {
    var rowdata = editRef.data;
    var field = editRef.field;
    var v = rowdata[field];
    if (v)
        return v;
    else
        return '';
};
TEditor.getCellWidth = function (editRef) {
    var col = editRef.column;
    return col.width;
};
TEditor.setEditorWidth = function (editor, editRef) {
    var col = editRef.column;
    var w = col.width - 10;
    editor.style.cssText = "width:" + w + "px;";
};
TEditor.textEditCell = function (e, editRef) {
    e = (e) || window.event;
    var keyNumber = e.keyCode;
    var cell = e.srcElement || e.target;
    if (cell.tagName == "TD") {
        TEditor._editCell = cell;
        if (TEditor._inputEditor == null)
            TEditor.createInput(editRef);
        TEditor._inputEditor.value = TEditor.getCellValue(editRef);
        TEditor.setEditorWidth(TEditor._inputEditor, editRef);
        // TEditor._inputEditor.width = TEditor.getCellWidth(editRef) - 30;
        TEditor._inputEditor.editRef = editRef;
        cell.innerHTML = "";
        // cell.align='left';
        cell.appendChild(TEditor._inputEditor);
        TEditor._inputEditor.focus();
        TEditor._editRow = cell.parentNode;
    }
};

TEditor.optionsEditCell = function (e, editRef) {
    var cell = e.srcElement || e.target;
    if (cell.tagName == "TD") {
        TEditor._editCell = cell;
        editRef.oldValue = cell.innerHTML;
        if (TEditor._selectEditor == null)
            TEditor._selectEditor = TEditor.createSelect(editRef);
        if (typeof (editRef.column) !== 'undefined') {
            var columDef = editRef.column;
            if (typeof (columDef.editorCreated) == 'function')
                columDef.editorCreated(TEditor._selectEditor, editRef,
                    columDef.editType);
        }
        // if (cell.innerHTML == '&nbsp;')
        // TEditor._selectEditor.value = '';
        // else
        TEditor._selectEditor.value = TEditor.getCellValue(editRef);
        TEditor._selectEditor.editRef = editRef;
        TEditor.setEditorWidth(TEditor._selectEditor, editRef);
        // TEditor._selectEditor.width = TEditor.getCellWidth(editRef) - 10;
        // TEditor.setEditorWidth
        cell.innerHTML = "";
        cell.appendChild(TEditor._selectEditor);
        // cell.align='left';
        TEditor._selectEditor.focus();
        TEditor._editRow = cell.parentNode;
    }
};
TEditor.createInput = function (editRef) {
    var rInput = document.createElement("input");
    rInput.type = "text";
    rInput.onblur = TEditor.createEventFunc(TEditor, "inputLast");
    rInput.onkeyup = TEditor.createEventFunc(TEditor, "keyHandler");
    rInput.ondblclick = TEditor.cancelEvent;

    TEditor._inputEditor = rInput;
};
TEditor.createSelect = function () {
    var rSelect = document.createElement("select");
    rSelect.name = "tttys";
    rSelect.className = "testCs";
    rSelect.onblur = TEditor.createEventFunc(TEditor, "selectLast");
    rSelect.onkeyup = TEditor.createEventFunc(TEditor, "keyHandler");
    rSelect.onchange = TEditor.createEventFunc(TEditor, "selectChange");
    rSelect.ondblclick = TEditor.cancelEvent;
    return rSelect;
};
TEditor.inputLast = function (e) {
    var value = TEditor._inputEditor.value;
    try {
        e = (e) || window.event;
        var ele = e.srcElement || e.target;
        var f = true;
        var editRef = ele.editRef;
        var ov = TEditor.getCellValue(editRef);
        if (ov == value)
            f = false;
        if (editRef)
            editRef.fn(editRef.field, value);
        if (f) {
            var cst = TEditor._editCell.style.cssText;
            if (cst.indexOf('background-color: red;') == -1) {
                cst = cst + 'background-color: red;';
                TEditor._editCell.style.cssText = cst;
            }
        }
        if (TEditor._editCell) {
            TEditor._editCell.removeChild(ele);
            TEditor._editCell.appendChild(document.createTextNode(value));
        }
    } catch (e) {
        var s = "";
        for (var vr in e) {
            s += "," + vr + "=" + e[vr];
        }
        alert(s);
    }
};
TEditor.selectLast = function (e) {
    e = (e) || window.event;
    var ele = e.srcElement || e.target;
    if (typeof (TEditor._editCell) === 'undefined')
        return;
    if (ele.selectedIndex == 0) {
        TEditor._editCell.removeChild(ele);
        return;
    }
    var value = ele.value;
    var label = ele.options[ele.selectedIndex].text;
    TEditor._editCell.removeChild(ele);
    try {
        var editRef = ele.editRef;
        var f = true;
        var ov = TEditor.getCellValue(editRef);
        if (ov == value)
            f = false;
        if (editRef)
            editRef.fn(editRef.field, value);
        if (label == '')
            label = '&nbsp;';
        if (f) {
            var cst = TEditor._editCell.style.cssText;
            if (cst.indexOf('background-color: red;') == -1) {
                cst = cst + 'background-color: red;';
                TEditor._editCell.style.cssText = cst;
            }
        }
        TEditor._editCell.innerHTML = label;
    } catch (e) {
        var s = "";
        for (var vr in e) {
            s += "," + vr + "=" + e[vr];
        }
        alert(s);
    }
};
TEditor.selectChange = function (e) {
    if (typeof (TEditor._editCell) === 'undefined')
        return;
    var ele = TEditor._selectEditor;
    if (ele.selectedIndex == 0) {
        // TEditor._editCell.removeChild(ele);
        return;
    }
    var value = ele.value;
    var label = ele.options[ele.selectedIndex].text;
    var editRef = TEditor._selectEditor.editRef;
    TEditor._editCell.removeChild(TEditor._selectEditor);
    try {
        var f = true;
        var ov = TEditor.getCellValue(editRef);
        if (ov == value)
            f = false;
        if (value == "") {
            if (editRef.oldValue)
                TEditor._editCell.innerHTML = editRef.oldValue;
            return;
        }
        if (editRef)
            editRef.fn(editRef.field, value);
        if (f) {
            var cst = TEditor._editCell.style.cssText;
            if (cst.indexOf('background-color: red;') == -1) {
                cst = cst + 'background-color: red;';
                TEditor._editCell.style.cssText = cst;
            }
        }
        TEditor._editCell.innerHTML = label;
    } catch (e) {
        var s = "";
        for (var vr in e) {
            s += "," + vr + "=" + e[vr];
        }
        alert(s);
    }
};
TEditor.keyHandler = function (e) {
    e = (e) || window.event;
    var keyNumber = e.keyCode || e.which;
    var ele = e.srcElement || e.target;
    if (keyNumber == 13) {
        var editRef = ele.editRef;
        var value = ele.value;
        var f = true;
        var ov = TEditor.getCellValue(editRef);
        if (ov == value)
            f = false;
        if (value == "") {
            if (editRef.oldValue)
                TEditor._editCell.innerHTML = editRef.oldValue;
            return;
        }
        if (editRef)
            editRef.fn(editRef.field, value);
        if (f) {
            var cst = TEditor._editCell.style.cssText;
            if (cst.indexOf('background-color: red;') == -1) {
                cst = cst + 'background-color: red;';
                TEditor._editCell.style.cssText = cst;
            }
        }
        if (TEditor._editCell)
            TEditor._editCell.removeChild(ele);
        TEditor._editCell.innerHTML = ele.value;
    }
    if (e.stopPropagation) {
        e.stopPropagation();
    } else {
        e.cancelBubble = true;
    }
};
TEditor.cancelEvent = function (e) {
    e = (e) || window.event;
    /* 阻止事件冒泡 */
    if (e.stopPropagation) {
        e.stopPropagation();
    } else {
        e.cancelBubble = true;
    }
    /* 阻止默认行为 */
    if (e.preventDefault) {
        e.preventDefault();
    } else {
        e.returnValue = false;
    }
};

TEditor.prototype = {
    afterLoaded: null,
    columns: null,
    customDef: false,
    _columnDialog: null,
    _tableHeader: null,
    _columnMap: null,
    _timestamp: (new Date()).getTime(),
    dataSource: {},
    clearColSet: function () {
        var cols = this.columns;
        for (var i = 0; i < cols.length; i++) {
            var col = cols[i];
            if (col.fixed)
                continue;
            col.hidden = true;
        }
    },
    forceDraw: function (data) {
        if (!this._inited || !this._rendered)
            return;
        this.clearGrid();
        this.createHeader();
        this.draw(data);
    },
    drawGrid: function (da, headers) {
        this.setColumnCfg(headers)
        this.forceDraw(da);
    },
    gxload: function (url, headers) {
        if (url) {
            var _self = this;
            var re = {
                url: url,
                force: true,
                method: 'GET',
                responseType: "json",
                timeout: 3000,
                scope: this,
                success: function (data) {
                    var da = data[this.dataRoot];
                    _self.drawGrid(da, headers);
                },
                error: function (errorMsg, responseType, status) {
                    alert(errorMsg);
                }
            };
            AjaxReq.sendRequest(re);
        }
    },
    hasIn: function (cols, f) {
        for (var i = 0; i < cols.length; i++)
            if (cols[i] == f)
                return true;
        return false;
    },
    setColumnCfg: function (arr) {
        arr = arr || this._default;
        // this.clearColSet();
        if (arr && arr.length) {
            var cols = this.columns;
            for (var i = 0; i < cols.length; i++) {
                var col = cols[i];
                if (col.fixed)
                    continue;
                if (this.hasIn(arr, col.field))
                    col.hidden = undefined;
                else
                    col.hidden = true;
            }
            GxTool.setChecked(this._timestamp, arr);
            // this._inited = false;
        }
    },
    resetTab: function (args) {
        if (args.url) {
            var re = {
                url: args.url,
                force: args.force,
                method: 'GET',
                responseType: "json",
                timeout: 3000,
                scope: this,
                success: this.resetloadBack,
                error: function (errorMsg, responseType, status) {
                    alert(errorMsg);
                }
            };
            AjaxReq.sendRequest(re);
        }
    },
    getChangedRows: function () {
        var d = this.data;
        var cd = [];
        for (var i = 0; i < d.length; i++) {
            var da = d[i];
            if (da._changed)
                cd[cd.length] = da._data;
        }
        return cd;
    },
    getAllRows: function () {
        var d = this.data;
        var cd = [];
        for (var i = 0; i < d.length; i++) {
            var da = d[i];
            cd[cd.length] = da._data;
        }
        return cd;
    },
    getColumns: function () {
        var cols = this.columns;
        var ncols = '';
        for (var i = 0; i < cols.length; i++) {
            var cl = cols[i];
            if (cl.hidden)
                continue;
            ncols = ncols + ',' + cl.field;
        }
        if (ncols != '')
            ncols = ncols.substring(1);
        return ncols;
    },
    load: function (args) {
        if (args.afterLoaded)
            this.afterLoaded = args.afterLoaded;
        if (args.url) {
            var re = {
                url: args.url,
                force: args.force,
                method: 'GET',
                responseType: "json",
                timeout: 3000,
                scope: this,
                success: this.loadBack,
                error: function (errorMsg, responseType, status) {
                    alert(errorMsg);
                }
            };
            AjaxReq.sendRequest(re);
        }
    },
    loadBack: function (result, type, status) {
        var data = result[this.dataRoot];
        this.reDraw(data);
        if (typeof (this.afterLoaded) == 'function') {
            this.afterLoaded();
        }
    },
    resetloadBack: function (result, type, status) {
        var headers = result.header;
        var data = result.data;
        if (headers == undefined) {
            alert(result.msg);
            return;
        }
        headers[0].fixed = true;
        headers[0].icon = '/app/js/tools/images/ico_6.gif';
        for (var i = 1; i < headers.length; i++) {
            var header = headers[i];
            if (i < 3) header.fixed = true;
            if (i > 13) header.hidden = true;
        }
        this.resetGrid(headers, data);
        if (typeof (this.afterLoaded) == 'function')
            this.afterLoaded();
    },
    copyData: function (data) {
        var da = [];
        for (var i = 0; i < data.length; i++)
            da[i] = data[i]._data;
        return da;
    },
    forceRefresh: function () {
        var da = this.copyData(this.data);
        this.clearGrid();
        this.createHeader();
        this.draw(da);
    },
    refresh: function (headers) {
        var da = this.copyData(this.data);
        // var columns=this.columnDef.columns;
        // var len=columns.length;
        // for(var i=0;i<headers.length;i++)
        // columns[len++]=headers[i];
        this.columnDef.columns = headers;

        this.removeAll();
        // this.data=undefined;
        // this.data=[];
        this.draw(da);
    },
    drawColDlg: function (n) {
        if (typeof (n) == 'undefined')
            n = 2;
        if (this.customDef && this._columnDialog == null) {
            var width = 130 * n + 40;
            var pov = new PopDialog(width, 150, 20, 25);
            pov.titleText = '列定义设置';
            // pov.model = true;
            pov.dragEnable = true;
            var el = document.createElement('DIV');
            pov.renderId = el;
            var _self = this;
            pov.callback = function () {
                if (_self.columnSet)
                    _self.columnSet(_self.getColumns());
                _self.forceRefresh();
            };
            GxTool.create(el, this.columnDef.columns, this._timestamp);
            this._columnDialog = pov;
        }
    },
    resetGrid: function (headers, data) {
        this.data = undefined;
        this.data = [];
        if (headers) {
            this.removeAll();
            this.columnDef = {
                id: headers[0].field,
                columns: headers
            };
            this.drawColDlg(3);
            this.draw(data);
        } else {
            this.clearGrid();
            this.draw(data);
        }
    },
    boundTo: function (tid) {
        var tbl;
        var con = document.getElementById(tid);

        if (con == null) {
            tbl = document.createElement("TABLE");
            // tbl.className = "table table-bordered";
        } else if (con.tagName == 'DIV') {
            tbl = document.createElement("TABLE");
            // tbl.className = "table table-bordered";
            con.appendChild(tbl);
        } else if (con.tagName == 'TABLE')
            tbl = con;
        else {
            tbl = document.createElement("TABLE");
            // tbl.className = "table table-bordered";
            document.body.appendChild(tbl);
        }

        tbl.border = 0;
        tbl.cellPadding = 0;
        tbl.cellSpacing = 0;
        tbl.style.cssText = 'font-size:12px;';
        tbl.align = "left";
        /*
         * tbl.align = "center"; tbl.height = 1;
         */

        this._table = tbl;

        var tbody = tbl.getElementsByTagName("TBODY").item(0);
        if (tbody == null) {
            tbody = document.createElement("TBODY");
            if (tbl == null)
                return;
            tbl.appendChild(tbody);
        }
        this.tableREF = tbody;
    },
    render: function (tid) {
        if (!this._inited)
            this.initializeArgs();
        this.drawColDlg();
        if (this._rendered)
            return;
        var tbl;
        var con = document.getElementById(tid);

        if (con == null) {
            tbl = document.createElement("TABLE");
            // tbl.className = "table table-bordered";
        } else if (con.tagName == 'DIV') {
            tbl = document.createElement("TABLE");
            // tbl.className = "table table-bordered";
            con.appendChild(tbl);
        } else if (con.tagName == 'TABLE')
            tbl = con;
        else {
            tbl = document.createElement("TABLE");
            // tbl.className = "table table-bordered";
            document.body.appendChild(tbl);
        }

        tbl.border = 0;
        tbl.cellPadding = 0;
        tbl.cellSpacing = 0;
        tbl.cssText = 'font-size:12px;';
        tbl.align = "left";
        /*
         * tbl.align = "center"; tbl.height = 1;
         */

        this._table = tbl;
        if (this.haderShow)
            this.createHeader();
        var tbody = tbl.getElementsByTagName("TBODY").item(0);
        if (tbody == null) {
            tbody = document.createElement("TBODY");
            if (tbl == null)
                return;
            tbl.appendChild(tbody);
        }
        this.tableREF = tbody;
        this._rendered = true;
    },
    initializeArgs: function () {
        if (this._inited)
            return;
        if (typeof (this.columnDef) !== 'undefined') {
            this.columns = this.columnDef.columns;
            this.idField = this.columnDef.id;
        }

        if (typeof (this.idField) == 'undefined')
            this.idField = "id";
        if (this.columns && this.columns.length) {
            this._columnMap = {};
            var df = [];
            for (var k = 0; k < this.columns.length; k++) {
                var column = this.columns[k];
                if (typeof (column.width) == 'undefined')
                    column.width = 100;
                if (typeof (column.cellType) == 'undefined')
                    column.cellType = 0;
                this._columnMap[column.field] = column;
                if (column.hidden)
                    continue;
                df[df.length] = column.field;
            }
            this._default = df;
        }
        this._inited = true;
    },
    draw: function (data) {
        if (!this._inited && this.haderShow) {
            this.initializeArgs();
            this.createHeader();
        }
        if (data && data.length) {
            var da;
            for (var i = 0; i < data.length; i++) {
                da = data[i];
                this.insertRow(da, i + 1);
            }
        }
    },
    reDraw: function (data) {
        if (!this._inited || !this._rendered)
            return;
        this.clearGrid();
        this.draw(data);
    },
    createTh: function () {
        var th = this._tableHeader;
        if (th) {
            this.clearElement(th);
        } else {
            th = document.createElement("thead");
            this._tableHeader = th;
            this._table.appendChild(th);
        }
        return th;
    },
    createHeader: function () {
        var th = this.createTh();
        var row = document.createElement("tr");
        // row.height = 30;
        row.setAttribute("height", 30, 0);
        for (var k = 0; k < this.columns.length; k++) {
            var column = this.columns[k];
            if (column.hidden)
                continue;
            var cell = this.createTextTh(column.header, column.icon);
            cell.setAttribute("width", column.width, 0);
            if (column.className)
                cell.className = column.className;
            else
                cell.setAttribute('class', 'thc', 0);
            if (column.headerCssText)
                cell.style.cssText = column.headerCssText;
            if (typeof (column.headClassName) !== 'undefined')
                cell.className = column.headClassName;
            if (typeof (column.align) !== 'undefined') {
                cell.setAttribute('align', column.align);
            }
            if (typeof (column.valign) !== 'undefined')
                cell.setAttribute('valign', column.valign);
            // cell.valign = column.valign;
            row.appendChild(cell);
        }
        th.appendChild(row);
    },
    createTextCell: function (text) {
        var cell = document.createElement("td");
        if (typeof (text) == 'undefined')
            text = "&nbsp;";
        var nodeText = document.createTextNode(text);
        cell.appendChild(nodeText);
        return cell;
    },
    createTextTh: function (text, icon) {
        var cell = document.createElement("th");
        if (typeof (text) == 'undefined')
            text = "&nbsp;";
        if (icon) {
            cell.setAttribute('valign', 'middle', 0);
            var el = document.createElement('IMG');
            el.setAttribute('src', icon, 0);
            // el.setAttribute('height', 30, 0);
            el.style.cssText = 'margin-top:5px;';
            if (this.customDef) {
                var _self = this;
                el.onclick = function (e) {
                    e = (e) || window.event;
                    var x = e.clientX || e.pageX;
                    var y = e.clientY || e.pageY;
                    y = y - 50;
                    if (y < 0)
                        y = 0;
                    _self._columnDialog.show(x, y);
                };
            }
            cell.appendChild(el);
        }
        var nodeText = document.createTextNode(text);
        cell.appendChild(nodeText);
        return cell;
    },
    insertRow: function (obj, idx) {
        if (obj == null || typeof (obj) == 'undefined')
            return;
        if (typeof (idx) == 'undefined')
            idx = 1;
        var row = document.createElement('tr'); // this.tableREF.insertRow(idx);
        row.align = "center";
        // row.height = 35;
        row.setAttribute("height", 30, 0);
        // row.setAttribute("cursor","hand");
        // row.cursor = "hand";
        row.__DATA = obj;

        var dobj = {
            _data: obj,
            _changed: false,
            _rIdx: row.rowIndex
        };
        var key = this.idField;
        if (typeof (obj[key]) !== 'undefined')
            dobj.id = obj[key];
        var fn = function (fd, v) {
            var _dat = dobj._data;
            if (v === _dat[fd])
                return;
            _dat[fd] = v;
            dobj._changed = true;
            // _dat._changed = true;
        };
        var cell;
        var columns = this.columns;
        var index = 0;
        var idval;

        for (var i = 0; i < columns.length; i++) {
            var column = columns[i];
            if (column.hidden || column.cellType == -1)
                continue;
            cell = document.createElement('TD');
            row.appendChild(cell);
            index++;
            TEditor.renderCell(row, cell, column, obj, fn, this);
        }
        this.data[this.data.length] = dobj;
        this.tableREF.appendChild(row);
    },
    rowClick: function (row) {
    },
    clearGrid: function () {
        var tabl = this.tableREF;
        while (tabl.childNodes.length > 0) {
            tabl.removeChild(tabl.childNodes[0]);
        }
        this.clearData();
    },
    clearElement: function (el) {
        while (el.childNodes.length > 0) {
            el.removeChild(el.childNodes[0]);
        }
    },
    clearData: function () {
        var l = this.data.length;
        for (var i = 0; i < l; i++)
            this.data[i] = undefined;
        this.data = [];
    },
    removeAll: function () {
        var tabl = this.tableREF;
        while (tabl.childNodes.length > 0) {
            tabl.removeChild(tabl.childNodes[0]);
        }
        this.clearData();
        this._inited = false;
        // this._rendered = false;
    },
    toString: function () {
        return this._desc;
    }
};
