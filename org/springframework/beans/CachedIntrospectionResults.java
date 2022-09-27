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

package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.SpringProperties;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

/**
 * Internal class that caches JavaBeans {@link java.beans.PropertyDescriptor}
 * information for a Java class. Not intended for direct use by application code.
 *
 * <p>Necessary for own caching of descriptors within the application's
 * ClassLoader, rather than rely on the JDK's system-wide BeanInfo cache
 * (in order to avoid leaks on ClassLoader shutdown).
 *
 * <p>Information is cached statically, so we don't need to create new
 * objects of this class for every JavaBean we manipulate. Hence, this class
 * implements the factory design pattern, using a private constructor and
 * a static {@link #forClass(Class)} factory method to obtain instances.
 *
 * <p>Note that for caching to work effectively, some preconditions need to be met:
 * Prefer an arrangement where the Spring jars live in the same ClassLoader as the
 * application classes, which allows for clean caching along with the application's
 * lifecycle in any case. For a web application, consider declaring a local
 * {@link org.springframework.web.util.IntrospectorCleanupListener} in {@code web.xml}
 * in case of a multi-ClassLoader layout, which will allow for effective caching as well.
 *
 * <p>In case of a non-clean ClassLoader arrangement without a cleanup listener having
 * been set up, this class will fall back to a weak-reference-based caching model that
 * recreates much-requested entries every time the garbage collector removed them. In
 * such a scenario, consider the {@link #IGNORE_BEANINFO_PROPERTY_NAME} system property.
 *
 * <p>
 *  缓存JavaBeans的内部类{@link javabeansPropertyDescriptor} Java类的信息不适用于应用程序代码的直接使用
 * 
 * 自己对应用程序ClassLoader中描述符的缓存是必需的,而不是依赖于JDK的全系统BeanInfo缓存(为了避免在ClassLoader关闭时出现泄漏)
 * 
 *  信息被静态缓存,所以我们不需要为每个我们操作的JavaBean创建这个类的新对象。
 * 因此,这个类实现了工厂设计模式,使用一个私有构造函数和一个静态{@link #forClass(Class )}工厂方法来获取实例。
 * 
 * 请注意,要使缓存有效工作,需要满足一些前提条件：首选安排Spring jar与应用程序类在同一个ClassLoader中,这样可以在任何情况下实现与应用程序生命周期的清理缓存。
 *  Web应用程序,考虑在{Classcode_Webxml}中声明一个本地{@link orgspringframeworkwebutilIntrospectorCleanupListener},以防多
 * 个ClassLoader布局,这样也可以有效的缓存。
 * 请注意,要使缓存有效工作,需要满足一些前提条件：首选安排Spring jar与应用程序类在同一个ClassLoader中,这样可以在任何情况下实现与应用程序生命周期的清理缓存。
 * 
 * <p>如果没有设置清理侦听器的不干净的ClassLoader安排,则此类将返回到基于弱引用的缓存模型,每次垃圾回收器将其删除时,将重新创建大量请求的条目。
 * 考虑{@link #IGNORE_BEANINFO_PROPERTY_NAME}系统属性的情况。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 05 May 2001
 * @see #acceptClassLoader(ClassLoader)
 * @see #clearClassLoader(ClassLoader)
 * @see #forClass(Class)
 */
public class CachedIntrospectionResults {

