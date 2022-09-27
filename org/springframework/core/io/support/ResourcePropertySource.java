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

package org.springframework.core.io.support;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

/**
 * Subclass of {@link PropertiesPropertySource} that loads a {@link Properties} object
 * from a given {@link org.springframework.core.io.Resource} or resource location such as
 * {@code "classpath:/com/myco/foo.properties"} or {@code "file:/path/to/file.xml"}.
 *
 * <p>Both traditional and XML-based properties file formats are supported; however, in
 * order for XML processing to take effect, the underlying {@code Resource}'s
 * {@link org.springframework.core.io.Resource#getFilename() getFilename()} method must
 * return a non-{@code null} value that ends in {@code ".xml"}.
 *
 * <p>
 * 从{@link orgspringframeworkcoreioResource}或资源位置(例如{@code"classpath：/ com / myco / fooproperties"}或{@code"文件中加载{@link Properties}
 * 对象的{@link PropertiesPropertySource}子类： /路径/到/ filexml"}。
 * 
 *  <p>支持传统和基于XML的属性文件格式;然而,为了使XML处理生效,底层{@code Resource}的{@link orgspringframeworkcoreioResource#getFilename()getFilename())方法必须返回一个非{@ code null}
 * 值,以{@code "XML"}。
 * 
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see org.springframework.core.io.Resource
 * @see org.springframework.core.io.support.EncodedResource
 */
public class ResourcePropertySource extends PropertiesPropertySource {

	/** The original resource name, if different from the given name */
	private final String resourceName;


	/**
	 * Create a PropertySource having the given name based on Properties
	 * loaded from the given encoded resource.
	 * <p>
	 *  根据从给定编码资源加载的属性创建一个具有给定名称的PropertySource
	 * 
	 */
	public ResourcePropertySource(String name, EncodedResource resource) throws IOException {
		super(name, PropertiesLoaderUtils.loadProperties(resource));
		this.resourceName = getNameForResource(resource.getResource());
	}

	/**
	 * Create a PropertySource based on Properties loaded from the given resource.
	 * The name of the PropertySource will be generated based on the
	 * {@link Resource#getDescription() description} of the given resource.
	 * <p>
	 * 基于从给定资源加载的属性创建PropertySource将基于给定资源的{@link资源#getDescription()描述}生成PropertySource的名称
	 * 
	 */
	public ResourcePropertySource(EncodedResource resource) throws IOException {
		super(getNameForResource(resource.getResource()), PropertiesLoaderUtils.loadProperties(resource));
		this.resourceName = null;
	}

	/**
	 * Create a PropertySource having the given name based on Properties
	 * loaded from the given encoded resource.
	 * <p>
	 *  根据从给定编码资源加载的属性创建一个具有给定名称的PropertySource
	 * 
	 */
	public ResourcePropertySource(String name, Resource resource) throws IOException {
		super(name, PropertiesLoaderUtils.loadProperties(new EncodedResource(resource)));
		this.resourceName = getNameForResource(resource);
	}

	/**
	 * Create a PropertySource based on Properties loaded from the given resource.
	 * The name of the PropertySource will be generated based on the
	 * {@link Resource#getDescription() description} of the given resource.
	 * <p>
	 *  基于从给定资源加载的属性创建PropertySource将基于给定资源的{@link资源#getDescription()描述}生成PropertySource的名称
	 * 
	 */
	public ResourcePropertySource(Resource resource) throws IOException {
		super(getNameForResource(resource), PropertiesLoaderUtils.loadProperties(new EncodedResource(resource)));
		this.resourceName = null;
	}

	/**
	 * Create a PropertySource having the given name based on Properties loaded from
	 * the given resource location and using the given class loader to load the
	 * resource (assuming it is prefixed with {@code classpath:}).
	 * <p>
	 *  根据从给定资源位置加载的属性创建一个具有给定名称的PropertySource,并使用给定的类加载器加载资源(假设它以{@code classpath：}为前缀)
	 * 
	 */
	public ResourcePropertySource(String name, String location, ClassLoader classLoader) throws IOException {
		this(name, new DefaultResourceLoader(classLoader).getResource(location));
	}

