package ${modelPack};

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

<#if (fieldImports??)>
    <#list fieldImports as da>
import ${da};
    </#list>
</#if>
/**
* @author yzc
* @time 2016年10月25日 下午11:07:44
*/
@Api(value = "/api/${module}", tags = "${moduleX} Service")
@Controller
@RequestMapping("/api/${module}")
public class ${moduleX}RestService {

<#assign items = requestWraps>
<#list items as data>
    /**
    * 根据用户名获取用户对象
    *
    * @param ${data.reqClassL}
    * @return
    */
    @RequestMapping(value = "${data.reqMapL}", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "${data.desc?if_exists}", httpMethod = "POST", response = <#if data.replyValid>${data.respClass}<#else>OperationSucceed</#if>.class, notes = "${data.desc?if_exists} service endpoint")
    @ApiResponse(message = "<#if data.replyValid>${data.respClass}<#else>OperationSucceed</#if>", code = 200)
    public <#if data.replyValid>${data.respClass}<#else>OperationSucceed</#if> ${data.reqPathL}(@ApiParam(required = true, title = "title", value = "${data.reqClass}") @RequestBody ${data.reqClass} ${data.reqClassL} <#if (data.params??)><#list data.params as itm>,@RequestParam(title = "${itm.paraName}") ${itm.paraType} ${itm.paraName}</#list></#if>)
    {
        return new <#if data.replyValid>${data.respClass}<#else>OperationSucceed</#if>();
    }
</#list>
}