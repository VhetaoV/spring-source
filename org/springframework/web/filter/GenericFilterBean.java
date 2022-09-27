/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.web.filter;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourceLoader;
import org.springframework.web.context.support.StandardServletEnvironment;
import org.springframework.web.util.NestedServletException;

/**
 * Simple base implementation of {@link javax.servlet.Filter} which treats
 * its config parameters ({@code init-param} entries within the
 * {@code filter} tag in {@code web.xml}) as bean properties.
 *
 * <p>A handy superclass for any type of filter. Type conversion of config
 * parameters is automatic, with the corresponding setter method getting
 * invoked with the converted value. It is also possible for subclasses to
 * specify required properties. Parameters without matching bean property
 * setter will simply be ignored.
 *
 * <p>This filter leaves actual filtering to subclasses, which have to
 * implement the {@link javax.servlet.Filter#doFilter} method.
 *
 * <p>This generic filter base class has no dependency on the Spring
 * {@link org.springframework.context.ApplicationContext} concept.
 * Filters usually don't load their own context but rather access service
 * beans from the Spring root application context, accessible via the
 * filter's {@link #getServletContext() ServletContext} (see
 * {@link org.springframework.web.context.support.WebApplicationContextUtils}).
 *
 * <p>
 *  {@link javaxservletFilter}的简单基础实现将其配置参数({@code init-param}条目{{codecode} {@code webxml})中的条目)作为bean属性
 * 。
 * 
 * <p>任何类型的过滤器的一个方便的超类配置参数的类型转换是自动的,相应的setter方法使用转换的值调用。子类也可以指定所需属性不匹配bean属性setter的参数将被忽略
 * 
 *  <p>此过滤器将实际过滤留给子类,这些子类必须实现{@link javaxservletFilter#doFilter}方法
 * 
 * <p>这个通用的过滤器基类不依赖于Spring {@link orgspringframeworkcontextApplicationContext}概念Filter通常不加载自己的上下文,而是从Spr
 * ing根应用程序上下文访问服务bean,可以通过过滤器的{@link #getServletContext ()ServletContext}(参见{@link orgspringframeworkwebcontextsupportWebApplicationContextUtils}
 * )。
 * 
 * 
 * @author Juergen Hoeller
 * @since 06.12.2003
 * @see #addRequiredProperty
 * @see #initFilterBean
 * @see #doFilter
 */