	/**
	 * System property that instructs Spring to use the {@link Introspector#IGNORE_ALL_BEANINFO}
	 * mode when calling the JavaBeans {@link Introspector}: "spring.beaninfo.ignore", with a
	 * value of "true" skipping the search for {@code BeanInfo} classes (typically for scenarios
	 * where no such classes are being defined for beans in the application in the first place).
	 * <p>The default is "false", considering all {@code BeanInfo} metadata classes, like for
	 * standard {@link Introspector#getBeanInfo(Class)} calls. Consider switching this flag to
	 * "true" if you experience repeated ClassLoader access for non-existing {@code BeanInfo}
	 * classes, in case such access is expensive on startup or on lazy loading.
	 * <p>Note that such an effect may also indicate a scenario where caching doesn't work
	 * effectively: Prefer an arrangement where the Spring jars live in the same ClassLoader
	 * as the application classes, which allows for clean caching along with the application's
	 * lifecycle in any case. For a web application, consider declaring a local
	 * {@link org.springframework.web.util.IntrospectorCleanupListener} in {@code web.xml}
	 * in case of a multi-ClassLoader layout, which will allow for effective caching as well.
	 * <p>
	 * 系统属性指示Spring在调用JavaBeans {@link Introspector}时使用{@link Introspector#IGNORE_ALL_BEANINFO}模式："springbea
	 * ninfoignore",值为"true"跳过搜索{@code BeanInfo}类(通常用于考虑到所有{@code BeanInfo}元数据类,例如标准的{@link Introspector#getBeanInfo(Class)),默认值为"false",默认情况下, }
	 * 调用如果您对于不存在的{@code BeanInfo}类遇到重复的ClassLoader访问,则考虑将此标志切换为"true",以防在启动时或延迟加载时访问成本高昂<p>请注意,这种效果也可能表示缓存无
	 * 效的场景：喜欢一个安排,其中Spring jar与实际应用程序类生活在同一个ClassLoader中,这样可以在应用程序的生命周期内实现干净的缓存case对于Web应用程序,请考虑在{@code webxml}
	 * 中声明本地{@link orgspringframeworkwebutilIntrospectorCleanupListener},以防多个ClassLoader布局,这将允许有效的缓存。
	 * 
	 * 
	 * @see Introspector#getBeanInfo(Class, int)
	 */
	public static final String IGNORE_BEANINFO_PROPERTY_NAME = "spring.beaninfo.ignore";


	private static final boolean shouldIntrospectorIgnoreBeaninfoClasses =
			SpringProperties.getFlag(IGNORE_BEANINFO_PROPERTY_NAME);

	/** Stores the BeanInfoFactory instances */
	private static List<BeanInfoFactory> beanInfoFactories = SpringFactoriesLoader.loadFactories(
			BeanInfoFactory.class, CachedIntrospectionResults.class.getClassLoader());

	private static final Log logger = LogFactory.getLog(CachedIntrospectionResults.class);

	/**
	 * Set of ClassLoaders that this CachedIntrospectionResults class will always
	 * accept classes from, even if the classes do not qualify as cache-safe.
	 * <p>
	 * 一组ClassLoaders,这个CachedIntrospectionResults类将始终接受类,即使这些类不符合缓存安全性
	 * 
	 */
	static final Set<ClassLoader> acceptedClassLoaders =
			Collections.newSetFromMap(new ConcurrentHashMap<ClassLoader, Boolean>(16));

	/**
	 * Map keyed by Class containing CachedIntrospectionResults, strongly held.
	 * This variant is being used for cache-safe bean classes.
	 * <p>
	 *  由包含CachedIntrospectionResults的类键入的映射强烈地保持此变体用于缓存安全的bean类
	 * 
	 */
	static final ConcurrentMap<Class<?>, CachedIntrospectionResults> strongClassCache =
			new ConcurrentHashMap<Class<?>, CachedIntrospectionResults>(64);

	/**
	 * Map keyed by Class containing CachedIntrospectionResults, softly held.
	 * This variant is being used for non-cache-safe bean classes.
	 * <p>
	 *  由包含CachedIntrospectionResults的类键入的映射,轻轻地保持此变体用于非缓存安全的bean类
	 * 
	 */
	static final ConcurrentMap<Class<?>, CachedIntrospectionResults> softClassCache =
			new ConcurrentReferenceHashMap<Class<?>, CachedIntrospectionResults>(64);


