package com.bfauble;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit testing for {@link DataConversionService}.
 *
 * @author Bryan Fauble
 */
public class DataConversionServiceTest {

	private final DataConversionService dataConversionService = new DataConversionService();

	@Test
	public void convertFieldValue_nullParameters_returnsValue() {
		Assert.assertNull(dataConversionService.convertFieldValue(null, null, null));
		Assert.assertEquals("7777", dataConversionService.convertFieldValue(null, null, "7777"));
		Assert.assertEquals("7777", dataConversionService.convertFieldValue(null, "aaa", "7777"));
		Assert.assertEquals("7777", dataConversionService.convertFieldValue("aaa", null, "7777"));
	}

	@Test
	public void convertFieldValue_stringToString_returnsString() {
		Assert.assertEquals("thisIsAString", dataConversionService.convertFieldValue("string", "string", "thisIsAString"));
		Assert.assertEquals("thisIsAString2", dataConversionService.convertFieldValue("sTrInG", "sTrInG", "thisIsAString2"));
	}

	@Test
	public void convertFieldValue_stringToUnknown_returnsString() {
		Assert.assertEquals("thisIsAString", dataConversionService.convertFieldValue("string", "sssss", "thisIsAString"));
		Assert.assertEquals("thisIsAString2", dataConversionService.convertFieldValue("sTrInG", "zzzzz", "thisIsAString2"));
	}

	@Test
	public void convertFieldValue_stringToInteger_returnsInteger() {
		Assert.assertEquals(7777, dataConversionService.convertFieldValue("string", "integer", "7777"));
		Assert.assertEquals(2222, dataConversionService.convertFieldValue("sTrInG", "iNtEgEr", "2222"));
	}

	@Test
	public void convertFieldValue_stringToIntegerInvalidValue_returnsNull() {
		Assert.assertNull(dataConversionService.convertFieldValue("string", "integer", "jfghj444bbb"));
		Assert.assertNull(dataConversionService.convertFieldValue("sTrInG", "iNtEgEr", "asdf"));
	}

	@Test
	public void convertFieldValue_genderAbbrevToGenderFull_returnsFullGender() {
		Assert.assertEquals("male", dataConversionService.convertFieldValue("GENDERABBREV", "GENDERFULL", "m"));
		Assert.assertEquals("female", dataConversionService.convertFieldValue("GENDERABBREV", "GENDERFULL", "f"));
	}

	@Test
	public void convertFieldValue_stateFullToStateAbbrev_returnsStateAbbrev() {
		Assert.assertEquals("MI", dataConversionService.convertFieldValue("STATEFULL", "STATEABBREV", "michigan"));
		Assert.assertEquals("OH", dataConversionService.convertFieldValue("STATEFULL", "STATEABBREV", "ohio"));
	}

}
