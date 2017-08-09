<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>${itemName?if_exists}——资源管理</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="/app/js/bootstrap/css/bootstrap.min.css" type="text/css"/>
    <link rel="stylesheet" href="/app/js/bootstrap/css/bootstrap-responsive.min.css" type="text/css"/>
    <link rel="stylesheet" href="/app/css/tooltip/tooltip.css" type="text/css"/>
    <link rel="stylesheet" href="/app/css/style.css" type="text/css"/>
    <style type="text/css">
    .table td i{margin:0 2px;}
    .active_el { background-color: #AAAAAA;color: #FFFFFF;}</style>
    <script type="text/javascript" src="/app/js/jquery/jquery-1.7.1.min.js"></script>
    <script type="text/javascript" src="/app/js/jquery/jquery-migrate-1.1.1.min.js"></script>
    <script type="text/javascript" src="/app/js/bootstrap/bootstrap.min.js"></script>
    <script type="text/javascript" src="/app/js/common/common.js"></script>
    <script type="text/javascript" src="/app/js/common/page.js"></script>
    <script type="text/javascript" src="/app/js/common/crud-grid.js"></script>
    <script type="text/javascript">
        var columns=[
          ${gridItems}
	    ];
        var _url='/cmd/${subModule}/${restRoot}';
        var _baseUrl='${appRoot}';
        var _imgRoot='${imgRoot}';
        var _id = '${r"${id}"}';
        var _dlg;
        $(document).ready(function() {
        	_dlg = new _Panel({cols:columns,url:_url});
		    _dlg._redirectUrl='/app/views/${restRoot}/list.html';
			_dlg.title='${tableDesc?if_exists}信息';
			_dlg.draw();
			Common.ajax(_url, {_t:'da',id:_id}, function(data) {
				 _dlg.render(data);
				 _dlg.endDraw();
			});
        });
    </script>
</head>
<body>
<ul class="nav nav-tabs">
	<li><a href="/app/views/${restRoot}/list.html">${tableDesc?if_exists}列表</a></li>
	<li class="active"><a href="#">${tableDesc?if_exists}编辑</a></li>
</ul>
<div id="searchForm"> 
</div>
<div id="grid">
</div>
<div id="pageBar" class="pagination" style="margin:10px 0;">
</div>
</body>
</html>