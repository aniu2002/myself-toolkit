package com.sparrow.server.base.command;

import com.sparrow.common.source.OptionItem;
import com.sparrow.common.source.SourceHandler;
import com.sparrow.common.source.SourceManager;
import com.sparrow.core.json.JsonMapper;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.utils.file.FileUtils;
import com.sparrow.http.command.BaseCommand;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.JsonResponse;
import com.sparrow.http.command.resp.OkResponse;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SourceCommand extends BaseCommand {
    final Map<String, Object> sources = new HashMap<String, Object>();
    final List<String> systems;
    final File ff;
    final File rootDir;

    {
        List<String> sysList = null;
        rootDir = new File(SystemConfig.getProperty("web.root.path"));
        ff = new File(rootDir, "json/systems.json");
        if (ff.exists() && ff.isFile()) {
            String str = FileUtils.readFileStringx(ff.getPath(), "utf-8");
            try {
                sysList = JsonMapper.mapper.readValue(str,
                        new TypeReference<List<String>>() {
                        }
                );
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (sysList == null)
            sysList = new ArrayList<String>();
        systems = sysList;
    }

    void writeSystems() {
        try {
            JsonMapper.mapper.writeValue(ff, this.systems);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Response doPost(Request request) {
        String _t = request.get("_t");
        if ("clean".equals(_t)) {
            this.sources.clear();
        }
        return OkResponse.OK;
    }

    protected Response doGet(Request request) {
        String s = request.get("_s");
        String ext = request.get("_e");
        Object data = this.getSource(s, ext);
        return new JsonResponse(data);
    }

    Object getSource(String s, String ext) {
        int idx = s.indexOf(',');
        if (idx != -1) {
            String ss[] = StringUtils.tokenizeToStringArray(s, ",");
            Map<String, Object> mp = new HashMap<String, Object>();
            for (String ts : ss) {
                this.fetchSources(ts, mp);
            }
            return mp;
        } else
            return this.getSourceEx(s, ext);
    }

    void fetchSources(String s, Map<String, Object> mp) {
        int idx = s.indexOf('-');
        Object data;
        String k = s;
        if (idx != -1) {
            k = s.substring(0, idx);
            data = this.getSourceEx(k, s.substring(idx + 1));
        } else
            data = this.getSourceEx(s, null);
        mp.put(k, data);
    }

    Object getFileSource(String s) {
        Object data = null;
        File ff = new File(rootDir, "json/" + s + ".json");
        if (ff.exists() && ff.isFile()) {
            String str = FileUtils.readFileStringx(ff.getPath(), "utf-8");
            try {
                data = JsonMapper.mapper.readValue(str, new TypeReference<List<OptionItem>>() {
                });
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    Object getSourceEx(String s, String t) {
        Object data = sources.get(s);
        if (data != null)
            return data;
        if ("f".equals(s)) {
            data = this.getFileSource(t);
        } else {
            SourceHandler sh = SourceManager.getSourceHandler(s);
            List<OptionItem> items = null;
            if (sh != null)
                items = sh.getSource(t);
            data = items;
        }
        if (data != null)
            this.sources.put(s, data);
        return data;
    }
}
