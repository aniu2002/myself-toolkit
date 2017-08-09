package com.sparrow.orm.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import com.sparrow.core.utils.file.FileUtils;


public class TableConfigBuilder {
	private Digester configDigester;
	private TableConfiguration configuration;

	public TableConfigBuilder(File file) {
		this.initializeCfg(file);
	}

	public TableConfigBuilder(InputStream ins) {
		this.initializeCfg(ins);
	}

	public TableConfigBuilder(List<File> flist) {
		this.initializeCfg(flist);
	}

	private void initDigester() {
		if (configDigester != null) {
			return;
		}
		configDigester = new Digester();
		configDigester.setNamespaceAware(false);
		configDigester.setValidating(false);
		configDigester.setUseContextClassLoader(true);
		configDigester.addRuleSet(new TableCfgRuleSet());
	}

	private void initializeCfg(InputStream ins) {
		this.initDigester();
		configuration = new TableConfiguration();
		configDigester.push(configuration);
		try {
			configuration = (TableConfiguration) configDigester.parse(ins);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	private void initializeCfg(List<File> list) {
		if (list == null || list.isEmpty())
			return;
		this.initDigester();
		configuration = new TableConfiguration();
		try {
			Iterator<File> ite = list.iterator();
			File file;
			while (ite.hasNext()) {
				configDigester.push(configuration);
				file=ite.next();
				System.out.println("#Mapping File : "+file.getPath());
				configDigester.parse(FileUtils.getFileInputStream(file));
				// configDigester.p
			}
			// configuration = (TableConfiguration) configDigester.parse(ins);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	public TableConfiguration getCfgContainer() {
		return configuration;
	}

	public void setCfgContainer(TableConfiguration cfgContainer) {
		this.configuration = cfgContainer;
	}

	private void initializeCfg(File file) {
		try {
			this.initializeCfg(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
