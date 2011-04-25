/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.github.internal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

/**
 * Date formatter for date format present in the GitHub v3 API.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class DateFormatter implements JsonDeserializer<Date> {

	private List<DateFormat> formats;

	/**
	 * Create date formatter
	 */
	public DateFormatter() {
		this.formats = new LinkedList<DateFormat>();
		this.formats.add(new SimpleDateFormat(IGitHubConstants.DATE_FORMAT));
		this.formats
				.add(new SimpleDateFormat(IGitHubConstants.DATE_FORMAT_V2_1));
		TimeZone timeZone = TimeZone.getTimeZone("Zulu"); //$NON-NLS-1$
		for (DateFormat format : this.formats)
			format.setTimeZone(timeZone);
	}

	/**
	 * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
	 *      java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
	 */
	public Date deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonParseException exception = null;
		for (DateFormat format : this.formats)
			try {
				synchronized (format) {
					return format.parse(json.getAsString());
				}
			} catch (ParseException e) {
				exception = new JsonParseException(e);
			}
		throw exception;
	}

}
