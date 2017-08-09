var _$P = {
    _$Pov: undefined,
    _$Fel: undefined,
    _$Mel: undefined,
    _$Cel: undefined,
    _$ProBar: undefined,
    _counter: 0,
    createProcess: function (cfg) {
        cfg = cfg || {
            w: 100
        };
        var el;
        if (cfg.el)
            el = $(cfg.el);
        else {
            el = $('<div></div>');
            $(document.body).append(el);
        }
        var left = 0;
        var w = 700;
        el[0].style.cssText = 'background:#fff;width:' + cfg.w + 'px;';
        var n = document.createElement('DIV');
        n.style.cssText = 'position:relative;left:0px;margin:0 0 0 0;height:20px;background:#e4e7eb;width:'
            + w + 'px';
        var gn = document.createElement('DIV');
        gn.style.cssText = "background:#090;position:relative;height:20px;width:50px;";
        var span = document.createElement('SPAN');
        span.style.cssText = 'position:absolute;text-align:center;width:50px;left:' + left
            + 'px';
        n.appendChild(gn);
        gn.appendChild(span);
        el.append($(n));
        // -------
        var p = {
            process: function (p, m) {
                span.innerHTML = p + '%';
                gn.style.width = p + '%';
            }
        };
        return p;
    },
    showProcess: function (url, cb) {
        if (this._$Pov) {
            this._$Fel.attr('src', url);
            this._$Pov.show(150, 50);
            return;
        }
        this._$Fel = $('<iframe frameborder="no" border="0" marginwidth="0" src="'
            + url
            + '" style="visibility:inherit;left:0px;z-index:-1;top:0px;width:97%;height:98%;"></iframe>');
        this._$Pov = PopDialog.pop({
            title: '执行进度信息...',
            renderTo: this._$Fel[0],
            model: true,
            dragEnable: false,
            width: 700,
            height: 280
        });
        this._$Pov.afterClose = cb;
        this._$Pov.show(150, 20);
    },
    appendMsg: function (d) {
        var el = this._$Mel;
        if (this.counter > 200) {
            el.empty();
            this.counter = 0;
        }
        this.counter++;
        if (d.charAt(0) == '%')
            el.append('<font color="red">&nbsp;' + d.substring(1) + '</font><br/>');
        else
            el.append("&nbsp;" + d + "<br/>");
    },
    scrollDiv: function () {
        var el = this._$Mel;
        el.animate({ scrollTop: el.height() });
    },
    openTimer: function (pBar, token, func) {
        var timeFn = function () {
            fn(token);
        };
        var fn = function (sid) {
            Common.get('/cmd/data/tool', { _t: 'q', sid: sid}, function (d) {
                if (d.msgs) {
                    for (var i = 0; i < d.msgs.length; i++)
                        _$P.appendMsg(d.msgs[i]);
                }
                pBar.process(d.percent)
                if (d.state == 1) {
                    window.setTimeout(timeFn, 1000);
                } else {
                    _$P.appendMsg(d.result + ' , 耗时:' + d.costSeconds + '秒');
                    if (func) {
                        func(_$P._$Pov, _$P._$ProBar, d.result);
                    }
                }
                _$P.scrollDiv();
            });
        };
        timeFn();
    },
    open: function (token, cb, func) {
        if (this._$Pov) {
            this._$Mel.empty();
            this._$Pov.afterClose = cb;
            this._$Pov.show(150, 50);
            this._$ProBar.process(0);
            this.openTimer(this._$ProBar, token, func);
            return;
        }
        this._$Cel = $('<div></div>');
        this._$Cel.css({
            'height': '230px',
            'background-color': '#ffffff'
        });
        this._$Mel = $('<div></div>');
        this._$Mel.css({
            'margin-top': '5px',
            'margin-left': '0px',
            'margin-right': '10px',
            'background-color': '#ffffff',
            'border': '1px solid #5a667b',
            'font-size': '13',
            'height': '220px',
            'overflow-y': 'auto'
        });
        this._$Fel = $('<div></div>');
        var divEl = $('<div></div>');
        this._$Cel.append(this._$Mel);
        divEl.append(this._$Cel);
        divEl.append(this._$Fel);
        this._$ProBar = this.createProcess({el: this._$Fel});
        this._$Pov = PopDialog.pop({
            title: '执行进度信息...',
            renderTo: divEl[0],
            model: true,
            dragEnable: false,
            width: 720,
            height: 280
        });
        this._$Pov.afterClose = cb;
        this._$Pov.show(150, 20);
        this.openTimer(this._$ProBar, token, func);
    }
};