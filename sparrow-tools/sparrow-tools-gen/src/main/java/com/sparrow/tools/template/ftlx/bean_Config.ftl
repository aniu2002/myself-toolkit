<?xml version="1.0" encoding="UTF-8"?>
<beans>
    <bean name="sessionFactory" clazz="com.sparrow.core.orm.session.simple.ConfigureSessionFactory" init="initialize">
        <property name="configFile" value="${jdbcConfigPath}" />
        <property name="mapXml" value="${mapConfigPath}" />
        <!--<property name="mappingSource" value="conf/table-conf.xml" />-->
    </bean>

    <bean name="operateTemplate" clazz="com.sparrow.core.orm.template.simple.NormalOperateTemplate">
        <property name="sessionFactory" ref="sessionFactory" />
    </bean>

    <bean name="baseDao" clazz="com.sparrow.core.orm.dao.simple.NormalDao">
        <property name="operateTemplate" ref="operateTemplate" />
        <!-- <property name="mapXml" value="${mapConfigPath}" /> -->
    </bean>

    <!-- service bean configuration -->
<#if servClasses??>
    <#list servClasses as c>
    <bean name="${c.regularPojoName}Service" clazz="${c.servicePackName}.${c.pojoClassName}Service">
        <property name="baseDao" ref="baseDao" />
    </bean>
    </#list>
</#if>

    <!-- command bean configuration -->
<#if servClasses??>
    <#list servClasses as da>
    <bean name="${da.regularPojoName}Cmd" parameter="${da.subModule}/${da.pojo4LowerCase}" clazz="${cmdPackage}.${da.subModule}.${da.pojoClassName}Command">
        <property name="${da.regularPojoName}Service" ref="${da.regularPojoName}Service" />
    </bean>
    </#list>
</#if>
</beans>