	/**
	 * Accept the given ClassLoader as cache-safe, even if its classes would
	 * not qualify as cache-safe in this CachedIntrospectionResults class.
	 * <p>This configuration method is only relevant in scenarios where the Spring
	 * classes reside in a 'common' ClassLoader (e.g. the system ClassLoader)
	 * whose lifecycle is not coupled to the application. In such a scenario,
	 * CachedIntrospectionResults would by default not cache any of the application's
	 * classes, since they would create a leak in the common ClassLoader.
	 * <p>Any {@code acceptClassLoader} call at application startup should
	 * be paired with a {@link #clearClassLoader} call at application shutdown.
	 * <p>
	 * 接受给定的ClassLoader作为缓存安全性,即使它的类不能在此CachedIntrospectionResults类中被限定为缓存安全性<p>此配置方法仅在Spring类驻留在"常用"ClassLo
	 * ader中的情况下(例如系统ClassLoader)其生命周期未耦合到应用程序在这种情况下,CachedIntrospectionResults默认情况下不缓存任何应用程序的类,因为它们将在应用程序启动
	 * 时在常见的ClassLoader <p>任何{@code acceptClassLoader}调用中创建泄漏应该在应用程序关闭时与{@link #clearClassLoader}调用配对。
	 * 
	 * 
	 * @param classLoader the ClassLoader to accept
	 */
	public static void acceptClassLoader(ClassLoader classLoader) {
		if (classLoader != null) {
			acceptedClassLoaders.add(classLoader);
		}
	}

	/**
	 * Clear the introspection cache for the given ClassLoader, removing the
	 * introspection results for all classes underneath that ClassLoader, and
	 * removing the ClassLoader (and its children) from the acceptance list.
	 * <p>
	 * 清除给定ClassLoader的内省缓存,删除该ClassLoader下的所有类的内省结果,并从接受列表中删除ClassLoader(及其子代)
	 * 
	 * 
	 * @param classLoader the ClassLoader to clear the cache for
	 */
	public static void clearClassLoader(ClassLoader classLoader) {
		for (Iterator<ClassLoader> it = acceptedClassLoaders.iterator(); it.hasNext();) {
			ClassLoader registeredLoader = it.next();
			if (isUnderneathClassLoader(registeredLoader, classLoader)) {
				it.remove();
			}
		}
		for (Iterator<Class<?>> it = strongClassCache.keySet().iterator(); it.hasNext();) {
			Class<?> beanClass = it.next();
			if (isUnderneathClassLoader(beanClass.getClassLoader(), classLoader)) {
				it.remove();
			}
		}
		for (Iterator<Class<?>> it = softClassCache.keySet().iterator(); it.hasNext();) {
			Class<?> beanClass = it.next();
			if (isUnderneathClassLoader(beanClass.getClassLoader(), classLoader)) {
				it.remove();
			}
		}
	}

	/**
	 * Create CachedIntrospectionResults for the given bean class.
	 * <p>
	 *  为给定的bean类创建CachedIntrospectionResults
	 * 
	 * 
	 * @param beanClass the bean class to analyze
	 * @return the corresponding CachedIntrospectionResults
	 * @throws BeansException in case of introspection failure
	 */
	@SuppressWarnings("unchecked")
	static CachedIntrospectionResults forClass(Class<?> beanClass) throws BeansException {
		CachedIntrospectionResults results = strongClassCache.get(beanClass);
		if (results != null) {
			return results;
		}
		results = softClassCache.get(beanClass);
		if (results != null) {
			return results;
		}

		results = new CachedIntrospectionResults(beanClass);
		ConcurrentMap<Class<?>, CachedIntrospectionResults> classCacheToUse;

		if (ClassUtils.isCacheSafe(beanClass, CachedIntrospectionResults.class.getClassLoader()) ||
				isClassLoaderAccepted(beanClass.getClassLoader())) {
			classCacheToUse = strongClassCache;
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("Not strongly caching class [" + beanClass.getName() + "] because it is not cache-safe");
			}
			classCacheToUse = softClassCache;
		}

