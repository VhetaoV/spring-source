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

package org.springframework.ui.freemarker;

import java.io.IOException;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;

/**
 * Factory bean that creates a FreeMarker Configuration and provides it as
 * bean reference. This bean is intended for any kind of usage of FreeMarker
 * in application code, e.g. for generating email content. For web views,
 * FreeMarkerConfigurer is used to set up a FreeMarkerConfigurationFactory.
 *
 * The simplest way to use this class is to specify just a "templateLoaderPath";
 * you do not need any further configuration then. For example, in a web
 * application context:
 *
 * <pre class="code"> &lt;bean id="freemarkerConfiguration" class="org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean"&gt;
 *   &lt;property name="templateLoaderPath" value="/WEB-INF/freemarker/"/&gt;
 * &lt;/bean&gt;</pre>

 * See the base class FreeMarkerConfigurationFactory for configuration details.
 *
 * <p>Note: Spring's FreeMarker support requires FreeMarker 2.3 or higher.
 *
 * <p>
 * 创建FreeMarker配置并将其提供为bean参考的工厂bean此bean适用于应用程序代码中的FreeMarker的任何类型的使用,例如用于生成电子邮件内容对于Web视图,FreeMarkerCon
 * figurer用于设置FreeMarkerConfigurationFactory。
 * 
 *  使用这个类的最简单的方法是只指定一个"templateLoaderPath";您不需要任何进一步的配置,例如,在Web应用程序上下文中：
 * 
 *  <pre class ="code">&lt; bean id ="freemarkerConfiguration"class ="orgspringframeworkuifreemarkerFree
 * MarkerConfigurationFactoryBean"&gt; &lt; property name ="templateLoaderPath"value ="/ WEB-INF / freem
 * 
 * @author Darren Davison
 * @since 03.03.2004
 * @see #setConfigLocation
 * @see #setFreemarkerSettings
 * @see #setTemplateLoaderPath
 * @see org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer
 */
public class FreeMarkerConfigurationFactoryBean extends FreeMarkerConfigurationFactory
		implements FactoryBean<Configuration>, InitializingBean, ResourceLoaderAware {

	private Configuration configuration;


	@Override
	public void afterPropertiesSet() throws IOException, TemplateException {
		this.configuration = createConfiguration();
	}


	@Override
	public Configuration getObject() {
		return this.configuration;
	}

	@Override
	public Class<? extends Configuration> getObjectType() {
		return Configuration.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
