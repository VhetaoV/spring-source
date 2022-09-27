/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.core.annotation.AliasFor;

/**
 * Indicates one or more resources containing bean definitions to import.
 *
 * <p>Like {@link Import @Import}, this annotation provides functionality similar to
 * the {@code <import/>} element in Spring XML. It is typically used when designing
 * {@link Configuration @Configuration} classes to be bootstrapped by an
 * {@link AnnotationConfigApplicationContext}, but where some XML functionality such
 * as namespaces is still necessary.
 *
 * <p>By default, arguments to the {@link #value} attribute will be processed using a
 * {@link org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader GroovyBeanDefinitionReader}
 * if ending in {@code ".groovy"}; otherwise, an
 * {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader XmlBeanDefinitionReader}
 * will be used to parse Spring {@code <beans/>} XML files. Optionally, the {@link #reader}
 * attribute may be declared, allowing the user to choose a custom {@link BeanDefinitionReader}
 * implementation.
 *
 * <p>
 *  指示一个或多个包含要导入的bean定义的资源
 * 
 * <p>像{@link Import @Import}一样,此注释提供与Spring XML中的{@code <import />}元素类似的功能。
 * 通常在将{@link Configuration @Configuration}类设计为由{@link AnnotationConfigApplicationContext},但是仍然需要一些XML功能
 * ,如命名空间。
 * <p>像{@link Import @Import}一样,此注释提供与Spring XML中的{@code <import />}元素类似的功能。
 * 
 * <p>默认情况下,{@link #value}属性的参数将使用{@link orgspringframeworkbeansfactorygroovyGroovyBeanDefinitionReader GroovyBeanDefinitionReader}
 * 处理,如果以{@code"groovy"}结尾;否则,将使用{@link orgspringframeworkbeansfactoryxmlXmlBeanDefinitionReader XmlBeanDefinitionReader}
 * 来解析Spring {@code <beans />} XML文件可选地,可以声明{@link #reader}属性,允许用户选择自定义{@link BeanDefinitionReader }实现。
 * 
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.0
 * @see Configuration
 * @see Import
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ImportResource {

	/**
	 * Alias for {@link #locations}.
	 * <p>
	 *  {@link #locations}的别名
	 * 
	 * 
	 * @see #locations
	 * @see #reader
	 */
	@AliasFor("locations")
	String[] value() default {};

	/**
	 * Resource locations from which to import.
	 * <p>Supports resource-loading prefixes such as {@code classpath:},
	 * {@code file:}, etc.
	 * <p>Consult the Javadoc for {@link #reader} for details on how resources
	 * will be processed.
	 * <p>
	 *  要导入的资源位置<p>支持资源加载前缀,例如{@code classpath：},{@code file：}等等<p>请查阅{@link #reader}的Javadoc,了解资源将如何被处理
	 * 
	 * 
	 * @since 4.2
	 * @see #value
	 * @see #reader
	 */
	@AliasFor("value")
	String[] locations() default {};

	/**
	 * {@link BeanDefinitionReader} implementation to use when processing
	 * resources specified via the {@link #value} attribute.
	 * <p>By default, the reader will be adapted to the resource path specified:
	 * {@code ".groovy"} files will be processed with a
	 * {@link org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader GroovyBeanDefinitionReader};
	 * whereas, all other resources will be processed with an
	 * {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader XmlBeanDefinitionReader}.
	 * <p>
	 * {@link BeanDefinitionReader}实现在处理通过{@link #value}属性指定的资源时使用<p>默认情况下,读取器将适应指定的资源路径：{@code"groovy"}文件将被
	 * 处理{@link orgspringframeworkbeansfactorygroovyGroovyBeanDefinitionReader GroovyBeanDefinitionReader};而
	 * 所有其他资源将使用{@link orgspringframeworkbeansfactoryxmlXmlBeanDefinitionReader XmlBeanDefinitionReader}进行处理
	 * 
	 * @see #value
	 */
	Class<? extends BeanDefinitionReader> reader() default BeanDefinitionReader.class;

}
