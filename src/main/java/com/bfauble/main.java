package com.bfauble;

import java.io.File;

/**
 * Main.
 *
 * @author Bryan Fauble
 */
public class main {

	public static void main(String[] args) throws Exception {
		final XmlToJsonService xmlToJsonService = new XmlToJsonService();
		System.out.println(xmlToJsonService.buildXmlToJson(new File("src/main/resources/mapping.xml"), new File("src/main/resources/xmlInputFile.xml")));
	}

}
