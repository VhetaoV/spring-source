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

package org.springframework.web.client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.transform.Source;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.InterceptingHttpAccessor;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.util.AbstractUriTemplateHandler;
import org.springframework.web.util.DefaultUriTemplateHandler;
import org.springframework.web.util.UriTemplateHandler;

/**
 * <strong>Spring's central class for synchronous client-side HTTP access.</strong>
 * It simplifies communication with HTTP servers, and enforces RESTful principles.
 * It handles HTTP connections, leaving application code to provide URLs
 * (with possible template variables) and extract results.
 *
 * <p><strong>Note:</strong> by default the RestTemplate relies on standard JDK
 * facilities to establish HTTP connections. You can switch to use a different
 * HTTP library such as Apache HttpComponents, Netty, and OkHttp through the
 * {@link #setRequestFactory} property.
 *
 * <p>The main entry points of this template are the methods named after the six main HTTP methods:
 * <table>
 * <tr><th>HTTP method</th><th>RestTemplate methods</th></tr>
 * <tr><td>DELETE</td><td>{@link #delete}</td></tr>
 * <tr><td>GET</td><td>{@link #getForObject}</td></tr>
 * <tr><td></td><td>{@link #getForEntity}</td></tr>
 * <tr><td>HEAD</td><td>{@link #headForHeaders}</td></tr>
 * <tr><td>OPTIONS</td><td>{@link #optionsForAllow}</td></tr>
 * <tr><td>POST</td><td>{@link #postForLocation}</td></tr>
 * <tr><td></td><td>{@link #postForObject}</td></tr>
 * <tr><td>PUT</td><td>{@link #put}</td></tr>
 * <tr><td>any</td><td>{@link #exchange}</td></tr>
 * <tr><td></td><td>{@link #execute}</td></tr> </table>
 *
 * <p>In addition the {@code exchange} and {@code execute} methods are generalized versions of
 * the above methods and can be used to support additional, less frequent combinations (e.g.
 * HTTP PATCH, HTTP PUT with response body, etc.). Note however that the underlying HTTP
 * library used must also support the desired combination.
 *
 * <p>For each HTTP method there are three variants: two accept a URI template string
 * and URI variables (array or map) while a third accepts a {@link URI}.
 * Note that for URI templates it is assumed encoding is necessary, e.g.
 * {@code restTemplate.getForObject("http://example.com/hotel list")} becomes
 * {@code "http://example.com/hotel%20list"}. This also means if the URI template
 * or URI variables are already encoded, double encoding will occur, e.g.
 * {@code http://example.com/hotel%20list} becomes
 * {@code http://example.com/hotel%2520list}). To avoid that use a {@code URI} method
 * variant to provide (or re-use) a previously encoded URI. To prepare such an URI
 * with full control over encoding, consider using
 * {@link org.springframework.web.util.UriComponentsBuilder}.
 *
 * <p>Internally the template uses {@link HttpMessageConverter} instances to
 * convert HTTP messages to and from POJOs. Converters for the main mime types
 * are registered by default but you can also register additional converters
 * via {@link #setMessageConverters}.
 *
 * <p>This template uses a
 * {@link org.springframework.http.client.SimpleClientHttpRequestFactory} and a
 * {@link DefaultResponseErrorHandler} as default strategies for creating HTTP
 * connections or handling HTTP errors, respectively. These defaults can be overridden
 * through {@link #setRequestFactory} and {@link #setErrorHandler} respectively.
 *
 * <p>
 * <strong> Spring的同步客户端HTTP访问的中心类</strong>它简化了与HTTP服务器的通信,并强制执行RESTful原则它处理HTTP连接,留下应用程序代码来提供URL(可能的模板变
 * 量)并提取结果。
 * 
 *  <p> <strong>注意：</strong>默认情况下,RestTemplate依赖于标准JDK功能来建立HTTP连接您可以通过{@link #setRequestFactory)切换到使用不同的HTTP库,如Apache HttpComponents,Netty和OkHttp }
 * 属性。
 * 
 *  <p>此模板的主要入口点是以六种主要HTTP方法命名的方法：
 * <table>
 * <tr> <th> HTTP方法</th> <th> RestTemplate方法</th> </tr> <tr> <td> DELETE </td> <td> {@ link #delete} </td>
 *  / tr> <tr> <td> GET </td> <td> {@ link #getForObject} </td> </tr> <tr> <td> </td> <td> {@ link #getForEntity}
 *  </td> </tr> <tr> <td> HEAD </td> <td> {@ link #headForHeaders} </td> </tr> <tr> <td>选项</td> {@link #optionsForAllow}
 *  </td> </tr> <tr> <td> POST </td> <td> {@ link #postForLocation} </td> </tr> <tr> <td> td> <td> {@ link #postForObject}
 *  </td> </tr> <tr> <td> PUT </td> <td> {@ link #put} </td> </tr> <tr> <td>任何</td> <td> {@ link #exchange}
 *  </td> </tr> <tr> <td> </td> <td> {@ link #execute} </td> tr> </table>。
 * 
 * 此外,{@code exchange}和{@code execute}方法是上述方法的通用版本,可用于支持额外的较不频繁的组合(例如HTTP PATCH,具有响应主体的HTTP PUT等)注意然而,使用
 * 的底层HTTP库也必须支持所需的组合。
 * 
 * <p>对于每个HTTP方法,有三个变体：两个接受URI模板字符串和URI变量(数组或映射),而第三个接受{@link URI}注意,对于URI模板,假设编码是必需的,例如{ @code restTemplategetForObject("http：// examplecom / hotel list")}
 * 成为{@code"http：// examplecom / hotel％20list"}这也意味着如果URI模板或URI变量已经被编码,则会发生双重编码,例如{@code http：// examplecom / hotel％20list}
 * 成为{@code http：// examplecom / hotel％2520list})为避免使用{@code URI}方法变体提供(或重复使用)一个以前编码的URI为了准备这样一个URI,完全控制
 * 编码,考虑使用{@link orgspringframeworkwebutilUriComponentsBuilder}。
 * 
 * <p>内部模板使用{@link HttpMessageConverter}实例将HTTP消息转换为POJO和/或从POJO转换主要mime类型的转换器默认注册,但您也可以通过{@link #setMessageConverters}
 * 注册其他转换器。
 * 
 *  <p>此模板使用{@link orgspringframeworkhttpclientSimpleClientHttpRequestFactory}和{@link DefaultResponseErrorHandler}
 * 作为创建HTTP连接或处理HTTP错误的默认策略。
 * 这些默认值可以通过{@link #setRequestFactory}和{@link #setErrorHandler } 分别。
 * 
 * 
 * @author Arjen Poutsma
 * @author Brian Clozel
 * @author Roy Clarkson
 * @author Juergen Hoeller
 * @since 3.0
 * @see HttpMessageConverter
 * @see RequestCallback
 * @see ResponseExtractor
 * @see ResponseErrorHandler
 * @see AsyncRestTemplate
 */
