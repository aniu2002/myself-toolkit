package ${controllerPackage};

/* package ${controllerPackage}; */
 
<#if (pojoIdType??)>import ${pojoIdType};</#if>

/* import ${pojoClass}; */
import ${pojoClass};
//import com.dili.dd.cornucopia.bps.domain.${pojoClassName};
import ${basePackage}.http.controller.BaseCommand;
import ${basePackage}.http.controller.FreeMarkerResponse;
import ${basePackage}.http.controller.JsonResponse;
import ${basePackage}.http.controller.OkResponse;
import ${basePackage}.http.controller.Request;
import ${basePackage}.http.controller.Response;
import ${basePackage}.http.controller.BeanWrapper;
import ${servicePackName}.ServiceManager;

/**
 *
 * 完成(${table}-${tableDesc?if_exists})的基本操作<br/>
 *
 * ${table}: ${tableDesc?if_exists}
 *
 * @author YZC
 * @version ${version?if_exists}
 * date: ${dateTime?if_exists}
*/
public class ${pojoClassName}Command extends BaseCommand {

	public Response doPost(Request request) {
	    ${pojoClassName} ${regularPojoName} = BeanWrapper.wrapBean(${pojoClassName}.class, request);
	    ServiceManager.get${pojoClassName}().add(${regularPojoName});
	    return OkResponse.OK;
	}
	
	public Response doDelete(Request request) {
	    boolean m = request.hasMuiltVal("id");
	    if (m)
	        ServiceManager.get${pojoClassName}().batchDelete(request.getLongList("id"));
	    else
	        ServiceManager.get${pojoClassName}().delete(request.getLong("id"));
	    return OkResponse.OK;
	}
	
	public Response doPut(Request request) {
	    ${pojoClassName} ${regularPojoName}  = BeanWrapper.wrapBean(${pojoClassName}.class, request);
	    ServiceManager.get${pojoClassName}().update(${regularPojoName});
	    return OkResponse.OK;
	}
	
	public Response doGet(Request request) {
	    String t = request.get("_t");
	    if ("et".equals(t)) {
	        return new FreeMarkerResponse("${pojo4LowerCase}/edit", request.getParas());
	    } else if ("dt".equals(t)) {
	        return new FreeMarkerResponse("${pojo4LowerCase}/detail",request.getParas());
	    } else if ("da".equals(t)) {
	        long id=request.getLong("id");
	        ${pojoClassName} ${regularPojoName}=ServiceManager.get${pojoClassName}().get(id);
	        return new JsonResponse(${regularPojoName});
	    } else {
	        int page = request.getInt("page"), limit = request.getInt("limit");
	        ${pojoClassName} ${regularPojoName} = BeanWrapper.wrapBean(${pojoClassName}.class, request);
	        Object data=ServiceManager.get${pojoClassName}().pageQuery(${regularPojoName}, page, limit);
	        return new JsonResponse(data);
	    }
	}
 }