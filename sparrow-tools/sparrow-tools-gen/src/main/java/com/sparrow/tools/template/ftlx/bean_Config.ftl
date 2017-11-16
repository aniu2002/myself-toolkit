<?xml version="1.0" encoding="UTF-8"?>
<beans>
    <bean title="sessionFactory" clazz="com.sparrow.core.orm.session.simple.ConfigureSessionFactory" init="initialize">
        <property title="configFile" value="${jdbcConfigPath}" />
        <!--<property title="mappingSource" value="conf/table-conf.xml" />-->
    </bean>

    <bean title="operateTemplate" clazz="com.sparrow.core.orm.template.simple.NormalOperateTemplate">
        <property title="sessionFactory" ref="sessionFactory" />
    </bean>

    <bean title="baseDao" clazz="com.sparrow.core.orm.dao.simple.NormalDao">
        <property title="operateTemplate" ref="operateTemplate" />
        <property title="mapXml" value="${mapConfigPath}" />
    </bean>

    <!-- service bean configuration -->
<#if servClasses??>
    <#list servClasses as c>
    <bean title="${c.regularPojoName}Service" clazz="${c.servicePackName}.${c.pojoClassName}Service">
        <property title="baseDao" ref="baseDao" />
    </bean>
    </#list>
</#if>

    <!-- command bean configuration -->
<#if servClasses??>
    <#list servClasses as da>
    <bean title="${da.regularPojoName}Cmd" parameter="${da.subModule}/${da.pojo4LowerCase}" clazz="${cmdPackage}.${da.subModule}.${da.pojoClassName}Command">
        <property title="${da.regularPojoName}Service" ref="${da.regularPojoName}Service" />
    </bean>
    </#list>
</#if>
</beans>