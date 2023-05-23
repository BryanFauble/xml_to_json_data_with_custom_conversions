package com.bfauble;

import lombok.Data;

/**
 * Holds onto all of the information needed to convert an XML field into a JSON field.
 *
 * @author Bryan Fauble
 */
@Data
public class MappedField {
	private String xmlName;
	private String xmlType;
	private String xmlDataType;

	private String jsonName;
	private String jsonDataType;
}
