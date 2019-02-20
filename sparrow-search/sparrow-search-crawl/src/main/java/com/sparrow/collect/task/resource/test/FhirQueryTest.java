package com.sparrow.collect.task.resource.test;

import com.sparrow.collect.crawler.httpclient.CrawlKit;
import com.sparrow.collect.crawler.httpclient.HttpResp;
import com.sparrow.collect.utils.FileIOUtil;
import com.sparrow.collect.utils.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Yzc on 2017/6/8.
 */
public class FhirQueryTest {
    static final String NONE = "none";
    static final String UN_HANDLE = "-1";
    static final String NEED_RESULT = "1";
    static final String EMPTY_RESULT = "0";
    static final String TYPE_OPERATION_OUTCOME = "OperationOutcome";
    static final String TYPE_BUNDLE = "Bundle";
    static final String TYPE_PATIENT = "Patient";
    static final String TYPE_SEARCH_SET = "searchset";
    static CrawlKit kit = CrawlKit.KIT;
    static final Charset UTF8 = Charset.forName("UTF-8");
    //static final Logger logger = LoggerFactory.getLogger(FhirQueryTest.class);

    public static void main(String args[]) {
        String json = FileIOUtil.readString("classpath:fhir/query-params.json");
        Map st = JsonMapper.bean(json, Map.class);
        new FhirQueryTest().postResource(st);
    }

    String format(String url) {
        StringBuilder sb = new StringBuilder();
        int idx = url.indexOf('?');
        if (idx == -1)
            return url;
        return sb.append(url.substring(0, idx)).append('?').append(this.encodeQuery(url.substring(idx + 1))).toString();
    }

    int randomIdx(int n) {
        return new Random(3).nextInt(n);
    }

    void updateEncounterPatientRef(Map<String, String> map) {
        String base = map.get("resBase");
        HttpResp resp = kit.get(String.format("%s/Encounter?_count=10", base));
        if (resp.getStatus() != 200)
            return;
        JsonNode node = JsonMapper.jsonNode(resp.getHtml());
        JsonNode entry = node.get("entry");
        if (entry == null)
            return;
        int size = entry.size();
        int idx = randomIdx(size);
        JsonNode first = entry.get(idx);
        if (first == null)
            return;
        JsonNode resNode = first.get("resource");
        if (resNode == null)
            return;
        String id = resNode.get("id").getTextValue();
        JsonNode patient = resNode.get("patient");
        if (patient != null) {
            ObjectNode objectNode = (ObjectNode) patient;
            objectNode.put("reference", String.format("Patient/%s", map.get("id")));
        }
        ObjectNode rn = (ObjectNode) resNode;
        rn.put("status", "planned");
        resp = kit.put(String.format("%s/Encounter/%s", base, id), JsonMapper.node2String(resNode));

        System.out.println(resp.getHtml());
    }

