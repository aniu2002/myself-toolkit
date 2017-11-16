pool.title=${title}
pool.${title}.db_type=${type}
pool.${title}.driver=${driver}
pool.${title}.url=${url}
pool.${title}.user=${user}
pool.${title}.password=${password}
pool.${title}.maxIdle=${maxIdle}
pool.${title}.maxActive=${maxActive}
pool.${title}.minIdle=${minIdle}
# minute setting
pool.${title}.maxWait=${maxWait}
pool.${title}.longTimeFlag=<#if (longTimeFlag==1)>true<#else>false</#if>
pool.${title}.showSql=<#if (showSql==1)>true<#else>false</#if>
pool.${title}.formatSql=<#if (formatSql==1)>true<#else>false</#if>

table.mapping.source=config/table-config.xml
default.id.generator=temp_*.id-sequence.seq;*.guid-uuid;*.id-auto