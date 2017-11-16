package com.szl.icu.miner.tools.resouces;

import com.szl.icu.miner.tools.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassSearch {
    public static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";
    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    private static final String FOLDER_SEPARATOR = "/";

    public static final String FILE_URL_PREFIX = "file:";
    public static final String FILE_PROTOCOL = "file";

    public static final String URL_PROTOCOL_JAR = "jar";
    public static final String URL_PROTOCOL_ZIP = "zip";
    public static final String JAR_URL_SEPARATOR = "!/";

    private static ClassSearch instance = null;

    private Map<String, String> showTypeInfos = null;
    private ClassLoader classLoader = null;

    private static Method equinoxResolveMethod;

    static {
        // Detect Equinox OSGi (e.g. on WebSphere 6.1)
        try {
            Class<?> fileLocatorClass = ClassSearch.class.getClassLoader()
                    .loadClass("org.eclipse.core.runtime.FileLocator");
            equinoxResolveMethod = fileLocatorClass.getMethod("toFileURL",
                    new Class[]{URL.class});
        } catch (Throwable ex) {
            equinoxResolveMethod = null;
        }
    }

    /**
     * private 的构造函数 保证只能在内部创建它的实例
     */
    private ClassSearch() {

    }

    private ClassSearch(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 只能通过该对外的静态接口获得该类的唯一实例
     *
     * @return
     */
    public static ClassSearch getInstance() {
        if (instance != null)
            return instance;
        instance = new ClassSearch();
        return instance;
    }

    public static ClassSearch createInstance(ClassLoader classLoader) {
        return new ClassSearch(classLoader);
    }

    /**
     * 获取到指定包下的class files
     *
     * @param url
     * @return
     */
    private String[] getClassFiles(URL url) {
        File file = null;
        String filePath = "";

        if (url == null)
            return null;

        if ("jar".equalsIgnoreCase(url.getProtocol())) {
            String jarPath = url.getPath();
            String entryPath = "";
            int location = 0;

            jarPath = jarPath.substring(6);
            location = jarPath.indexOf("!/");
            if (location != -1) {
                entryPath = jarPath.substring(location + 2);
                jarPath = jarPath.substring(0, location);
                try {
                    jarPath = java.net.URLDecoder.decode(jarPath, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            return this.jarFileFind(jarPath, entryPath);
        } else if ("file".equalsIgnoreCase(url.getProtocol())) {
            filePath = url.getPath();
            filePath = filePath.substring(6);
            try {
                filePath = java.net.URLDecoder.decode(filePath, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            file = new File(filePath);

            if (!file.isDirectory()) {
                if (file.getName().endsWith(".class")) {
                    return new String[]{file.getName()};
                }
            }

            return file.list();
        } else
            return null;
    }

    /**
     * 采用jarFileFind指定路径下的class
     */
    @SuppressWarnings("resource")
    private String[] jarFileFind(String jarFile, String entryName) {
        JarFile jfile;
        List<String> list = new ArrayList<String>();
        try {
            jfile = new JarFile(jarFile);
            Enumeration<JarEntry> files = jfile.entries();
            while (files.hasMoreElements()) {
                JarEntry entry = files.nextElement();
                String keyName = entry.getName();

                if (keyName.startsWith(entryName) && keyName.endsWith(".class")) {
                    keyName = keyName.substring(keyName.lastIndexOf("/") + 1);
                    list.add(keyName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (String[]) list.toArray(new String[0]);
    }

    /**
     * 加载指定包下的class
     *
     * @param packg
     * @param classFileName
     * @param loader
     * @return
     */
    private Class<?>[] initClasses(String packg, String classFileName[],
                                   ClassLoader loader) {
        List<Class<?>> list = null;
        if (classFileName == null || packg == null)
            return null;
        if (loader == null)
            loader = Thread.currentThread().getContextClassLoader();
        if (loader == null)
            loader = this.getClass().getClassLoader();
        list = new ArrayList<Class<?>>();
        for (int i = 0; i < classFileName.length; i++) {
            try {
                String className = classFileName[i];
                Class<?> clas;
                if (!className.endsWith(".class")) {
                    continue;
                }
                className = className.substring(0, className.length() - 6);
                className = packg + "." + className;
                clas = loader.loadClass(className);
                list.add(clas);
            } catch (ClassNotFoundException e) {
                continue;
            }
        }
        return (Class[]) list.toArray(new Class[0]);
    }

    /**
     * 加载指定包下的class
     *
     * @param classFileName
     * @param loader
     * @return
     */
    private Class<?>[] loadClasses(String[] classFileName, ClassLoader loader) {
        List<Class<?>> list = null;
        if (classFileName == null)
            return null;
        if (loader == null)
            loader = Thread.currentThread().getContextClassLoader();
        if (loader == null)
            loader = this.getClass().getClassLoader();
        list = new ArrayList<Class<?>>();
        for (int i = 0; i < classFileName.length; i++) {
            try {
                String className = classFileName[i];
                Class<?> clas;
                if (!className.endsWith(".class")) {
                    continue;
                }
                className = className.substring(0, className.length() - 6);
                className = className.replace('/', '.');
                clas = loader.loadClass(className);
                list.add(clas);
            } catch (ClassNotFoundException e) {
                continue;
            }
        }
        return (Class[]) list.toArray(new Class[0]);
    }

    /**
     * 通过指定ClassLoader加载指定包路径下的所有class
     *
     * @param packageName
     * @param oLoader
     * @return
     */
    public Class<?>[] getClasses(String packageName, ClassLoader oLoader) {
        ClassLoader loader = null;
        String classFileNames[] = null;
        String path = null;
        if (packageName == null)
            return null;
        if (oLoader == null)
            loader = Thread.currentThread().getContextClassLoader();
        else
            loader = oLoader;
        path = packageName.replaceAll("\\.", "/");
        try {
            Enumeration<URL> eml = loader.getResources(path);
            while (eml.hasMoreElements()) {
                URL url = eml.nextElement();
                if (classFileNames == null)
                    classFileNames = this.getClassFiles(url);
                else {
                    String src[] = this.getClassFiles(url);
                    System.arraycopy(src, 0, classFileNames,
                            classFileNames.length, src.length);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.initClasses(packageName, classFileNames, loader);
    }

    /**
     * 获取指定包下的实现了help接口的信息
     *
     * @param packg
     * @return
     */
    private Map<String, String> initHelpInfo(String packg, String meths) {
        Class<?> clas[] = this.getClasses(packg, null);
        Map<String, String> map = new HashMap<String, String>(0);
        int len = 0;

        if (packg == null || meths == null)
            return null;
        len = packg.length();

        for (int i = 0; i < clas.length; i++) {
            if (clas[i] != null) {
                Class<?> cla = clas[i];
                Object obj = null;
                Method method = null;
                String help = null;
                String mapKey = null;
                String mapValue = null;

                try {
                    method = cla.getDeclaredMethod(meths, new Class[0]);
                    if (method == null)
                        continue;

                    mapKey = cla.getName();
                    if (mapKey != null && mapKey.length() > len + 1)
                        mapKey = mapKey.substring(len + 1);

                    if (mapKey.startsWith("Abstract"))
                        continue;
                    if (mapKey.endsWith("ShowType"))
                        mapKey = mapKey.substring(0, mapKey.length() - 8);
                    mapKey = mapKey.toLowerCase();

                    obj = cla.newInstance();
                    help = (String) method.invoke(obj, new Object[]{});
                    mapValue = help;
                    map.put(mapKey, mapValue);

                    cla = null;
                    obj = null;
                    method = null;
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    continue;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else
                continue;
        }
        return map;
    }

    /**
     * @return 获取 showTypeInfo 信息
     */
    public Map<String, String> getShowTypeInfo() {
        if (this.showTypeInfos == null)
            this.showTypeInfos = this.initHelpInfo("au.showtype.impl",
                    "reference");
        return this.showTypeInfos;
    }

    /**
     * 根据package路径取得该路径下的class的method方法返回的对象
     *
     * @param packg
     * @param meth
     * @return
     */
    public Map<String, String> getShowTypeInfo(String packg, String meth) {
        if (this.showTypeInfos == null)
            this.showTypeInfos = this.initHelpInfo(packg, meth);
        return this.showTypeInfos;
    }

    protected String determineRootDir(String location) {
        int prefixEnd = location.indexOf(":") + 1;
        int rootDirEnd = location.length();
        while (rootDirEnd > prefixEnd
                && PathMatcher.isPattern(location.substring(prefixEnd,
                rootDirEnd))) {
            rootDirEnd = location.lastIndexOf('/', rootDirEnd - 2) + 1;
        }
        if (rootDirEnd == 0) {
            rootDirEnd = prefixEnd;
        }
        return location.substring(0, rootDirEnd);
    }

    public Class<?>[] searchClass(String pattern) {
        return searchClass(pattern, null);
    }

    public Class<?>[] searchClass(String pattern, Class<?> parent) {
        String array[] = StringUtils.tokenizeToStringArray(pattern, ";");
        return searchClass(array);
    }

    public Class<?>[] searchClassX(String... pattern) {
        return searchClass(pattern);
    }

    public Class<?>[] searchClassX(Class<?> parent, String... pattern) {
        return searchClass(pattern, new Class[]{parent});
    }

    public Class<?>[] searchClassX(Class<?>[] parent, String... pattern) {
        return searchClass(pattern, parent);
    }

    public Class<?>[] searchClass(String locationPattern[]) {
        return this.searchClass(locationPattern, null);
    }

    public Class<?>[] searchClass(String locationPattern[], Class<?>[] parent) {
        try {
            String[][] classNames = new String[locationPattern.length][];
            for (int i = 0; i < locationPattern.length; i++) {
                String location = locationPattern[i];
                location = location.replace('.', '/');
                if (location.charAt(location.length() - 1) == '*')
                    location = location + ".class";
                classNames[i] = this.getClasses(location);
            }
            if (parent == null)
                return loadClasses(classNames, this.getClassLoader());
            else
                return loadClassesX(classNames, this.getClassLoader(), parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加载指定包下的class
     *
     * @param classNames
     * @param loader
     */
    private Class<?>[] loadClasses(String[][] classNames, ClassLoader loader) {
        return this.loadClassesX(classNames, loader, null);
    }

    private Class<?>[] loadClasses(String[][] classNames, ClassLoader loader, Class<?> parent) {
        return this.loadClassesX(classNames, loader, new Class<?>[]{parent});
    }

    boolean isClassAssignableFrom(Class<?> clazz, Class<?>[] parents) {
        for (Class<?> c : parents) {
            if (c == null) continue;
            if (c.isAssignableFrom(clazz)) return true;
        }
        return false;
    }

    /**
     * 加载指定包下的class
     *
     * @param classNames
     * @param loader
     */
    private Class<?>[] loadClassesX(String[][] classNames, ClassLoader loader, Class<?>[] parent) {
        List<Class<?>> list = null;
        if (classNames == null)
            return null;
        if (loader == null)
            loader = Thread.currentThread().getContextClassLoader();
        if (loader == null)
            loader = this.getClass().getClassLoader();

        list = new ArrayList<Class<?>>();

        for (int i = 0; i < classNames.length; i++) {
            String[] classes = classNames[i];
            for (int j = 0; j < classes.length; j++) {
                try {
                    String className = classes[j];
                    Class<?> clazz;
                    if (!className.endsWith(".class")) {
                        continue;
                    }
                    className = className.substring(0, className.length() - 6);
                    className = className.replace('/', '.');
                    clazz = loader.loadClass(className);
                    if (parent != null && !this.isClassAssignableFrom(clazz, parent))
                        continue;
                    list.add(clazz);
                } catch (ClassNotFoundException e) {
                    continue;
                }
            }
        }
        return (Class[]) list.toArray(new Class[0]);
    }

    public Class<?>[] searchSampleClass(String locationPattern) {
        try {
            return loadClasses(getClasses(locationPattern),
                    this.getClassLoader());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String[] getClasses(String locationPattern) throws IOException {
        if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
            if (PathMatcher.isPattern(locationPattern
                    .substring(CLASSPATH_ALL_URL_PREFIX.length()))) {
                return findMatchingPath(locationPattern);
            } else {
                return findAllClassPathResources(locationPattern
                        .substring(CLASSPATH_ALL_URL_PREFIX.length()));
            }
        } else {
            String lp = locationPattern;
            int prefixEnd = locationPattern.indexOf(":");
            if (prefixEnd != -1)
                lp = locationPattern.substring(prefixEnd);
            if (PathMatcher.isPattern(lp)) {
                // a file pattern
                return findMatchingPath(locationPattern);
            } else {
                // a single resource with the given name
                return new String[]{getPathUrl(locationPattern)};
            }
        }
    }

    protected String[] findMatchingPath(String locationPattern)
            throws IOException {
        // au/path/
        String rootDirPath = determineRootDir(locationPattern);
        //
        String subPattern = locationPattern.substring(rootDirPath.length());
        String[] rootDirs = getClasses(rootDirPath);
        Set<String> result = new LinkedHashSet<String>(16);
        String bpath = rootDirPath;
        if (bpath.startsWith(CLASSPATH_ALL_URL_PREFIX))
            bpath = bpath.substring(CLASSPATH_ALL_URL_PREFIX.length());
        else if (bpath.startsWith(CLASSPATH_URL_PREFIX))
            bpath = bpath.substring(CLASSPATH_URL_PREFIX.length());
        String ndir;
        for (String rootDir : rootDirs) {
            ndir = resolveRootDirResource(rootDir);
            if (isJarPath(ndir)) {
                result.addAll(doFindJarPath(ndir, subPattern));
            } else {
                result.addAll(doFindFilePath(ndir, subPattern, bpath));
            }
        }
        return result.toArray(new String[result.size()]);
    }

    public static Object invokeMethod(Method method, Object target,
                                      Object[] args) {
        try {
            return method.invoke(target, args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String resolveRootDirResource(String dir) {
        if (dir.startsWith("bundle")) {
            URL url;
            try {
                url = (URL) invokeMethod(equinoxResolveMethod, null,
                        new Object[]{new URL(dir)});
                return url.getPath();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return dir;
    }

    public static boolean isBundle(String dir) {
        URL url;
        try {
            url = new URL(dir);
            if (url.getProtocol().startsWith("bundle")) {
                return true;
                // new UrlResource((URL)
                // ReflectionUtils.invokeMethod(equinoxResolveMethod, null, new
                // Object[] {url}));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isBundle(URL dir) {
        if (dir.getProtocol().startsWith("bundle"))
            return true;
        return false;
    }

    protected Set<String> doFindJarPath(String rootDir, String subPattern)
            throws IOException {
        JarFile jarFile = null;
        String jarFileUrl;
        String rootEntryPath;
        String urlFile = rootDir;
        int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
        if (separatorIndex != -1) {
            jarFileUrl = urlFile.substring(0, separatorIndex);
            rootEntryPath = urlFile.substring(separatorIndex
                    + JAR_URL_SEPARATOR.length());
            if (jarFileUrl.startsWith("jar"))
                jarFileUrl = jarFileUrl.substring(4);
            jarFile = getJarFile(jarFileUrl);
        } else {
            jarFile = new JarFile(urlFile);
            jarFileUrl = urlFile;
            rootEntryPath = "";
        }
        try {
            if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
                rootEntryPath = rootEntryPath + "/";
            }
            Set<String> result = new LinkedHashSet<String>(8);
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries
                    .hasMoreElements(); ) {
                JarEntry entry = entries.nextElement();
                String entryPath = entry.getName();
                if (entryPath.startsWith(rootEntryPath)) {
                    String relativePath = entryPath.substring(rootEntryPath
                            .length());
                    if (PathMatcher.doMatch(subPattern, relativePath)) {
                        result.add(applyRelativePath(rootEntryPath,
                                relativePath));
                    }
                }
            }
            return result;
        } finally {
            if (jarFile != null) {
                jarFile.close();
            }
        }
    }

    protected Set<String> doFindBundlePath(String rootDir, String subPattern)
            throws IOException {
        JarFile jarFile = null;
        String jarFileUrl;
        String rootEntryPath;
        String urlFile = rootDir;
        int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
        if (separatorIndex != -1) {
            jarFileUrl = urlFile.substring(0, separatorIndex);
            rootEntryPath = urlFile.substring(separatorIndex
                    + JAR_URL_SEPARATOR.length());
            if (jarFileUrl.startsWith("jar"))
                jarFileUrl = jarFileUrl.substring(4);
            jarFile = getJarFile(jarFileUrl);
        } else {
            jarFile = new JarFile(urlFile);
            jarFileUrl = urlFile;
            rootEntryPath = "";
        }
        try {
            if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
                rootEntryPath = rootEntryPath + "/";
            }
            Set<String> result = new LinkedHashSet<String>(8);
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries
                    .hasMoreElements(); ) {
                JarEntry entry = entries.nextElement();
                String entryPath = entry.getName();
                if (entryPath.startsWith(rootEntryPath)) {
                    String relativePath = entryPath.substring(rootEntryPath
                            .length());
                    if (PathMatcher.doMatch(subPattern, relativePath)) {
                        result.add(applyRelativePath(rootEntryPath,
                                relativePath));
                    }
                }
            }
            return result;
        } finally {
            if (jarFile != null) {
                jarFile.close();
            }
        }
    }

    protected JarFile getJarFile(String jarFileUrl) throws IOException {
        if (jarFileUrl.startsWith(FILE_URL_PREFIX)) {
            try {
                return new JarFile(toURI(jarFileUrl).getSchemeSpecificPart());
            } catch (URISyntaxException ex) {
                return new JarFile(jarFileUrl.substring(FILE_URL_PREFIX
                        .length()));
            }
        } else {
            return new JarFile(jarFileUrl);
        }
    }

    public static boolean isJarPath(String path) {
        return path.indexOf(URL_PROTOCOL_JAR) != -1
                || path.indexOf(URL_PROTOCOL_ZIP) != -1;
        // String protocol;
        //
        // try {
        // protocol = new URL(path).getProtocol();
        // return (URL_PROTOCOL_JAR.equals(protocol) || URL_PROTOCOL_ZIP
        // .equals(protocol));
        // } catch (MalformedURLException e) {
        // e.printStackTrace();
        // }
        // return false;
    }

    public static boolean isJarPath(URL path) {
        String protocol = path.getProtocol();
        return (URL_PROTOCOL_JAR.equals(protocol) || URL_PROTOCOL_ZIP.equals(protocol));
    }

    public static boolean isFilePath(URL path) {
        return FILE_PROTOCOL.equals(path.getProtocol());
    }

    public static URI toURI(String location) throws URISyntaxException {
        return new URI(StringUtils.replace(location, " ", "%20"));
    }

    public static String applyRelativePath(String path, String relativePath) {
        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        if (separatorIndex != -1) {
            String newPath = path.substring(0, separatorIndex);
            if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
                newPath += FOLDER_SEPARATOR;
            }
            return newPath + relativePath;
        } else {
            return relativePath;
        }
    }

    protected Set<String> doFindFilePath(String rootFileName,
                                         String subPattern, String rootDirPath) throws IOException {
        String rootDirName = rootFileName;
        if (rootDirName.startsWith("file:/"))
            rootDirName = rootDirName.substring(6);
        File rootDir = new File(rootDirName);
        return doFindFilePath(rootDir, subPattern, rootDirPath);
    }

    protected Set<String> doFindFilePath(File rootDir, String subPattern,
                                         String rootDirPath) throws IOException {
        String rootDirStr = StringUtils.replace(rootDir.getAbsolutePath(),
                File.separator, "/");
        int rootLength = rootDirStr.length();
        Set<File> matchingFiles = retrieveMatchingFiles(rootDir, subPattern,
                rootLength);
        Set<String> result = new LinkedHashSet<String>(matchingFiles.size());
        String tmpath;
        for (File file : matchingFiles) {
            tmpath = rootDirPath;
            // 匹配的文件路径，截取根路径 剩下的字符串再和rootDirPath相加
            tmpath += StringUtils.replace(file.getAbsolutePath(),
                    File.separator, "/").substring(rootDirStr.length() + 1);
            result.add(tmpath);
        }
        return result;
    }

    protected Set<File> retrieveMatchingFiles(File rootDir, String pattern,
                                              int rootLength) throws IOException {
        if (!rootDir.exists()) {
            return Collections.emptySet();
        }
        if (!rootDir.isDirectory()) {
            return Collections.emptySet();
        }
        if (!rootDir.canRead()) {
            return Collections.emptySet();
        }

        pattern = StringUtils.replace(pattern, File.separator, "/");
        if (!pattern.startsWith("/")) {
            pattern = "/" + pattern;
        }
        Set<File> result = new LinkedHashSet<File>(8);
        doRetrieveMatchingFiles(pattern, rootDir, result, rootLength);
        return result;
    }

    protected void doRetrieveMatchingFiles(String pattern, File dir,
                                           Set<File> result, int rootLength) throws IOException {
        File[] dirContents = dir.listFiles();
        if (dirContents == null) {
            return;
        }
        for (File content : dirContents) {
            String currPath = StringUtils.replace(content.getAbsolutePath(),
                    File.separator, "/");
            if (content.isDirectory()) {
                if (content.canRead()) {
                    doRetrieveMatchingFiles(pattern, content, result,
                            rootLength);
                }
            }
            if (currPath.length() > rootLength)
                currPath = currPath.substring(rootLength);
            if (PathMatcher.doMatch(pattern, currPath)) {
                result.add(content);
            }
        }
    }

    private String getPathUrl(String location) {
        String path = location;
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            path = location.substring(CLASSPATH_URL_PREFIX.length());
        }
        URL url = this.getClassLoader().getResource(path);
        return url == null ? path : url.toString();
    }

    private String getPathUrlExt(String location) {
        String path = location;
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            path = location.substring(CLASSPATH_URL_PREFIX.length());
        }
        String str = null;
        try {
            Enumeration<URL> resourceUrls = this.getClassLoader().getResources(path);
            if (resourceUrls != null) {
                while (resourceUrls.hasMoreElements()) {
                    URL url = resourceUrls.nextElement();
                    System.out.println(String.format(" filter url : %s , protocol : %s", url.toString(), url.getProtocol()));
                    if (isFilePath(url)) {
                        str = url.getFile();
                        if (str.charAt(0) == '/')
                            str = str.substring(1);
                        return str;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (str == null)
            str = path;
        return str;
    }

    protected String[] findAllClassPathResources(String location)
            throws IOException {
        String path = location;
        if (path.startsWith("bundleresource")) {
            path = path.substring("bundleresource".length() + 3);
            int idx = path.indexOf('/');
            if (idx != -1)
                path = path.substring(idx + 1);
        } else if (path.startsWith("/")) {
            path = path.substring(1);
        }
        Enumeration<URL> resourceUrls = this.getClassLoader().getResources(path);
        Set<String> result = new LinkedHashSet<String>(16);
        while (resourceUrls.hasMoreElements()) {
            URL url = resourceUrls.nextElement();
            String tmp = convertClassLoaderURL(url);
            if (tmp == null)
                tmp = location;
            result.add(tmp);
        }
        return result.toArray(new String[result.size()]);
    }

    private ClassLoader getClassLoader() {
        ClassLoader cl = this.classLoader;
        if (cl == null) {
            cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                try {
                    cl = ClassSearch.class.getClassLoader();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }
        return cl;
    }

    protected String convertClassLoaderURL(URL url) {
        if ("jar".equalsIgnoreCase(url.getProtocol())) {
            String jarPath = url.getPath();
            String entryPath = "";
            int location = 0;

            jarPath = jarPath.substring(6);
            location = jarPath.indexOf("!/");
            if (location != -1) {
                entryPath = jarPath.substring(location + 2);
                // if (entryPath.endsWith(".class"))
                // entryPath = entryPath.substring(0, entryPath.length() - 6);
                return entryPath.replace('/', '.');
            }
        } else if ("file".equalsIgnoreCase(url.getProtocol())) {
            String path = url.getFile();
            if (path.charAt(0) == '/')
                path = path.substring(1);
            return path;
        }
        return null;
    }
}
