var _Pov;
var _Fel;
var _Gflow;
function getDoc(ifm) {
	var doc = ifm.contentDocument || ifm.document;
	return doc;
}
function showProcessImg(imgUrl) {
	var defImg = '/app/images/ajax-loader.gif';
	if (_Pov) {
		_Pov.show(150, 50);
		_Fel.attr('ori', imgUrl);
		_Fel.attr('src', defImg);
		Common.lazyInit(_Fel);
		return;
	}
	_Fel = $('<img ori="' + imgUrl + '" src="' + defImg + '" />');
	var dv = $('<div style="height:100%;overflow-y:auto;"></div>');
	dv.append(_Fel);
	_Pov = PopDialog.pop({
		title : '流程信息',
		renderTo : dv[0],
		dragEnable : false,
		width : 850,
		height : 400
	});
	_Pov.show(150, 50);
	Common.lazyInit(_Fel);
}

function showProcessImgx(imgUrl) {
	var defImg = '/app/images/ajax-loader.gif';
	if (_Pov) {
		_Pov.show(150, 50);
		_Fel.attr('ori', imgUrl);
		_Fel.attr('src', defImg);
		Common.lazyInit(_Fel);
		return;
	}
	_Fel = $('<img ori="' + imgUrl + '" src="' + defImg + '" />');
	var dv = $('<div style="height:100%;overflow-y:auto;"></div>');
	dv.append(_Fel);
	_Pov = PopDialog.pop({
		title : '流程信息',
		renderTo : dv[0],
		dragEnable : false,
		width : 860,
		height : 400
	});
	_Pov.show(150, 50);

	var property = {
		width : 850,
		height : 380,
		haveHead : false,
		haveTool : false,
		haveGroup : false,
		useOperStack : false
	};
	
	_Gflow = $.createGooFlow(dv, property);

	Common.lazyInit(_Fel);
}