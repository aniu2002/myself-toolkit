function DK(cfg) {
	cfg = cfg || {};

	this.url = cfg.url;
	this.subject = this.url.substring(this.url.lastIndexOf('/') + 1);
	this.label = cfg.label;
	this.sessionId = null;
	this.handler = cfg.handler;
	this.errorHandler = cfg.errorHandler;
	this.after = cfg.after;
}

DK.E_ACK = "ack";
DK.E_ACK_HB = "ack-hb";
DK.E_ERR = "error";
DK.E_DATA = "data";
DK.E_DONE = "done";
DK.E_PUBLISH = "publish";
DK.E_LISTEN = "listen";
DK.E_FEATCH = "fetch";
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
	active : false,
	stopped : false,
	xhr : null,
	stop : function() {
		this.stopped = true;
	},
	listen : function() {
		var data = {};
		data[DK.P_EVENT] = DK.E_JOIN;
		var _dk = this;
		this._get(_dk.url, data, function(res, status) {
			_dk._process(res, status);
		});
	},
	_process : function(res, status) {
		var _dk = this;
		switch (res.event) {
		case DK.E_ERR:
			alert(res[DK.P_REASON]);
			_dk.active = false;
			return;
		case DK.E_ACK:
			var sId = res[DK.P_ID];
			_dk.sessionId = sId;
			_dk.active = true;
			_dk.stopped = false;
			_dk.fetch();
			break;
		case DK.E_ABORT:
			_dk.active = false;
			return;
		}
	},
	fetch : function() {
		if (this.stopped)
			return;
		var data = {};
		data[DK.P_EVENT] = DK.E_FEATCH;
		data[DK.P_ID] = this.sessionId;
		var _dk = this;
		this.xhr = this._get(this.url, data, function(res, status) {
			_dk._procResult(res, status);
		});
	},
	_procResult : function(res, status) {
		var p = this;
		switch (res.event) {
		case DK.E_ERR:
			alert(res[DK.P_REASON]);
			p.active = false;
			return;
		case DK.E_ACK:
			break;
		case DK.E_ACK_HB:
			p.fetch();
			break;
		case DK.E_DATA:
			if (p.handler) {
				var re = res.data;
				for ( var i = 0; i < re.length; i++)
					p.handler(re[i].data);
			}
			if (p.after)
				p.after();
			p.fetch();
			break;
		case DK.E_ABORT:
			if (p.errorHandler)
				p.errorHandler(res);
			p.listen();
			p.active = false;
			break;
		}
	},
	leave : function() {
		if (this.active) {
			this.stopped = true;
			var data = {};
			data[DK.P_EVENT] = DK.E_LEAVE;
			data[DK.P_ID] = this.sessionId;
			data[DK.P_SUBJECT] = this.subject;
			this.active = false;
			if (this.xhr != null) {
				this.xhr.abort();
				this.xhr = null;
			}
			this._req(this.url, 'POST', data, null, false);
		}
	},
	_post : function(url, data, func) {
		return this._req(url, 'POST', data, func);
	},
	_get : function(url, data, func) {
		return this._req(url, 'GET', data, func);
	},
	_req : function(url, method, data, func, async) {
		async = typeof (async) == 'undefined' ? true : async;
		var reqs = {
			type : method,
			url : url,
			cache : false,
			ifModified : true,
			dataType : 'json',
			async : async,
			data : data,
			error : function(xhr, status, err) {
			},
			success : function(data, status) {
				try {
					if (status == "success" && func)
						func(data, status)
				} catch (e) {
					alert(e)
				}
			}
		};
		if (method == 'POST')
			reqs.contentType = "application/x-www-form-urlencoded;charset=UTF-8";
		var xhr = $.ajax(reqs);
		return xhr;
	}
}