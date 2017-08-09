package ${servicePackName};

import ${basePackage}.core.db.dao.BaseDao;

<#if services??>
<#list services as serv>
import ${serv};
</#list>
</#if>

public class ServiceManager {
	private static ServiceManager instance;

	private final BaseDao baseDao;
<#if  servClasses??>
<#list servClasses as servClass>
	private final  ${servClass.pojoClassName}Service ${servClass.regularPojoName}Service;
</#list>
</#if>
	private ServiceManager() {
    this.baseDao = new BaseDao();
<#if  servClasses??>
    <#list servClasses as servClass>
    ${servClass.pojoClassName}Service new${servClass.pojoClassName}Srv=new ${servClass.pojoClassName}Service();
    new${servClass.pojoClassName}Srv.setBaseDao(this.baseDao);
    this.${servClass.regularPojoName}Service=new${servClass.pojoClassName}Srv;
    </#list>
</#if>
	}

	private static ServiceManager getInstance() {
	    if (instance == null) {
	        synchronized (ServiceManager.class) {
	            if (instance == null)
	                instance = new ServiceManager();
	        }
	     }
	     return instance;
	}

<#if  servClasses??>
    <#list servClasses as servClass>
	public static  ${servClass.pojoClassName}Service get${servClass.pojoClassName}() {
	    return getInstance().${servClass.regularPojoName}Service;
	}
    </#list>
</#if>
}
