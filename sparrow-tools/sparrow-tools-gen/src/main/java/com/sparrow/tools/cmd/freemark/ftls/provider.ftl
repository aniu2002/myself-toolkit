<?xml version="1.0" encoding="UTF-8"?>
<data>
    <sources>
    <#list sources as im>
    <source name="${im.name?if_exists}" type="${im.type?if_exists}" desc="${im.desc?if_exists}">
        <![CDATA[
${im.props?if_exists}
        ]]>
        </source>
    </#list>
    </sources>

    <providers>
    <#list data as itm>
        <provider app="${itm.app?if_exists}" source="${itm.source?if_exists}" name="${itm.table?if_exists}" clazz="${itm.pack?if_exists}.${itm.clazzName?if_exists}" desc="${itm.desc?if_exists}">
            <![CDATA[
        ${itm.selectSql?if_exists}
            ]]>
        </provider>
    </#list>
    </providers>
</data>