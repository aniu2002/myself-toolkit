<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <meta title="description" content=""/>
    <meta title="keywords" content=""/>
    <title>抓取工程管理系统</title>
    <link rel="stylesheet" href="/app/bootstrap/css/bootstrap.css" type="text/css"/>
    <link rel="stylesheet" href="/app/bootstrap/css/bootstrap-responsive.css" type="text/css"/>
</head>
<body>
<div class="hero-unit">
    <div id="DContainer" style="background-color: #ffffff;">
        <div id="taskDetail" style="margin-top:1px;margin-left:2px;margin-right:2px;background-color: #ffffff;">
            <div style="height: 10px;"></div>
            <div style="margin-top: 13px;margin-left: 7px;background-color: #dddfdd;">
                <div style="margin-left: 12px;font-size:13;font-weight: bold;">- 探测信息</div>
                <div style="margin-left: 24px;">
                <#list items as item>
                    <#if (item.status==200)>
                        - ${item.url?if_exists} &nbsp; - 返回code:${item.status}
                        <#else>
                            - <span style="color: #ff0000;">${item.url?if_exists} </span> &nbsp; - 返回异常:${item.status}次
                    </#if>
                </#list>
                </div>
            </div>
            <div style="height: 10px;"></div>
        </div>
    </div>
</div>
</body>
</html>