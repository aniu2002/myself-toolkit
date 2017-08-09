var AjaxLite = {
	Browser : {
		IE : !!(window.attachEvent && !window.opera),
		Opera : !!window.opera,
		WebKit : navigator.userAgent.indexOf('AppleWebKit/') > -1,
		Gecko : navigator.userAgent.indexOf('Gecko') > -1
				&& navigator.userAgent.indexOf('KHTML') == -1
	},
	IE : __getIE(),
	mode : {
		Post : "Post",
		Get : "Get"
	},
	getRequest : function() {
		if (window.XMLHttpRequest) {
			return new XMLHttpRequest()
		} else {
			try {
				return new ActiveXObject("MSXML2.XMLHTTP")
			} catch (e) {
				try {
					return new ActiveXObject("Microsoft.XMLHTTP")
				} catch (e) {
					return false
				}
			}
		}
	}
};
function __getIE() {
	if (window.ActiveXObject) {
		var v = navigator.userAgent.match(/MSIE ([^;]+)/)[1];
		return parseFloat(v.substring(0, v.indexOf(".")))
	}
	return false
};
Array.prototype.foreach = function(func) {
	if (func && this.length > 0) {
		for ( var i = 0; i < this.length; i++) {
			func(this[i])
		}
	}
};
String.format = function() {
	if (arguments.length == 0)
		return null;
	var str = arguments[0];
	for ( var i = 1; i < arguments.length; i++) {
		var regExp = new RegExp('\\{' + (i - 1) + '\\}', 'gm');
		str = str.replace(regExp, arguments[i])
	}
	return str
};
String.prototype.startWith = function(s) {
	return this.indexOf(s) == 0
};
String.prototype.endWith = function(s) {
	var d = this.length - s.length;
	return (d >= 0 && this.lastIndexOf(s) == d)
};
String.prototype.trim = function() {
	return this.replace(/(^\s*)|(\s*$)/g, '')
};
function getid(id) {
	return (typeof id == 'string') ? document.getElementById(id) : id
};

document.getElementsByClassName = function(name) {
	var tags = document.getElementsByTagName('*') || document.all;
	var els = [];
	for ( var i = 0; i < tags.length; i++) {
		if (tags[i].className) {
			var cs = tags[i].className.split(' ');
			for ( var j = 0; j < cs.length; j++) {
				if (name == cs[j]) {
					els.push(tags[i]);
					break
				}
			}
		}
	}
	return els
};
var getby = document.getElementsByClassName;
function Cookie() {
}
Cookie.Save = function(n, v, mins, dn, path) {
	if (n) {
		if (!mins)
			mins = 365 * 24 * 60;
		if (!path)
			path = "/";
		var date = new Date();
		date.setTime(date.getTime() + (mins * 60 * 1000));
		var expires = "; expires=" + date.toGMTString();
		if (dn)
			dn = "domain=" + dn + "; ";
		document.cookie = name + "=" + value + expires + "; " + dn + "path="
				+ path
	}
};
Cookie.Del = function(n) {
	save(n, '', -1)
};
Cookie.Get = function(n) {
	var name = n + "=";
	var ca = document.cookie.split(';');
	for ( var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ')
			c = c.substring(1, c.length);
		if (c.indexOf(name) == 0)
			return c.substring(name.length, c.length)
	}
	return ""
};

function getcookie(name) {
	var cookie_start = document.cookie.indexOf(name);
	var cookie_end = document.cookie.indexOf(";", cookie_start);
	return cookie_start == -1 ? '' : unescape(document.cookie.substring(
			cookie_start + name.length + 1,
			(cookie_end > cookie_start ? cookie_end : document.cookie.length)));
}
function setcookie(cookieName, cookieValue) {
	var expires = new Date();
	var now = parseInt(expires.getTime());
	var et = (86400 - expires.getHours() * 3600 - expires.getMinutes() * 60 - expires
			.getSeconds());
	expires.setTime(now + 1000000 * (et - expires.getTimezoneOffset() * 60));
	document.cookie = escape(cookieName) + "=" + escape(cookieValue)
			+ ";expires=" + expires.toGMTString() + "; path=/";
}
function getOffsetTop(el, p) {
	var _t = el.offsetTop;
	while (el = el.offsetParent) {
		if (el == p)
			break;
		_t += el.offsetTop
	}
	return _t
};
function getOffsetLeft(el, p) {
	var _l = el.offsetLeft;
	while (el = el.offsetParent) {
		if (el == p)
			break;
		_l += el.offsetLeft
	}
	return _l
};
function attach(o, e, f) {
	if (document.attachEvent)
		o.attachEvent("on" + e, f);
	else if (document.addEventListener)
		o.addEventListener(e, f, false);
}
function ajaxget(url, id) {
	var request = AjaxLite.getRequest();
	request.open("get", url, true);
	request.onreadystatechange = function() {
		if (request.readyState == 4) {
			if (document.getElementById(id)) {
				if (request.status == 200) {
					document.getElementById(id).innerHTML = request.responseText;
				} else {
					document.getElementById(id).innerHTML = "取消";
				}
			}
		}
	}
	request.setRequestHeader("If-Modified-Since", "0");
	request.send();
}