    String encodeQuery(String query) {
        String[] pairs = StringUtils.split(query, '&');
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String pair : pairs) {
            if (first)
                first = false;
            else
                sb.append('&');
            this.pairAppendToSb(sb, pair);

        }
        return sb.toString();
        //return URLEncodedUtils.format(URLEncodedUtils.parse(query, UTF8), UTF8);
    }

    void pairAppendToSb(StringBuilder sb, String pair) {
        int idx = pair.indexOf('=');
        if (idx == -1)
            sb.append(pair);
        else
            sb.append(pair.substring(0, idx)).append('=').append(this.encodeParamVal(pair.substring(idx + 1)));
    }

    String encodeParamVal(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    void invokeHttp(String url, Map<String, String> map) {
        String[] p = parseQueryUrl(url);
        if (StringUtils.equals("update", p[0])) {
            this.updateEncounterPatientRef(map);
            return;
        }
        HttpResp resp = kit.get(this.format(p[1]));
        if (resp.getStatus() != 200) {
            System.err.println(String.format("Http request err - %s [%s] : %s ", resp.getStatus(), p[1], resp.getHtml()));
            return;
        }
        JsonNode node = JsonMapper.jsonNode(resp.getHtml());
        if (StringUtils.equals("bundle", p[0])) {
            if (!this.checkBundle(node, p[2])) {
                System.err.println(String.format("Search check not passed : %s - %s", p[2], p[1]));
            }
        } else if (StringUtils.equals("resource", p[0])) {
            if (!this.checkResource(node, p[2])) {
                System.err.println(String.format("Resource check not passed : %s - %s", p[2], p[1]));
            }
        } else if (StringUtils.equals("history", p[0])) {
            if (!this.checkHistory(node, p[2])) {
                System.err.println(String.format("History check not passed : %s - %s", p[2], p[1]));
            }
        } else System.err.println("unknown " + p[0]);
    }

    boolean checkResourceType(JsonNode node, String type) {
        return StringUtils.equals(type, node.get("resourceType").getTextValue());
    }

    boolean checkType(JsonNode node, String type) {
        JsonNode t = node.get("type");
        if (t == null || t.isNull() || t.isMissingNode()) {
            JsonNode tt = node.get("total");
            if (tt == null)
                return false;
            else
                return true;
        }
        return StringUtils.equals(type, node.get("type").getTextValue());
    }

    boolean checkTotal(JsonNode node, String totalCheckExpr) {
        JsonNode n = node.get("total");
        if (n == null || n.isNull() || n.isMissingNode()) {
            System.err.println("Bundle has not 'total' field");
            return false;
        }
        if (StringUtils.equals(totalCheckExpr, UN_HANDLE))
            return true;
        int t = n.getIntValue();
        if (StringUtils.equals(totalCheckExpr, EMPTY_RESULT)) {
            if (t == 0) {
                return true;
            } else {
                System.err.println(String.format("Result total assert [0] , but [%s]", t));
                return false;
            }
        } else {
            boolean b = t > 0;
            if (!b)
                System.err.println(String.format("Result total assert [1-n] , but [%s]", t));
            return b;
        }
    }

    boolean checkResource(JsonNode node, String totalCheckExpr) {
        if (this.checkResourceType(node, TYPE_OPERATION_OUTCOME)) {
            if (StringUtils.equals(totalCheckExpr, UN_HANDLE))
                return true;
            else if (StringUtils.equals(totalCheckExpr, EMPTY_RESULT))
                return true;
            System.err.println("Search result resource type error : " + JsonMapper.node2String(node));
            return false;
        } else if (this.checkResourceType(node, TYPE_PATIENT)) {
            return true;
        }
        return true;
    }

    boolean checkHistory(JsonNode node, String totalCheckExpr) {
        if (!this.checkResourceType(node, TYPE_BUNDLE)) {
            System.err.println("Result resource type error : " + JsonMapper.node2String(node));
            return false;
        }
        if (!this.checkTotal(node, totalCheckExpr)) {
            return false;
        }
        JsonNode entry = node.get("entry");
        if (entry == null)
            System.out.println(" Entry is empty .... ");
        /**           throw new RuntimeException("Bundle search set entry is empty");
         int size = entry.size();
         if (size > 0)
         return true;
         else
         return false;*/
        return true;
    }

    boolean checkBundle(JsonNode node, String totalCheckExpr) {
        if (!this.checkResourceType(node, TYPE_BUNDLE)) {
            System.err.println("Search result resource type error : " + JsonMapper.node2String(node));
            return false;
        }
        if (!this.checkType(node, TYPE_SEARCH_SET)) {
            System.err.println("The result's type is not searchset");
            return false;
        }
        if (!this.checkTotal(node, totalCheckExpr)) {
            return false;
        }
        /**    JsonNode entry = node.get("entry");
         if (entry == null)
         System.out.println(String.format(" -- _summary=count .... [%s] ", totalCheckExpr));
         throw new RuntimeException("Bundle search set entry is empty");
         int size = entry.size();
         if (size > 0)
         return true;
         else
         return false;*/
        return true;
    }

    String[] parseQueryUrl(String url) {
        int idx = url.indexOf('~');
        if (idx != -1) {
            String tmp = url.substring(0, idx);
            String u = url.substring(idx + 1);
            int d = tmp.indexOf('|');
            if (d != -1)
                return new String[]{tmp.substring(0, d), u, tmp.substring(d + 1)};
            else
                return new String[]{tmp, u, NEED_RESULT};
        } else
            return new String[]{NONE, url, NEED_RESULT};
    }

    void postResource(Map<String, String> parameters) {
        Map<String, String> map = new HashMap<String, String>();
        if (parameters != null) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                map.put(entry.getKey(), this.parse(entry.getValue(), parameters));
            }
        }
        List<String> lines = FileIOUtil.readLines("classpath:fhir/fhir-query.txt");
       // StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (StringUtils.isEmpty(line) || line.charAt(0) == '#')
                continue;
            String ll = parse(line, map);
            invokeHttp(ll, map);
           // sb.append(ll).append("\r\n");
        }
        //System.out.println(sb.toString());
    }

    private String parse(String line, Map<String, String> args) {
        StringBuilder sb = new StringBuilder();
        parse(line, args, sb);
        return sb.toString();
    }

    static final String paramProfix[] = new String[]{"${", "@{"};

    private ParamPrefixType fetchParamPrefixType(String line) {
        int i = line.length();
        ParamPrefixType type = new ParamPrefixType(0, -1);
        for (int n = 0; n < paramProfix.length; n++) {
            String str = paramProfix[n];
            int idx = line.indexOf(str);
            if (idx == -1)
                continue;
            if (idx < i) {
                i = idx;
                type.setIdx(idx);
                type.setType(n);
            }
        }
        return type;
    }

    private void parse(String line, Map<String, String> args, StringBuilder sb) {
        ParamPrefixType paramPrefixType = this.fetchParamPrefixType(line);
        int idx = paramPrefixType.getIdx();
        if (idx == -1) {
            sb.append(line);
            return;
        }
        sb.append(line.substring(0, idx));
        String tmp = line.substring(idx + 2);
        idx = tmp.indexOf('}');
        if (idx == -1) {
            sb.append(tmp);
            return;
        }
        String param = tmp.substring(0, idx);
        String v = replaceParameter(param, args, paramPrefixType.getType());
        if (v == null)
            throw new RuntimeException("Not set variable for map : " + param);
        sb.append(v);
        parse(tmp.substring(idx + 1), args, sb);
    }

    String formatDate(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date());
    }

    String replaceParameter(String param, Map<String, String> args, int type) {
        if (args == null)
            return null;
        if (type == 0)
            return args.get(param);
        else if (type == 1)
            return formatDate(param);
        else
            return null;
    }
}
