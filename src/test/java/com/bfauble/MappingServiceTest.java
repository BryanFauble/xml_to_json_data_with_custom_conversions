package com.bfauble;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Unit tests for {@link MappingService}.
 *
 * @author Bryan Fauble
 */
public class MappingServiceTest {

	private MappingService mappingService;

	/**
	 * Setup for unit testing.
	 * @throws ParserConfigurationException {@link ParserConfigurationException}.
	 */
	@Before
	public void setup() throws ParserConfigurationException {
		mappingService = new MappingService();
	}

	/**
	 * Unit test for {@link MappingService#buildMappingFile(File)}. Tests a few of the fields and confirms that it is
	 * pulling the correct fields from the mapping file.
	 * @throws SAXException {@link SAXException}.
	 * @throws IOException {@link IOException}.
	 */
	@Test
	public void buildMappingFile_testMappingFile_mappingFileBuilt() throws SAXException, IOException {
		final Map<String, MappedField> mappedFieldMap
				= mappingService.buildMappingFile(new File("src/main/resources/testing/testingMapping.xml"));
		final MappedField ingredientArray = mappedFieldMap.get("dish.ingredients");
		final MappedField restaurantAddress1 = mappedFieldMap.get("dish.localRestaurantSellingDish.address");
		final MappedField dishCreationLocation = mappedFieldMap.get("dish.dishCreation.dishLocationOfCreation");

		Assert.assertEquals("ingredientsJson", ingredientArray.getJsonName());
		Assert.assertEquals("addressJson", restaurantAddress1.getJsonName());
		Assert.assertEquals("dishLocationOfCreationJson", dishCreationLocation.getJsonName());
	}

}
