import ${cmdPackage};

import ${basePackage}.http.command.EventController;

public abstract CmdReg{
	public static void regCommands(EventController controller) {
		if (this.controller != null){
<#if servClasses??>
    <#list servClasses as da>
        	this.controller.regCommand("${da.subModule?if_exists}/${da.pojo4LowerCase?if_exists}", new ${cmdPackage}.${da.subModule}.${da.pojoClassName}Command());
    </#list>
</#if>
       }
	}
}