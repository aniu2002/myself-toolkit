package com.sparrow.collect.utils;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/15 0015.
 */
public class GsonKit {
    //static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    static final Gson gson = new GsonBuilder().create();

    private GsonKit() {

    }

    public static <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }

    public static <T> T toBean(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    public static String toJson(Object o) {
        return gson.toJson(o);
    }

    public static String toFunctionJson(Object src) {
        if (src == null) {
            return toJson(JsonNull.INSTANCE);
        }
        return toJson(src, src.getClass());
    }

    private static String toJson(Object src, Type typeOfSrc) {
        StringWriter writer = new StringWriter();
        toJson(src, typeOfSrc, writer);
        return writer.toString();
    }

    private static void toJson(Object src, Type typeOfSrc, Appendable writer) throws JsonIOException {
        try {
            JsonWriter jsonWriter = newJsonWriter(Streams.writerForAppendable(writer));
            toJson(src, typeOfSrc, jsonWriter);
        } catch (IOException e) {
            throw new JsonIOException(e);
        }
    }

    private static void toJson(Object src, Type typeOfSrc, JsonWriter writer) throws JsonIOException {
        TypeAdapter<?> adapter = getAdapter(TypeToken.get(typeOfSrc));
        boolean oldLenient = writer.isLenient();
        writer.setLenient(true);
        boolean oldHtmlSafe = writer.isHtmlSafe();
        writer.setHtmlSafe(false);
        boolean oldSerializeNulls = writer.getSerializeNulls();
        writer.setSerializeNulls(false);
        try {
            ((TypeAdapter<Object>) adapter).write(writer, src);
        } catch (IOException e) {
            throw new JsonIOException(e);
        } finally {
            writer.setLenient(oldLenient);
            writer.setHtmlSafe(oldHtmlSafe);
            writer.setSerializeNulls(oldSerializeNulls);
        }
    }

    private static <T> TypeAdapter<T> getAdapter(TypeToken<T> type) {
        return gson.getAdapter(type);
    }

    private static JsonWriter newJsonWriter(Writer writer) throws IOException {
        JsonWriter jsonWriter = new JsonWriter(writer);
        jsonWriter.setSerializeNulls(false);
        return jsonWriter;
    }

    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
        BufferedReader reader = FileIOUtil.getBufferedReader(GsonKit.class.getResourceAsStream("/ValueRanges.json"));
        String s = FileIOUtil.readString(reader);
        List<Map> list = GsonKit.fromJson(s, new TypeToken<List<Map>>() {
        }.getType());
        File file = new File("E:\\docs\\BI\\文档\\APCD", "术语编码.txt");
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
        List<Map> nlist = new ArrayList<Map>(5);
        File d = new File("E:\\docs\\BI\\文档\\APCD\\术语对照Dictionaries-now");
        if (!d.exists())
            d.mkdirs();
        for (Map map : list) {
            System.out.println(map.get("name") + " - " + map.get("description"));
            writer.write(map.get("name") + " - " + map.get("description") + "\r\n");
            String name = map.get("name").toString();
            if (StringUtils.equals("GBT2260", name)
                    || StringUtils.equals("CV0710004", name)
                    || StringUtils.equals("CV990113", name)
                    || StringUtils.equals("CV9900327", name)
                    || StringUtils.equals("CV9900376", name)
                    || StringUtils.equals("CV9900281", name)
                    || StringUtils.equals("CV9900326", name)
                    || StringUtils.equals("CV990100", name)
                    || StringUtils.equals("CV990111", name)) {
                nlist.add(map);
                writeDir(map, d);
            }
        }
        writer.flush();
        writer.close();

     /*   File f = new File("E:\\docs\\BI\\文档\\APCD", "术语编码对照.json");
        PrintWriter fwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "utf-8"));
        fwriter.write(toJson(nlist));
        fwriter.flush();
        fwriter.close();*/
    }

    static void writeDir(Map map, File dir) throws FileNotFoundException, UnsupportedEncodingException {
        String name = map.get("name").toString();
        String desc = map.get("description").toString();
        desc = desc.replace('/', '或').replace('\\', '或');
        File f = new File(dir, name + " - " + desc + ".json");
        List list = (List) map.get("concept");
        Map nw = new HashMap(list.size());
        for (int i = 0; i < list.size(); i++) {
            Map m = (Map) list.get(i);
            String d = m.get("display").toString();
            if (d.indexOf("农合") != -1)
                System.out.println(" --  -------------------- \n\t" + name);
            nw.put(m.get("code"), m.get("display"));
        }
        PrintWriter fwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "utf-8"));
        fwriter.write(toJson(nw));
        fwriter.flush();
        fwriter.close();
    }
}
