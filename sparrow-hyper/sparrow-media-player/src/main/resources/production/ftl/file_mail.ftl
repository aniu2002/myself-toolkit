<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <meta title="description" content=""/>
    <meta title="keywords" content=""/>
    <title>抓取工程管理系统</title>
<#if showWeb??>
    <link rel="stylesheet" href="/app/bootstrap/css/bootstrap.css" type="text/css"/>
    <link rel="stylesheet" href="/app/bootstrap/css/bootstrap-responsive.css" type="text/css"/>
</#if>
</head>
<body>
<div style="margin-left: 30px;margin-top: 35px;">
    <div id="DContainer" style="background-color: #ffffff;overflow-y: auto;">
        <div id="taskDetail" style="margin-top:1px;margin-left:2px;margin-right:2px;background-color: #ffffff;">
        <#if detail??>
            <div style="height: 10px;"><a href="${detail}">查看详情</a></div></#if>
            <table cellpadding="0" cellspacing="0" border="1"
                   style="border-style: solid;border-color: black;border-collapse: collapse;">
                <tr>
                    <th width="150" align="center">程序组</th>
                    <th width="150">应用程序</th>
                    <th width="200">主机信息</th>
                    <th width="220">资源情况</th>
                    <th width="350">探测信息</th>
                    <th width="120">更新时间</th>
                </tr>
            <#list items as itm>
                <#list itm.merges as data>
                    <#assign a=data_index />
                    <tr>
                        <#if a==0>
                            <td rowspan="${itm.mergeCount}" align="center">
                            ${itm.group}
                            </td>
                        </#if>
                        <#assign appInfo = data.app>
                        <#assign location = data.location>
                        <#if appInfo??>
                            <td align="center"> ${appInfo.title}(${appInfo.version})</td>
                            <td align="center">
                                <#if appInfo.desc??>${appInfo.desc}</#if>
                                <br/>
                                <#if location??>
                                    <#if location.ip??>${location.ip}</#if>
                                    <br/>
                                    <#if location.host??>(${location.host})</#if>
                                </#if>
                            </td>
                        </#if>
                        <#assign machine = data.machine>
                        <#if machine??>
                            <td align="center">
                                <#if machine.mem??>内存:${machine.mem} <br/></#if>
                                <#if machine.cpu??>CPU:${machine.cpu} <br/></#if>
                                <#if machine.disk??>硬盘:${machine.disk}<br/></#if>
                            </td>
                        </#if>

                        <#assign extInfo = data.infos>
                        <#if extInfo??>
                            <td valign="top">
                                <#list extInfo as item>
                                    <#if (item.flag==-1)>
                                        - <span style="color: #ff0000;">${item.info?if_exists}</span>（${item.counter}
                                        ）<br/>
                                        <#else>
                                            - ${item.info?if_exists}（${item.counter}）<br/>
                                    </#if>
                                </#list>
                            </td>
                        </#if>
                        <td align="center">${data.date}</td>
                    </tr>
                </#list>
            </#list>
            </table>
        </div>
    </div>
</div>
<#if showWeb??>
<script src="/app/bootstrap/jquery-1.7.1.min.js" type="text/javascript"></script>
<script type="text/javascript">
    $(document).ready(function() {
        var hgt = $(window).height() - 30;
        $('#DContainer').css({
            'height':hgt + 'px'
        });
    });
</script>
</#if>
</body>
</html>