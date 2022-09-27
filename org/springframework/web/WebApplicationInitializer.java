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

package org.springframework.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Interface to be implemented in Servlet 3.0+ environments in order to configure the
 * {@link ServletContext} programmatically -- as opposed to (or possibly in conjunction
 * with) the traditional {@code web.xml}-based approach.
 *
 * <p>Implementations of this SPI will be detected automatically by {@link
 * SpringServletContainerInitializer}, which itself is bootstrapped automatically
 * by any Servlet 3.0 container. See {@linkplain SpringServletContainerInitializer its
 * Javadoc} for details on this bootstrapping mechanism.
 *
 * <h2>Example</h2>
 * <h3>The traditional, XML-based approach</h3>
 * Most Spring users building a web application will need to register Spring's {@code
 * DispatcherServlet}. For reference, in WEB-INF/web.xml, this would typically be done as
 * follows:
 * <pre class="code">
 * {@code
 * <servlet>
 *   <servlet-name>dispatcher</servlet-name>
 *   <servlet-class>
 *     org.springframework.web.servlet.DispatcherServlet
 *   </servlet-class>
 *   <init-param>
 *     <param-name>contextConfigLocation</param-name>
 *     <param-value>/WEB-INF/spring/dispatcher-config.xml</param-value>
 *   </init-param>
 *   <load-on-startup>1</load-on-startup>
 * </servlet>
 *
 * <servlet-mapping>
 *   <servlet-name>dispatcher</servlet-name>
 *   <url-pattern>/</url-pattern>
 * </servlet-mapping>}</pre>
 *
 * <h3>The code-based approach with {@code WebApplicationInitializer}</h3>
 * Here is the equivalent {@code DispatcherServlet} registration logic,
 * {@code WebApplicationInitializer}-style:
 * <pre class="code">
 * public class MyWebAppInitializer implements WebApplicationInitializer {
 *
 *    &#064;Override
 *    public void onStartup(ServletContext container) {
 *      XmlWebApplicationContext appContext = new XmlWebApplicationContext();
 *      appContext.setConfigLocation("/WEB-INF/spring/dispatcher-config.xml");
 *
 *      ServletRegistration.Dynamic dispatcher =
 *        container.addServlet("dispatcher", new DispatcherServlet(appContext));
 *      dispatcher.setLoadOnStartup(1);
 *      dispatcher.addMapping("/");
 *    }
 *
 * }</pre>
 *
 * As an alternative to the above, you can also extend from {@link
 * org.springframework.web.servlet.support.AbstractDispatcherServletInitializer}.
 *
 * As you can see, thanks to Servlet 3.0's new {@link ServletContext#addServlet} method
 * we're actually registering an <em>instance</em> of the {@code DispatcherServlet}, and
 * this means that the {@code DispatcherServlet} can now be treated like any other object
 * -- receiving constructor injection of its application context in this case.
 *
 * <p>This style is both simpler and more concise. There is no concern for dealing with
 * init-params, etc, just normal JavaBean-style properties and constructor arguments. You
 * are free to create and work with your Spring application contexts as necessary before
 * injecting them into the {@code DispatcherServlet}.
 *
 * <p>Most major Spring Web components have been updated to support this style of
 * registration.  You'll find that {@code DispatcherServlet}, {@code FrameworkServlet},
 * {@code ContextLoaderListener} and {@code DelegatingFilterProxy} all now support
 * constructor arguments. Even if a component (e.g. non-Spring, other third party) has not
 * been specifically updated for use within {@code WebApplicationInitializers}, they still
 * may be used in any case. The Servlet 3.0 {@code ServletContext} API allows for setting
 * init-params, context-params, etc programmatically.
 *
 * <h2>A 100% code-based approach to configuration</h2>
 * In the example above, {@code WEB-INF/web.xml} was successfully replaced with code in
 * the form of a {@code WebApplicationInitializer}, but the actual
 * {@code dispatcher-config.xml} Spring configuration remained XML-based.
 * {@code WebApplicationInitializer} is a perfect fit for use with Spring's code-based
 * {@code @Configuration} classes. See @{@link
 * org.springframework.context.annotation.Configuration Configuration} Javadoc for
 * complete details, but the following example demonstrates refactoring to use Spring's
 * {@link org.springframework.web.context.support.AnnotationConfigWebApplicationContext
 * AnnotationConfigWebApplicationContext} in lieu of {@code XmlWebApplicationContext}, and
 * user-defined {@code @Configuration} classes {@code AppConfig} and
 * {@code DispatcherConfig} instead of Spring XML files. This example also goes a bit
 * beyond those above to demonstrate typical configuration of the 'root' application
 * context and registration of the {@code ContextLoaderListener}:
 * <pre class="code">
 * public class MyWebAppInitializer implements WebApplicationInitializer {
 *
 *    &#064;Override
 *    public void onStartup(ServletContext container) {
 *      // Create the 'root' Spring application context
 *      AnnotationConfigWebApplicationContext rootContext =
 *        new AnnotationConfigWebApplicationContext();
 *      rootContext.register(AppConfig.class);
 *
 *      // Manage the lifecycle of the root application context
 *      container.addListener(new ContextLoaderListener(rootContext));
 *
 *      // Create the dispatcher servlet's Spring application context
 *      AnnotationConfigWebApplicationContext dispatcherContext =
 *        new AnnotationConfigWebApplicationContext();
 *      dispatcherContext.register(DispatcherConfig.class);
 *
 *      // Register and map the dispatcher servlet
 *      ServletRegistration.Dynamic dispatcher =
 *        container.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
 *      dispatcher.setLoadOnStartup(1);
 *      dispatcher.addMapping("/");
 *    }
 *
 * }</pre>
 *
 * As an alternative to the above, you can also extend from {@link
 * org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer}.
 *
 * Remember that {@code WebApplicationInitializer} implementations are <em>detected
 * automatically</em> -- so you are free to package them within your application as you
 * see fit.
 *
 * <h2>Ordering {@code WebApplicationInitializer} execution</h2>
 * {@code WebApplicationInitializer} implementations may optionally be annotated at the
 * class level with Spring's @{@link org.springframework.core.annotation.Order Order}
 * annotation or may implement Spring's {@link org.springframework.core.Ordered Ordered}
 * interface. If so, the initializers will be ordered prior to invocation. This provides
 * a mechanism for users to ensure the order in which servlet container initialization
 * occurs. Use of this feature is expected to be rare, as typical applications will likely
 * centralize all container initialization within a single {@code WebApplicationInitializer}.
 *
 * <h2>Caveats</h2>
 *
 * <h3>web.xml versioning</h3>
 * <p>{@code WEB-INF/web.xml} and {@code WebApplicationInitializer} use are not mutually
 * exclusive; for example, web.xml can register one servlet, and a {@code
 * WebApplicationInitializer} can register another. An initializer can even
 * <em>modify</em> registrations performed in {@code web.xml} through methods such as
 * {@link ServletContext#getServletRegistration(String)}. <strong>However, if
 * {@code WEB-INF/web.xml} is present in the application, its {@code version} attribute
 * must be set to "3.0" or greater, otherwise {@code ServletContainerInitializer}
 * bootstrapping will be ignored by the servlet container.</strong>
 *
 * <h3>Mapping to '/' under Tomcat</h3>
 * <p>Apache Tomcat maps its internal {@code DefaultServlet} to "/", and on Tomcat versions
 * &lt;= 7.0.14, this servlet mapping <em>cannot be overridden programmatically</em>.
 * 7.0.15 fixes this issue. Overriding the "/" servlet mapping has also been tested
 * successfully under GlassFish 3.1.<p>
 *
 * <p>
 *  要在Servlet 30+环境中实现接口,以便以编程方式配置{@link ServletContext}  - 而不是传统的{@code webxml}方法(或可能结合)
 * 
 * <p>此SPI的实现将由{@link SpringServletContainerInitializer}自动检测,该属性本身由任何Servlet 30容器自动引导有关此引导机制的详细信息,请参阅{@linkplain SpringServletContainerInitializer其Javadoc}
 * 。
 * 
 *  <h2>示例</h2> <h3>传统的基于XML的方法</h3>构建Web应用程序的大多数Spring用户都需要注册Spring的{@code DispatcherServlet}作为参考,在WEB-
 * INF / webxml中,通常将如下进行：。
 * <pre class="code">
 *  {@码
 * <servlet>
 *  <servlet的名称>调度</servlet的名称>
 * <servlet-class>
 *  orgspringframeworkwebservletDispatcherServlet
 * </servlet-class>
 * <init-param>
 *  <param-name> contextConfigLocation </param-name> <param-value> / WEB-INF / spring / dispatcher-confi
 * gxml </param-value>。
 * </init-param>
 *  <负载上启动> 1 </负载上启动>
 * </servlet>
 * 
 * <servlet-mapping>
 * <servlet-name> dispatcher </servlet-name> <url-pattern> / </url-pattern> </servlet-mapping>} </pre>
 * 
 *  <h3>使用{@code WebApplicationInitializer} </h3>的基于代码的方法以下是等效的{@code DispatcherServlet}注册逻辑,{@code WebApplicationInitializer}
 *  -style：。
 * <pre class="code">
 *  public class MyWebAppInitializer实现WebApplicationInitializer {
 * 
 *  @Override public void onStartup(ServletContext container){XmlWebApplicationContext appContext = new XmlWebApplicationContext(); appContextsetConfigLocation( "/ WEB-INF /弹簧/调度程序-configxml");。
 * 
 *  ServletRegistrationDynamic dispatcher = containeraddServlet("dispatcher",新的DispatcherServlet(appCont
 * ext)); dispatchersetLoadOnStartup(1); dispatcheraddMapping( "/"); }。
 * 
 *  } </PRE>
 * 
 * 作为上述的替代方案,您还可以从{@link orgspringframeworkwebservletsupportAbstractDispatcherServletInitializer}
 * 
 *  正如你所看到的,感谢Servlet 30的新的{@link ServletContext#addServlet}方法,我们实际上注册了一个{@code DispatcherServlet}的<em>实
 * 例</em>,这意味着{@code DispatcherServlet}现在可以像任何其他对象一样对待 - 在这种情况下接收其应用程序上下文的构造函数注入。
 * 
 *  <p>这个样式既简单又简洁,不需要处理init-params等,只是一般的JavaBean样式属性和构造函数参数您可以在注入之前根据需要自由创建和使用Spring应用程序上下文进入{@code DispatcherServlet}
 * 。
 * 
 * <p>大多数主要的Spring Web组件已经更新,以支持这种注册风格您会发现{@code DispatcherServlet},{@code FrameworkServlet},{@code ContextLoaderListener}
 * 和{@code DelegatingFilterProxy}现在都支持构造函数参数即使一个组件(例如非Spring,其他第三方)没有被特别更新以在{@code WebApplicationInitializers}
 * 中使用,它们仍然可以在任何情况下使用。
 * 
 * @author Chris Beams
 * @since 3.1
 * @see SpringServletContainerInitializer
 * @see org.springframework.web.context.AbstractContextLoaderInitializer
 * @see org.springframework.web.servlet.support.AbstractDispatcherServletInitializer
 * @see org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer
 */
public interface WebApplicationInitializer {

	/**
	 * Configure the given {@link ServletContext} with any servlets, filters, listeners
	 * context-params and attributes necessary for initializing this web application. See
	 * examples {@linkplain WebApplicationInitializer above}.
	 * <p>
	 * Servlet 30 {@code ServletContext} API允许设置init-参数,上下文参数等。
	 * 
	 * </h2>在上面的例子中,{@code WEB-INF / webxml}已成功地替换为{@code WebApplicationInitializer}形式的代码,但实际的{ @code dispatcher-configxml}
	 *  Spring配置仍然是基于XML的{@code WebApplicationInitializer}非常适合使用Spring的基于代码的{@code @Configuration}类。
	 * 请参阅@ {@ link orgspringframeworkcontextannotationConfiguration Configuration} Javadoc获取完整的详细信息,但是以下示例演
	 * 示如何重构使用Spring的{@link orgspringframeworkwebcontextsupportAnnotationConfigWebApplicationContext AnnotationConfigWebApplicationContext}
	 * 代替{@code XmlWebApplicationContext}和用户定义的{@code @Configuration}类{@code AppConfig}和{@code DispatcherConfig}
	 * 而不是Spring XML文件这个例子也有点超出上面的内容来演示"root"应用程序上下文的典型配置和{@code ContextLoaderListener}的注册：。
	 * <pre class="code">
	 * public class MyWebAppInitializer实现WebApplicationInitializer {
	 * 
	 *  @Override public void onStartup(ServletContext container){//创建'root'Spring应用程序上下文AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext(); rootContextregister(AppConfigclass);。
	 * 
	 *  //管理根应用程序上下文的生命周期containeraddListener(新的ContextLoaderListener(rootContext));
	 * 
	 *  //创建dispatcher servlet的Spring应用程序上下文AnnotationConfigWebApplicationContext dispatcherContext = new An
	 * notationConfigWebApplicationContext(); dispatcherContextregister(DispatcherConfigclass);。
	 * 
	 * //注册和映射dispatcher servlet ServletRegistrationDynamic dispatcher = containeraddServlet("dispatcher",新的
	 * DispatcherServlet(dispatcherContext)); dispatchersetLoadOnStartup(1); dispatcheraddMapping( "/"); }。
	 * 
	 *  } </PRE>
	 * 
	 * 
	 * @param servletContext the {@code ServletContext} to initialize
	 * @throws ServletException if any call against the given {@code ServletContext}
	 * throws a {@code ServletException}
	 */
	void onStartup(ServletContext servletContext) throws ServletException;

}
