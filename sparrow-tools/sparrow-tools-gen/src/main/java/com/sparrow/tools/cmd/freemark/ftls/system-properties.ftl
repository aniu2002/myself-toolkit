pool.name=${name}
pool.${name}.db_type=${type}
pool.${name}.driver=${driver}
pool.${name}.url=${url}
pool.${name}.user=${user}
pool.${name}.password=${password}
pool.${name}.maxIdle=${maxIdle}
pool.${name}.maxActive=${maxActive}
pool.${name}.minIdle=${minIdle}
# minute setting
pool.${name}.maxWait=${maxWait}
pool.${name}.longTimeFlag=<#if (longTimeFlag==1)>true<#else>false</#if>
pool.${name}.showSql=<#if (showSql==1)>true<#else>false</#if>
pool.${name}.formatSql=<#if (formatSql==1)>true<#else>false</#if>

table.mapping.source=config/table-config.xml
default.id.generator=temp_*.id-sequence.seq;*.guid-uuid;*.id-auto