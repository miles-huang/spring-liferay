/**
 * 
 * SURWING CONFIDENTIAL
 * 
 * Copyright (c) 2009-2013 Surwing Incorporated
 * All rights reserved.
 * 
 * NOTICE:  All information contained herein is, and remains the property
 * of Surwing Information Technology Inc. and its suppliers, if any. The
 * intellectual and technical concepts contained herein are proprietary to
 * Surwing Information Technology Inc. and its suppliers and may be covered
 * by China and Foreign Patents, patents in process, and are protected by
 * trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is
 * strictly forbidden unless prior written permission is obtained from
 * Surwing Information Technology Inc.
 *
 */

package com.brownstonetech.springliferay.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PrimitiveArrayUtil {

	public static List<Long> asList(long ... array) {
		if ( array == null ) return Collections.emptyList();
		List<Long> ret = new ArrayList<Long>(array.length);
		for ( long element: array) {
			ret.add(element);
		}
		return ret;
	}
	
	public static long[] toArray(Collection<Long> collection, Long defaultValue) {
		if ( collection == null ) return new long[0];
		long[] ret = new long[collection.size()];
		int count = 0;
		for (Long element: collection) {
			if ( element == null ) {
				if ( defaultValue == null ) continue;
				element = defaultValue;
			}
			ret[count++]=element;
		}
		if ( count == ret.length) {
			return ret;
		}
		return Arrays.copyOf(ret, count);
	}

	public static List<Integer> asList(int ... array) {
		if ( array == null ) return Collections.emptyList();
		List<Integer> ret = new ArrayList<Integer>(array.length);
		for ( int element: array) {
			ret.add(element);
		}
		return ret;
	}
	
	public static int[] toArray(Collection<Integer> collection, Integer defaultValue) {
		if ( collection == null ) return new int[0];
		int[] ret = new int[collection.size()];
		int count = 0;
		for (Integer element: collection) {
			if ( element == null ) {
				if (defaultValue == null) continue;
				element = defaultValue;
			}
			ret[count++]=element;
		}
		if ( count == ret.length) {
			return ret;
		}
		return Arrays.copyOf(ret, count);
	}

	public static List<Character> asList(char ... array) {
		if ( array == null ) return Collections.emptyList();
		List<Character> ret = new ArrayList<Character>(array.length);
		for ( char element: array) {
			ret.add(element);
		}
		return ret;
	}
	
	public static char[] toArray(Collection<Character> collection, Character defaultValue) {
		if ( collection == null ) return new char[0];
		char[] ret = new char[collection.size()];
		int count = 0;
		for (Character element: collection) {
			if ( element == null ) {
				if (defaultValue == null) continue;
				element = defaultValue;
			}
			ret[count++]=element;
		}
		if ( count == ret.length) {
			return ret;
		}
		return Arrays.copyOf(ret, count);
	}
	
	public static List<Short> asList(short ... array) {
		if ( array == null ) return Collections.emptyList();
		List<Short> ret = new ArrayList<Short>(array.length);
		for ( short element: array) {
			ret.add(element);
		}
		return ret;
	}
	
	public static short[] toArray(Collection<Short> collection, Short defaultValue) {
		if ( collection == null ) return new short[0];
		short[] ret = new short[collection.size()];
		int count = 0;
		for (Short element: collection) {
			if ( element == null ) {
				if (defaultValue == null) continue;
				element = defaultValue;
			}
			ret[count++]=element;
		}
		if ( count == ret.length) {
			return ret;
		}
		return Arrays.copyOf(ret, count);
	}
	
	public static List<Byte> asList(byte ... array) {
		if ( array == null ) return Collections.emptyList();
		List<Byte> ret = new ArrayList<Byte>(array.length);
		for ( byte element: array) {
			ret.add(element);
		}
		return ret;
	}
	
	public static byte[] toArray(Collection<Byte> collection, Byte defaultValue) {
		if ( collection == null ) return new byte[0];
		byte[] ret = new byte[collection.size()];
		int count = 0;
		for (Byte element: collection) {
			if ( element == null ) {
				if (defaultValue == null) continue;
				element = defaultValue;
			}
			ret[count++]=element;
		}
		if ( count == ret.length) {
			return ret;
		}
		return Arrays.copyOf(ret, count);
	}

	public static List<Boolean> asList(boolean ... array) {
		if ( array == null ) return Collections.emptyList();
		List<Boolean> ret = new ArrayList<Boolean>(array.length);
		for ( boolean element: array) {
			ret.add(element);
		}
		return ret;
	}
	
	public static boolean[] toArray(Collection<Boolean> collection, Boolean defaultValue) {
		if ( collection == null ) return new boolean[0];
		boolean[] ret = new boolean[collection.size()];
		int count = 0;
		for (Boolean element: collection) {
			if ( element == null ) {
				if (defaultValue == null) continue;
				element = defaultValue;
			}
			ret[count++]=element;
		}
		if ( count == ret.length) {
			return ret;
		}
		return Arrays.copyOf(ret, count);
	}

	public static List<Double> asList(double ... array) {
		if ( array == null ) return Collections.emptyList();
		List<Double> ret = new ArrayList<Double>(array.length);
		for ( double element: array) {
			ret.add(element);
		}
		return ret;
	}
	
	public static double[] toArray(Collection<Double> collection, Double defaultValue) {
		if ( collection == null ) return new double[0];
		double[] ret = new double[collection.size()];
		int count = 0;
		for (Double element: collection) {
			if ( element == null ) {
				if (defaultValue == null) continue;
				element = defaultValue;
			}
			ret[count++]=element;
		}
		if ( count == ret.length) {
			return ret;
		}
		return Arrays.copyOf(ret, count);
	}
	
	public static List<Float> asList(float ... array) {
		if ( array == null ) return Collections.emptyList();
		List<Float> ret = new ArrayList<Float>(array.length);
		for ( float element: array) {
			ret.add(element);
		}
		return ret;
	}
	
	public static float[] toArray(Collection<Float> collection, Float defaultValue) {
		if ( collection == null ) return new float[0];
		float[] ret = new float[collection.size()];
		int count = 0;
		for (Float element: collection) {
			if ( element == null ) {
				if (defaultValue == null) continue;
				element = defaultValue;
			}
			ret[count++]=element;
		}
		if ( count == ret.length) {
			return ret;
		}
		return Arrays.copyOf(ret, count);
	}

	public static List<String> arrayToStringList(Object array) {
		if ( array == null ) return null;
		if ( !array.getClass().isArray() ) return null;
		Class<?> componentType = array.getClass().getComponentType();
		if ( componentType == null) return null;
		if ( String.class.isAssignableFrom(componentType)) {
			List<String> ret = Arrays.asList((String[])array);
			return ret;
		}
		List<?> list = null;
		if (Object.class.isAssignableFrom(componentType)) {
			list = Arrays.asList((Object[])array);
		} else if ( long.class.isAssignableFrom(componentType) ) {
			list = asList((long[])array);
		} else if ( int.class.isAssignableFrom(componentType) ) {
			list = asList((int[])array);
		} else if ( char.class.isAssignableFrom(componentType ) ) {
			list = asList((char[])array);
		} else if ( short.class.isAssignableFrom(componentType ) ) {
			list = asList((short[])array);
		} else if ( byte.class.isAssignableFrom(componentType)) {
			list = asList((byte[])array);
		} else if ( boolean.class.isAssignableFrom(componentType)) {
			list = asList((boolean[])array);
		} else if ( double.class.isAssignableFrom(componentType)) {
			list = asList((double[])array);
		} else if ( float.class.isAssignableFrom(componentType)) {
			list = asList((float[])array);
		}
		if ( list == null ) {
			return null;
		}
		List<String> ret = new ArrayList<String>(list.size());
		for ( Object e: list ) {
			if ( e == null ) continue;
			ret.add(String.valueOf(e));
		}
		return ret;
	}
	
}
