package ${table.pakage};

<#if table.imports??>
<#list table.imports as impt>
import ${impt};
</#list>
</#if>

import com.sparrow.core.orm.annotation.Table;
<#if table.primaryKeys??>
import com.sparrow.core.orm.annotation.Key;
</#if>
import com.sparrow.core.orm.annotation.Column;

@Table(table = "${table.name}" , desc = "${table.desc?if_exists}")
public class ${table.objName} {
<#assign items = table.items>
<#list items as data>
	<#if (data.primary==true)>@Key(column = "${data.name}", type = ${data.sqlType?c}, <#if (data.generator??)> generator = "${data.generator}", </#if> <#if (data.notNull==true)> notnull = true,</#if> <#if (data.size!=0)> length = ${data.size?c},</#if> comment = "${data.desc?if_exists}")
	private ${data.sampleType} ${data.fieldName};
<#else>@Column(column = "${data.name}", type = ${data.sqlType?c}, <#if (data.notNull==true)>notnull = true,</#if><#if (data.size!=0)>length = ${data.size?c},</#if> comment = "${data.desc?if_exists}")
	private ${data.sampleType} ${data.fieldName};
	</#if>
</#list>

<#list items as data>
	public ${data.sampleType} get${data.fieldNameX}() {
		return ${data.fieldName};
	}

	public void set${data.fieldNameX}(${data.sampleType} ${data.fieldName}) {
		this.${data.fieldName} = ${data.fieldName};
	}
</#list>
}