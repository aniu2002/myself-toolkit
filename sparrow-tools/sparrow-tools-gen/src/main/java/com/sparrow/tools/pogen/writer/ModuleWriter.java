/**
 * Project Name:http-server  
 * File Name:ModuleWriter.java  
 * Package Name:au.tools.pogen.writer  
 * Date:2014-2-12下午5:04:55  
 * Copyright (c) 2014, Boco.com All Rights Reserved.  
 *
 */

package com.sparrow.tools.pogen.writer;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.core.utils.FileIOUtil;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.tools.utils.FreeMarkerUtils;

/**
 * ClassName:ModuleWriter <br/>
 * Date: 2014-2-12 下午5:04:55 <br/>
 *
 * @author YZC
 * @see
 * @since JDK 1.6
 */
public class ModuleWriter {
    private Writer writer;
    private String curModule;
    private String curLabel;
    private List<String> items = new ArrayList<String>();
    private String path;
    private int counter = 10;

    public ModuleWriter() {
        this.path = SystemConfig.WEB_ROOT;
    }

    public ModuleWriter(String path) {
        this.path = path;
    }

    public void write(String module, String label, Map<String, Object> datamap) {
        try {
            if (this.writer == null) {
                File wb = new File(this.path);
                File file = new File(wb, "menu.html");
                if (file.exists()) {
                    File toFile = new File(wb, "menu.html._bak");
                    file.renameTo(toFile);
                    file.delete();
                }
                //System.out.println("--------------" + file.getAbsolutePath());
                this.writer = FileIOUtil.getWriter(file, "utf-8", true);
            }
            if (!StringUtils.equals(module, this.curModule)) {
                if (!this.items.isEmpty()) {
                    Map<String, Object> lap = new HashMap<String, Object>();
                    lap.put("items", this.items);
                    lap.put("label", label);
                    this.curLabel = label;
                    lap.put("counter", counter);
                    if (counter == 10)
                        lap.put("cssIn", "in");
                    String s = FreeMarkerUtils.getInstance().writeString(
                            "au_Menu", lap);
                    this.writer.write(s);
                    this.writer.write("\r\n");
                    this.items.clear();
                    counter++;
                }
                // this.writer.write("<li class=\"nav-header\">");
                // this.writer.write(label);
                // this.writer.write("</li>\r\n");
                this.curModule = module;
                this.curLabel = label;
            }
            String str = FreeMarkerUtils.getInstance().writeString(
                    "au_MenuItem", datamap);
            this.items.add(str + "\r\n");
            // this.writer.write(str);
            // this.writer.write("\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (writer != null) {
            try {
                if (!this.items.isEmpty()) {
                    Map<String, Object> lap = new HashMap<String, Object>();
                    lap.put("items", this.items);
                    lap.put("label", this.curLabel);
                    lap.put("counter", counter);
                    if (counter == 10)
                        lap.put("cssIn", "in");
                    String s = FreeMarkerUtils.getInstance().writeString(
                            "au_Menu", lap);
                    this.writer.write(s);
                    this.writer.write("\r\n");
                    this.items.clear();
                    counter++;
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
