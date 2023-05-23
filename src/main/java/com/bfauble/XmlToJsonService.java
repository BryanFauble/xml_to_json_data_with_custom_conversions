package com.bfauble;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Service class to handle processing incoming XML and converting it into a JSON response.
 *
 * @author Bryan Fauble
 */
public class XmlToJsonService {
	private static final String ARRAY = "array";
	private static final String ELEMENT = "element";
	private static final String OBJECT = "object";
	private static final String NOT_MAPPED = "notMapped";
	private static final String PERIOD = ".";

	private final DocumentBuilder documentBuilder;
	private final MappingService mappingService;
	private final ObjectMapper objectMapper;
	private final DataConversionService dataConversionService;

	/**
	 * Constructor.
	 * @throws ParserConfigurationException {@link ParserConfigurationException}.
	 */
	public XmlToJsonService() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		documentBuilder = factory.newDocumentBuilder();
		mappingService = new MappingService();
		objectMapper = new ObjectMapper();
		dataConversionService = new DataConversionService();
	}

	/**
	 * Handle the creation of JSON from an XML file. This requires an applicable mapping file and applicable XML.
	 * @param pMappingFile The mapping file.
	 * @param pInputFile The input file.
	 * @throws IOException {@link IOException}.
	 * @throws SAXException {@link SAXException}.
	 */
	public String buildXmlToJson(File pMappingFile, File pInputFile) throws IOException, SAXException {
		//These files are currently hard coded - they would come from wherever the source of the XML would be coming from.
		final Map<String, MappedField> mappingFile = mappingService.buildMappingFile(pMappingFile);
		final Document inputDocument = documentBuilder.parse(pInputFile);

		final Element rootXmlElement = inputDocument.getDocumentElement();
		final JSONObject rootJsonObject = new JSONObject();

		for (int i = 0 ; i < rootXmlElement.getChildNodes().getLength(); i++) {
			final Node node = rootXmlElement.getChildNodes().item(i);

			final String xmlNodeName = node.getNodeName();
			final MappedField mappedField = mappingFile.get(xmlNodeName);
			buildJsonFromNode(node, rootJsonObject, "", mappedField, mappingFile);
		}

		//If there is only one element under the root JSON and it is 'notMapped' then we can treat this as an
		//array and print it without a JSON root.
		if (rootJsonObject.keySet().size()== 1
			&& NOT_MAPPED.equals(rootJsonObject.keySet().iterator().next())) {
			return objectMapper.writeValueAsString(rootJsonObject.values().iterator().next());
		} else {
			return objectMapper.writeValueAsString(rootJsonObject);
		}
	}

	/**
	 * Build up JSON from the current node in the tree. Places the JSON onto the passed in {@link JSONObject}.
	 *
	 * There are recursive calls setup between this method, {@link #buildJsonArrayFromNode}, {@link #buildJsonObjectFromNode},that
	 * traverses the whole XML tree.
	 *
	 * @param pNode The current node in the tree.
	 * @param pJSONObject The {@link JSONObject} to start adding fields to.
	 * @param pCurrentPositionInTree The current position in the XML tree that we are parsing.
	 * @param pMappedField The {@link MappedField} for the current position in the tree.
	 * @param pMappingFile The mapping file for the current XML being parsed.
	 */
	private void buildJsonFromNode(Node pNode, JSONObject pJSONObject, String pCurrentPositionInTree, MappedField pMappedField,
								   Map<String, MappedField> pMappingFile) {
		//If the mappedField in null then this means it's a path in the XML being parsed that is not in the mapping file.
		if (pMappedField == null) {
			return;
		}

		if (ARRAY.equals(pMappedField.getXmlType())) {
			if (pJSONObject.containsKey(pMappedField.getJsonName())) {
				buildJsonArrayFromNode(pNode,
						(JSONArray) pJSONObject.get(pMappedField.getJsonName()), pCurrentPositionInTree, pMappingFile);
			} else {
				//If a JSONArray isn't already created for the current JSON field, create a new one and put it onto the
				//JSONObject.
				final JSONArray jsonArray = new JSONArray();
				pJSONObject.put(pMappedField.getJsonName(), jsonArray);
				buildJsonArrayFromNode(pNode, jsonArray, pCurrentPositionInTree, pMappingFile);
			}
		} else if (ELEMENT.equals(pMappedField.getXmlType())) {
			pJSONObject.put(pMappedField.getJsonName(),
					dataConversionService.convertFieldValue(pMappedField.getXmlDataType(),
							pMappedField.getJsonDataType(), pNode.getTextContent()));
		} else if (OBJECT.equals(pMappedField.getXmlType())) {
			//When the current field is an object then we are going 1 level deeper in the tree. This is going to create
			//the child JSON node and populate it with all of the applicable fields.
			final JSONObject childJSONObject = new JSONObject();
			pJSONObject.put(pMappedField.getJsonName(), childJSONObject);
			buildJsonObjectFromNode(pNode, childJSONObject,
					pCurrentPositionInTree, pMappingFile);
		}
	}

	/**
	 * Handle for building out a JSON Array. This is called in cases where the current node is a <array></array>.
	 * An example of this in XML:
	 *
	 * <menu> <- Array
	 *     <food> <- Object
	 * 	       <name>Burrito</name> <- Element
	 * 	   </food>
	 * 	   <food> <- Object
	 * 	       <name>Burger</name> <- Element
	 * 	   </food>
	 * </menu>
	 *
	 * An example of this in JSON:
	 * "menu":[{"food":{"name":"Burrito"}}, {"food":{"name":"Burger"}}]
	 *
	 * @param pNode The current node which is an <array></array>
	 * @param pJsonArray The {@link JSONArray} to start adding fields to.
	 * @param pCurrentPositionInTree The current position in the XML tree that we are parsing.
	 * @param pMappingFile The mapping file for the current XML being parsed.
	 */
	private void buildJsonArrayFromNode(Node pNode, JSONArray pJsonArray, String pCurrentPositionInTree,
										Map<String, MappedField> pMappingFile) {
		final JSONObject jsonObject = new JSONObject();
		final String newPositionInTree;
		if ("".equals(pCurrentPositionInTree)) {
			newPositionInTree = pNode.getNodeName();
		} else {
			newPositionInTree = pCurrentPositionInTree + PERIOD + pNode.getNodeName();
		}

		pJsonArray.add(jsonObject);

		for (int i = 0 ; i < pNode.getChildNodes().getLength(); i++) {
			final Node node = pNode.getChildNodes().item(i);
			final MappedField mappedField = pMappingFile.get(newPositionInTree + PERIOD + node.getNodeName());
			buildJsonFromNode(node, jsonObject, newPositionInTree, mappedField, pMappingFile);
		}
	}

	/**
	 * Handle for building out a JSON Object. This is called in cases where the current node is a <object></object>.
	 * An example of this in XML:
	 * <food> <- Object
	 *     <name>Burrito</name> <- Element
	 * </food>
	 *
	 * An example of this in JSON:
	 * "food":{"name":"Burrito"}
	 *
	 * @param pNode The current node which is an <object></object>
	 * @param pJsonObject The {@link JSONObject} to start adding fields to.
	 * @param pCurrentPositionInTree The current position in the XML tree that we are parsing.
	 * @param pMappingFile The mapping file for the current XML being parsed.
	 */
	private void buildJsonObjectFromNode(Node pNode, JSONObject pJsonObject, String pCurrentPositionInTree,
										 Map<String, MappedField> pMappingFile) {
		final String newPositionInTree;

		if ("".equals(pCurrentPositionInTree)) {
			newPositionInTree = pNode.getNodeName();
		} else {
			newPositionInTree = pCurrentPositionInTree + PERIOD + pNode.getNodeName();
		}

		for (int i = 0 ; i < pNode.getChildNodes().getLength(); i++) {
			final Node node = pNode.getChildNodes().item(i);
			final MappedField mappedField = pMappingFile.get(newPositionInTree + PERIOD + node.getNodeName());
			buildJsonFromNode(node, pJsonObject, newPositionInTree, mappedField, pMappingFile);
		}
	}
}
