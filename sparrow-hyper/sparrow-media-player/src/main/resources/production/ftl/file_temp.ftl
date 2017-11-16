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
<div style="margin-left: 0px;margin-top: 5px;">
    <div id="DContainer" style="background-color: #ffffff;overflow-y: auto;">
        <div id="taskDetail" style="margin-top:1px;margin-left:2px;margin-right:2px;background-color: #ffffff;">
            <div style="background-color: #dddddd;">
                <div style="margin-top: 12px;margin-left: 5px;font-weight:bolder;font-size:14;">监控组：${group}</div>
            <#list items as data>
                <div style="margin-top: 13px;margin-left: 7px;margin-right:7px;background-color: #ffffff;">
                    <#assign appInfo = data.app>
                    <#assign location = data.location>
                    <#if appInfo??>
                        <div style="color: #003399;font-weight:bolder;margin-left: 6px;padding-top: 7px;">应用程序
                            - ${appInfo.title}&nbsp;(${data.date})
                        </div>
                        <div style="margin-left: 12px;font-size:13;font-style:italic;">- 基本信息</div>
                        <div style="margin-left: 24px;font-size: 12;">
                            <span style="color:#003399;">归属组：</span>  ${appInfo.group} &nbsp;&nbsp; <span
                                style="color:#003399;">版本：</span> ${appInfo.version} &nbsp;&nbsp;<br/>
                            <#if location??>
                                <span style="color:#003399;">IP地址：</span>${location.ip} &nbsp;&nbsp; <span
                                    style="color:#003399;">主机名：</span> ${location.host} <br/>
                                <span style="color:#003399;">执行程序：</span>${location.shell?if_exists} &nbsp;&nbsp;
                                <span style="color:#003399;">日志文件：</span>${location.log?if_exists} <br/>
                            </#if>
                            <span style="color:#003399;">描述：</span>${appInfo.desc?if_exists}
                        </div>
                    </#if>
                    <#assign machine = data.machine>
                    <#if machine??>
                        <div style="margin-left: 12px;font-size:13;font-style:italic;">- 资源情况</div>
                        <div style="margin-left: 24px;">
                            <#if machine.mem??> <span style="color:#003399;">内存使用：</span> ${machine.mem}
                                &nbsp;&nbsp; </#if>
                            <#if machine.cpu??> <span style="color:#003399;">CPU使用：</span> ${machine.cpu}
                                &nbsp;&nbsp; </#if>
                            <#if machine.disk??> <span style="color:#003399;">硬盘使用：</span> ${machine.disk}
                                &nbsp;&nbsp; </#if>
                        </div>
                    </#if>

                    <#assign extInfo = data.infos>
                    <#if extInfo??>
                        <div style="margin-left: 12px;font-size:13;font-style:italic;">- 探测信息</div>
                        <div style="margin-left: 24px;">
                            <#list extInfo as item>
                                <#if (item.flag==-1)>
                                    - <span style="color: #ff0000;">${item.info?if_exists} </span> &nbsp; - 出现${item.counter}次<br/>
                                <#else>
                                    - ${item.info?if_exists} &nbsp; - 出现${item.counter}次 <br/>
                                </#if>
                            </#list>
                        </div>
                    </#if>
                </div>
            </#list>
                <div style="height: 10px;"></div>
            </div>
            <div style="height: 10px;"></div>
        </div>
    </div>
</div>
<script src="/app/bootstrap/jquery-1.7.1.min.js" type="text/javascript"></script>
<script type="text/javascript">
    $(document).ready(function() {
        var hgt = $(window).height() - 30;
        $('#DContainer').css({
            'height':hgt + 'px'
        });
    });
</script>
</body>
</html>