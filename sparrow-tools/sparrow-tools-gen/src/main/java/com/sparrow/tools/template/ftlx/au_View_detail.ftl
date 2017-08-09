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
	    var _id = '${r"${id}"}';
        var _url='/cmd/${subModule}/${restRoot}?_t=da&id='+id;
        $(document).ready(function() {
       		 var dd=new _DetailDlg({cols:columns});
       		 dd.drawDirect(_url,'#grid');
        });
    </script>
</head>
<body>
<ul class="nav nav-tabs">
	<li><a href="/app/views/${restRoot}/list.html">${tableDesc?if_exists}列表</a></li>
	<li class="active"><a href="#">${tableDesc?if_exists}详情信息</a></li>
</ul>
<div id="grid">
</div>
</body>
</html>