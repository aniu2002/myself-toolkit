package com.szl.icu.miner.plugin;

import com.szl.icu.miner.tools.ApiClassGenerator;
import com.szl.icu.miner.tools.SwaggerJsonGenerator;
import com.szl.icu.miner.tools.utils.ClassUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Goal which touches a timestamp file.
 */
//@Mojo(name = "generate", defaultPhase = LifecyclePhase.COMPILE, configurator = "include-project-dependencies", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class RestGenerateMojo1 extends AbstractMojo {
    /**
     * read or write file with encoding
     */
//    @Parameter(defaultValue = "${project.build.sourceEncoding}")
    private String encoding;

//    @Parameter(defaultValue = "${basedir}/src/main/resources/RestService.conf", required = false, property = "rest.config")
    private String restConfig;

//    @Parameter(defaultValue = "${basedir}/src/main/java", property = "rest.path")
    private String restPath;

//    @Parameter(defaultValue = "com.szl.icu.miner.rest", property = "rest.packageName")
    private String packageName;

//    @Parameter(defaultValue = "com.szl.icu.miner.zk", property = "rest.replyPackage")
    private String replyPackage;

//    @Parameter
    private List<String> scanPackages;

//    @Parameter
    private List<String> parentClasses;

//    @Parameter
    private List<String> requestClasses;

//    @Parameter
    private List<String> copyClasses;

//    @Parameter
    private List<String> modules;

//    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

//    @Parameter
    private boolean ignoreConfig;

    //    @Parameter(property = "project.compileClasspathElements", required = true, readonly = true)
    //    private List<String> classpath;

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getRestConfig() {
        return restConfig;
    }

    public void setRestConfig(String restConfig) {
        this.restConfig = restConfig;
    }

    public String getRestPath() {
        return restPath;
    }

    public void setRestPath(String restPath) {
        this.restPath = restPath;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getReplyPackage() {
        return replyPackage;
    }

    public void setReplyPackage(String replyPackage) {
        this.replyPackage = replyPackage;
    }

    public List<String> getScanPackages() {
        return scanPackages;
    }

    public void setScanPackages(List<String> scanPackages) {
        this.scanPackages = scanPackages;
    }

    public List<String> getParentClasses() {
        return parentClasses;
    }

    public void setParentClasses(List<String> parentClasses) {
        this.parentClasses = parentClasses;
    }

    public List<String> getRequestClasses() {
        return requestClasses;
    }

    public void setRequestClasses(List<String> requestClasses) {
        this.requestClasses = requestClasses;
    }

    public List<String> getCopyClasses() {
        return copyClasses;
    }

    public void setCopyClasses(List<String> copyClasses) {
        this.copyClasses = copyClasses;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    public boolean isIgnoreConfig() {
        return ignoreConfig;
    }

    public void setIgnoreConfig(boolean ignoreConfig) {
        this.ignoreConfig = ignoreConfig;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(" ------ Rest Service Generate Begin ---------- ");
        try {
            Set<URL> urls = new HashSet<URL>();
            List<String> elements = project.getTestClasspathElements();
            //getRuntimeClasspathElements()
            //getCompileClasspathElements()
            //getSystemClasspathElements()
            for (String element : elements) {
                urls.add(new File(element).toURI().toURL());
            }
            ClassLoader contextClassLoader = URLClassLoader.newInstance(
                    urls.toArray(new URL[0]),
                    Thread.currentThread().getContextClassLoader());
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            new ApiClassGenerator()
                    .setLog(new LogAdapter(getLog()))
                    .setRestConfig(this.restConfig)
                    .setIgnoreConfig(this.ignoreConfig)
                    .setRequestClasses(ClassUtils.getClasses(this.requestClasses))
                    .setParentClasses(ClassUtils.getClasses(this.parentClasses))
                    .setCopiedClasses(ClassUtils.getClasses(this.copyClasses))
                    .setScanPackages(this.scanPackages == null ? null : this.scanPackages.toArray(new String[0]))
                    .setRestPackage(this.packageName)
                    .setReplyPackage(this.replyPackage)
                    .setModules(this.modules == null ? null : this.modules.toArray(new String[0]))
                    .setCodePath(this.restPath)
                    .generate();
            new SwaggerJsonGenerator()
                    .setLog(new LogAdapter(getLog()))
                    .setModules(this.modules == null ? null : this.modules.toArray(new String[0]))
                    .setRestConfig(this.restConfig)
                    .setCodePath(this.restPath)
                    .generate();
            //this.projectHelper.attachArtifact(this.project, format.toLowerCase(), classifier, swaggerFile);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new MojoFailureException(e.getMessage(), e);
        }
    }
}