public class RestTemplate extends InterceptingHttpAccessor implements RestOperations {

	private static boolean romePresent =
			ClassUtils.isPresent("com.rometools.rome.feed.WireFeed", RestTemplate.class.getClassLoader());

	private static final boolean jaxb2Present =
			ClassUtils.isPresent("javax.xml.bind.Binder", RestTemplate.class.getClassLoader());

	private static final boolean jackson2Present =
			ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", RestTemplate.class.getClassLoader()) &&
					ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", RestTemplate.class.getClassLoader());

	private static final boolean jackson2XmlPresent =
			ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper", RestTemplate.class.getClassLoader());

	private static final boolean gsonPresent =
			ClassUtils.isPresent("com.google.gson.Gson", RestTemplate.class.getClassLoader());


	private final List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();

	private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

	private UriTemplateHandler uriTemplateHandler = new DefaultUriTemplateHandler();

	private final ResponseExtractor<HttpHeaders> headersExtractor = new HeadersExtractor();


	/**
	 * Create a new instance of the {@link RestTemplate} using default settings.
	 * Default {@link HttpMessageConverter}s are initialized.
	 * <p>
	 *  使用默认设置创建{@link RestTemplate}的新实例默认{@link HttpMessageConverter}已初始化
	 * 
	 */
	public RestTemplate() {
		this.messageConverters.add(new ByteArrayHttpMessageConverter());
		this.messageConverters.add(new StringHttpMessageConverter());
		this.messageConverters.add(new ResourceHttpMessageConverter());
		this.messageConverters.add(new SourceHttpMessageConverter<Source>());
		this.messageConverters.add(new AllEncompassingFormHttpMessageConverter());

		if (romePresent) {
			this.messageConverters.add(new AtomFeedHttpMessageConverter());
			this.messageConverters.add(new RssChannelHttpMessageConverter());
		}

		if (jackson2XmlPresent) {
			this.messageConverters.add(new MappingJackson2XmlHttpMessageConverter());
		}
		else if (jaxb2Present) {
			this.messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
		}

		if (jackson2Present) {
			this.messageConverters.add(new MappingJackson2HttpMessageConverter());
		}
		else if (gsonPresent) {
			this.messageConverters.add(new GsonHttpMessageConverter());
		}
	}

