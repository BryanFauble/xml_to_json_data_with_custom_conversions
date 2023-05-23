package com.bfauble;

import org.joda.time.DateTime;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class to handle converting data from one format to another.
 *
 * @author Bryan Fauble
 */
public class DataConversionService {
	private static final DateTimeFormatter MM_DD_YYYY_DATE = DateTimeFormat.forPattern("MM/dd/yyyy");
	private static final Logger LOGGER = Logger.getLogger(DataConversionService.class.getName());

	/**
	 * Handle for converting data from one format to another. All values are coming in from the XML document as a
	 * String - convert them to whatever object type it needs to in order to write the data back to the JSON.
	 * @param pStartingDataType The starting data type.
	 * @param pEndingDataType The ending data type.
	 * @param pValue The value to convert.
	 * @return The converted object.
	 */
	public Object convertFieldValue(String pStartingDataType, String pEndingDataType, String pValue) {
		if (pValue == null
			|| pStartingDataType == null
			|| pEndingDataType == null) {
			if (LOGGER.isLoggable(Level.WARNING)) {
				LOGGER.warning(String.format("One or more fields to convert data is not passed in: %s | %s | %s", pStartingDataType, pEndingDataType, pValue));
			}
			return pValue;
		}

		//These would probably get moved over to an ENUM to hold all of the actions rather than relying on strings.
		switch (pStartingDataType.toUpperCase()) {
			case "STRING":
				return convertFieldValueFromString(pEndingDataType, pValue);
			case "GENDERABBREV":
				return convertFieldValueFromGenderAbbrev(pEndingDataType, pValue);
			case "STATEFULL":
				return convertFieldValueFromStateFull(pEndingDataType, pValue);
			case "BIRTHDAYMM/DD/YYYY":
				return convertFieldValueFromBirthdayMM_DD_YYYY(pEndingDataType, pValue);
			default:
				return pValue;
		}
	}

	/**
	 * Handles all of the paths where the starting data type if a free form String.
	 * @param pEndingDataType The ending data type.
	 * @param pValue The value to convert.
	 * @return The appropriate object the data was converted to, or the original value if it was not implemented.
	 */
	private Object convertFieldValueFromString(String pEndingDataType, String pValue) {
		switch (pEndingDataType.toUpperCase()) {
			case "INTEGER":
				return convertStringToInteger(pValue);
			case "STRING":
				return pValue;
			default:
				if (LOGGER.isLoggable(Level.INFO)) {
					LOGGER.warning(String.format("Ending data type is not implemented, original value is returned: %s | %s", pEndingDataType, pValue));
				}
				return pValue;
		}
	}

	/**
	 * Handles converting string to integer.
	 * @param pValue The value to convert.
	 * @return The appropriate object the data was converted to, or the original value if it was not implemented.
	 */
	private Integer convertStringToInteger(String pValue) {
		try {
			return new Integer(pValue);
		} catch (NumberFormatException ex) {
			//Depending on business specs this could be a situation we shouldn't default a value and we should stop
			//processing.
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.warning(String.format("Failed to parse integer from string: %s", pValue) + ex.toString());
			}
		}

		return null;
	}

	/**
	 * Handles the conversion paths from gender abbreviation.
	 * @param pEndingDataType The ending data type.
	 * @param pValue The value to convert.
	 * @return The appropriate object the data was converted to, or the original value if it was not implemented.
	 */
	private Object convertFieldValueFromGenderAbbrev(String pEndingDataType, String pValue) {
		switch (pEndingDataType.toUpperCase()) {
			case "GENDERFULL":
				return convertGenderAbbrevToGenderFull(pValue);
			default:
				if (LOGGER.isLoggable(Level.INFO)) {
					LOGGER.warning(String.format("Ending data type is not implemented, original value is returned: %s | %s", pEndingDataType, pValue));
				}
				return pValue;
		}
	}

	/**
	 * Handles the conversion path from gender abbreviation to gender full.
	 * @param pValue The value to convert.
	 * @return The appropriate object the data was converted to, or the original value if it was not implemented.
	 */
	private String convertGenderAbbrevToGenderFull(String pValue) {
		if ("M".equalsIgnoreCase(pValue)) {
			return "male";
		} else if ("F".equalsIgnoreCase(pValue)) {
			return "female";
		}
		if (LOGGER.isLoggable(Level.WARNING)) {
			LOGGER.warning(String.format("Value for gender not implemented for conversion: %s", pValue));
		}

		return pValue;
	}

	/**
	 * Handles the conversion paths from the state full.
	 * @param pEndingDataType The ending data type.
	 * @param pValue The value to convert.
	 * @return The appropriate object the data was converted to, or the original value if it was not implemented.
	 */
	private Object convertFieldValueFromStateFull(String pEndingDataType, String pValue) {
		switch (pEndingDataType.toUpperCase()) {
			case "STATEABBREV":
				return convertStateFullToStateAbbrev(pValue);
			default:
				if (LOGGER.isLoggable(Level.INFO)) {
					LOGGER.warning(String.format("Ending data type is not implemented, original value is returned: %s | %s", pEndingDataType, pValue));
				}
				return pValue;
		}
	}

	/**
	 * Handles the conversion path from the state full to the state abbreviation.
	 *
	 * //todo - This would need to be moved over to a lookup table rather than hard coding everything in.
	 *
	 * @param pValue The value to convert.
	 * @return The appropriate object the data was converted to, or the original value if it was not implemented.
	 */
	private String convertStateFullToStateAbbrev(String pValue) {
		if ("MICHIGAN".equalsIgnoreCase(pValue)) {
			return "MI";
		} else if ("OHIO".equalsIgnoreCase(pValue)) {
			return "OH";
		}

		return pValue;
	}

	/**
	 * Handles the conversion paths from birthday MM_DD_YYYY.
	 * @param pEndingDataType The ending data type.
	 * @param pValue The value to convert.
	 * @return The appropriate object the data was converted to, or the original value if it was not implemented.
	 */
	private Object convertFieldValueFromBirthdayMM_DD_YYYY(String pEndingDataType, String pValue) {
		switch (pEndingDataType.toUpperCase()) {
			case "AGECALCULATION":
				return convertBirthdayMM_DD_YYYYToAge(pValue);
			default:
				if (LOGGER.isLoggable(Level.INFO)) {
					LOGGER.warning(String.format("Ending data type is not implemented, original value is returned: %s | %s", pEndingDataType, pValue));
				}
				return pValue;
		}
	}

	/**
	 * Handles parsing the incoming birth date and figures out the years inbetween it and todays date.
	 * @param pValue The value to convert.
	 * @return The appropriate object the data was converted to, or the original value if it was not implemented.
	 */
	private Integer convertBirthdayMM_DD_YYYYToAge(String pValue) {
		final DateTime birthDay = MM_DD_YYYY_DATE.parseDateTime(pValue);
		final Years age = Years.yearsBetween(birthDay, new DateTime());

		return age.getYears();
	}

}
