package com.sparrow.core.resource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.sparrow.core.resource.loader.DefaultResourceLoader;
import com.sparrow.core.resource.loader.ResourceLoader;
import com.sparrow.core.resource.source.FileSystemResource;
import com.sparrow.core.resource.source.Resource;
import com.sparrow.core.resource.source.UrlResource;
import com.sparrow.core.utils.ResourceUtils;
import com.sparrow.core.utils.StringUtils;


public class PathMatchingResourceResolver {
    public static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";
    private static Method equinoxResolveMethod;
    private final ResourceLoader resourceLoader;

    static {
        // Detect Equinox OSGi (e.g. on WebSphere 6.1)
        try {
            Class<?> fileLocatorClass = PathMatchingResourceResolver.class
                    .getClassLoader().loadClass(
                            "org.eclipse.core.runtime.FileLocator");
            equinoxResolveMethod = fileLocatorClass.getMethod("resolve",
                    URL.class);
            // Class cl=URL.class;
        } catch (Throwable ex) {
            equinoxResolveMethod = null;
        }
    }

    public PathMatchingResourceResolver() {
        this.resourceLoader = new DefaultResourceLoader();

    }

    public PathMatchingResourceResolver(ClassLoader loader) {
        this.resourceLoader = new DefaultResourceLoader(loader);
    }

    public Resource[] getResources(String locationPattern) throws IOException {
        if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {

            if (PathMatcher.isPattern(locationPattern
                    .substring(CLASSPATH_ALL_URL_PREFIX.length()))) {
                return findPathMatchingResources(locationPattern);
            } else {
                return findAllClassPathResources(locationPattern
                        .substring(CLASSPATH_ALL_URL_PREFIX.length()));
            }
        } else {
            // Only look for a pattern after a prefix here
            // (to not get fooled by a pattern symbol in a strange prefix).
            File file = new File(locationPattern);
            if (file.exists())
                return new Resource[]{new FileSystemResource(file)};

            int prefixEnd = locationPattern.indexOf(":") + 1;
            if (PathMatcher.isPattern(locationPattern.substring(prefixEnd))) {
                // a file pattern
                return findPathMatchingResources(locationPattern);
            } else {
                // a single resource with the given name
                return new Resource[]{getResourceLoader().getResource(
                        locationPattern)};
            }
        }
    }

