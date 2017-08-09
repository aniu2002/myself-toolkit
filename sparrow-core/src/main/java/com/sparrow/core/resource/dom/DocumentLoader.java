package com.sparrow.core.resource.dom;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sparrow.core.resource.source.Resource;


public class DocumentLoader {
	public Document loadDocument(InputSource inputSource,
			EntityResolver entityResolver, ErrorHandler errorHandler,
			int validationMode, boolean namespaceAware) throws Exception {

		DocumentBuilderFactory factory = createDocumentBuilderFactory(
				validationMode, namespaceAware);
		DocumentBuilder builder = createDocumentBuilder(factory,
				entityResolver, errorHandler);
		return builder.parse(inputSource);
	}

	protected DocumentBuilderFactory createDocumentBuilderFactory(
			int validationMode, boolean namespaceAware)
			throws ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(namespaceAware);

		factory.setValidating(false);
		factory.setNamespaceAware(false);
		// try {
		// factory.setAttribute(SCHEMA_LANGUAGE_ATTRIBUTE,
		// XSD_SCHEMA_LANGUAGE);
		// } catch (IllegalArgumentException ex) {
		// throw ex;
		// }

		return factory;
	}

	protected DocumentBuilder createDocumentBuilder(
			DocumentBuilderFactory factory, EntityResolver entityResolver,
			ErrorHandler errorHandler) throws ParserConfigurationException {
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		if (entityResolver != null) {
			docBuilder.setEntityResolver(entityResolver);
		}
		if (errorHandler != null) {
			docBuilder.setErrorHandler(errorHandler);
		}
		return docBuilder;
	}

	protected int loadX(Resource resource) {
		try {
			InputStream inputStream = resource.getInputStream();
			try {
				InputSource inputSource = new InputSource(inputStream);
				// if (encodedResource.getEncoding() != null) {
				inputSource.setEncoding("utf-8");
				// }
				return doLoadBeanDefinitions(inputSource, resource);
			} finally {
				inputStream.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	protected int doLoadBeanDefinitions(InputSource inputSource,
			Resource resource) {
		try {

			Document doc = this.loadDocument(inputSource, null, null, 0, false);
			doc.getAttributes();
			return 0;
		} catch (SAXParseException ex) {
			ex.printStackTrace();
		} catch (SAXException ex) {
			ex.printStackTrace();
		} catch (ParserConfigurationException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return 0;
	}

}
