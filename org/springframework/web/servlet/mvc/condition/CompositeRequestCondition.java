/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.web.servlet.mvc.condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Implements the {@link RequestCondition} contract by delegating to multiple
 * {@code RequestCondition} types and using a logical conjunction (' && ') to
 * ensure all conditions match a given request.
 *
 * <p>When {@code CompositeRequestCondition} instances are combined or compared
 * they are expected to (a) contain the same number of conditions and (b) that
 * conditions in the respective index are of the same type. It is acceptable to
 * provide {@code null} conditions or no conditions at all to the constructor.
 *
 * <p>
 *  通过委托多个{@code RequestCondition}类型并使用逻辑连接('&&')来实现{@link RequestCondition}合同,以确保所有条件符合给定的请求
 * 
 * <p>当{@code CompositeRequestCondition}实例组合或比较时,它们预期(a)包含相同数量的条件,(b)相应索引中的条件是相同类型可以提供{@code null}条件或根本没
 * 有条件给构造函数。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class CompositeRequestCondition extends AbstractRequestCondition<CompositeRequestCondition> {

	private final RequestConditionHolder[] requestConditions;


	/**
	 * Create an instance with 0 or more {@code RequestCondition} types. It is
	 * important to create {@code CompositeRequestCondition} instances with the
	 * same number of conditions so they may be compared and combined.
	 * It is acceptable to provide {@code null} conditions.
	 * <p>
	 *  创建一个具有0个或更多{@code RequestCondition}类型的实例重要的是创建具有相同数量条件的{@code CompositeRequestCondition}实例,以便它们可以进行比
	 * 较和组合。
	 * 可以提供{@code null}条件。
	 * 
	 */
	public CompositeRequestCondition(RequestCondition<?>... requestConditions) {
		this.requestConditions = wrap(requestConditions);
	}

	private CompositeRequestCondition(RequestConditionHolder[] requestConditions) {
		this.requestConditions = requestConditions;
	}


	private RequestConditionHolder[] wrap(RequestCondition<?>... rawConditions) {
		RequestConditionHolder[] wrappedConditions = new RequestConditionHolder[rawConditions.length];
		for (int i = 0; i < rawConditions.length; i++) {
			wrappedConditions[i] = new RequestConditionHolder(rawConditions[i]);
		}
		return wrappedConditions;
	}

	/**
	 * Whether this instance contains 0 conditions or not.
	 * <p>
	 *  该实例是否包含0个条件
	 * 
	 */
	public boolean isEmpty() {
		return ObjectUtils.isEmpty(this.requestConditions);
	}

	/**
	 * Return the underlying conditions, possibly empty but never {@code null}.
	 * <p>
	 *  返回基础条件,可能为空,但从不{@code null}
	 * 
	 */
	public List<RequestCondition<?>> getConditions() {
		return unwrap();
	}

	private List<RequestCondition<?>> unwrap() {
		List<RequestCondition<?>> result = new ArrayList<RequestCondition<?>>();
		for (RequestConditionHolder holder : this.requestConditions) {
			result.add(holder.getCondition());
		}
		return result;
	}

	@Override
	protected Collection<?> getContent() {
		return (isEmpty()) ? Collections.emptyList() : getConditions();
	}

	@Override
	protected String getToStringInfix() {
		return " && ";
	}

	private int getLength() {
		return this.requestConditions.length;
	}

	/**
	 * If one instance is empty, return the other.
	 * If both instances have conditions, combine the individual conditions
	 * after ensuring they are of the same type and number.
	 * <p>
	 * 如果一个实例为空,则返回另一个实例如果两个实例都有条件,则在确定它们的类型和数量相同后,组合各个条件
	 * 
	 */
	@Override
	public CompositeRequestCondition combine(CompositeRequestCondition other) {
		if (isEmpty() && other.isEmpty()) {
			return this;
		}
		else if (other.isEmpty()) {
			return this;
		}
		else if (isEmpty()) {
			return other;
		}
		else {
			assertNumberOfConditions(other);
			RequestConditionHolder[] combinedConditions = new RequestConditionHolder[getLength()];
			for (int i = 0; i < getLength(); i++) {
				combinedConditions[i] = this.requestConditions[i].combine(other.requestConditions[i]);
			}
			return new CompositeRequestCondition(combinedConditions);
		}
	}

	private void assertNumberOfConditions(CompositeRequestCondition other) {
		Assert.isTrue(getLength() == other.getLength(),
				"Cannot combine CompositeRequestConditions with a different number of conditions. " +
				ObjectUtils.nullSafeToString(this.requestConditions) + " and  " +
				ObjectUtils.nullSafeToString(other.requestConditions));
	}

	/**
	 * Delegate to <em>all</em> contained conditions to match the request and return the
	 * resulting "matching" condition instances.
	 * <p>An empty {@code CompositeRequestCondition} matches to all requests.
	 * <p>
	 *  代表<em>所有</em>包含条件以匹配请求并返回生成的"匹配"条件实例<p>空的{@code CompositeRequestCondition}匹配所有请求
	 * 
	 */
	@Override
	public CompositeRequestCondition getMatchingCondition(HttpServletRequest request) {
		if (isEmpty()) {
			return this;
		}
		RequestConditionHolder[] matchingConditions = new RequestConditionHolder[getLength()];
		for (int i = 0; i < getLength(); i++) {
			matchingConditions[i] = this.requestConditions[i].getMatchingCondition(request);
			if (matchingConditions[i] == null) {
				return null;
			}
		}
		return new CompositeRequestCondition(matchingConditions);
	}

	/**
	 * If one instance is empty, the other "wins". If both instances have
	 * conditions, compare them in the order in which they were provided.
	 * <p>
	 *  如果一个实例是空的,其他的"赢"如果两个实例都有条件,请按照提供的顺序进行比较
	 */
	@Override
	public int compareTo(CompositeRequestCondition other, HttpServletRequest request) {
		if (isEmpty() && other.isEmpty()) {
			return 0;
		}
		else if (isEmpty()) {
			return 1;
		}
		else if (other.isEmpty()) {
			return -1;
		}
		else {
			assertNumberOfConditions(other);
			for (int i = 0; i < getLength(); i++) {
				int result = this.requestConditions[i].compareTo(other.requestConditions[i], request);
				if (result != 0) {
					return result;
				}
			}
			return 0;
		}
	}

}
