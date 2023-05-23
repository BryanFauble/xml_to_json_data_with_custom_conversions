package com.bfauble;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Unit tests for {@link XmlToJsonService}.
 *
 * @author Bryan Fauble
 */
public class XmlToJsonServiceTest {
	private XmlToJsonService xmlToJsonService;

	/**
	 * Setup for unit testing.
	 * @throws ParserConfigurationException {@link ParserConfigurationException}.
	 */
	@Before
	public void setup() throws ParserConfigurationException {
		xmlToJsonService = new XmlToJsonService();
	}

	/**
	 * Unit testing {@link XmlToJsonService#buildXmlToJson(File, File)}).
	 * @throws SAXException {@link SAXException}.
	 * @throws IOException {@link IOException}.
	 */
	@Test
	public void buildXmlToJson_testingFile_buildsJson() throws SAXException, IOException {
		final String json
				= xmlToJsonService.buildXmlToJson((new File("src/main/resources/testing/testingMapping.xml")),
				new File("src/main/resources/testing/testXmlInputFile.xml"));
		Assert.assertEquals("{\"bookTitleJson\":\"Bryan's great book of food - Part 2 the electric boogalo\",\"dishJson\":[{\"dishCreatorJson\":\"The burrito man\",\"ingredientsJson\":[{\"ingredientQuantityJson\":\"1 Pound\",\"ingredientCommentJson\":\"Season the meat while cooking or use a marinade before cooking.\",\"ingredientNameJson\":\"Chicken\"},{\"ingredientQuantityJson\":\"1 Can\",\"ingredientCommentJson\":\"Try adding a small amount of lime juice.\",\"ingredientNameJson\":\"Beans\"},{\"ingredientQuantityJson\":\"1 Head\",\"ingredientCommentJson\":\"Cut up lettuce into small strips.\",\"ingredientNameJson\":\"Lettuce\"}],\"dishCreationJson\":{\"dishLocationOfCreationJson\":\"Earth\",\"dishDateOfCreationJson\":\"19000101\"},\"dishTitleJson\":\"Chicken Burritos\",\"localRestaurantSellingDishJson\":[{\"restaurantJson\":\"Panchos\",\"addressJson\":{\"addressStateJson\":\"MI\",\"addressLine1Json\":\"123 Right Around The Corner\"}},{\"restaurantJson\":\"Chipotle\",\"addressJson\":{\"addressStateJson\":\"MI\",\"addressLine1Json\":\"Across the street from the one that closed\"}}]},{\"dishCreatorJson\":\"The Curry man\",\"ingredientsJson\":[{\"ingredientQuantityJson\":\"1 Pound\",\"ingredientCommentJson\":\"Cook thoroughly to make sure there is no raw meat.\",\"ingredientNameJson\":\"Chicken\"},{\"ingredientQuantityJson\":\"1 (32oz) Can\",\"ingredientNameJson\":\"Tomato puree\"},{\"ingredientQuantityJson\":\"1 Cup\",\"ingredientNameJson\":\"Yogurt\"},{\"ingredientQuantityJson\":\"1/3 Cup\",\"ingredientCommentJson\":\"Curry powder, Cumin, Coriander, Cayenne pepper, Garam Masala, Tumeric\",\"ingredientNameJson\":\"Spices\"}],\"dishCreationJson\":{\"dishLocationOfCreationJson\":\"Earth\",\"dishDateOfCreationJson\":\"19000101\"},\"dishTitleJson\":\"Spicy Curry\",\"localRestaurantSellingDishJson\":[{\"restaurantJson\":\"Grill of india\",\"addressJson\":{\"addressStateJson\":\"MI\",\"addressLine1Json\":\"South Lansing\"}}]}],\"authorJson\":\"Bryan Fauble\"}",
				json);
	}
}
