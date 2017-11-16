#${title?if_exists}

## HTTP | HTTPS://${host?if_exists}${context?if_exists}

${description?if_exists}

[**Contact the developer**](mailto:${email?if_exists})

**Version** ${version?if_exists}

[**Terms of Service**](http://www.github.com/kongchen/swagger-maven-plugin)

[**Apache 2.0**](http://www.apache.org/licenses/LICENSE-2.0.html)

# APIs

<#if (services??)>
<#list services as srv>
## ${context}${srv.path}

### ${srv.method?upper_case}

<a id="definition">${srv.operationId}</a>

${srv.description}

#### Request

##### Parameters

<table border="1">
    <tr>
        <th>Name</th>
        <th>Located in</th>
        <th>Required</th>
        <th>Description</th>
        <th>Default</th>
        <th>Schema</th>
    </tr>
<#if (srv.params??)>
<#list srv.params as param>
<tr>
    <td>${param.title}</td>
    <td>${param.type}</td>
    <td>${param.required?string('true','false')}</td>
    <td>${param.ref}</td>
    <td> - </td>
    <td>
<#if param.complex>
    <a href="#/definitions/${param.ref}">${param.ref}</a>
<#else>
    ${param.ref}<#if (param.format??)> (${param.format})</#if>
</#if>
    </td>
</tr>
</#list>
</#if>
</table>

#### Response

<table border="1">
    <tr>
        <th>Status Code</th>
        <th>Reason</th>
        <th>Response Model</th>
    </tr>
    <tr>
        <td>${srv.response.status}</td>
        <td>${srv.response.description} successful operation </td>
        <td><#if srv.response.complex><a href="#/definitions/${srv.response.ref}">${srv.response.ref}</a><#else>${srv.response.ref}<#if (srv.response.format??)> (${srv.response.format})</#if></#if></td>
    </tr>
</table>
</#list>
</#if>

# Definitions

<#if (objects??)>
<#list objects as obj>
## <a title="/definitions/${obj.title}">${obj.title}</a>

<table border="1">
    <tr>
        <th>title</th>
        <th>type</th>
        <th>required</th>
        <th>description</th>
        <th>example</th>
    </tr>
<#if (obj.props??)>
<#list obj.props as prop>
        <tr>
            <td>${prop.title}</td>
            <td>
<#if prop.complex>
                <a href="#/definitions/${prop.type}">${prop.type}</a>
<#else>
                ${prop.type}<#if (prop.format??)> (${prop.format})</#if>
</#if>
            </td>
            <td>optional</td>
            <td>-</td>
            <td>-</td>
        </tr>
</#list>
</#if>
</table>
</#list>
</#if>

