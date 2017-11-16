package ${reqPack};

<#if (fieldImports??)>
    <#list fieldImports as da>
import ${da};
    </#list>
</#if>

/**
* Created by yzc on 2016/10/25.
*/
public class ${reqClass} {
<#if (fields??)>
<#assign items = fields>
<#list items as data>
   /**
    *  ${data.fieldName} (${data.desc?if_exists})
    */
    private ${data.fieldType} ${data.fieldName};
</#list>

<#list items as data>
   /**
    *
    * 获取${data.desc?if_exists}值
    *
    * @return ${data.desc?if_exists}(${data.fieldType})
    */
    public ${data.fieldType} get${data.fieldNameX}() {
        return ${data.fieldName};
    }

   /**
    *
    * 设置${data.desc?if_exists}值
    *
    * @param ${data.fieldName}
    *        ${data.desc?if_exists}
    */
    public void set${data.fieldNameX}(${data.fieldType} ${data.fieldName}) {
        this.${data.fieldName} = ${data.fieldName};
    }
</#list>
</#if>
}