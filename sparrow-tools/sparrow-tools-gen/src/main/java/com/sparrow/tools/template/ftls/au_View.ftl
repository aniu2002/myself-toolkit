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
          'x',
          ${gridItems},
          {name:'op',label:'操作',width:'90',align:'left',render:function(v,data){
        	  v=data.id;
        	  var s='<a href="/cmd/${subModule}/${restRoot}?_t=et&id='+v+'">编辑</a>&nbsp;<a href="#" onclick="delRow('+v+')">删除</a>';
        	  return s;
          },noe:true}
	    ];
        var _url='/cmd/${subModule}/${restRoot}';
        var _baseUrl='${appRoot}';
        var _imgRoot='${imgRoot}';
        var _UI;
       
        var rowDblClick=function(r){
        	self.location="/cmd/${subModule}/${restRoot}?_t=et&id="+r._id;
        };
        var delRow = function(v){
        	_CRUD_.openAlert({
				confirm : '确认删除该记录么?',
				ok : function() {
					_CRUD_.deleteRow(_UI.url, v, _UI.grid);
				}
			});
        };
        $(document).ready(function() {
            _CRUD_.baseUrl=_imgRoot;
        	_UI = _CRUD_.renderTable({
            	url:_url,
            	cols:columns,
            	<#if (idFeild??)>idFeild:'${idFeild}', </#if>
            	searchEl:'#searchForm',
            	rowDblClick:rowDblClick,
            	opBar: _CRUD_.opBar,
            	showDetail:true,
            	showMenu:true,
            	canOp:true
             });
        });
    </script>
</head>
<body>
<ul class="nav nav-tabs">
	<li class="active"><a href="#">${tableDesc?if_exists}列表</a></li>
	<li><a href="add.html">${tableDesc?if_exists}添加</a></li>
</ul>
<div id="searchForm"> 
</div>
<div id="grid">
</div>
<div id="pageBar" class="pagination" style="margin-top:0px;">
</div>
</body>
</html>