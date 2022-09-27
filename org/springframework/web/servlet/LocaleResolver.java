/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface for web-based locale resolution strategies that allows for
 * both locale resolution via the request and locale modification via
 * request and response.
 *
 * <p>This interface allows for implementations based on request, session,
 * cookies, etc. The default implementation is
 * {@link org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver},
 * simply using the request's locale provided by the respective HTTP header.
 *
 * <p>Use {@link org.springframework.web.servlet.support.RequestContext#getLocale()}
 * to retrieve the current locale in controllers or views, independent
 * of the actual resolution strategy.
 *
 * <p>Note: As of Spring 4.0, there is an extended strategy interface
 * called {@link LocaleContextResolver}, allowing for resolution of
 * a {@link org.springframework.context.i18n.LocaleContext} object,
 * potentially including associated time zone information. Spring's
 * provided resolver implementations implement the extended
 * {@link LocaleContextResolver} interface wherever appropriate.
 *
 * <p>
 *  基于Web的区域设置解决策略的界面,可通过请求和响应通过请求和区域设置修改来进行区域设置解析
 * 
 * <p>此接口允许基于请求,会话,Cookie等的实现。
 * 默认实现是{@link orgspringframeworkwebservleti18nAcceptHeaderLocaleResolver},只需使用相应HTTP头提供的请求的区域设置。
 * 
 *  <p>使用{@link orgspringframeworkwebservletsupportRequestContext#getLocale()}来检索控制器或​​视图中的当前区域设置,而与实际分辨
 * 率策略无关。
 * 
 * 注意：从Spring 40开始,有一个名为{@link LocaleContextResolver}的扩展策略接口,允许解析{@link orgspringframeworkcontexti18nLocaleContext}
 * 
 * @author Juergen Hoeller
 * @since 27.02.2003
 * @see LocaleContextResolver
 * @see org.springframework.context.i18n.LocaleContextHolder
 * @see org.springframework.web.servlet.support.RequestContext#getLocale
 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocale
 */
public interface LocaleResolver {

	/**
	 * Resolve the current locale via the given request.
	 * Can return a default locale as fallback in any case.
	 * <p>
	 * 对象,可能包括关联的时区信息Spring提供的解析器实现实现扩展的{@链接LocaleContextResolver}接口。
	 * 
	 * 
	 * @param request the request to resolve the locale for
	 * @return the current locale (never {@code null})
	 */
	Locale resolveLocale(HttpServletRequest request);

	/**
	 * Set the current locale to the given one.
	 * <p>
	 *  通过给定的请求解析当前的区域设置在任何情况下都可以返回默认的区域设置作为回退
	 * 
	 * 
	 * @param request the request to be used for locale modification
	 * @param response the response to be used for locale modification
	 * @param locale the new locale, or {@code null} to clear the locale
	 * @throws UnsupportedOperationException if the LocaleResolver
	 * implementation does not support dynamic changing of the locale
	 */
	void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale);

}
