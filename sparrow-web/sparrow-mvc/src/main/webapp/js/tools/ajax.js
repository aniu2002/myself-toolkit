/**
 * responseXML,服务器端设置 response.setContentType("text/xml");
 * response.setHader("ContentType","text/xml");
 */
function XMLHttp() {
};
/**
 * xmlDom
 */
XMLHttp._DOMDocument = [ "MSXML.DOMDocument", "Microsoft.XMLDOM",
    "Msxml2.DOMDocument.5.0", "Msxml2.DOMDocument.4.0",
    "Msxml2.DOMDocument.3.0", "MSXML2.DOMDocument" ];
/**
 * xmlRequest
 */
XMLHttp._XMLHTTP = [ "Microsoft.XMLHTTP", "Msxml2.XMLHTTP.5.0",
    "Msxml2.XMLHTTP.4.0", "MSXML2.XMLHTTP.3.0", "MSXML2.XMLHTTP" ];

/**
 * dom
 */
XMLHttp._createActiveDomParser = function () {
    var ret = null;
    var axarray = XMLHttp._DOMDocument;
    for (var i = 0; i < axarray.length; i++) {
        try {
            ret = new ActiveXObject(axarray[i]);
            break;
        } catch (ex) {
        }
    }
    return ret;
};

XMLHttp.Parser = function (xml) {
    var dom;
    if (window.DOMParser) {
        var parser = new DOMParser();
        dom = parser.parseFromString(xml, "text/xml");
        if (!dom.documentElement
            || dom.documentElement.tagName == "parsererror") {
            var message = "error parse ! ";
            throw message;
        }
        return dom;
    } else {
        dom = XMLHttp._createActiveDomParser();
        dom.async = false;
        dom.loadXML(xml);
        return dom;
    }
};

/**
 * get ajax req
 */
XMLHttp.getXMLHttpRequest = function () {
    var req = null;
    if (window.XMLHttpRequest) {
        req = new XMLHttpRequest();
    } else if (window.ActiveXObject
        && !(navigator.userAgent.indexOf('Mac') >= 0 && navigator.userAgent
            .indexOf("MSIE") >= 0)) {
        var axarray = XMLHttp._XMLHTTP
        for (var i = 0; i < axarray.length; i++) {
            try {
                req = new ActiveXObject(axarray[i]);
                break;
            } catch (ex) {
            }
        }
    }
    return req;
};

/**
 * ajax
 */
function AjaxReq() {
};

/**
 * ajax queue
 */

AjaxReq.Queue = new Array();
/**
 * remove queue
 */
AjaxReq.removeReq = function (req, timer) {
    var len = AjaxReq.Queue.length;
    var index = -1;
    for (var i = 0; i < len; i++) {
        if (req == AjaxReq.Queue[i]) {
            if (timer && typeof (timer) == 'number') {
                window.clearTimeout(timer);
            }
            AjaxReq.Queue[i] = null;
            index = i;
            break;
        }
    }
    if (index == -1)
        return;
    for (var j = index; j < len - 1; j++) {
        AjaxReq.Queue[j] = AjaxReq.Queue[j + 1];
    }
    AjaxReq.Queue.length = len - 1;
};
/**
 * get ajax req
 */
AjaxReq.getRequestObj = function () {
    if (window.XMLHttpRequest) { // Non-IE browsers
        return new XMLHttpRequest();
    } else if (window.ActiveXObject) { // IE
        return new ActiveXObject("Microsoft.XMLHTTP");
    }
    return null;
};
/**
 **/
