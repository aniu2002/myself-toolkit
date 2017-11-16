#Agent端REST服务文档

## HTTP | HTTPS://127.0.0.1:8090/api

Agent端REST接口api文档

[**Contact the developer**](mailto:yuanzhengchu@unionbigdata.com)

**Version** v1

[**Terms of Service**](http://www.github.com/kongchen/swagger-maven-plugin)

[**Apache 2.0**](http://www.apache.org/licenses/LICENSE-2.0.html)

# APIs

## /api/job/{runningId}/{jobId}

### GET

<a id="definition">{runningId}/{jobId}</a>

{runningId}/{jobId} Service Endpoint

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
<tr>
    <td>jobId</td>
    <td>path</td>
    <td>true</td>
    <td>string</td>
    <td> - </td>
    <td>
    string
    </td>
</tr>
<tr>
    <td>runningId</td>
    <td>path</td>
    <td>true</td>
    <td>integer</td>
    <td> - </td>
    <td>
    integer (int64)
    </td>
</tr>
</table>

#### Response

<table border="1">
    <tr>
        <th>Status Code</th>
        <th>Reason</th>
        <th>Response Model</th>
    </tr>
    <tr>
        <td>200</td>
        <td>String text successful operation </td>
        <td>string</td>
    </tr>
</table>

# Definitions

## <a title="/definitions/SimpleRequest">SimpleRequest</a>

<table border="1">
    <tr>
        <th>title</th>
        <th>type</th>
        <th>required</th>
        <th>description</th>
        <th>example</th>
    </tr>
        <tr>
            <td>title</td>
            <td>
                string
            </td>
            <td>optional</td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>sex</td>
            <td>
                string
            </td>
            <td>optional</td>
            <td>-</td>
            <td>-</td>
        </tr>
</table>