		CachedIntrospectionResults existing = classCacheToUse.putIfAbsent(beanClass, results);
		return (existing != null ? existing : results);
	}

	/**
	 * Check whether this CachedIntrospectionResults class is configured
	 * to accept the given ClassLoader.
	 * <p>
	 *  检查此CachedIntrospectionResults类是否配置为接受给定的ClassLoader
	 * 
	 * 
	 * @param classLoader the ClassLoader to check
	 * @return whether the given ClassLoader is accepted
	 * @see #acceptClassLoader
	 */
	private static boolean isClassLoaderAccepted(ClassLoader classLoader) {
		for (ClassLoader acceptedLoader : acceptedClassLoaders) {
			if (isUnderneathClassLoader(classLoader, acceptedLoader)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given ClassLoader is underneath the given parent,
	 * that is, whether the parent is within the candidate's hierarchy.
	 * <p>
	 *  检查给定的ClassLoader是否在给定的父项下面,也就是说父进程是否在候选的层次结构中
	 * 
	 * 
	 * @param candidate the candidate ClassLoader to check
	 * @param parent the parent ClassLoader to check for
	 */
	private static boolean isUnderneathClassLoader(ClassLoader candidate, ClassLoader parent) {
		if (candidate == parent) {
			return true;
		}
		if (candidate == null) {
			return false;
		}
		ClassLoader classLoaderToCheck = candidate;
		while (classLoaderToCheck != null) {
			classLoaderToCheck = classLoaderToCheck.getParent();
			if (classLoaderToCheck == parent) {
				return true;
			}
		}
		return false;
	}


	/** The BeanInfo object for the introspected bean class */
	private final BeanInfo beanInfo;

	/** PropertyDescriptor objects keyed by property name String */
	private final Map<String, PropertyDescriptor> propertyDescriptorCache;

	/** TypeDescriptor objects keyed by PropertyDescriptor */
	private final ConcurrentMap<PropertyDescriptor, TypeDescriptor> typeDescriptorCache;


	/**
	 * Create a new CachedIntrospectionResults instance for the given class.
	 * <p>
	 *  为给定的类创建一个新的CachedIntrospectionResults实例
	 * 
	 * @param beanClass the bean class to analyze
	 * @throws BeansException in case of introspection failure
	 */
	private CachedIntrospectionResults(Class<?> beanClass) throws BeansException {
		try {
			if (logger.isTraceEnabled()) {
				logger.trace("Getting BeanInfo for class [" + beanClass.getName() + "]");
			}

			BeanInfo beanInfo = null;
			for (BeanInfoFactory beanInfoFactory : beanInfoFactories) {
				beanInfo = beanInfoFactory.getBeanInfo(beanClass);
				if (beanInfo != null) {
					break;
				}
			}
			if (beanInfo == null) {
				// If none of the factories supported the class, fall back to the default
				beanInfo = (shouldIntrospectorIgnoreBeaninfoClasses ?
						Introspector.getBeanInfo(beanClass, Introspector.IGNORE_ALL_BEANINFO) :
						Introspector.getBeanInfo(beanClass));
			}
			this.beanInfo = beanInfo;

			if (logger.isTraceEnabled()) {
				logger.trace("Caching PropertyDescriptors for class [" + beanClass.getName() + "]");
			}
			this.propertyDescriptorCache = new LinkedHashMap<String, PropertyDescriptor>();

			// This call is slow so we do it once.
			PropertyDescriptor[] pds = this.beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				if (Class.class == beanClass &&
						("classLoader".equals(pd.getName()) ||  "protectionDomain".equals(pd.getName()))) {
					// Ignore Class.getClassLoader() and getProtectionDomain() methods - nobody needs to bind to those
					continue;
				}
				if (logger.isTraceEnabled()) {
					logger.trace("Found bean property '" + pd.getName() + "'" +
							(pd.getPropertyType() != null ? " of type [" + pd.getPropertyType().getName() + "]" : "") +
							(pd.getPropertyEditorClass() != null ?
									"; editor [" + pd.getPropertyEditorClass().getName() + "]" : ""));
				}
				pd = buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
				this.propertyDescriptorCache.put(pd.getName(), pd);
			}

			// Explicitly check implemented interfaces for setter/getter methods as well,
			// in particular for Java 8 default methods...
			Class<?> clazz = beanClass;
			while (clazz != null) {
				Class<?>[] ifcs = clazz.getInterfaces();
				for (Class<?> ifc : ifcs) {
					BeanInfo ifcInfo = Introspector.getBeanInfo(ifc, Introspector.IGNORE_ALL_BEANINFO);
					PropertyDescriptor[] ifcPds = ifcInfo.getPropertyDescriptors();
					for (PropertyDescriptor pd : ifcPds) {
						if (!this.propertyDescriptorCache.containsKey(pd.getName())) {
							pd = buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
							this.propertyDescriptorCache.put(pd.getName(), pd);
						}
					}
				}
				clazz = clazz.getSuperclass();
			}

			this.typeDescriptorCache = new ConcurrentReferenceHashMap<PropertyDescriptor, TypeDescriptor>();
		}
		catch (IntrospectionException ex) {
			throw new FatalBeanException("Failed to obtain BeanInfo for class [" + beanClass.getName() + "]", ex);
		}
	}

	BeanInfo getBeanInfo() {
		return this.beanInfo;
	}

	Class<?> getBeanClass() {
		return this.beanInfo.getBeanDescriptor().getBeanClass();
	}

	PropertyDescriptor getPropertyDescriptor(String name) {
		PropertyDescriptor pd = this.propertyDescriptorCache.get(name);
		if (pd == null && StringUtils.hasLength(name)) {
			// Same lenient fallback checking as in PropertyTypeDescriptor...
			pd = this.propertyDescriptorCache.get(name.substring(0, 1).toLowerCase() + name.substring(1));
			if (pd == null) {
				pd = this.propertyDescriptorCache.get(name.substring(0, 1).toUpperCase() + name.substring(1));
			}
		}
		return (pd == null || pd instanceof GenericTypeAwarePropertyDescriptor ? pd :
				buildGenericTypeAwarePropertyDescriptor(getBeanClass(), pd));
	}

	PropertyDescriptor[] getPropertyDescriptors() {
		PropertyDescriptor[] pds = new PropertyDescriptor[this.propertyDescriptorCache.size()];
		int i = 0;
		for (PropertyDescriptor pd : this.propertyDescriptorCache.values()) {
			pds[i] = (pd instanceof GenericTypeAwarePropertyDescriptor ? pd :
					buildGenericTypeAwarePropertyDescriptor(getBeanClass(), pd));
			i++;
		}
		return pds;
	}

	private PropertyDescriptor buildGenericTypeAwarePropertyDescriptor(Class<?> beanClass, PropertyDescriptor pd) {
		try {
			return new GenericTypeAwarePropertyDescriptor(beanClass, pd.getName(), pd.getReadMethod(),
					pd.getWriteMethod(), pd.getPropertyEditorClass());
		}
		catch (IntrospectionException ex) {
			throw new FatalBeanException("Failed to re-introspect class [" + beanClass.getName() + "]", ex);
		}
	}

	TypeDescriptor addTypeDescriptor(PropertyDescriptor pd, TypeDescriptor td) {
		TypeDescriptor existing = this.typeDescriptorCache.putIfAbsent(pd, td);
		return (existing != null ? existing : td);
	}

	TypeDescriptor getTypeDescriptor(PropertyDescriptor pd) {
		return this.typeDescriptorCache.get(pd);
	}

}
