function DK(cfg) {
    cfg = cfg || {};

    this.url = cfg.url;
    this.subject = cfg.subject;
    this.label = this.label;
    this.sessionId = null;
}

DK.E_ACK = "ack";
DK.E_ACK_HB = "ack-hb";
DK.E_ERR = "error";
DK.E_DATA = "data";
DK.E_DONE = "done";
DK.E_PUBLISH = "publish";
DK.E_LISTEN = "listen";
DK.E_FEATCH = "fetch";
DK.E_SESSIONS = "sessions";
DK.E_JOIN = "join";
DK.E_LEAVE = "leave";
DK.E_ABORT = "abort";
DK.E_SUSCRIBE = "subscribe";
DK.E_UN_SUBSCRIBE = "un_subscribe";

DK.P_SUBJECT = "subject";
DK.P_EVENT = "event";
DK.P_REASON = "reason";
DK.P_LABEL = "label";
DK.P_ID = "id";

DK.prototype = {
    active: false,
    stopped: false,
    xhr: null,
    stop: function () {
        this.stopped = true;
    },
    join: function (subject, reqParas, callback) {
        var data = {};
        if (reqParas) {
            for (var n in reqParas)
                data[n] = reqParas[n];
        }
        data[DK.P_EVENT] = DK.E_JOIN;
        var reqUrl = this.url + "/" + subject;
        this.subject = subject;
        var p = this;
        this.ajaxRequest(reqUrl, data, function (res, status) {
            switch (res.event) {
                case DK.E_ERR:
                    alert(res[DK.P_REASON]);
                    p.active = false;
                    return;
                case DK.E_ACK:
                    var sId = res[DK.P_ID];
                    p.sessionId = sId;
                    p.active = true;
                    p.stopped = false;
                    if (callback)
                        callback(sId);
                    break;
                case DK.E_ABORT:
                    p.active = false;
                    return;
            }
        });
    },
    subscribe: function (subject, callback) {
        if (this.active) {
            this.fetch(this.sessionId, subject, callback);
            return;
        } else {
            var p = this;
            this.join(subject, null, function (sid) {
                p.fetch(sid, subject, callback);
            });
        }
    },
    publish: function (from, to, msg, time, func) {
        var reqUrl = this.url + "/" + to;
        var data = {};
        data[DK.P_EVENT] = DK.E_PUBLISH;
        data[DK.P_ID] = this.sessionId;
        data["from2"] = from;
        data["to2"] = to;
        data["msg"] = msg;
        data["time"] = time;
        this.ajaxRequestX(reqUrl, 'POST', data, function (res, status) {
            switch (res.event) {
                case DK.E_ERR:
                    if (func)
                        func(-1);
                    return;
                case DK.E_ACK:
                    if (func)
                        func(0);
                    break;
                case DK.E_ABORT:
                    if (func)
                        func(-2);
                    return;
            }
        });
    },
    leave: function () {
        if (this.active) {
            this.stopped = true;
            var data = {};
            data[DK.P_EVENT] = DK.E_LEAVE;//DK.E_UN_SUBSCRIBE;
            data[DK.P_ID] = this.sessionId;
            data[DK.P_SUBJECT] = this.subject;
            this.active = false;
            if (this.xhr != null) {
                this.xhr.abort();
                this.xhr = null;
            }
            this.ajaxRequest(this.url, data, function (res) {

            }, false);
        }
    },
    sessions: function (subject, callback) {
        var data = {};
        data[DK.P_EVENT] = DK.E_SESSIONS;
        data[DK.P_ID] = this.sessionId;
        var reqUrl = this.url + "/" + subject;
        this.ajaxRequest(reqUrl, data, function (res, status) {
            switch (res.event) {
                case DK.E_ERR:
                    alert(res[DK.P_REASON]);
                    if (callback)
                        callback(res, -1);
                    return;
                case DK.E_DATA:
                case DK.E_ACK:
                    if (callback)
                        callback(res, status);
                    break;
                case DK.E_ABORT:
                    if (callback)
                        callback(res, -1);
                    return;
            }
        });
    },
    listen: function (subject, reqParas, callback) {
        var data = {};
        if (reqParas) {
            for (var n in reqParas)
                data[n] = reqParas[n];
        }
        data[DK.P_EVENT] = DK.E_JOIN;
        var reqUrl = this.url + "/" + subject;
        this.subject = subject;
        if (this.active) {
            this.fetchEvents(this.sessionId, subject, callback, reqParas);
            return;
        }
        var p = this;
        this.ajaxRequest(reqUrl, data, function (res, status) {
            switch (res.event) {
                case DK.E_ERR:
                    alert(res[DK.P_REASON]);
                    p.active = false;
                    return;
                case DK.E_ACK:
                    p.doListen(res, subject, callback, reqParas);
                    break;
                case DK.E_ABORT:
                    p.active = false;
                    return;
            }
        });
    },
    doListen: function (res, subject, callback, reqParas) {
        var sId = res[DK.P_ID];
        this.sessionId = sId;
        this.active = true;
        this.stopped = false;
        this.fetchEvents(sId, subject, callback, reqParas);
    },
    fetch: function (sid, subject, callback) {
        if (this.stopped)
            return;
        var data = {};
        data[DK.P_EVENT] = DK.E_FEATCH;
        data[DK.P_ID] = sid;
        var reqUrl = this.url + "/" + subject;
        var p = this;
        var func = this.join;
        this.xhr = this.ajaxRequest(reqUrl, data, function (res, status) {
            switch (res.event) {
                case DK.E_ERR:
                    alert(res[DK.P_REASON]);
                    p.active = false;
                    return;
                case DK.E_ACK:
                    //p(sid, subject, callback, label);
                    break;
                case DK.E_ACK_HB:
                    p.fetch(sid, subject, callback);
                    break;
                case DK.E_DATA:
                    var flg = callback(res.data, subject, status);
                    if (flg)
                        p.fetch(sid, subject, callback);
                    else
                        p.leave();
                    break;
                case DK.E_ABORT:
                    func(subject, callback);
                    p.active = false;
                    break;
            }
        });
    },
    fetchEvents: function (sid, subject, callback, reqParas) {
        if (this.stopped)
            return;
        var data = {};
        data[DK.P_EVENT] = DK.E_FEATCH;
        data[DK.P_ID] = sid;
        var reqUrl = this.url + "/" + subject;
        var p = this;
        var func = this.listen;
        this.xhr = this.ajaxRequest(reqUrl, data, function (res, status) {
            switch (res.event) {
                case DK.E_ERR:
                    alert(res[DK.P_REASON]);
                    p.active = false;
                    return;
                case DK.E_ACK:
                    //p(sid, subject, callback, label);
                    break;
                case DK.E_ACK_HB:
                    p.fetchEvents(sid, subject, callback, reqParas);
                    break;
                case DK.E_DATA:
                    var flg = callback(res.data, subject, status);
                    if (flg)
                        p.fetchEvents(sid, subject, callback, reqParas);
                    else
                        p.leave();
                    break;
                case DK.E_ABORT:
                    func(subject, reqParas, callback);
                    p.active = false;
                    break;
            }
        });
    },
    ajaxRequest: function (url, data, func, async) {
        return this.ajaxRequestX(url, "GET", data, func, async);
    },
    ajaxRequestX: function (url, method, data, func, async) {
        async = typeof(async) == 'undefined' ? true : async;
        var xhr = $.ajax({type: method,
            url: url,
            contentType: "application/x-www-form-urlencoded",
            cache: false,
            dataType: "text",
            ifModified: true,
            async: async,
            data: data,
            statusCode: {
                302: function () {
                    alert('page not found');
                }
            },
            error: function (xhr, status, text) {
                //alert("请求出异常了哦");
            },
            success: function (text, status, jqXHR) {
                if (func) {
                    var res = eval('(' + text + ')')
                    func(res, status)
                }
            }
        });
        return xhr;
    }
}