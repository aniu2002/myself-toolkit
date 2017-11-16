package ${table.pakage};

import java.io.Serializable;
<#if table.imports??>
<#list table.imports as impt>
import ${impt};
</#list>
</#if>

/**
 *	DTO 对应表: "${table.desc?if_exists}" <br/>
 *  ============================== <br/>
 * @author YZC
 */
public class ${table.objName}Dto implements Serializable {
	/** 序列化版本ID */
	private static final long serialVersionUID = 1L;
<#assign items = table.items>
<#list items as data>
	/** ${data.desc?if_exists}(${data.title})
	 *  ${data.fieldName} (${data.desc?if_exists})
	 */
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