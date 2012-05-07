/**
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package org.freshwaterlife.servlet.filters.sso;


import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.util.CookieUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * See http://issues.liferay.com/browse/LEP-5943.
 * </p>
 *
 * @author Prashant Dighe
 * @author Brian Wing Shun Chan
 * @author Wesley Gong
 */
public class OpenSSOUtil {

		public static Map<String, String> getAttributes(
		HttpServletRequest request, String serviceUrl) {

		return _instance._getAttributes(request, serviceUrl);
	}
		
	public static String getSubjectId(
		HttpServletRequest request, String serviceUrl) {

		return _instance._getSubjectId(request, serviceUrl);
	}	
	
	public static boolean isAuthenticated(
			HttpServletRequest request, String serviceUrl)
		throws IOException {

		return _instance._isAuthenticated(request, serviceUrl);
	}
	
	private Map<String, String> _getAttributes(
		HttpServletRequest request, String serviceUrl) {

		Map<String, String> nameValues = new HashMap<String, String>();

		String url = serviceUrl + _GET_ATTRIBUTES;

		try {
			URL urlObj = new URL(url);

			HttpURLConnection urlc = (HttpURLConnection)urlObj.openConnection();

			urlc.setDoOutput(true);
			urlc.setRequestMethod("POST");
			urlc.setRequestProperty(
				"Content-type", "application/x-www-form-urlencoded");

			String[] cookieNames = _getCookieNames(serviceUrl);

			_setCookieProperty(request, urlc, cookieNames);

			OutputStreamWriter osw = new OutputStreamWriter(
				urlc.getOutputStream());

			osw.write("dummy");

			osw.flush();

			int responseCode = urlc.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				UnsyncBufferedReader unsyncBufferedReader =
					new UnsyncBufferedReader(
						new InputStreamReader((InputStream)urlc.getContent()));

				String line = null;

				while ((line = unsyncBufferedReader.readLine()) != null) {
					if (line.startsWith("userdetails.attribute.name=")) {
						String name = line.replaceFirst(
							"userdetails.attribute.name=", "");

						line = unsyncBufferedReader.readLine();

						if (line.startsWith("userdetails.attribute.value=")) {
							String value = line.replaceFirst(
								"userdetails.attribute.value=", "");

							nameValues.put(name, value);
						}
					}
				}
			}
			else {
				System.out.println("Attributes response code " + responseCode);
			}
		}
		catch (MalformedURLException mfue) {			
				System.out.println(mfue);			
		}
		catch (IOException ioe) {			
				System.out.println(ioe);			
		}

		return nameValues;
	}	
	
	private String[] _getCookieNames(String serviceUrl) {
		String[] cookieNames = _cookieNamesMap.get(serviceUrl);

		if (cookieNames != null) {
			return cookieNames;
		}

		List<String> cookieNamesList = new ArrayList<String>();

		try {
			String cookieName = null;

			String url = serviceUrl + _GET_COOKIE_NAME;

			URL urlObj = new URL(url);

			HttpURLConnection urlc = (HttpURLConnection)urlObj.openConnection();

			UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(
					new InputStreamReader((InputStream)urlc.getContent()));

			int responseCode = urlc.getResponseCode();

			if (responseCode != HttpURLConnection.HTTP_OK) {				
					System.out.println(url + " has response code " + responseCode);			
			}
			else {
				String line = null;

				while ((line = unsyncBufferedReader.readLine()) != null) {
					if (line.startsWith("string=")) {
						line = line.replaceFirst("string=", "");

						cookieName = line;
					}
				}
			}

			url = serviceUrl + _GET_COOKIE_NAMES;

			urlObj = new URL(url);

			urlc = (HttpURLConnection)urlObj.openConnection();

			unsyncBufferedReader = new UnsyncBufferedReader(
				new InputStreamReader((InputStream)urlc.getContent()));

			if (urlc.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println(url + " has response code " + responseCode);				
			}
			else {
				String line = null;

				while ((line = unsyncBufferedReader.readLine()) != null) {
					if (line.startsWith("string=")) {
						line = line.replaceFirst("string=", "");

						if (cookieName.equals(line)) {
							cookieNamesList.add(0, cookieName);
						}
						else {
							cookieNamesList.add(line);
						}
					}
				}
			}
		}
		catch (IOException ioe) {
			System.out.println(ioe);			
		}

		cookieNames = cookieNamesList.toArray(
			new String[cookieNamesList.size()]);

		if (cookieNames.length > 0) {
			_cookieNamesMap.put(serviceUrl, cookieNames);
		}

		return cookieNames;
	}
		
	private String _getSubjectId(
		HttpServletRequest request, String serviceUrl) {

		String cookieName = _getCookieNames(serviceUrl)[0];

		return CookieUtil.get(request, cookieName);
	}	
	
	private boolean _isAuthenticated(
			HttpServletRequest request, String serviceUrl)
		throws IOException {

		boolean authenticated = false;

		String url = serviceUrl + _VALIDATE_TOKEN;

		URL urlObj = new URL(url);

		HttpURLConnection urlc = (HttpURLConnection)urlObj.openConnection();

		urlc.setDoOutput(true);
		urlc.setRequestMethod("POST");
		urlc.setRequestProperty(
			"Content-type", "application/x-www-form-urlencoded");

		String[] cookieNames = _getCookieNames(serviceUrl);

		if (cookieNames.length == 0) {
			throw new IOException(
				"Cookie names from OpenSSO service are not accessible");
		}

		_setCookieProperty(request, urlc, cookieNames);

		OutputStreamWriter osw = new OutputStreamWriter(urlc.getOutputStream());

		osw.write("dummy");

		osw.flush();

		int responseCode = urlc.getResponseCode();

		if (responseCode == HttpURLConnection.HTTP_OK) {
			String data = StringUtil.read(urlc.getInputStream());

			if (data.toLowerCase().indexOf("boolean=true") != -1) {
				authenticated = true;
			}
		}		

		return authenticated;
	}
	
	private void _setCookieProperty(
		HttpServletRequest request, HttpURLConnection urlc,
		String[] cookieNames) {

		if (cookieNames.length == 0) {
			return;
		}

		StringBundler sb = new StringBundler(cookieNames.length * 4);

		for (String cookieName : cookieNames) {
			String cookieValue = CookieUtil.get(request, cookieName);

			sb.append(cookieName);
			sb.append(StringPool.EQUAL);
			sb.append(StringPool.QUOTE);
			sb.append(cookieValue);
			sb.append(StringPool.QUOTE);
			sb.append(StringPool.SEMICOLON);
		}

		urlc.setRequestProperty("Cookie", sb.toString());
	}
	
	private static final String _GET_ATTRIBUTES = "/identity/attributes";

	private static final String _GET_COOKIE_NAME =
		"/identity/getCookieNameForToken";

	private static final String _GET_COOKIE_NAMES =
		"/identity/getCookieNamesToForward";

	private static final String _VALIDATE_TOKEN = "/identity/isTokenValid";

	private static OpenSSOUtil _instance = new OpenSSOUtil();

	private Map<String, String[]> _cookieNamesMap =
		new ConcurrentHashMap<String, String[]>();

}