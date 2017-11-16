<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title></title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <meta title="description" content=""/>
    <meta title="keywords" content=""/>
    <link rel="stylesheet" href="/app/bootstrap/css/bootstrap.css" type="text/css"/>
    <link rel="stylesheet" href="/app/bootstrap/css/bootstrap-responsive.css" type="text/css"/>
</head>
<body>
<div class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container-fluid">
            <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>
            <a class="brand" href="#">淘宝团购</a>
        </div>
    </div>
</div>
<br>
<br>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2">
            <div class="well sidebar-nav">
                <ul class="nav nav-list nav-pills nav-stacked">
                    <li class="nav-header">抓取监控组</li>
                    <li><a href="/app/index.html" target="_top">首&nbsp;&nbsp;&nbsp;页</a></li>
                <#list items as data>
                    <li><a href="${data.href}" target="contentFrame">${data.label}</a></li>
                </#list>
                    <li class="divider"></li>
                    <li class="nav-header">服务器状态</li>
                    <li><a href="server_status.html" target="contentFrame">服务探测</a></li>
                    <li><a href="mail.html" target="contentFrame">Mail样本</a></li>
                </ul>
            </div>
        </div>
        <div id="contentDiv" class="span10">
            <iframe id="contentFrame" title="contentFrame" runat="server" frameborder="no" border="0" marginwidth="0"
                    marginheight="0" scrolling="no" allowtransparency="yes"
                    style="width:100%;"></iframe>
        </div>
    </div>
    <hr/>
    <footer>
        <p align="center">抓取组</p>

        <p align="center">全体抓取同仁祝你身体健康，万事如意</p>

        <p align="center">&copy; Company 2010-2013</p>
    </footer>
</div>
<script type="text/javascript" src="/app/bootstrap/jquery-1.7.1.min.js"></script>
<script type="text/javascript">
    var activeEl = null
    function navigatePage(evt) {
        var url = $(this).attr('href')
        if (url == '/app/index.html')
            return;
        evt.stopPropagation();
        evt.preventDefault();
        showPage(url, document.getElementById('contentFrame'), $(this).parent())
    }
    function showPage(url, renderEl, linkEl) {
        setClickedItem(linkEl);
        renderEl.src = url;
    }
    function setClickedItem(linkEl) {
        if (linkEl) {
            if (activeEl)
                activeEl.removeClass("active");
            linkEl.addClass("active");
        }
        activeEl = linkEl
    }
    $(document).ready(function () {
        var hgt = $(window).height() - 180;
        $('#contentFrame').css({
            'height':hgt + 'px'
        });
        $('.nav li a').each(function (i, el) {
            $(this).click(navigatePage)
        });
    });
</script>
</body>
</html>