{
  "swagger": "2.0",
  "info": {
    "description": "${description?if_exists}",
    "version": "${version?if_exists}",
    "title": "${title?if_exists}",
    "termsOfService": "http://www.github.com/kongchen/swagger-maven-plugin",
    "contact": {
      "title": "${author?if_exists}",
      "url": "${site?if_exists}",
      "email": "${email?if_exists}"
    },
    "license": {
      "title": "Apache 2.0",
      "url": "http://www.apache.org/licenses/LICENSE-2.0.html"
    }
  },
  "host": "${host?if_exists}",
  "basePath": "${context?if_exists}",
  "tags": [
<#if (tags??)>
<#list tags as itm>
    <#if itm_index gt 0>,</#if>
    {
      "title": "${itm.title?if_exists}"
    }
</#list>
</#if>
  ],
  "schemes": [
    "http",
    "https"
  ],
  "paths": {
<#if (services??)>
<#list services as srv>
    <#if srv_index gt 0>,</#if>
    "${srv.path}": {
      "${srv.method}": {
        "tags": [
          "${srv.tag}"
        ],
        "summary": "${srv.summary}",
        "description": "${srv.description}",
        "operationId": "${srv.operationId}",
<#if (srv.consume??)>
        "consumes": [
            "${srv.consume}"
        ],
</#if>
<#if (srv.produce??)>
        "produces": [
            "${srv.produce}"
        ],
</#if>
        "parameters": [
<#if (srv.params??)>
<#list srv.params as param>
          <#if param_index gt 0>,</#if>
          {
            "in": "${param.type}",
            "title": "${param.title}",
            "description": "${param.description}",
            "required": ${param.required?string('true','false')},
            "schema": {
<#if param.complex>
              "$ref": "#/definitions/${param.ref}"
<#else>
              "type" : "${param.ref}"<#if (param.format??)>,
              "format": "${param.format}"</#if>
</#if>
            }
          }
</#list>
</#if>
        ],
        "responses": {
          "${srv.response.status}": {
            "description": "${srv.response.description}",
            "schema": {
<#if srv.response.complex>
              "$ref": "#/definitions/${srv.response.ref}"
<#else>
              "type" : "${srv.response.ref}"<#if (srv.response.format??)>,
              "format": "${srv.response.format}"</#if>
</#if>
            }
          }
        }
      }
    }
</#list>
</#if>
  },
  "definitions": {
<#if (objects??)>
<#list objects as obj>
    <#if obj_index gt 0>,</#if>
    "${obj.title}": {
      "type": "${obj.type}",
      "properties": {
<#if (obj.props??)>
<#list obj.props as prop>
        <#if prop_index gt 0>,</#if>
        "${prop.title}": {
<#if prop.complex>
          "$ref" : "#/definitions/${prop.type}"
<#else>
          "type": "${prop.type}"<#if (prop.format??)>,
          "format": "${prop.format}"</#if>
</#if>
        }
</#list>
</#if>
      }
    }
</#list>
</#if>
  }
}