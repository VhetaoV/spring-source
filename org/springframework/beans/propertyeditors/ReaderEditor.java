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

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.util.Assert;

/**
 * One-way PropertyEditor which can convert from a text String to a
 * {@code java.io.Reader}, interpreting the given String as a Spring
 * resource location (e.g. a URL String).
 *
 * <p>Supports Spring-style URL notation: any fully qualified standard URL
 * ("file:", "http:", etc.) and Spring's special "classpath:" pseudo-URL.
 *
 * <p>Note that such readers usually do not get closed by Spring itself!
 *
 * <p>
 *  单向PropertyEditor可以将文本字符串转换为{@code javaioReader},将给定的字符串解释为Spring资源位置(例如URL字符串)
 * 
 * <p>支持Spring样式的URL表示法：任何完全合格的标准URL("file：","http："等)和Spring的特殊"classpath："伪URL
 * 
 *  注意,这样的读者通常不会被Spring本身关闭！
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.2
 * @see java.io.Reader
 * @see org.springframework.core.io.ResourceEditor
 * @see org.springframework.core.io.ResourceLoader
 * @see InputStreamEditor
 */
public class ReaderEditor extends PropertyEditorSupport {

	private final ResourceEditor resourceEditor;


	/**
	 * Create a new ReaderEditor, using the default ResourceEditor underneath.
	 * <p>
	 *  创建一个新的ReaderEditor,使用下面的默认ResourceEditor
	 * 
	 */
	public ReaderEditor() {
		this.resourceEditor = new ResourceEditor();
	}

	/**
	 * Create a new ReaderEditor, using the given ResourceEditor underneath.
	 * <p>
	 *  创建一个新的ReaderEditor,使用给定的ResourceEditor下面
	 * 
	 * 
	 * @param resourceEditor the ResourceEditor to use
	 */
	public ReaderEditor(ResourceEditor resourceEditor) {
		Assert.notNull(resourceEditor, "ResourceEditor must not be null");
		this.resourceEditor = resourceEditor;
	}


	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		this.resourceEditor.setAsText(text);
		Resource resource = (Resource) this.resourceEditor.getValue();
		try {
			setValue(resource != null ? new EncodedResource(resource).getReader() : null);
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Failed to retrieve Reader for " + resource, ex);
		}
	}

	/**
	 * This implementation returns {@code null} to indicate that
	 * there is no appropriate text representation.
	 * <p>
	 *  此实现返回{@code null}以指示没有适当的文本表示
	 */
	@Override
	public String getAsText() {
		return null;
	}

}