	/**
	 * Create a new instance of the {@link RestTemplate} based on the given {@link ClientHttpRequestFactory}.
	 * <p>
	 * 根据给定的{@link ClientHttpRequestFactory}创建{@link RestTemplate}的新实例
	 * 
	 * 
	 * @param requestFactory HTTP request factory to use
	 * @see org.springframework.http.client.SimpleClientHttpRequestFactory
	 * @see org.springframework.http.client.HttpComponentsClientHttpRequestFactory
	 */
	public RestTemplate(ClientHttpRequestFactory requestFactory) {
		this();
		setRequestFactory(requestFactory);
	}

	/**
	 * Create a new instance of the {@link RestTemplate} using the given list of
	 * {@link HttpMessageConverter} to use
	 * <p>
	 *  使用{@link HttpMessageConverter}的给定列表创建{@link RestTemplate}的新实例,以使用
	 * 
	 * 
	 * @param messageConverters the list of {@link HttpMessageConverter} to use
	 * @since 3.2.7
	 */
	public RestTemplate(List<HttpMessageConverter<?>> messageConverters) {
		Assert.notEmpty(messageConverters, "At least one HttpMessageConverter required");
		this.messageConverters.addAll(messageConverters);
	}


	/**
	 * Set the message body converters to use.
	 * <p>These converters are used to convert from and to HTTP requests and responses.
	 * <p>
	 *  将消息体转换器设置为使用<p>这些转换器用于从HTTP请求和响应转换
	 * 
	 */
	public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		Assert.notEmpty(messageConverters, "At least one HttpMessageConverter required");
		// Take getMessageConverters() List as-is when passed in here
		if (this.messageConverters != messageConverters) {
			this.messageConverters.clear();
			this.messageConverters.addAll(messageConverters);
		}
	}

	/**
	 * Return the message body converters.
	 * <p>
	 *  返回消息体转换器
	 * 
	 */
	public List<HttpMessageConverter<?>> getMessageConverters() {
		return this.messageConverters;
	}

	/**
	 * Set the error handler.
	 * <p>By default, RestTemplate uses a {@link DefaultResponseErrorHandler}.
	 * <p>
	 *  设置错误处理程序<p>默认情况下,RestTemplate使用{@link DefaultResponseErrorHandler}
	 * 
	 */
	public void setErrorHandler(ResponseErrorHandler errorHandler) {
		Assert.notNull(errorHandler, "ResponseErrorHandler must not be null");
		this.errorHandler = errorHandler;
	}

	/**
	 * Return the error handler.
	 * <p>
	 *  返回错误处理程序
	 * 
	 */
	public ResponseErrorHandler getErrorHandler() {
		return this.errorHandler;
	}

	/**
	 * Configure default URI variable values. This is a shortcut for:
	 * <pre class="code">
	 * DefaultUriTemplateHandler handler = new DefaultUriTemplateHandler();
	 * handler.setDefaultUriVariables(...);
	 *
	 * RestTemplate restTemplate = new RestTemplate();
	 * restTemplate.setUriTemplateHandler(handler);
	 * </pre>
	 * <p>
	 *  配置默认URI变量值这是一个快捷方式：
	 * <pre class="code">
	 *  DefaultUriTemplateHandler handler = new DefaultUriTemplateHandler(); handlersetDefaultUriVariables()
	 * ;。
	 * 
	 *  RestTemplate restTemplate = new RestTemplate(); restTemplatesetUriTemplateHandler(处理程序);
	 * </pre>
	 * 
	 * @param defaultUriVariables the default URI variable values
	 * @since 4.3
	 */
	public void setDefaultUriVariables(Map<String, ?> defaultUriVariables) {
		Assert.isInstanceOf(AbstractUriTemplateHandler.class, this.uriTemplateHandler,
				"Can only use this property in conjunction with an AbstractUriTemplateHandler");
		((AbstractUriTemplateHandler) this.uriTemplateHandler).setDefaultUriVariables(defaultUriVariables);
	}

	/**
	 * Configure the {@link UriTemplateHandler} to use to expand URI templates.
	 * By default the {@link DefaultUriTemplateHandler} is used which relies on
	 * Spring's URI template support and exposes several useful properties that
	 * customize its behavior for encoding and for prepending a common base URL.
	 * An alternative implementation may be used to plug an external URI
	 * template library.
	 * <p>
	 * 配置用于扩展URI模板的{@link UriTemplateHandler}默认情况下,使用依赖于Spring的URI模板支持的{@link DefaultUriTemplateHandler},并公开
	 * 了一些有用的属性,可以自定义其编码行为和前缀的基本URL替代实现可以用于插入外部URI模板库。
	 * 
	 * 
	 * @param handler the URI template handler to use
	 */
	public void setUriTemplateHandler(UriTemplateHandler handler) {
		Assert.notNull(handler, "UriTemplateHandler must not be null");
		this.uriTemplateHandler = handler;
	}

	/**
	 * Return the configured URI template handler.
	 * <p>
	 *  返回配置的URI模板处理程序
	 * 
	 */
	public UriTemplateHandler getUriTemplateHandler() {
		return this.uriTemplateHandler;
	}


	// GET

	@Override
	public <T> T getForObject(String url, Class<T> responseType, Object... urlVariables) throws RestClientException {
		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		HttpMessageConverterExtractor<T> responseExtractor =
				new HttpMessageConverterExtractor<T>(responseType, getMessageConverters(), logger);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, urlVariables);
	}

	@Override
	public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> urlVariables) throws RestClientException {
		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		HttpMessageConverterExtractor<T> responseExtractor =
				new HttpMessageConverterExtractor<T>(responseType, getMessageConverters(), logger);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, urlVariables);
	}

	@Override
	public <T> T getForObject(URI url, Class<T> responseType) throws RestClientException {
		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		HttpMessageConverterExtractor<T> responseExtractor =
				new HttpMessageConverterExtractor<T>(responseType, getMessageConverters(), logger);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor);
	}

	@Override
	public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... urlVariables)
			throws RestClientException {

		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, urlVariables);
	}

	@Override
	public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> urlVariables)
			throws RestClientException {

		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, urlVariables);
	}

	@Override
	public <T> ResponseEntity<T> getForEntity(URI url, Class<T> responseType) throws RestClientException {
		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor);
	}


	// HEAD

	@Override
	public HttpHeaders headForHeaders(String url, Object... urlVariables) throws RestClientException {
		return execute(url, HttpMethod.HEAD, null, headersExtractor(), urlVariables);
	}

	@Override
	public HttpHeaders headForHeaders(String url, Map<String, ?> urlVariables) throws RestClientException {
		return execute(url, HttpMethod.HEAD, null, headersExtractor(), urlVariables);
	}

	@Override
	public HttpHeaders headForHeaders(URI url) throws RestClientException {
		return execute(url, HttpMethod.HEAD, null, headersExtractor());
	}


	// POST

	@Override
	public URI postForLocation(String url, Object request, Object... urlVariables) throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request);
		HttpHeaders headers = execute(url, HttpMethod.POST, requestCallback, headersExtractor(), urlVariables);
		return headers.getLocation();
	}

	@Override
	public URI postForLocation(String url, Object request, Map<String, ?> urlVariables) throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request);
		HttpHeaders headers = execute(url, HttpMethod.POST, requestCallback, headersExtractor(), urlVariables);
		return headers.getLocation();
	}

	@Override
	public URI postForLocation(URI url, Object request) throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request);
		HttpHeaders headers = execute(url, HttpMethod.POST, requestCallback, headersExtractor());
		return headers.getLocation();
	}

	@Override
	public <T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables)
			throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		HttpMessageConverterExtractor<T> responseExtractor =
				new HttpMessageConverterExtractor<T>(responseType, getMessageConverters(), logger);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> T postForObject(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		HttpMessageConverterExtractor<T> responseExtractor =
				new HttpMessageConverterExtractor<T>(responseType, getMessageConverters(), logger);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> T postForObject(URI url, Object request, Class<T> responseType) throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		HttpMessageConverterExtractor<T> responseExtractor =
				new HttpMessageConverterExtractor<T>(responseType, getMessageConverters());
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor);
	}

	@Override
	public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Object... uriVariables)
			throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> postForEntity(URI url, Object request, Class<T> responseType) throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor);
	}


	// PUT

	@Override
	public void put(String url, Object request, Object... urlVariables) throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request);
		execute(url, HttpMethod.PUT, requestCallback, null, urlVariables);
	}

	@Override
	public void put(String url, Object request, Map<String, ?> urlVariables) throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request);
		execute(url, HttpMethod.PUT, requestCallback, null, urlVariables);
	}

	@Override
	public void put(URI url, Object request) throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request);
		execute(url, HttpMethod.PUT, requestCallback, null);
	}


	// DELETE

	@Override
	public void delete(String url, Object... urlVariables) throws RestClientException {
		execute(url, HttpMethod.DELETE, null, null, urlVariables);
	}

	@Override
	public void delete(String url, Map<String, ?> urlVariables) throws RestClientException {
		execute(url, HttpMethod.DELETE, null, null, urlVariables);
	}

	@Override
	public void delete(URI url) throws RestClientException {
		execute(url, HttpMethod.DELETE, null, null);
	}


	// OPTIONS

	@Override
	public Set<HttpMethod> optionsForAllow(String url, Object... urlVariables) throws RestClientException {
		ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
		HttpHeaders headers = execute(url, HttpMethod.OPTIONS, null, headersExtractor, urlVariables);
		return headers.getAllow();
	}

	@Override
	public Set<HttpMethod> optionsForAllow(String url, Map<String, ?> urlVariables) throws RestClientException {
		ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
		HttpHeaders headers = execute(url, HttpMethod.OPTIONS, null, headersExtractor, urlVariables);
		return headers.getAllow();
	}

	@Override
	public Set<HttpMethod> optionsForAllow(URI url) throws RestClientException {
		ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
		HttpHeaders headers = execute(url, HttpMethod.OPTIONS, null, headersExtractor);
		return headers.getAllow();
	}


	// exchange

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method,
			HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method,
			HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity,
			Class<T> responseType) throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor);
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException {

		Type type = responseType.getType();
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {

		Type type = responseType.getType();
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType) throws RestClientException {

		Type type = responseType.getType();
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor);
	}

	@Override
	public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, Class<T> responseType)
			throws RestClientException {

		Assert.notNull(requestEntity, "'requestEntity' must not be null");

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(requestEntity.getUrl(), requestEntity.getMethod(), requestCallback, responseExtractor);
	}

	@Override
	public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, ParameterizedTypeReference<T> responseType)
			throws RestClientException {

		Assert.notNull(requestEntity, "'requestEntity' must not be null");

		Type type = responseType.getType();
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(requestEntity.getUrl(), requestEntity.getMethod(), requestCallback, responseExtractor);
	}


	// general execution

	@Override
	public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor, Object... urlVariables) throws RestClientException {

		URI expanded = getUriTemplateHandler().expand(url, urlVariables);
		return doExecute(expanded, method, requestCallback, responseExtractor);
	}

	@Override
	public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor, Map<String, ?> urlVariables) throws RestClientException {

		URI expanded = getUriTemplateHandler().expand(url, urlVariables);
		return doExecute(expanded, method, requestCallback, responseExtractor);
	}

	@Override
	public <T> T execute(URI url, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor) throws RestClientException {

		return doExecute(url, method, requestCallback, responseExtractor);
	}

	/**
	 * Execute the given method on the provided URI.
	 * <p>The {@link ClientHttpRequest} is processed using the {@link RequestCallback};
	 * the response with the {@link ResponseExtractor}.
	 * <p>
	 *  在提供的URI上执行给定的方法<p>使用{@link RequestCallback}处理{@link ClientHttpRequest};使用{@link ResponseExtractor}的回
	 * 复。
	 * 
	 * 
	 * @param url the fully-expanded URL to connect to
	 * @param method the HTTP method to execute (GET, POST, etc.)
	 * @param requestCallback object that prepares the request (can be {@code null})
	 * @param responseExtractor object that extracts the return value from the response (can be {@code null})
	 * @return an arbitrary object, as returned by the {@link ResponseExtractor}
	 */
	protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor) throws RestClientException {

		Assert.notNull(url, "'url' must not be null");
		Assert.notNull(method, "'method' must not be null");
		ClientHttpResponse response = null;
		try {
			ClientHttpRequest request = createRequest(url, method);
			if (requestCallback != null) {
				requestCallback.doWithRequest(request);
			}
			response = request.execute();
			handleResponse(url, method, response);
			if (responseExtractor != null) {
				return responseExtractor.extractData(response);
			}
			else {
				return null;
			}
		}
		catch (IOException ex) {
			String resource = url.toString();
			String query = url.getRawQuery();
			resource = (query != null ? resource.substring(0, resource.indexOf(query) - 1) : resource);
			throw new ResourceAccessException("I/O error on " + method.name() +
					" request for \"" + resource + "\": " + ex.getMessage(), ex);
		}
		finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * Handle the given response, performing appropriate logging and
	 * invoking the {@link ResponseErrorHandler} if necessary.
	 * <p>Can be overridden in subclasses.
	 * <p>
	 *  处理给定的响应,执行适当的日志记录并调用必要的{@link ResponseErrorHandler} <p>可以在子类中覆盖
	 * 
	 * 
	 * @param url the fully-expanded URL to connect to
	 * @param method the HTTP method to execute (GET, POST, etc.)
	 * @param response the resulting {@link ClientHttpResponse}
	 * @throws IOException if propagated from {@link ResponseErrorHandler}
	 * @see #setErrorHandler
	 * @since 4.1.6
	 */
	protected void handleResponse(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
		ResponseErrorHandler errorHandler = getErrorHandler();
		boolean hasError = errorHandler.hasError(response);
		if (logger.isDebugEnabled()) {
			try {
				logger.debug(method.name() + " request for \"" + url + "\" resulted in " +
						response.getRawStatusCode() + " (" + response.getStatusText() + ")" +
						(hasError ? "; invoking error handler" : ""));
			}
			catch (IOException ex) {
				// ignore
			}
		}
		if (hasError) {
			errorHandler.handleError(response);
		}
	}

	/**
	 * Returns a request callback implementation that prepares the request {@code Accept}
	 * headers based on the given response type and configured
	 * {@linkplain #getMessageConverters() message converters}.
	 * <p>
	 * 返回一个请求回调实现,它根据给定的响应类型和配置的{@linkplain #getMessageConverters()消息转换器)准备请求{@code Accept}标头}
	 * 
	 */
	protected <T> RequestCallback acceptHeaderRequestCallback(Class<T> responseType) {
		return new AcceptHeaderRequestCallback(responseType);
	}

	/**
	 * Returns a request callback implementation that writes the given object to the
	 * request stream.
	 * <p>
	 *  返回将给定对象写入请求流的请求回调实现
	 * 
	 */
	protected <T> RequestCallback httpEntityCallback(Object requestBody) {
		return new HttpEntityRequestCallback(requestBody);
	}

	/**
	 * Returns a request callback implementation that writes the given object to the
	 * request stream.
	 * <p>
	 *  返回将给定对象写入请求流的请求回调实现
	 * 
	 */
	protected <T> RequestCallback httpEntityCallback(Object requestBody, Type responseType) {
		return new HttpEntityRequestCallback(requestBody, responseType);
	}

	/**
	 * Returns a response extractor for {@link ResponseEntity}.
	 * <p>
	 *  返回{@link ResponseEntity}的响应提取器
	 * 
	 */
	protected <T> ResponseExtractor<ResponseEntity<T>> responseEntityExtractor(Type responseType) {
		return new ResponseEntityResponseExtractor<T>(responseType);
	}

	/**
	 * Returns a response extractor for {@link HttpHeaders}.
	 * <p>
	 *  返回{@link HttpHeaders}的响应提取器
	 * 
	 */
	protected ResponseExtractor<HttpHeaders> headersExtractor() {
		return this.headersExtractor;
	}


	/**
	 * Request callback implementation that prepares the request's accept headers.
	 * <p>
	 *  请求回调实现,准备请求的接受头
	 * 
	 */
	private class AcceptHeaderRequestCallback implements RequestCallback {

		private final Type responseType;

		private AcceptHeaderRequestCallback(Type responseType) {
			this.responseType = responseType;
		}

		@Override
		public void doWithRequest(ClientHttpRequest request) throws IOException {
			if (this.responseType != null) {
				Class<?> responseClass = null;
				if (this.responseType instanceof Class) {
					responseClass = (Class<?>) this.responseType;
				}
				List<MediaType> allSupportedMediaTypes = new ArrayList<MediaType>();
				for (HttpMessageConverter<?> converter : getMessageConverters()) {
					if (responseClass != null) {
						if (converter.canRead(responseClass, null)) {
							allSupportedMediaTypes.addAll(getSupportedMediaTypes(converter));
						}
					}
					else if (converter instanceof GenericHttpMessageConverter) {
						GenericHttpMessageConverter<?> genericConverter = (GenericHttpMessageConverter<?>) converter;
						if (genericConverter.canRead(this.responseType, null, null)) {
							allSupportedMediaTypes.addAll(getSupportedMediaTypes(converter));
						}
					}
				}
				if (!allSupportedMediaTypes.isEmpty()) {
					MediaType.sortBySpecificity(allSupportedMediaTypes);
					if (logger.isDebugEnabled()) {
						logger.debug("Setting request Accept header to " + allSupportedMediaTypes);
					}
					request.getHeaders().setAccept(allSupportedMediaTypes);
				}
			}
		}

		private List<MediaType> getSupportedMediaTypes(HttpMessageConverter<?> messageConverter) {
			List<MediaType> supportedMediaTypes = messageConverter.getSupportedMediaTypes();
			List<MediaType> result = new ArrayList<MediaType>(supportedMediaTypes.size());
			for (MediaType supportedMediaType : supportedMediaTypes) {
				if (supportedMediaType.getCharset() != null) {
					supportedMediaType =
							new MediaType(supportedMediaType.getType(), supportedMediaType.getSubtype());
				}
				result.add(supportedMediaType);
			}
			return result;
		}
	}


	/**
	 * Request callback implementation that writes the given object to the request stream.
	 * <p>
	 *  请求回调实现将给定对象写入请求流
	 * 
	 */
	private class HttpEntityRequestCallback extends AcceptHeaderRequestCallback {

		private final HttpEntity<?> requestEntity;

		private HttpEntityRequestCallback(Object requestBody) {
			this(requestBody, null);
		}

		private HttpEntityRequestCallback(Object requestBody, Type responseType) {
			super(responseType);
			if (requestBody instanceof HttpEntity) {
				this.requestEntity = (HttpEntity<?>) requestBody;
			}
			else if (requestBody != null) {
				this.requestEntity = new HttpEntity<Object>(requestBody);
			}
			else {
				this.requestEntity = HttpEntity.EMPTY;
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public void doWithRequest(ClientHttpRequest httpRequest) throws IOException {
			super.doWithRequest(httpRequest);
			if (!this.requestEntity.hasBody()) {
				HttpHeaders httpHeaders = httpRequest.getHeaders();
				HttpHeaders requestHeaders = this.requestEntity.getHeaders();
				if (!requestHeaders.isEmpty()) {
					httpHeaders.putAll(requestHeaders);
				}
				if (httpHeaders.getContentLength() < 0) {
					httpHeaders.setContentLength(0L);
				}
			}
			else {
				Object requestBody = this.requestEntity.getBody();
				Class<?> requestBodyClass = requestBody.getClass();
				Type requestBodyType = (this.requestEntity instanceof RequestEntity ?
						((RequestEntity<?>)this.requestEntity).getType() : requestBodyClass);
				HttpHeaders requestHeaders = this.requestEntity.getHeaders();
				MediaType requestContentType = requestHeaders.getContentType();
				for (HttpMessageConverter<?> messageConverter : getMessageConverters()) {
					if (messageConverter instanceof GenericHttpMessageConverter) {
						GenericHttpMessageConverter<Object> genericMessageConverter = (GenericHttpMessageConverter<Object>) messageConverter;
						if (genericMessageConverter.canWrite(requestBodyType, requestBodyClass, requestContentType)) {
							if (!requestHeaders.isEmpty()) {
								httpRequest.getHeaders().putAll(requestHeaders);
							}
							if (logger.isDebugEnabled()) {
								if (requestContentType != null) {
									logger.debug("Writing [" + requestBody + "] as \"" + requestContentType +
											"\" using [" + messageConverter + "]");
								}
								else {
									logger.debug("Writing [" + requestBody + "] using [" + messageConverter + "]");
								}

							}
							genericMessageConverter.write(
									requestBody, requestBodyType, requestContentType, httpRequest);
							return;
						}
					}
					else if (messageConverter.canWrite(requestBodyClass, requestContentType)) {
						if (!requestHeaders.isEmpty()) {
							httpRequest.getHeaders().putAll(requestHeaders);
						}
						if (logger.isDebugEnabled()) {
							if (requestContentType != null) {
								logger.debug("Writing [" + requestBody + "] as \"" + requestContentType +
										"\" using [" + messageConverter + "]");
							}
							else {
								logger.debug("Writing [" + requestBody + "] using [" + messageConverter + "]");
							}

						}
						((HttpMessageConverter<Object>) messageConverter).write(
								requestBody, requestContentType, httpRequest);
						return;
					}
				}
				String message = "Could not write request: no suitable HttpMessageConverter found for request type [" +
						requestBodyClass.getName() + "]";
				if (requestContentType != null) {
					message += " and content type [" + requestContentType + "]";
				}
				throw new RestClientException(message);
			}
		}
	}


	/**
	 * Response extractor for {@link HttpEntity}.
	 * <p>
	 *  {@link HttpEntity}的响应提取器
	 * 
	 */
	private class ResponseEntityResponseExtractor<T> implements ResponseExtractor<ResponseEntity<T>> {

		private final HttpMessageConverterExtractor<T> delegate;

		public ResponseEntityResponseExtractor(Type responseType) {
			if (responseType != null && Void.class != responseType) {
				this.delegate = new HttpMessageConverterExtractor<T>(responseType, getMessageConverters(), logger);
			}
			else {
				this.delegate = null;
			}
		}

		@Override
		public ResponseEntity<T> extractData(ClientHttpResponse response) throws IOException {
			if (this.delegate != null) {
				T body = this.delegate.extractData(response);
				return new ResponseEntity<T>(body, response.getHeaders(), response.getStatusCode());
			}
			else {
				return new ResponseEntity<T>(response.getHeaders(), response.getStatusCode());
			}
		}
	}


	/**
	 * Response extractor that extracts the response {@link HttpHeaders}.
	 * <p>
	 *  响应提取器提取响应{@link HttpHeaders}
	 */
	private static class HeadersExtractor implements ResponseExtractor<HttpHeaders> {

		@Override
		public HttpHeaders extractData(ClientHttpResponse response) throws IOException {
			return response.getHeaders();
		}
	}

}
