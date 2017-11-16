package com.szl.icu.miner.plugin;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.configurator.AbstractComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/21.
 */
public class IncludeDependenciesComponentConfigurator extends AbstractComponentConfigurator
{
    public void configureComponent(Object component, PlexusConfiguration configuration, ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm, ConfigurationListener listener)
            throws ComponentConfigurationException
    {
        addProjectDependenciesToClassRealm(expressionEvaluator, containerRealm);

        ObjectWithFieldsConverter converter = new ObjectWithFieldsConverter();
        converter.processConfiguration(this.converterLookup, component, containerRealm.getClassLoader(), configuration, expressionEvaluator, listener);
    }

    private void addProjectDependenciesToClassRealm(ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm) throws ComponentConfigurationException
    {
        List compileClasspathElements;
        try
        {
            compileClasspathElements = (List)expressionEvaluator.evaluate("${project.compileClasspathElements}");
        } catch (ExpressionEvaluationException e) {
            throw new ComponentConfigurationException("There was a problem evaluating: ${project.compileClasspathElements}", e);
        }
        URL[] urls = buildURLs(compileClasspathElements);
        for (URL url : urls)
            containerRealm.addConstituent(url);
    }

    private URL[] buildURLs(List<String> runtimeClasspathElements)
            throws ComponentConfigurationException
    {
        List urls = new ArrayList(runtimeClasspathElements.size());
        for (String element : runtimeClasspathElements) {
            try {
                URL url = new File(element).toURI().toURL();
                urls.add(url);
            } catch (MalformedURLException e) {
                throw new ComponentConfigurationException("Unable to access project dependency: " + element, e);
            }
        }

        return (URL[])urls.toArray(new URL[urls.size()]);
    }
}