	/**
	 * Create a PropertySource based on Properties loaded from the given resource
	 * location and use the given class loader to load the resource, assuming it is
	 * prefixed with {@code classpath:}. The name of the PropertySource will be
	 * generated based on the {@link Resource#getDescription() description} of the
	 * resource.
	 * <p>
	 * 基于从给定资源位置加载的属性创建一个PropertySource,并使用给定的类加载器加载资源,假设它的前缀为{@code classpath：}。
	 * PropertySource的名称将基于{@link资源# getDescription()描述}的资源。
	 * 
	 */
	public ResourcePropertySource(String location, ClassLoader classLoader) throws IOException {
		this(new DefaultResourceLoader(classLoader).getResource(location));
	}

	/**
	 * Create a PropertySource having the given name based on Properties loaded from
	 * the given resource location. The default thread context class loader will be
	 * used to load the resource (assuming the location string is prefixed with
	 * {@code classpath:}.
	 * <p>
	 *  根据从给定资源位置加载的属性创建一个具有给定名称的PropertySource默认线程上下文类加载器将用于加载资源(假设位置字符串以{@code classpath：}为前缀
	 * 
	 */
	public ResourcePropertySource(String name, String location) throws IOException {
		this(name, new DefaultResourceLoader().getResource(location));
	}

	/**
	 * Create a PropertySource based on Properties loaded from the given resource
	 * location. The name of the PropertySource will be generated based on the
	 * {@link Resource#getDescription() description} of the resource.
	 * <p>
	 *  基于从给定资源位置加载的属性创建PropertySource将根据资源的{@link资源#getDescription()描述}生成PropertySource的名称
	 * 
	 */
	public ResourcePropertySource(String location) throws IOException {
		this(new DefaultResourceLoader().getResource(location));
	}

	private ResourcePropertySource(String name, String resourceName, Map<String, Object> source) {
		super(name, source);
		this.resourceName = resourceName;
	}


	/**
	 * Return a potentially adapted variant of this {@link ResourcePropertySource},
	 * overriding the previously given (or derived) name with the specified name.
	 * <p>
	 * 返回此{@link ResourcePropertySource}的潜在适应变体,用指定的名称覆盖以前给定的(或派生)名称
	 * 
	 * 
	 * @since 4.0.4
	 */
	public ResourcePropertySource withName(String name) {
		if (this.name.equals(name)) {
			return this;
		}
		// Store the original resource name if necessary...
		if (this.resourceName != null) {
			if (this.resourceName.equals(name)) {
				return new ResourcePropertySource(this.resourceName, null, this.source);
			}
			else {
				return new ResourcePropertySource(name, this.resourceName, this.source);
			}
		}
		else {
			// Current name is resource name -> preserve it in the extra field...
			return new ResourcePropertySource(name, this.name, this.source);
		}
	}

	/**
	 * Return a potentially adapted variant of this {@link ResourcePropertySource},
	 * overriding the previously given name (if any) with the original resource name
	 * (equivalent to the name generated by the name-less constructor variants).
	 * <p>
	 *  返回此{@link ResourcePropertySource}的潜在适应变体,覆盖以前给出的名称(如果有)与原始资源名称(相当于无名称构造函数变体生成的名称)
	 * 
	 * 
	 * @since 4.1
	 */
	public ResourcePropertySource withResourceName() {
		if (this.resourceName == null) {
			return this;
		}
		return new ResourcePropertySource(this.resourceName, null, this.source);
	}


	/**
	 * Return the description for the given Resource; if the description is
	 * empty, return the class name of the resource plus its identity hash code.
	 * <p>
	 *  返回给定资源的描述;如果描述为空,则返回资源的类名称及其标识哈希码
	 * 
	 * @see org.springframework.core.io.Resource#getDescription()
	 */
	private static String getNameForResource(Resource resource) {
		String name = resource.getDescription();
		if (!StringUtils.hasText(name)) {
			name = resource.getClass().getSimpleName() + "@" + System.identityHashCode(resource);
		}
		return name;
	}

}
