package ${table.pakage};

<#if table.imports??>
<#list table.imports as impt>
import ${impt};
</#list>
</#if>

/**
 *	表模型: "${table.desc?if_exists}" <br/>
 *  ============================== <br/>
 *	选择: ${_select?if_exists} <br/>
 *	统计: ${_count?if_exists} <br/>
 *	插入: ${_insert?if_exists} <br/>
 *	更新: ${_update?if_exists} <br/>
 *	删除: ${_delete?if_exists} <br/>
 *  ============================== <br/>
 * @author YZC
 */
public class ${table.objName} {
<#assign items = table.items>
<#list items as data>
	/** ${data.desc?if_exists}(${data.name}) */
	private ${data.sampleType} ${data.fieldName};
</#list>

<#list items as data>
	/**
	 * 
	 * 获取${data.desc?if_exists}值    
	 *  
	 * @return ${data.desc?if_exists}(${data.sampleType})
	 */
	public ${data.sampleType} get${data.fieldNameX}() {
		return ${data.fieldName};
	}
	
	/**
	 * 
	 * 设置${data.desc?if_exists}值   
	 *  
	 * @param ${data.fieldName} 
	 *        ${data.desc?if_exists}
	 */
	public void set${data.fieldNameX}(${data.sampleType} ${data.fieldName}) {
		this.${data.fieldName} = ${data.fieldName};
	}
</#list>
}