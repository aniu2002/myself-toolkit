package ${servicePackName};

import com.sparrow.orm.dao.simple.NormalDao;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.orm.template.simple.NormalOperateTemplate;

<#if services??>
<#list services as serv>
import ${serv};
</#list>
</#if>

public class ServiceManager {
	private static ServiceManager instance;

    private final NormalDao baseDao;
<#if  servClasses??>
<#list servClasses as servClass>
	private final  ${servClass.pojoClassName}Service ${servClass.regularPojoName}Service;
</#list>
</#if>
	private ServiceManager() {
        SessionFactory sessionFactory=SessionFactory.configureFactory("classpath:conf/jdbc.properties");
        NormalOperateTemplate operateTemplate=  new NormalOperateTemplate();
        operateTemplate.setSessionFactory(sessionFactory);

        this.baseDao = new NormalDao();
        this.baseDao.setOperateTemplate(operateTemplate);
        // this.baseDao.setMapXml("classpath:conf/pojo.xml");
        sessionFactory.setMapXml("classpath:conf/pojo.xml");

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
