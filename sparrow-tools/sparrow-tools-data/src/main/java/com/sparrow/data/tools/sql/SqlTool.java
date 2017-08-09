package com.sparrow.data.tools.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class SqlTool {
    private static final char[] PARAMETER_SEPARATORS = new char[]{'"', '\'',
            ':', '&', ',', ';', '(', ')', '|', '=', '+', '-', '*', '%', '/',
            '\\', '<', '>', '^'};

    private static final char CHAR_COMMENTS = '-';
    private static final char CHAR_ASTERISK = '*';
    private static final char CHAR_SLASH = '/';
    static final char CHAR_BACKSLASH = '\\';
    static final char CHAR_QUESTION = '?';
    static final String STR_QUESTION = "?";
    private static final char CHAR_LINE = '\n';
    private static final char CHAR_QUOTES = '\'';
    private static final char CHAR_DOUBLE_QUOTES = '"';

    public static ParsedSql parseSqlStatement(final String sql) {
        if (StringUtils.isEmpty(sql))
            return null;
        StringBuilder sb = new StringBuilder();
        List<NamedParameter> namedItems = new ArrayList<NamedParameter>();

        final char[] statement = sql.toCharArray();
        final int len = sql.length();
        int i = 0, k, paraIdx = 0, namedIdx = 0;

        while (i < len) {
            k = skipCommentsAndQuotes(statement, i, len);
            if (k >= len)
                break;
            // 有字符被忽略，那么actual sql需要重新定义最后一次cut的pos
            if (k > i + 1) {
                sb.append(sql.substring(i, k));
                i = k;
            }
            char c = statement[i];
            if (c == '\r' || c == '\n') {
                if (i > 0 && !isBlank(statement[i - 1]))
                    sb.append(' ');
                int l = i + 1;
                while (l < len && isBlank(statement[l])) {
                    l++;
                }
                i = l;
                continue;
            }
            // named parameter
            if (c == ':' || c == '&' || c == '#') {
                boolean gl = (c == '#');
                int j = i + 1;
                if (j < len && statement[j] == ':' && c == ':') {
                    // Postgres-style "::" casting operator - to be skipped.
                    i = i + 2;
                    continue;
                }
                while (j < len && !isParameterSeparator(statement[j])) {
                    j++;
                }
                if (j - i > 1) {
                    String parameter = sql.substring(i + 1, j);
                    sb.append(CHAR_QUESTION);
                    namedItems.add(new NamedParameter(parameter, paraIdx, gl));
                    paraIdx++;
                    namedIdx++;
                }
                i = j - 1;
            } else if (c == CHAR_QUESTION) {
                // JDBCS parameter
                sb.append(CHAR_QUESTION);
                paraIdx++;
            } else
                sb.append(c);
            i++;
        }
        if (i < len)
            sb.append(sql.substring(i));
        if (paraIdx == 0) {
            return new ParsedSql(sql, sql, null, 0, 0);
        } else {
            String actualSql = sb.toString();
            return new ParsedSql(sql, actualSql,
                    namedItems.toArray(new NamedParameter[namedItems.size()]),
                    paraIdx, namedIdx);
        }
    }

    private static int skipCommentsAndQuotes(final char[] statement,
                                             int position, final int length) {
        // 7 - 6 - 4
        if (position > length - 3)
            return position;
        int pos = position;
        char fChar = statement[pos], sChar = statement[pos + 1];
        // start "--"
        if (fChar == CHAR_COMMENTS) {
            if (sChar == CHAR_COMMENTS) {
                pos = pos + 2;
                for (; pos < length; pos++)
                    if (statement[pos] == CHAR_LINE)
                        break;
            }
        } else if (fChar == CHAR_SLASH) {
            // start "/*"
            if (sChar == CHAR_ASTERISK) {
                pos = pos + 2;
                for (; pos < length; pos++) {
                    // end "*/"
                    if (statement[pos] == CHAR_ASTERISK
                            && statement[pos + 1] == CHAR_SLASH)
                        break;
                }
                pos++;
            }
        } else if (fChar == CHAR_QUOTES) {
            pos = pos + 1;
            for (; pos < length; pos++)
                if (statement[pos] == CHAR_QUOTES)
                    break;
        } else if (fChar == CHAR_DOUBLE_QUOTES) {
            pos = pos + 1;
            for (; pos < length; pos++)
                if (statement[pos] == CHAR_DOUBLE_QUOTES)
                    break;
        }
        return pos + 1;
    }

    private static boolean isParameterSeparator(char c) {
        if (Character.isWhitespace(c)) {
            return true;
        }
        for (int i = 0; i < PARAMETER_SEPARATORS.length; i++) {
            if (c == PARAMETER_SEPARATORS[i]) {
                return true;
            }
        }
        return false;
    }

    private static boolean isBlank(char c) {
        if (c == '\n' || c == '\r' || c == ' ' || c == '\t')
            return true;
        return false;
    }


    public static void main(String args[]) {
        String sql = "select beb.barcode as \"barcode\",beb.material_id as \"materialId\","
                + "spm.entity_name as \"materialName\",spm.entity_code as \"materialCode\","
                + "decode(beb.accounting_type,1,'买断物资',2,'备货物资',3,'铺货物资') as \"accountingType\","
                + "decode(bob.custom_type,'1','集团客户','2','大众市场') as \"customType\","
                + "beb.color as \"color\" from qd_b_exchange_barcode beb inner join "
                + "scm_product_material spm on beb.material_id = spm.id inner join "
                + "qd_b_output_bill bob on beb.biz_instance_id = bob.biz_instance_id "
                + "where beb.biz_instance_id = :bizInstanceId ";
        String sqll = "INSERT INTO lf_members(name,qq,sex,age,bra,phone,province,city,district,referee_name,referee_qq,price_p,price_pp,price_desc,simple_desc,special,checked,leval,comment,images,create_date,mark)\n" +
                "            VALUES(:name,:qq,'F',:age,'A','','四川','成都',:district,'明天','3063702092',:priceP,:pricePp,'无','验证过或有口碑的靠谱妹子','无',0,1,'验证过或有口碑的靠谱妹子','',#createDate,1)";
        ParsedSql s = parseSqlStatement(sql);
        System.out.println(s.getActualSql());
        s = parseSqlStatement(sqll);
        System.out.println(s.getActualSql());
    }
}
