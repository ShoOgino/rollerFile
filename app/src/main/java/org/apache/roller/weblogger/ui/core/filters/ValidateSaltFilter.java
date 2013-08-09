/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */

package org.apache.roller.weblogger.ui.core.filters;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.weblogger.config.WebloggerConfig;
import org.apache.roller.weblogger.ui.rendering.util.cache.SaltCache;

/**
 * Filter checks all POST request for presence of valid salt value and rejects
 * those without a salt value or with a salt value not generated by this Roller
 * instance.
 */
public class ValidateSaltFilter implements Filter {
	private static Log log = LogFactory.getLog(ValidateSaltFilter.class);
	private Set<String> ignored = new HashSet<String>();

	// @Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;

		if (httpReq.getMethod().equals("POST")) {

			// TODO multipart/form-data does not send parameters
			if (!isIgnoredURL(((HttpServletRequest) request).getServletPath())) {
				String salt = (String) httpReq.getParameter("salt");
				SaltCache saltCache = SaltCache.getInstance();
				if (salt == null || saltCache.get(salt) == null
						|| saltCache.get(salt).equals(false)) {
					throw new ServletException("Security Violation");
				}
			}

		}
		chain.doFilter(request, response);
	}

	// @Override
	public void init(FilterConfig filterConfig) throws ServletException {

		// Construct our list of ignord urls
		String urls = WebloggerConfig.getProperty("salt.ignored.urls");
		String[] urlsArray = StringUtils.stripAll(StringUtils.split(urls, ","));
		for (int i = 0; i < urlsArray.length; i++)
			this.ignored.add(urlsArray[i]);

	}

	// @Override
	public void destroy() {
	}

	/**
	 * Checks if this is an ignored url.
	 * 
	 * @param theUrl
	 *            the the url
	 * 
	 * @return true, if is ignored resource
	 */
	private boolean isIgnoredURL(String theUrl) {

		int i = theUrl.lastIndexOf('/');

		// If its not a resource then do not ignore it
		if (i <= 0 || i == theUrl.length() - 1)
			return false;

		return ignored.contains(theUrl.substring(i + 1));

	}
}