package com.sparrow.collect.website.handler;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import com.sparrow.collect.website.lucene.creator.IFileCreator;
import com.sparrow.collect.website.utils.PathResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.IndexWriter;


public class FileHandlerFactory {
    private static final Map<String, FileHandler> cache = new HashMap<String, FileHandler>();

    /**
     * <p>
     * Title: getHandler
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param key
     * @return
     * @author Yzc
     */
    private static FileHandler getHandler(String key) {
        FileHandler handler = (FileHandler) cache.get(key);
        if (handler == null) {
            handler = build(key);
            if (handler != null) {
                handler.setWrite2file(true);
                cache.put(key, handler);
            }
        }
        return handler;
    }

    /**
     * <p>
     * Title: build
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param key
     * @return
     * @author Yzc
     */
    private static FileHandler build(String key) {
        if ("txt|text|js|".indexOf(key) != -1) {
            return new TextFileHandler();
        } else if ("pdf".equals(key)) {
            return new PdfFileHandler();
        } else if ("html|xml|jsp|asp".indexOf(key) != -1) {
            return new HtmlFileHandler();
        } else if ("doc".equals(key)) {
            return new WordFileHandler();
        } else if ("docx".equals(key)) {
            return new Word2007FileHandler();
        } else if ("xls".equals(key)) {
            return new ExcelFileHandler();
        } else if ("xlsx".equals(key)) {
            return new Excel2007FileHandler();
        }
        return null;
    }

    /**
     * <p>
     * Title: buildFileDocument
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param file
     * @return
     * @author Yzc
     */
    public static void buildFileDocument(File file, IndexWriter writer,
                                         IFileCreator creator) {
        if (file == null)
            return;
        FileHandler handler = null;
        if (file.isFile()) {
            String extension = PathResolver.getExtension(file);
            handler = getHandler(extension);
        }
        if (handler != null)
            handler.handle(file, writer, creator);
    }

    static boolean hasInArray(String v, String array[]) {
        if (array == null || array.length == 0)
            return false;
        for (String str : array) {
            if (StringUtils.equalsIgnoreCase(str, v))
                return true;
        }
        return false;
    }

    public static void parseAllFile(File dir, final String suffix) {
        if (dir.exists()) {
            final String arr[] = suffix.split(",");
            File files[] = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    String fix = PathResolver.getExtension(name);
                    return hasInArray(fix, arr);
                }
            });

            for (File file : files) {
                System.out.println(file.getPath());
                buildFileDocument(file, null, null);
            }
        }
    }

    static void formatFile(File file) {

    }

    public static void main(String args[]) {
        System.setProperty("data.store.path", "E:\\_server\\playServer\\config\\data");
        parseAllFile(new File("E:\\_server\\playServer\\config\\_src"), "pdf,doc,xls");
    }
}