    protected Resource[] findPathMatchingResources(String locationPattern)
            throws IOException {
        String rootDirPath = determineRootDir(locationPattern);
        String subPattern = locationPattern.substring(rootDirPath.length());
        Resource[] rootDirResources = getResources(rootDirPath);
        Set<Resource> result = new LinkedHashSet<Resource>(16);
        for (Resource rootDirResource : rootDirResources) {
            rootDirResource = resolveRootDirResource(rootDirResource);
            if (isJarResource(rootDirResource)) {
                result.addAll(doFindPathMatchingJarResources(rootDirResource,
                        subPattern));
            } else {
                result.addAll(doFindPathMatchingFileResources(rootDirResource,
                        subPattern));
            }
        }
        return result.toArray(new Resource[result.size()]);
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

    protected boolean isJarResource(Resource resource) throws IOException {
        return ResourceUtils.isJarURL(resource.getURL());
    }

    protected Resource resolveRootDirResource(Resource original)
            throws IOException {
        if (equinoxResolveMethod != null) {
            URL url = original.getURL();
            if (url.getProtocol().startsWith("bundle")) {
                return new UrlResource((URL) this.invokeMethod(
                        equinoxResolveMethod, null, url));
            }
        }
        return original;
    }

    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

    public ClassLoader getClassLoader() {
        return getResourceLoader().getClassLoader();
    }

    public Object invokeMethod(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        throw new IllegalStateException("Should never get here");
    }

    protected Set<Resource> doFindPathMatchingJarResources(
            Resource rootDirResource, String subPattern) throws IOException {
        URLConnection con = rootDirResource.getURL().openConnection();
        JarFile jarFile;
        String jarFileUrl;
        String rootEntryPath;
        boolean newJarFile = false;

        if (con instanceof JarURLConnection) {
            // Should usually be the case for traditional JAR files.
            JarURLConnection jarCon = (JarURLConnection) con;
            jarCon.setUseCaches(false);
            jarFile = jarCon.getJarFile();
            jarFileUrl = jarCon.getJarFileURL().toExternalForm();
            JarEntry jarEntry = jarCon.getJarEntry();
            rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
        } else {
            // No JarURLConnection -> need to resort to URL file parsing.
            // We'll assume URLs of the format "jar:path!/entry", with the
            // protocol
            // being arbitrary as long as following the entry format.
            // We'll also handle paths with and without leading "file:" prefix.
            String urlFile = rootDirResource.getURL().getFile();
            int separatorIndex = urlFile
                    .indexOf(ResourceUtils.JAR_URL_SEPARATOR);
            if (separatorIndex != -1) {
                jarFileUrl = urlFile.substring(0, separatorIndex);
                rootEntryPath = urlFile.substring(separatorIndex
                        + ResourceUtils.JAR_URL_SEPARATOR.length());
                jarFile = getJarFile(jarFileUrl);
            } else {
                jarFile = new JarFile(urlFile);
                jarFileUrl = urlFile;
                rootEntryPath = "";
            }
            newJarFile = true;
        }

        try {
            if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
                // Root entry path must end with slash to allow for proper
                // matching.
                // The Sun JRE does not return a slash here, but BEA JRockit
                // does.
                rootEntryPath = rootEntryPath + "/";
            }
            Set<Resource> result = new LinkedHashSet<Resource>(8);
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries
                    .hasMoreElements(); ) {
                JarEntry entry = entries.nextElement();
                String entryPath = entry.getName();
                if (entryPath.startsWith(rootEntryPath)) {
                    String relativePath = entryPath.substring(rootEntryPath
                            .length());
                    if (PathMatcher.match(subPattern, relativePath)) {
                        result.add(rootDirResource.createRelative(relativePath));
                    }
                }
            }
            return result;
        } finally {
            if (newJarFile) {
                jarFile.close();
            }
        }
    }

    protected JarFile getJarFile(String jarFileUrl) throws IOException {
        if (jarFileUrl.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
            try {
                return new JarFile(ResourceUtils.toURI(jarFileUrl)
                        .getSchemeSpecificPart());
            } catch (URISyntaxException ex) {
                // Fallback for URLs that are not valid URIs (should hardly ever
                // happen).
                return new JarFile(
                        jarFileUrl.substring(ResourceUtils.FILE_URL_PREFIX
                                .length())
                );
            }
        } else {
            return new JarFile(jarFileUrl);
        }
    }

    protected Set<Resource> doFindPathMatchingFileResources(
            Resource rootDirResource, String subPattern) throws IOException {

        File rootDir;
        try {
            rootDir = rootDirResource.getFile().getAbsoluteFile();
        } catch (IOException ex) {
            return Collections.emptySet();
        }
        return doFindMatchingFileSystemResources(rootDir, subPattern);
    }

    protected Set<Resource> doFindMatchingFileSystemResources(File rootDir,
                                                              String subPattern) throws IOException {
        Set<File> matchingFiles = retrieveMatchingFiles(rootDir, subPattern);
        Set<Resource> result = new LinkedHashSet<Resource>(matchingFiles.size());
        for (File file : matchingFiles) {
            result.add(new FileSystemResource(file));
        }
        return result;
    }

    protected Set<File> retrieveMatchingFiles(File rootDir, String pattern)
            throws IOException {
        if (!rootDir.exists()) {
            return Collections.emptySet();
        }
        if (!rootDir.isDirectory()) {
            return Collections.emptySet();
        }
        if (!rootDir.canRead()) {
            return Collections.emptySet();
        }
        String fullPattern = StringUtils.replace(rootDir.getAbsolutePath(),
                File.separator, "/");
        if (!pattern.startsWith("/")) {
            fullPattern += "/";
        }
        fullPattern = fullPattern
                + StringUtils.replace(pattern, File.separator, "/");
        Set<File> result = new LinkedHashSet<File>(8);
        doRetrieveMatchingFiles(fullPattern, rootDir, result);
        return result;
    }

    protected void doRetrieveMatchingFiles(String fullPattern, File dir,
                                           Set<File> result) throws IOException {
        File[] dirContents = dir.listFiles();
        if (dirContents == null) {
            return;
        }
        for (File content : dirContents) {
            String currPath = StringUtils.replace(content.getAbsolutePath(),
                    File.separator, "/");
            if (content.isDirectory()
                    && PathMatcher.matchStart(fullPattern, currPath + "/")) {
                if (!content.canRead()) {
                } else {
                    doRetrieveMatchingFiles(fullPattern, content, result);
                }
            }
            if (PathMatcher.match(fullPattern, currPath)) {
                result.add(content);
            }
        }
    }

    protected Resource[] findAllClassPathResources(String location)
            throws IOException {
        String path = location;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        Enumeration<URL> resourceUrls = getClassLoader().getResources(path);
        Set<Resource> result = new LinkedHashSet<Resource>(16);
        while (resourceUrls.hasMoreElements()) {
            URL url = resourceUrls.nextElement();
            result.add(convertClassLoaderURL(url));
        }
        return result.toArray(new Resource[result.size()]);
    }

    protected Resource convertClassLoaderURL(URL url) {
        return new UrlResource(url);
    }
}
