package com.szl.icu.miner.tools;

import com.szl.icu.miner.tools.template.FreeMarkerUtils;
import com.szl.icu.miner.tools.template.swagger.Swagger;
import com.szl.icu.miner.tools.utils.FileIOUtil;
import com.szl.icu.miner.tools.utils.JsonFormat;
import org.markdown4j.Markdown4jProcessor;

import java.io.File;

/**
 * Created by yzc on 2016/9/28.
 */
@Deprecated
public class SwaggerJsonGeneratorBak extends SwaggerJsonGenerator {

    @Deprecated
    public void generate(String generateRootDir, String[] modules) {
        File baseFile = new File(generateRootDir);
        if (!baseFile.exists())
            baseFile.mkdirs();
        Swagger swagger = generateController(modules);
        File file = new File(baseFile, "swagger.json");
        String content = FreeMarkerUtils.getInstance().writeString("swagger-json", swagger);
        FileIOUtil.writeFile(file, JsonFormat.clearBlankLine(content), FileIOUtil.DEFAULT_ENCODING);

        // FreeMarkerUtils.getInstance().writeFile("swagger-markdown", swagger, new File(baseFile, "swagger.md"));
        String markDown = FreeMarkerUtils.getInstance().writeString("swagger-markdown", swagger);

        String fp = System.getProperty("editor.markdown.font.p", "color:red;");
        String fli = System.getProperty("editor.markdown.font.li", "color:red;");
        String fh1 = System.getProperty("editor.markdown.font.h1", "color:red;");
        String fh2 = System.getProperty("editor.markdown.font.h2", "color:red;");
        String fh3 = System.getProperty("editor.markdown.font.h3", "color:red;");
        String fpre = System.getProperty("editor.markdown.font.pre", "color:red;");

        try {
            Markdown4jProcessor markdown4jProcessor = new Markdown4jProcessor().addHtmlAttribute("style", fpre, "pre")
                    .addHtmlAttribute("style", fp, "p")
                    .addHtmlAttribute("style", fli, "li")
                    .addHtmlAttribute("style", fh1, "h1")
                    .addHtmlAttribute("style", fh2, "h2")
                    .addHtmlAttribute("style", fh3, "h3");
            String html = new StringBuilder().append("<html><head><title>test</title><meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\"></head><body>")
                    .append(
                            markdown4jProcessor.process(markDown))
                    .append("</body></html>").toString();
//            String html=new StringBuilder().append("<html><head><title>test</title><meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\"></head><body>").append(new MarkdownProcessor()
//                    .markdown(markDown)).append("</body></html>").toString();

            FileIOUtil.writeFile(new File(baseFile, "swagger.html"), html, FileIOUtil.DEFAULT_ENCODING);
            FileIOUtil.writeFile(new File(baseFile, "swagger.md"), markDown, FileIOUtil.DEFAULT_ENCODING);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //String html = new MarkdownProcessor().markdown()
    }

}
