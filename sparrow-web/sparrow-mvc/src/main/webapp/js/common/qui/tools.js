var Tool = {};
Tool.form = {
	cache : {},
	canFet : function(item) {
		var tag = item.tagName.toLowerCase();
		var ty = item.type.toLowerCase();
		return ty == 'text' || ty == 'hidden' || tag == 'select'
				|| ty == "textarea" || ty == "password";
	},
	getFormFields : function(form) {
		var eles = form.elements;
		var fields = {};
		for ( var i = 0; i < eles.length; i++) {
			var item = eles[i];
			if (item.name == '')
				continue;
			if (this.canFet(item))
				fields[item.name] = item;
			else if (item.type == "checkbox") {
				var exl = $(item);
				if (exl.is(':checked'))
					exl.val('1');
				else
					exl.val('0');
				fields[item.name] = exl;
			}
		}
		return fields;
	},
	formSet : function(form, paras) {
		var el = null;
		if (typeof (form) == "string")
			el = document.getElementById(form);
		else
			el = form;
		var fields = this.cache[form];
		if (fields == null) {
			fields = this.getFormFields(el);
			this.cache[form] = fields;
		}
		for ( var name in paras) {
			var itm = fields[name];
			if (itm)
				itm.value = paras[name];
		}
	},
	serializeForm : function(form) {
		var i, query = '', and = '';
		var item;
		var val;
		if (typeof (form) == "string")
			form = document.getElementById(form);
		var eles = form.elements();
		for (i = 0; i < eles.length; i++) {
			item = eles[i];
			if (item.name == '')
				continue;
			if (item.type == 'select-one')
				val = item.options[item.selectedIndex].value;
			else if (item.type == 'checkbox' || item.type == 'radio') {
				if (item.checked == false)
					continue;
				val = item.value;
			} else if (item.type == 'button' || item.type == 'submit'
					|| item.type == 'reset' || item.type == 'image')
				continue;
			else
				val = item.value;
			val = encodeURIComponent(val);
			query += and + item.name + '=' + val;
			and = "&";
		}
		return query;
	}
};