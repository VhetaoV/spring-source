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

package org.springframework.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Miscellaneous object utility methods.
 *
 * <p>Mainly for internal use within the framework.
 *
 * <p>Thanks to Alex Ruiz for contributing several enhancements to this class!
 *
 * <p>
 *  杂项对象实用方法
 * 
 *  <p>主要用于框架内部使用
 * 
 *  感谢Alex Ruiz为这个课程做了几个改进！
 * 
 * 
 * @author Juergen Hoeller
 * @author Keith Donald
 * @author Rod Johnson
 * @author Rob Harrop
 * @author Chris Beams
 * @author Sam Brannen
 * @since 19.03.2004
 * @see ClassUtils
 * @see CollectionUtils
 * @see StringUtils
 */
public abstract class ObjectUtils {

	private static final int INITIAL_HASH = 7;
	private static final int MULTIPLIER = 31;

	private static final String EMPTY_STRING = "";
	private static final String NULL_STRING = "null";
	private static final String ARRAY_START = "{";
	private static final String ARRAY_END = "}";
	private static final String EMPTY_ARRAY = ARRAY_START + ARRAY_END;
	private static final String ARRAY_ELEMENT_SEPARATOR = ", ";


	/**
	 * Return whether the given throwable is a checked exception:
	 * that is, neither a RuntimeException nor an Error.
	 * <p>
	 * 返回给定的throwable是否是被检查的异常：也就是说,RuntimeException也不是Error
	 * 
	 * 
	 * @param ex the throwable to check
	 * @return whether the throwable is a checked exception
	 * @see java.lang.Exception
	 * @see java.lang.RuntimeException
	 * @see java.lang.Error
	 */
	public static boolean isCheckedException(Throwable ex) {
		return !(ex instanceof RuntimeException || ex instanceof Error);
	}