AjaxReq.serializeForm = function (form) {
    var i, queryString = "", and = "";
    var item; // for each form's object
    var itemValue;// store each form object's value
    if (typeof (form) == "string")
        form = document.getElementById(form);
    for (i = 0; i < frmID.length; i++) {
        item = frmID[i];
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
}

/**
 * serialize http request can't use escape , just use encodeURI
 * ,encodeURIComponent method
 */
AjaxReq.serializeObject = function (obj, transform) {
    if (typeof (obj) == 'string')
        return obj;
    if (obj == null)
        return "";
    if (typeof (obj) != 'object')
        return "";
    var tmp = "";
    for (var name in obj) {
        var value = obj[name];
        if (transform)
            value = encodeURIComponent(value);
        tmp = tmp + "&" + name + "=" + value;
    }
    if (tmp != "")
        tmp = tmp.substring(1);
    return tmp;
};

/**
 * {url:'test.do',method:'POST',args:{name:'aniu',pass:'0'},responseType:'xml',timeout:1000,scope:window,
 * success:function(result,responseType,status){},error:function(errorMsg,responseType,status){}}
 */
AjaxReq.sendRequest = function (config) {
    if (!config) {
        alert("Error remote invoke,parameters is null!");
        return;
    }
    if (typeof (config) != 'object') {
        alert("Error remote invoke,Request syntax error !");
        return;
    }
    if (typeof (config.url) != 'string') {
        alert("Error remote invoke,Url is error !");
        return;
    }
    if (!config.method)
        config.method = 'POST';
    var req = AjaxReq.getRequestObj();
    if (req) {
        if (config.method == 'GET') {
            if (config.force) {
                if (config.args)
                    config.args._ = (new Date()).valueOf();
                else
                    config.args = {
                        _: (new Date()).valueOf()
                    };
            }
        }
        var callBack = AjaxReq.createCallBack(req, config);
        req.onreadystatechange = callBack;
        AjaxReq.Queue.push(req);
        if (config.method == 'POST') {
            var ctype;
            if (config.dataType)
                ctype = config.dataType + ';charset=';
            else
                ctype = 'application/x-www-form-urlencoded;charset=';
            if (config.encoding) {
                ctype += config.encoding;
            } else {
                ctype += 'utf-8';
            }
            req.open("POST", config.url, true);
            req.setRequestHeader('Content-Type', ctype);
            req.setRequestHeader('X-Requested-With', "XMLHttpRequest");
            var queryStr = AjaxReq.serializeObject(config.args, true);
            req.send(queryStr);
        } else if (config.method == 'GET') {
            var queryStr = AjaxReq.serializeObject(config.args, true);
            if (queryStr == '')
                req.open("GET", config.url);
            else {
                var _url = config.url;
                var idx = _url.indexOf('?');
                if (idx != -1)
                    _url = _url + '&' + queryStr;
                else
                    _url = _url + '?' + queryStr;
                req.open("GET", _url);
            }
            req.setRequestHeader('X-Requested-With', "XMLHttpRequest");
            req.send(null);
        }
        if (config.timeout) {
            AjaxReq.setTimeOut(req, config);
        }
    }
};

/**
 * request json,text,xml
 */
AjaxReq.createCallBack = function (req, cfg) {
    if (req == null)
        return null;
    if (cfg.responseType != 'json' && cfg.responseType != 'text'
        && cfg.responseType != 'xml')
        cfg.responseType = 'text';
    var callBack = function () {
        // request completed
        if (req.readyState == 4) {
            if (req.status == 200) { // ok
                var result = null;
                try {
                    if (cfg.responseType == 'text') {
                        result = req.responseText;
                    } else if (cfg.responseType == 'json') {
                        var s = req.responseText;
                        if (s)
                            result = eval("(" + req.responseText + ")");
                    } else if (cfg.responseType == 'xml') {
                        result = req.responseXML;
                    }
                    if (cfg.success) {
                        if (cfg.scope)
                            cfg.success.apply(cfg.scope, [ result,
                                cfg.responseType, 200 ]);
                        else
                            cfg.success(result, cfg.responseType, 200);
                    }
                    AjaxReq.removeReq(req, cfg.timerSet);
                } catch (e) {
                    AjaxReq.removeReq(req, cfg.timerSet);
                    if (cfg.error) {
                        if (cfg.scope)
                            cfg.error.apply(cfg.scope, [ e, cfg.responseType,
                                200 ]);
                        else
                            cfg.error(e, cfg.responseType, 200);
                    }
                }
            } else if (req.status == 555) {
                var m = req.responseText;
                req.abort();
                AjaxReq.removeReq(req, cfg.timerSet);
                top.location.href = m;
            } else { // error
                var msg = req.responseText;// req.statusText;
                if (cfg.error) {
                    if (cfg.scope)
                        cfg.error.apply(cfg.scope, [ msg, cfg.responseType,
                            req.status ]);
                    else
                        cfg.error(msg, cfg.responseType, req.status);
                }
                req.abort();
                AjaxReq.removeReq(req, cfg.timerSet);
            }
        }
    };
    return callBack;
};

/**
 * 超时取消请求
 */
AjaxReq.setTimeOut = function (req, cfg) {
    if (req == null)
        return;
    if (typeof (cfg.timeout) != 'number' || cfg.timeout < 2000)
        cfg.timeout = 16000;
    var abortReq = function () {
        if (req == null)
            return;
        // && req.status == 200
        if (req.readyState == 4)
            return;
        if (req != null) {
            req.abort();
            AjaxReq.removeReq(req);
            if (cfg.error) {
                var msg = " Request timeout!";
                if (cfg.scope)
                    cfg.error.apply(cfg.scope, [ msg, 'json', 1 ]);
                else
                    cfg.error(msg, 'json', 1);
            }
        }
    };
    try {
        cfg.timerSet = window.setTimeout(abortReq, cfg.timeout);
    } catch (err) {
        alert(err.description);
    }
};