public abstract class GenericFilterBean implements
		Filter, BeanNameAware, EnvironmentAware, ServletContextAware, InitializingBean, DisposableBean {

	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * Set of required properties (Strings) that must be supplied as
	 * config parameters to this filter.
	 * <p>
	 *  必须作为配置参数提供的必需属性(字符串)集合到此过滤器
	 * 
	 */
	private final Set<String> requiredProperties = new HashSet<String>();

	private FilterConfig filterConfig;

	private String beanName;

	private Environment environment = new StandardServletEnvironment();

	private ServletContext servletContext;


	/**
	 * Stores the bean name as defined in the Spring bean factory.
	 * <p>Only relevant in case of initialization as bean, to have a name as
	 * fallback to the filter name usually provided by a FilterConfig instance.
	 * <p>
	 *  存储Spring bean工厂中定义的bean名称<p>只有在初始化为bean的情况下相关,才能将名称替换为通常由FilterConfig实例提供的过滤器名称
	 * 
	 * 
	 * @see org.springframework.beans.factory.BeanNameAware
	 * @see #getFilterName()
	 */
	@Override
	public final void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	/**
	 * {@inheritDoc}
	 * <p>Any environment set here overrides the {@link StandardServletEnvironment}
	 * provided by default.
	 * <p>This {@code Environment} object is used only for resolving placeholders in
	 * resource paths passed into init-parameters for this filter. If no init-params are
	 * used, this {@code Environment} can be essentially ignored.
	 * <p>
	 * {@inheritDoc} <p>此处设置的任何环境都将覆盖默认提供的{@link StandardServletEnvironment} <p>此{@code Environment}对象仅用于解析传
	 * 递给此过滤器的init参数的资源路径中的占位符如果没有使用init-params,这个{@code Environment}可以被本质上忽略。
	 * 
	 * 
	 * @see #init(FilterConfig)
	 */
	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	/**
	 * Stores the ServletContext that the bean factory runs in.
	 * <p>Only relevant in case of initialization as bean, to have a ServletContext
	 * as fallback to the context usually provided by a FilterConfig instance.
	 * <p>
	 *  存储bean工厂运行的ServletContext <p>只有在初始化为bean的情况下相关,才能将ServletContext作为通常由FilterConfig实例提供的上下文回退
	 * 
	 * 
	 * @see org.springframework.web.context.ServletContextAware
	 * @see #getServletContext()
	 */
	@Override
	public final void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * Calls the {@code initFilterBean()} method that might
	 * contain custom initialization of a subclass.
	 * <p>Only relevant in case of initialization as bean, where the
	 * standard {@code init(FilterConfig)} method won't be called.
	 * <p>
	 *  调用可能包含子类的自定义初始化的{@code initFilterBean()}方法<p>仅在初始化为bean的情况下相关,其中将不会调用标准的{@code init(FilterConfig))方法。
	 * 
	 * 
	 * @see #initFilterBean()
	 * @see #init(javax.servlet.FilterConfig)
	 */
	@Override
	public void afterPropertiesSet() throws ServletException {
		initFilterBean();
	}


	/**
	 * Subclasses can invoke this method to specify that this property
	 * (which must match a JavaBean property they expose) is mandatory,
	 * and must be supplied as a config parameter. This should be called
	 * from the constructor of a subclass.
	 * <p>This method is only relevant in case of traditional initialization
	 * driven by a FilterConfig instance.
	 * <p>
	 * 子类可以调用此方法来指定此属性(必须与其公开的JavaBean属性相匹配)是必需的,并且必须作为配置参数提供。
	 * 这应该从子类的构造函数调用<p>此方法仅与由FilterConfig实例驱动的传统初始化的情况。
	 * 
	 * 
	 * @param property name of the required property
	 */
	protected final void addRequiredProperty(String property) {
		this.requiredProperties.add(property);
	}

	/**
	 * Standard way of initializing this filter.
	 * Map config parameters onto bean properties of this filter, and
	 * invoke subclass initialization.
	 * <p>
	 *  初始化此过滤器的标准方法将配置参数映射到此过滤器的bean属性,并调用子类初始化
	 * 
	 * 
	 * @param filterConfig the configuration for this filter
	 * @throws ServletException if bean properties are invalid (or required
	 * properties are missing), or if subclass initialization fails.
	 * @see #initFilterBean
	 */
	@Override
	public final void init(FilterConfig filterConfig) throws ServletException {
		Assert.notNull(filterConfig, "FilterConfig must not be null");
		if (logger.isDebugEnabled()) {
			logger.debug("Initializing filter '" + filterConfig.getFilterName() + "'");
		}

		this.filterConfig = filterConfig;

		// Set bean properties from init parameters.
		try {
			PropertyValues pvs = new FilterConfigPropertyValues(filterConfig, this.requiredProperties);
			BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
			ResourceLoader resourceLoader = new ServletContextResourceLoader(filterConfig.getServletContext());
			bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader, this.environment));
			initBeanWrapper(bw);
			bw.setPropertyValues(pvs, true);
		}
		catch (BeansException ex) {
			String msg = "Failed to set bean properties on filter '" +
				filterConfig.getFilterName() + "': " + ex.getMessage();
			logger.error(msg, ex);
			throw new NestedServletException(msg, ex);
		}

		// Let subclasses do whatever initialization they like.
		initFilterBean();

		if (logger.isDebugEnabled()) {
			logger.debug("Filter '" + filterConfig.getFilterName() + "' configured successfully");
		}
	}

	/**
	 * Initialize the BeanWrapper for this GenericFilterBean,
	 * possibly with custom editors.
	 * <p>This default implementation is empty.
	 * <p>
	 *  初始化此GenericFilterBean的BeanWrapper,可能使用自定义编辑器<p>此默认实现为空
	 * 
	 * 
	 * @param bw the BeanWrapper to initialize
	 * @throws BeansException if thrown by BeanWrapper methods
	 * @see org.springframework.beans.BeanWrapper#registerCustomEditor
	 */
	protected void initBeanWrapper(BeanWrapper bw) throws BeansException {
	}


	/**
	 * Make the FilterConfig of this filter available, if any.
	 * Analogous to GenericServlet's {@code getServletConfig()}.
	 * <p>Public to resemble the {@code getFilterConfig()} method
	 * of the Servlet Filter version that shipped with WebLogic 6.1.
	 * <p>
	 * 如果有任何类似于GenericServlet的类似于{@code getServletConfig()} <p>的公开类似于WebLogic 61附带的Servlet过滤器版本的{@code getFilterConfig()}
	 * 方法,则可以使该过滤器的FilterConfig可用。
	 * 
	 * 
	 * @return the FilterConfig instance, or {@code null} if none available
	 * @see javax.servlet.GenericServlet#getServletConfig()
	 */
	public final FilterConfig getFilterConfig() {
		return this.filterConfig;
	}

	/**
	 * Make the name of this filter available to subclasses.
	 * Analogous to GenericServlet's {@code getServletName()}.
	 * <p>Takes the FilterConfig's filter name by default.
	 * If initialized as bean in a Spring application context,
	 * it falls back to the bean name as defined in the bean factory.
	 * <p>
	 *  将此过滤器的名称用于子类类似于GenericServlet的{@code getServletName()} <p>默认使用FilterConfig的过滤器名称如果在Spring应用程序上下文中初始化
	 * 为bean,则它将返回到豆厂。
	 * 
	 * 
	 * @return the filter name, or {@code null} if none available
	 * @see javax.servlet.GenericServlet#getServletName()
	 * @see javax.servlet.FilterConfig#getFilterName()
	 * @see #setBeanName
	 */
	protected final String getFilterName() {
		return (this.filterConfig != null ? this.filterConfig.getFilterName() : this.beanName);
	}

	/**
	 * Make the ServletContext of this filter available to subclasses.
	 * Analogous to GenericServlet's {@code getServletContext()}.
	 * <p>Takes the FilterConfig's ServletContext by default.
	 * If initialized as bean in a Spring application context,
	 * it falls back to the ServletContext that the bean factory runs in.
	 * <p>
	 * 使此过滤器的ServletContext可用于子类类似于GenericServlet的{@code getServletContext()} <p>默认情况下使用FilterConfig的Servlet
	 * Context如果在Spring应用程序上下文中初始化为Bean,则返回到Bean工厂运行的ServletContext。
	 * 
	 * 
	 * @return the ServletContext instance, or {@code null} if none available
	 * @see javax.servlet.GenericServlet#getServletContext()
	 * @see javax.servlet.FilterConfig#getServletContext()
	 * @see #setServletContext
	 */
	protected final ServletContext getServletContext() {
		return (this.filterConfig != null ? this.filterConfig.getServletContext() : this.servletContext);
	}


	/**
	 * Subclasses may override this to perform custom initialization.
	 * All bean properties of this filter will have been set before this
	 * method is invoked.
	 * <p>Note: This method will be called from standard filter initialization
	 * as well as filter bean initialization in a Spring application context.
	 * Filter name and ServletContext will be available in both cases.
	 * <p>This default implementation is empty.
	 * <p>
	 *  子类可以覆盖它来执行自定义初始化此过滤器的所有bean属性将在调用此方法之前设置<p>注意：此方法将从标准过滤器初始化以及Spring应用程序上下文中的过滤器bean初始化过滤名称并且ServletC
	 * ontext将在两种情况下都可用<p>此默认实现为空。
	 * 
	 * 
	 * @throws ServletException if subclass initialization fails
	 * @see #getFilterName()
	 * @see #getServletContext()
	 */
	protected void initFilterBean() throws ServletException {
	}

	/**
	 * Subclasses may override this to perform custom filter shutdown.
	 * <p>Note: This method will be called from standard filter destruction
	 * as well as filter bean destruction in a Spring application context.
	 * <p>This default implementation is empty.
	 * <p>
	 * 子类可以覆盖此执行自定义过滤器关闭注意：此方法将从标准过滤器销毁以及Spring应用程序上下文中的过滤器bean销毁调用<p>此默认实现为空
	 * 
	 */
	@Override
	public void destroy() {
	}


	/**
	 * PropertyValues implementation created from FilterConfig init parameters.
	 * <p>
	 *  从FilterConfig init参数创建的PropertyValues实现
	 * 
	 */
	@SuppressWarnings("serial")
	private static class FilterConfigPropertyValues extends MutablePropertyValues {

		/**
		 * Create new FilterConfigPropertyValues.
		 * <p>
		 *  创建新的FilterConfigPropertyValues
		 * 
		 * @param config FilterConfig we'll use to take PropertyValues from
		 * @param requiredProperties set of property names we need, where
		 * we can't accept default values
		 * @throws ServletException if any required properties are missing
		 */
		public FilterConfigPropertyValues(FilterConfig config, Set<String> requiredProperties)
			throws ServletException {

			Set<String> missingProps = (requiredProperties != null && !requiredProperties.isEmpty()) ?
					new HashSet<String>(requiredProperties) : null;

			Enumeration<?> en = config.getInitParameterNames();
			while (en.hasMoreElements()) {
				String property = (String) en.nextElement();
				Object value = config.getInitParameter(property);
				addPropertyValue(new PropertyValue(property, value));
				if (missingProps != null) {
					missingProps.remove(property);
				}
			}

			// Fail if we are still missing properties.
			if (missingProps != null && missingProps.size() > 0) {
				throw new ServletException(
					"Initialization from FilterConfig for filter '" + config.getFilterName() +
					"' failed; the following required properties were missing: " +
					StringUtils.collectionToDelimitedString(missingProps, ", "));
			}
		}
	}

}
