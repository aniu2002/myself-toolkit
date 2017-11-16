package com.szl.icu.miner.plugin;

import com.szl.icu.miner.tools.SwaggerJsonGeneratorExt;
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
@Mojo(name = "generate", defaultPhase = LifecyclePhase.COMPILE, configurator = "include-project-dependencies", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class RestGenerateMojo extends AbstractMojo {
    /**
     * read or write file with encoding
     */
    @Parameter(defaultValue = "${basedir}/src/main/resources/RestService.conf", required = false, property = "rest.config")
    private String restConfig;

    @Parameter(defaultValue = "${basedir}/src/main/resources/ApiDocs", property = "swagger.path")
    private String swaggerPath;


    @Parameter
    private List<String> modules;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "true")
    private boolean generateSwagger;

    @Parameter
    private boolean generateMarkDown;

    @Parameter
    private boolean generateHtmlDocs;

    @Parameter
    private DeployInfo deployInfo;

    public DeployInfo getDeployInfo() {
        return deployInfo;
    }

    public void setDeployInfo(DeployInfo deployInfo) {
        this.deployInfo = deployInfo;
    }

    public boolean isGenerateSwagger() {
        return generateSwagger;
    }

    public void setGenerateSwagger(boolean generateSwagger) {
        this.generateSwagger = generateSwagger;
    }

    public boolean isGenerateMarkDown() {
        return generateMarkDown;
    }

    public void setGenerateMarkDown(boolean generateMarkDown) {
        this.generateMarkDown = generateMarkDown;
    }

    public boolean isGenerateHtmlDocs() {
        return generateHtmlDocs;
    }

    public void setGenerateHtmlDocs(boolean generateHtmlDocs) {
        this.generateHtmlDocs = generateHtmlDocs;
    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }


    public String getRestConfig() {
        return restConfig;
    }

    public void setRestConfig(String restConfig) {
        this.restConfig = restConfig;
    }


    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    public String getSwaggerPath() {
        return swaggerPath;
    }

    public void setSwaggerPath(String swaggerPath) {
        this.swaggerPath = swaggerPath;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(" ------ Generate Swagger File Begin ---------- ");
        try {
            Set<URL> urls = new HashSet<URL>();
            List<String> elements = project.getTestClasspathElements();
            for (String element : elements) {
                urls.add(new File(element).toURI().toURL());
            }
            ClassLoader contextClassLoader = URLClassLoader.newInstance(
                    urls.toArray(new URL[0]),
                    Thread.currentThread().getContextClassLoader());
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            DeployInfo deployInfo = this.deployInfo;
            getLog().info(this.deployInfo.getAuthor() + " - " + this.deployInfo.getHost() + " - " + this.deployInfo.getContext());
            if (deployInfo == null)
                deployInfo = new DeployInfo();
            new SwaggerJsonGeneratorExt()
                    .setLog(new LogAdapter(getLog()))
                    .setModules(this.modules == null ? null : this.modules.toArray(new String[0]))
                    .setRestConfig(this.restConfig)
                    .setCodePath(this.swaggerPath)
                    .setGenerateHtmlDocs(this.generateHtmlDocs)
                    .setGenerateMarkDown(this.generateMarkDown)
                    .setGenerateSwagger(this.generateSwagger)
                    .setDescription(deployInfo.getDescription())
                    .setAuthor(deployInfo.getAuthor())
                    .setContext(deployInfo.getContext())
                    .setEmail(deployInfo.getEmail())
                    .setHost(deployInfo.getHost())
                    .setSite(deployInfo.getSite())
                    .setTitle(deployInfo.getTitle())
                    .setVersion(deployInfo.getVersion())
                    .setProjectName(project.getName())
                    .generate();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new MojoFailureException(e.getMessage(), e);
        }
    }
}