	/**
	 * Check whether the given exception is compatible with the specified
	 * exception types, as declared in a throws clause.
	 * <p>
	 *  检查给定的异常是否与指定的异常类型兼容,如在throws子句中声明的
	 * 
	 * 
	 * @param ex the exception to check
	 * @param declaredExceptions the exception types declared in the throws clause
	 * @return whether the given exception is compatible
	 */
	public static boolean isCompatibleWithThrowsClause(Throwable ex, Class<?>... declaredExceptions) {
		if (!isCheckedException(ex)) {
			return true;
		}
		if (declaredExceptions != null) {
			for (Class<?> declaredException : declaredExceptions) {
				if (declaredException.isInstance(ex)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Determine whether the given object is an array:
	 * either an Object array or a primitive array.
	 * <p>
	 *  确定给定对象是否为数组：对象数组还是原始数组
	 * 
	 * 
	 * @param obj the object to check
	 */
	public static boolean isArray(Object obj) {
		return (obj != null && obj.getClass().isArray());
	}

	/**
	 * Determine whether the given array is empty:
	 * i.e. {@code null} or of zero length.
	 * <p>
	 *  确定给定的数组是否为空：即{@code null}或零长度
	 * 
	 * 
	 * @param array the array to check
	 * @see #isEmpty(Object)
	 */
	public static boolean isEmpty(Object[] array) {
		return (array == null || array.length == 0);
	}

	/**
	 * Determine whether the given object is empty.
	 * <p>This method supports the following object types.
	 * <ul>
	 * <li>{@code Array}: considered empty if its length is zero</li>
	 * <li>{@link CharSequence}: considered empty if its length is zero</li>
	 * <li>{@link Collection}: delegates to {@link Collection#isEmpty()}</li>
	 * <li>{@link Map}: delegates to {@link Map#isEmpty()}</li>
	 * </ul>
	 * <p>If the given object is non-null and not one of the aforementioned
	 * supported types, this method returns {@code false}.
	 * <p>
	 *  确定给定对象是否为空<p>此方法支持以下对象类型
	 * <ul>
	 *  <li> {@ code Array}：如果长度为零,则视为空白</li> <li> {@ link CharSequence}：如果长度为零,则被视为空白</li> <li> {@ link Collection}
	 * ：代表{@link Collection#isEmpty()} </li> <li> {@ link Map}：委托{@link Map#isEmpty()} </li>。
	 * </ul>
	 * <p>如果给定的对象非空,而不是上述支持的类型之一,则此方法返回{@code false}
	 * 
	 * 
	 * @param obj the object to check
	 * @return {@code true} if the object is {@code null} or <em>empty</em>
	 * @since 4.2
	 * @see ObjectUtils#isEmpty(Object[])
	 * @see StringUtils#hasLength(CharSequence)
	 * @see StringUtils#isEmpty(Object)
	 * @see CollectionUtils#isEmpty(java.util.Collection)
	 * @see CollectionUtils#isEmpty(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}

		if (obj.getClass().isArray()) {
			return Array.getLength(obj) == 0;
		}
		if (obj instanceof CharSequence) {
			return ((CharSequence) obj).length() == 0;
		}
		if (obj instanceof Collection) {
			return ((Collection) obj).isEmpty();
		}
		if (obj instanceof Map) {
			return ((Map) obj).isEmpty();
		}

		// else
		return false;
	}

	/**
	 * Check whether the given array contains the given element.
	 * <p>
	 *  检查给定的数组是否包含给定的元素
	 * 
	 * 
	 * @param array the array to check (may be {@code null},
	 * in which case the return value will always be {@code false})
	 * @param element the element to check for
	 * @return whether the element has been found in the given array
	 */
	public static boolean containsElement(Object[] array, Object element) {
		if (array == null) {
			return false;
		}
		for (Object arrayEle : array) {
			if (nullSafeEquals(arrayEle, element)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given array of enum constants contains a constant with the given name,
	 * ignoring case when determining a match.
	 * <p>
	 *  检查给定的枚举常量数组是否包含具有给定名称的常量,在确定匹配时忽略大小写
	 * 
	 * 
	 * @param enumValues the enum values to check, typically the product of a call to MyEnum.values()
	 * @param constant the constant name to find (must not be null or empty string)
	 * @return whether the constant has been found in the given array
	 */
	public static boolean containsConstant(Enum<?>[] enumValues, String constant) {
		return containsConstant(enumValues, constant, false);
	}

	/**
	 * Check whether the given array of enum constants contains a constant with the given name.
	 * <p>
	 *  检查给定的枚举常量数组是否包含给定名称的常量
	 * 
	 * 
	 * @param enumValues the enum values to check, typically the product of a call to MyEnum.values()
	 * @param constant the constant name to find (must not be null or empty string)
	 * @param caseSensitive whether case is significant in determining a match
	 * @return whether the constant has been found in the given array
	 */
	public static boolean containsConstant(Enum<?>[] enumValues, String constant, boolean caseSensitive) {
		for (Enum<?> candidate : enumValues) {
			if (caseSensitive ?
					candidate.toString().equals(constant) :
					candidate.toString().equalsIgnoreCase(constant)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Case insensitive alternative to {@link Enum#valueOf(Class, String)}.
	 * <p>
	 *  不区分大小写的替代{@link Enum#valueOf(Class,String)}
	 * 
	 * 
	 * @param <E> the concrete Enum type
	 * @param enumValues the array of all Enum constants in question, usually per Enum.values()
	 * @param constant the constant to get the enum value of
	 * @throws IllegalArgumentException if the given constant is not found in the given array
	 * of enum values. Use {@link #containsConstant(Enum[], String)} as a guard to avoid this exception.
	 */
	public static <E extends Enum<?>> E caseInsensitiveValueOf(E[] enumValues, String constant) {
		for (E candidate : enumValues) {
			if (candidate.toString().equalsIgnoreCase(constant)) {
				return candidate;
			}
		}
		throw new IllegalArgumentException(
				String.format("constant [%s] does not exist in enum type %s",
						constant, enumValues.getClass().getComponentType().getName()));
	}

	/**
	 * Append the given object to the given array, returning a new array
	 * consisting of the input array contents plus the given object.
	 * <p>
	 *  将给定对象附加到给定的数组,返回由输入数组内容加上给定对象组成的新数组
	 * 
	 * 
	 * @param array the array to append to (can be {@code null})
	 * @param obj the object to append
	 * @return the new array (of the same component type; never {@code null})
	 */
	public static <A, O extends A> A[] addObjectToArray(A[] array, O obj) {
		Class<?> compType = Object.class;
		if (array != null) {
			compType = array.getClass().getComponentType();
		}
		else if (obj != null) {
			compType = obj.getClass();
		}
		int newArrLength = (array != null ? array.length + 1 : 1);
		@SuppressWarnings("unchecked")
		A[] newArr = (A[]) Array.newInstance(compType, newArrLength);
		if (array != null) {
			System.arraycopy(array, 0, newArr, 0, array.length);
		}
		newArr[newArr.length - 1] = obj;
		return newArr;
	}

	/**
	 * Convert the given array (which may be a primitive array) to an
	 * object array (if necessary of primitive wrapper objects).
	 * <p>A {@code null} source value will be converted to an
	 * empty Object array.
	 * <p>
	 *  将给定的数组(可能是原始数组)转换为对象数组(如果需要原始包装对象)<p> {@code null}源值将转换为空对象数组
	 * 
	 * 
	 * @param source the (potentially primitive) array
	 * @return the corresponding object array (never {@code null})
	 * @throws IllegalArgumentException if the parameter is not an array
	 */
	public static Object[] toObjectArray(Object source) {
		if (source instanceof Object[]) {
			return (Object[]) source;
		}
		if (source == null) {
			return new Object[0];
		}
		if (!source.getClass().isArray()) {
			throw new IllegalArgumentException("Source is not an array: " + source);
		}
		int length = Array.getLength(source);
		if (length == 0) {
			return new Object[0];
		}
		Class<?> wrapperType = Array.get(source, 0).getClass();
		Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
		for (int i = 0; i < length; i++) {
			newArray[i] = Array.get(source, i);
		}
		return newArray;
	}


	//---------------------------------------------------------------------
	// Convenience methods for content-based equality/hash-code handling
	//---------------------------------------------------------------------

	/**
	 * Determine if the given objects are equal, returning {@code true} if
	 * both are {@code null} or {@code false} if only one is {@code null}.
	 * <p>Compares arrays with {@code Arrays.equals}, performing an equality
	 * check based on the array elements rather than the array reference.
	 * <p>
	 * 确定给定对象是否相等,如果只有一个是{@code null} <p>使用{@code Arraysequals}比较数组,执行{@code null}或{@code false},返回{@code true}
	 * 基于数组元素而不是数组引用的等式检查。
	 * 
	 * 
	 * @param o1 first Object to compare
	 * @param o2 second Object to compare
	 * @return whether the given objects are equal
	 * @see Object#equals(Object)
	 * @see java.util.Arrays#equals
	 */
	public static boolean nullSafeEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}
		if (o1.equals(o2)) {
			return true;
		}
		if (o1.getClass().isArray() && o2.getClass().isArray()) {
			return arrayEquals(o1, o2);
		}
		return false;
	}

	/**
	 * Compare the given arrays with {@code Arrays.equals}, performing an equality
	 * check based on the array elements rather than the array reference.
	 * <p>
	 *  将给定的数组与{@code Arraysequals}进行比较,根据数组元素而不是数组引用执行相等性检查
	 * 
	 * 
	 * @param o1 first array to compare
	 * @param o2 second array to compare
	 * @return whether the given objects are equal
	 * @see #nullSafeEquals(Object, Object)
	 * @see java.util.Arrays#equals
	 */
	private static boolean arrayEquals(Object o1, Object o2) {
		if (o1 instanceof Object[] && o2 instanceof Object[]) {
			return Arrays.equals((Object[]) o1, (Object[]) o2);
		}
		if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
			return Arrays.equals((boolean[]) o1, (boolean[]) o2);
		}
		if (o1 instanceof byte[] && o2 instanceof byte[]) {
			return Arrays.equals((byte[]) o1, (byte[]) o2);
		}
		if (o1 instanceof char[] && o2 instanceof char[]) {
			return Arrays.equals((char[]) o1, (char[]) o2);
		}
		if (o1 instanceof double[] && o2 instanceof double[]) {
			return Arrays.equals((double[]) o1, (double[]) o2);
		}
		if (o1 instanceof float[] && o2 instanceof float[]) {
			return Arrays.equals((float[]) o1, (float[]) o2);
		}
		if (o1 instanceof int[] && o2 instanceof int[]) {
			return Arrays.equals((int[]) o1, (int[]) o2);
		}
		if (o1 instanceof long[] && o2 instanceof long[]) {
			return Arrays.equals((long[]) o1, (long[]) o2);
		}
		if (o1 instanceof short[] && o2 instanceof short[]) {
			return Arrays.equals((short[]) o1, (short[]) o2);
		}
		return false;
	}

	/**
	 * Return as hash code for the given object; typically the value of
	 * {@code Object#hashCode()}}. If the object is an array,
	 * this method will delegate to any of the {@code nullSafeHashCode}
	 * methods for arrays in this class. If the object is {@code null},
	 * this method returns 0.
	 * <p>
	 *  返回给定对象的哈希码;通常值为{@code Object#hashCode()}}如果对象是数组,则此方法将委托给此类中的数组的{@code nullSafeHashCode}方法。
	 * 如果对象为{@code null},此方法返回0。
	 * 
	 * 
	 * @see Object#hashCode()
	 * @see #nullSafeHashCode(Object[])
	 * @see #nullSafeHashCode(boolean[])
	 * @see #nullSafeHashCode(byte[])
	 * @see #nullSafeHashCode(char[])
	 * @see #nullSafeHashCode(double[])
	 * @see #nullSafeHashCode(float[])
	 * @see #nullSafeHashCode(int[])
	 * @see #nullSafeHashCode(long[])
	 * @see #nullSafeHashCode(short[])
	 */
	public static int nullSafeHashCode(Object obj) {
		if (obj == null) {
			return 0;
		}
		if (obj.getClass().isArray()) {
			if (obj instanceof Object[]) {
				return nullSafeHashCode((Object[]) obj);
			}
			if (obj instanceof boolean[]) {
				return nullSafeHashCode((boolean[]) obj);
			}
			if (obj instanceof byte[]) {
				return nullSafeHashCode((byte[]) obj);
			}
			if (obj instanceof char[]) {
				return nullSafeHashCode((char[]) obj);
			}
			if (obj instanceof double[]) {
				return nullSafeHashCode((double[]) obj);
			}
			if (obj instanceof float[]) {
				return nullSafeHashCode((float[]) obj);
			}
			if (obj instanceof int[]) {
				return nullSafeHashCode((int[]) obj);
			}
			if (obj instanceof long[]) {
				return nullSafeHashCode((long[]) obj);
			}
			if (obj instanceof short[]) {
				return nullSafeHashCode((short[]) obj);
			}
		}
		return obj.hashCode();
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 * <p>
	 * 根据指定数组的内容返回哈希码如果{@code array}为{@code null},则此方法返回0
	 * 
	 */
	public static int nullSafeHashCode(Object[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (Object element : array) {
			hash = MULTIPLIER * hash + nullSafeHashCode(element);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 * <p>
	 *  根据指定数组的内容返回哈希码如果{@code array}为{@code null},则此方法返回0
	 * 
	 */
	public static int nullSafeHashCode(boolean[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (boolean element : array) {
			hash = MULTIPLIER * hash + hashCode(element);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 * <p>
	 *  根据指定数组的内容返回哈希码如果{@code array}为{@code null},则此方法返回0
	 * 
	 */
	public static int nullSafeHashCode(byte[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (byte element : array) {
			hash = MULTIPLIER * hash + element;
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 * <p>
	 *  根据指定数组的内容返回哈希码如果{@code array}为{@code null},则此方法返回0
	 * 
	 */
	public static int nullSafeHashCode(char[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (char element : array) {
			hash = MULTIPLIER * hash + element;
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 * <p>
	 *  根据指定数组的内容返回哈希码如果{@code array}为{@code null},则此方法返回0
	 * 
	 */
	public static int nullSafeHashCode(double[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (double element : array) {
			hash = MULTIPLIER * hash + hashCode(element);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 * <p>
	 *  根据指定数组的内容返回哈希码如果{@code array}为{@code null},则此方法返回0
	 * 
	 */
	public static int nullSafeHashCode(float[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (float element : array) {
			hash = MULTIPLIER * hash + hashCode(element);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 * <p>
	 * 根据指定数组的内容返回哈希码如果{@code array}为{@code null},则此方法返回0
	 * 
	 */
	public static int nullSafeHashCode(int[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (int element : array) {
			hash = MULTIPLIER * hash + element;
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 * <p>
	 *  根据指定数组的内容返回哈希码如果{@code array}为{@code null},则此方法返回0
	 * 
	 */
	public static int nullSafeHashCode(long[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (long element : array) {
			hash = MULTIPLIER * hash + hashCode(element);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 * <p>
	 *  根据指定数组的内容返回哈希码如果{@code array}为{@code null},则此方法返回0
	 * 
	 */
	public static int nullSafeHashCode(short[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (short element : array) {
			hash = MULTIPLIER * hash + element;
		}
		return hash;
	}

	/**
	 * Return the same value as {@link Boolean#hashCode()}}.
	 * <p>
	 *  返回与{@link Boolean#hashCode()}相同的值}}
	 * 
	 * 
	 * @see Boolean#hashCode()
	 */
	public static int hashCode(boolean bool) {
		return (bool ? 1231 : 1237);
	}

	/**
	 * Return the same value as {@link Double#hashCode()}}.
	 * <p>
	 *  返回相同的值{@link Double#hashCode()}}
	 * 
	 * 
	 * @see Double#hashCode()
	 */
	public static int hashCode(double dbl) {
		return hashCode(Double.doubleToLongBits(dbl));
	}

	/**
	 * Return the same value as {@link Float#hashCode()}}.
	 * <p>
	 *  返回与{@link Float#hashCode()}相同的值}}
	 * 
	 * 
	 * @see Float#hashCode()
	 */
	public static int hashCode(float flt) {
		return Float.floatToIntBits(flt);
	}

	/**
	 * Return the same value as {@link Long#hashCode()}}.
	 * <p>
	 *  返回与{@link Long#hashCode()}相同的值}}
	 * 
	 * 
	 * @see Long#hashCode()
	 */
	public static int hashCode(long lng) {
		return (int) (lng ^ (lng >>> 32));
	}


	//---------------------------------------------------------------------
	// Convenience methods for toString output
	//---------------------------------------------------------------------

	/**
	 * Return a String representation of an object's overall identity.
	 * <p>
	 *  返回对象整体身份的字符串表示形式
	 * 
	 * 
	 * @param obj the object (may be {@code null})
	 * @return the object's identity as String representation,
	 * or an empty String if the object was {@code null}
	 */
	public static String identityToString(Object obj) {
		if (obj == null) {
			return EMPTY_STRING;
		}
		return obj.getClass().getName() + "@" + getIdentityHexString(obj);
	}

	/**
	 * Return a hex String form of an object's identity hash code.
	 * <p>
	 *  返回一个对象的标识哈希码的十六进制字符串形式
	 * 
	 * 
	 * @param obj the object
	 * @return the object's identity code in hex notation
	 */
	public static String getIdentityHexString(Object obj) {
		return Integer.toHexString(System.identityHashCode(obj));
	}

	/**
	 * Return a content-based String representation if {@code obj} is
	 * not {@code null}; otherwise returns an empty String.
	 * <p>Differs from {@link #nullSafeToString(Object)} in that it returns
	 * an empty String rather than "null" for a {@code null} value.
	 * <p>
	 * 如果{@code obj}不是{@code null},则返回基于内容的String表示;否则返回一个空字符串<p>不同于{@link #nullSafeToString(Object)},因为它为{@code null}
	 * 值返回一个空字符串而不是"null"。
	 * 
	 * 
	 * @param obj the object to build a display String for
	 * @return a display String representation of {@code obj}
	 * @see #nullSafeToString(Object)
	 */
	public static String getDisplayString(Object obj) {
		if (obj == null) {
			return EMPTY_STRING;
		}
		return nullSafeToString(obj);
	}

	/**
	 * Determine the class name for the given object.
	 * <p>Returns {@code "null"} if {@code obj} is {@code null}.
	 * <p>
	 *  确定给定对象的类名称如果{@code obj}为{@code null},则返回{@code"null"}
	 * 
	 * 
	 * @param obj the object to introspect (may be {@code null})
	 * @return the corresponding class name
	 */
	public static String nullSafeClassName(Object obj) {
		return (obj != null ? obj.getClass().getName() : NULL_STRING);
	}

	/**
	 * Return a String representation of the specified Object.
	 * <p>Builds a String representation of the contents in case of an array.
	 * Returns {@code "null"} if {@code obj} is {@code null}.
	 * <p>
	 *  返回指定对象的String表示形式<p>在数组的情况下生成内容的字符串表示返回{@code"null"},如果{@code obj}为{@code null}
	 * 
	 * 
	 * @param obj the object to build a String representation for
	 * @return a String representation of {@code obj}
	 */
	public static String nullSafeToString(Object obj) {
		if (obj == null) {
			return NULL_STRING;
		}
		if (obj instanceof String) {
			return (String) obj;
		}
		if (obj instanceof Object[]) {
			return nullSafeToString((Object[]) obj);
		}
		if (obj instanceof boolean[]) {
			return nullSafeToString((boolean[]) obj);
		}
		if (obj instanceof byte[]) {
			return nullSafeToString((byte[]) obj);
		}
		if (obj instanceof char[]) {
			return nullSafeToString((char[]) obj);
		}
		if (obj instanceof double[]) {
			return nullSafeToString((double[]) obj);
		}
		if (obj instanceof float[]) {
			return nullSafeToString((float[]) obj);
		}
		if (obj instanceof int[]) {
			return nullSafeToString((int[]) obj);
		}
		if (obj instanceof long[]) {
			return nullSafeToString((long[]) obj);
		}
		if (obj instanceof short[]) {
			return nullSafeToString((short[]) obj);
		}
		String str = obj.toString();
		return (str != null ? str : EMPTY_STRING);
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces ({@code "{}"}). Adjacent elements are separated
	 * by the characters {@code ", "} (a comma followed by a space). Returns
	 * {@code "null"} if {@code array} is {@code null}.
	 * <p>
	 * 返回指定数组的内容的字符串表示<p> String表示由数组元素的列表组成,括在花括号中({@code"{}"}))相邻元素由字符{@code ","}(后跟一个空格的逗号)如果{@code array}
	 * 为{@code null},则返回{@code"null"}。
	 * 
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of {@code array}
	 */
	public static String nullSafeToString(Object[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(String.valueOf(array[i]));
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces ({@code "{}"}). Adjacent elements are separated
	 * by the characters {@code ", "} (a comma followed by a space). Returns
	 * {@code "null"} if {@code array} is {@code null}.
	 * <p>
	 *  返回指定数组的内容的字符串表示<p> String表示由数组元素的列表组成,括在花括号中({@code"{}"}))相邻元素由字符{@code ","}(后跟一个空格的逗号)如果{@code array}
	 * 为{@code null},则返回{@code"null"}。
	 * 
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of {@code array}
	 */
	public static String nullSafeToString(boolean[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}

			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces ({@code "{}"}). Adjacent elements are separated
	 * by the characters {@code ", "} (a comma followed by a space). Returns
	 * {@code "null"} if {@code array} is {@code null}.
	 * <p>
	 * 返回指定数组的内容的字符串表示<p> String表示由数组元素的列表组成,括在花括号中({@code"{}"}))相邻元素由字符{@code ","}(后跟一个空格的逗号)如果{@code array}
	 * 为{@code null},则返回{@code"null"}。
	 * 
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of {@code array}
	 */
	public static String nullSafeToString(byte[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces ({@code "{}"}). Adjacent elements are separated
	 * by the characters {@code ", "} (a comma followed by a space). Returns
	 * {@code "null"} if {@code array} is {@code null}.
	 * <p>
	 *  返回指定数组的内容的字符串表示<p> String表示由数组元素的列表组成,括在花括号中({@code"{}"}))相邻元素由字符{@code ","}(后跟一个空格的逗号)如果{@code array}
	 * 为{@code null},则返回{@code"null"}。
	 * 
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of {@code array}
	 */
	public static String nullSafeToString(char[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append("'").append(array[i]).append("'");
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces ({@code "{}"}). Adjacent elements are separated
	 * by the characters {@code ", "} (a comma followed by a space). Returns
	 * {@code "null"} if {@code array} is {@code null}.
	 * <p>
	 * 返回指定数组的内容的字符串表示<p> String表示由数组元素的列表组成,括在花括号中({@code"{}"}))相邻元素由字符{@code ","}(后跟一个空格的逗号)如果{@code array}
	 * 为{@code null},则返回{@code"null"}。
	 * 
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of {@code array}
	 */
	public static String nullSafeToString(double[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}

			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces ({@code "{}"}). Adjacent elements are separated
	 * by the characters {@code ", "} (a comma followed by a space). Returns
	 * {@code "null"} if {@code array} is {@code null}.
	 * <p>
	 *  返回指定数组的内容的字符串表示<p> String表示由数组元素的列表组成,括在花括号中({@code"{}"}))相邻元素由字符{@code ","}(后跟一个空格的逗号)如果{@code array}
	 * 为{@code null},则返回{@code"null"}。
	 * 
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of {@code array}
	 */
	public static String nullSafeToString(float[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}

			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces ({@code "{}"}). Adjacent elements are separated
	 * by the characters {@code ", "} (a comma followed by a space). Returns
	 * {@code "null"} if {@code array} is {@code null}.
	 * <p>
	 * 返回指定数组的内容的字符串表示<p> String表示由数组元素的列表组成,括在花括号中({@code"{}"}))相邻元素由字符{@code ","}(后跟一个空格的逗号)如果{@code array}
	 * 为{@code null},则返回{@code"null"}。
	 * 
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of {@code array}
	 */
	public static String nullSafeToString(int[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces ({@code "{}"}). Adjacent elements are separated
	 * by the characters {@code ", "} (a comma followed by a space). Returns
	 * {@code "null"} if {@code array} is {@code null}.
	 * <p>
	 *  返回指定数组的内容的字符串表示<p> String表示由数组元素的列表组成,括在花括号中({@code"{}"}))相邻元素由字符{@code ","}(后跟一个空格的逗号)如果{@code array}
	 * 为{@code null},则返回{@code"null"}。
	 * 
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of {@code array}
	 */
	public static String nullSafeToString(long[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces ({@code "{}"}). Adjacent elements are separated
	 * by the characters {@code ", "} (a comma followed by a space). Returns
	 * {@code "null"} if {@code array} is {@code null}.
	 * <p>
	 * 返回指定数组的内容的字符串表示<p> String表示由数组元素的列表组成,括在花括号中({@code"{}"}))相邻元素由字符{@code ","}(后跟一个空格的逗号)如果{@code array}
	 * 为{@code null},则返回{@code"null"}。
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of {@code array}
	 */
	public static String nullSafeToString(short[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

}
