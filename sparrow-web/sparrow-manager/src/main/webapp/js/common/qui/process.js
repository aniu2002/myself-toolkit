var Process = {
    _Pov: null,
    _Fel: null,
    getDoc: function (ifm) {
        var doc = ifm.contentDocument || ifm.document;
        return doc;
    },
    create: function (cfg) {
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
        var left = 100;
        var w = el.width() - 2;
        el[0].style.cssText = 'background:#fff;width:' + cfg.w + 'px;';
        var n = document.createElement('DIV');
        n.style.cssText = 'position:relative;left:0px;margin:0 0 0 0;height:20px;background:#e4e7eb;width:' + w + 'px';
        var gn = document.createElement('DIV');
        gn.style.cssText = "background:#090;position:relative;height:20px;width:2%;";
        var span = document.createElement('SPAN');
        span.style.cssText = 'position:absolute;text-align:center;left:' + left + 'px';
        n.appendChild(gn);
        gn.appendChild(span);
        el.append($(n));
        var p = {
            process: function (p, m) {
                span.innerHTML = p + '%';
                gn.style.width = p + '%';
            }
        };
        return p;
    },
    showImg: function (imgUrl) {
        var defImg = '/app/images/ajax-loader.gif';
        var fel = Process._Fel;
        var pov = Process._Pov;
        if (pov) {
            pov.show(150, 50);
            fel.attr('ori', imgUrl);
            fel.attr('src', defImg);
            Common.lazyInit(fel);
            return;
        }
        fel = $('<img ori="' + imgUrl + '" src="' + defImg + '" />');
        var dv = $('<div style="height:100%;overflow-y:auto;"></div>');
        dv.append(fel);
        pov = PopDialog.pop({
            title: '流程信息',
            renderTo: dv[0],
            dragEnable: false,
            width: 850,
            height: 400
        });
        pov.show(150, 50);
        Common.lazyInit(fel);
        Process._Pov = pov;
        Process._Fel = fel;
    }
};
