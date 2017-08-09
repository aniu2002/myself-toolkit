<?xml version="1.0" encoding="UTF-8"?>
<commands>
<#if servClasses??>
    <#list servClasses as da>
     <command path="${da.subModule}/${da.pojo4LowerCase}" clazz="${cmdPackage}.${da.subModule}.${da.pojoClassName}Command" />
    </#list>
</#if>
</commands>