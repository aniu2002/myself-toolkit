<div style="height: 10px;"></div>
<div style="margin-top: 13px;margin-left: 7px;margin-right:7px;background-color: #ffffff;">
<#assign appInfo = data.app>
<#assign location = data.location>
<#if appInfo??>
    <div style="color: #003399;font-weight:bolder;margin-left: 6px;padding-top: 7px;">
        ${appInfo.group} - ${appInfo.name}&nbsp;(${data.date})
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