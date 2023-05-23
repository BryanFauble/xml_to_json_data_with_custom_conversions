package com.bfauble;

import com.sun.deploy.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class to handle creating the mapping in order to take XML, run through conversions and return XML.
 *
 * @author Bryan Fauble
 */
public class MappingService {
	private static final Logger LOGGER = Logger.getLogger(MappingService.class.getName());
	private static final Collection<String> VALID_NODES ;
	private static final String ARRAY = "array";
	private static final String ELEMENT = "element";
	private static final String OBJECT = "object";
	private static final String XML_NAME = "xmlName";
	private static final String XML_DATA_TYPE = "xmlDataType";
	private static final String JSON_NAME = "jsonName";
	private static final String JSON_DATA_TYPE = "jsonDataType";
	private static final String PERIOD = ".";

	static {
		VALID_NODES = new ArrayList<>();
		VALID_NODES.add(ARRAY);
		VALID_NODES.add(ELEMENT);
		VALID_NODES.add(OBJECT);
	}
	private final DocumentBuilder documentBuilder;

	/**
	 * Constructor.
	 * @throws ParserConfigurationException {@link ParserConfigurationException}.
	 */
	public MappingService() throws ParserConfigurationException {
		documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}

	/**
	 * Handles building the mapping. This parses through the mapping file and constructions directions on how we got
	 * to each node, and subsequently, what to do at each node.
	 * @param pFile The mapping file to parse.
	 * @return A map where the key is the directions to each of the nodes. The value is what to do at that node.
	 * @throws IOException {@link IOException} if there was an issue parsing the file or writing the value
	 */
	public Map<String, MappedField> buildMappingFile(File pFile) throws IOException, SAXException {
		final Map<String, MappedField> xmlMapping = new HashMap<>();
		final Document mappingDocument = documentBuilder.parse(pFile);
		final Element mappingRoot = mappingDocument.getDocumentElement();

		for (int i = 0 ; i < mappingRoot.getChildNodes().getLength(); i++) {
			final Node node = mappingRoot.getChildNodes().item(i);
			buildNode(node, new StringBuilder(), xmlMapping);
		}

		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.info(xmlMapping.toString());
		}
		return xmlMapping;
	}

	/**
	 * Look at the current node and control if we build it, return early or loop over the node going a level deeper in
	 * the tree.
	 * @param pNode The current node.
	 * @param pStringBuilder The {@link StringBuilder} with the current position in the tree that we are at.
	 * @param pXmlMapping The XML Mapping to fill.
	 */
	private void buildNode(Node pNode, StringBuilder pStringBuilder, Map<String, MappedField> pXmlMapping) {
		if (!VALID_NODES.contains(pNode.getNodeName())) {
			return;
		}

		final String xmlName = pNode.getAttributes().getNamedItem(XML_NAME).getNodeValue();

		if (ARRAY.equals(pNode.getNodeName())) {
			loopOverNode(pNode, new StringBuilder(pStringBuilder.toString()), pXmlMapping);
			pXmlMapping.put(StringUtils.join(Arrays.asList(pStringBuilder.toString(), xmlName), PERIOD), buildMappedField(pNode));
		} else if (ELEMENT.equals(pNode.getNodeName())) {
			pXmlMapping.put(StringUtils.join(Arrays.asList(pStringBuilder.toString(), xmlName), PERIOD), buildMappedField(pNode));
		} else if (OBJECT.equals(pNode.getNodeName())) {
			loopOverNode(pNode, new StringBuilder(pStringBuilder.toString()), pXmlMapping);
			pXmlMapping.put(StringUtils.join(Arrays.asList(pStringBuilder.toString(), xmlName), PERIOD), buildMappedField(pNode));
		}
	}

	/**
	 * Loop over all child nodes on the current node.
	 * @param pNode The current node.
	 * @param pStringBuilder The {@link StringBuilder} with the current position in the tree that we are at.
	 * @param pXmlMapping The XML Mapping to fill.
	 */
	private void loopOverNode(Node pNode, StringBuilder pStringBuilder, Map<String, MappedField> pXmlMapping) {
		final String xmlName = pNode.getAttributes().getNamedItem(XML_NAME).getNodeValue();
		if ("".equals(pStringBuilder.toString())) {
			pStringBuilder.append(xmlName);
		} else {
			pStringBuilder.append(PERIOD).append(xmlName);
		}

		for (int i = 0 ; i < pNode.getChildNodes().getLength(); i++) {
			final Node node = pNode.getChildNodes().item(i);
			buildNode(node, pStringBuilder, pXmlMapping);
		}
	}

	/**
	 * Sets all of the fields on the POJO in order to know when parsing the actual XML what to do.
	 * @param pNode The current node.
	 * @return The {@link MappedField}.
	 */
	private MappedField buildMappedField(Node pNode) {
		final NamedNodeMap namedNodeMap = pNode.getAttributes();
		final MappedField mappedField = new MappedField();
		mappedField.setXmlName(namedNodeMap.getNamedItem(XML_NAME).getNodeValue());
		mappedField.setJsonName(namedNodeMap.getNamedItem(JSON_NAME).getNodeValue());
		mappedField.setXmlDataType(namedNodeMap.getNamedItem(XML_DATA_TYPE).getNodeValue());
		mappedField.setJsonDataType(namedNodeMap.getNamedItem(JSON_DATA_TYPE).getNodeValue());
		mappedField.setXmlType(pNode.getNodeName());
		return mappedField;
	}

}
