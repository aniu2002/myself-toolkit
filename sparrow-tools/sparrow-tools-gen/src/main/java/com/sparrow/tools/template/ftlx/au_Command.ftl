package ${controllerPackage};

/* package ${controllerPackage}; */
 
<#if (pojoIdType??)>import ${pojoIdType};</#if>

/* import ${pojoClass}; */
import ${basePackage}.domain.${pojoClassName};
import com.sparrow.core.http.command.Request;
import com.sparrow.core.http.command.Response;
import com.sparrow.core.http.command.BeanWrapper;
import com.sparrow.core.http.command.BaseCommand;
import com.sparrow.core.http.command.resp.FreeMarkerResponse;
import com.sparrow.core.http.command.resp.JsonResponse;
import com.sparrow.core.http.command.resp.OkResponse;

import ${servicePackName}.${pojoClassName}Service;

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

    ${pojoClassName}Service ${regularPojoName}Service;

    public void set${pojoClassName}Service(${pojoClassName}Service ${regularPojoName}Service){
        this.${regularPojoName}Service=${regularPojoName}Service;
    }

	public Response doPost(Request request) {
	    ${pojoClassName} ${regularPojoName} = BeanWrapper.wrapBean(${pojoClassName}.class, request);
        this.${regularPojoName}Service.add(${regularPojoName});
	    return OkResponse.OK;
	}
	
	public Response doDelete(Request request) {
	    boolean m = request.hasMuiltVal("id");
	    if (m)
	        this.${regularPojoName}Service.batchDelete(request.getLongList("id"));
	    else
	        this.${regularPojoName}Service.delete(request.getLong("id"));
	    return OkResponse.OK;
	}
	
	public Response doPut(Request request) {
	    ${pojoClassName} ${regularPojoName}  = BeanWrapper.wrapBean(${pojoClassName}.class, request);
	    this.${regularPojoName}Service.update(${regularPojoName});
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
	        ${pojoClassName} ${regularPojoName}=this.${regularPojoName}Service.get(id);
	        return new JsonResponse(${regularPojoName});
	    } else {
	        int page = request.getInt("page"), limit = request.getInt("limit",20);
	        ${pojoClassName} ${regularPojoName} = BeanWrapper.wrapBean(${pojoClassName}.class, request);
	        Object data=this.${regularPojoName}Service.pageQuery(${regularPojoName}, page, limit);
	        return new JsonResponse(data);
	    }
	}
 }