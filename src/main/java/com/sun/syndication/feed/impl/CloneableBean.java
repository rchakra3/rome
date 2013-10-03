/*
 * Copyright 2004 Sun Microsystems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.sun.syndication.feed.impl;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Provides deep <b>Bean</b> clonning support.
 * <p>
 * It works on all read/write properties, recursively. It support all primitive
 * types, Strings, Collections, Cloneable objects and multi-dimensional arrays
 * of any of them.
 * <p>
 * 
 * @author Alejandro Abdelnur
 * 
 */
public class CloneableBean implements Serializable, Cloneable {

    private static final Class[] NO_PARAMS_DEF = new Class[0];
    private static final Object[] NO_PARAMS = new Object[0];

    private final Object obj;
    private Set ignoreProperties;

    /**
     * Default constructor.
     * <p>
     * To be used by classes extending CloneableBean only.
     * <p>
     * 
     */
    protected CloneableBean() {
        obj = this;
    }

    /**
     * Creates a CloneableBean to be used in a delegation pattern.
     * <p>
     * For example:
     * <p>
     * <code>
     *   public class Foo implements Cloneable {
     *       private CloneableBean cloneableBean;
     * 
     *       public Foo() {
     *           cloneableBean = new CloneableBean(this);
     *       }
     * 
     *       public Object clone() throws CloneNotSupportedException {
     *           return cloneableBean.beanClone();
     *       }
     * 
     *   }
     * </code>
     * <p>
     * 
     * @param obj object bean to clone.
     * 
     */
    public CloneableBean(final Object obj) {
        this(obj, null);
    }

    /**
     * Creates a CloneableBean to be used in a delegation pattern.
     * <p>
     * The property names in the ignoreProperties Set will not be copied into
     * the cloned instance. This is useful for cases where the Bean has
     * convenience properties (properties that are actually references to other
     * properties or properties of properties). For example SyndFeed and
     * SyndEntry beans have convenience properties, publishedDate, author,
     * copyright and categories all of them mapped to properties in the DC
     * Module.
     * <p>
     * 
     * @param obj object bean to clone.
     * @param ignoreProperties properties to ignore when cloning.
     * 
     */
    public CloneableBean(final Object obj, final Set ignoreProperties) {
        this.obj = obj;
        this.ignoreProperties = ignoreProperties != null ? ignoreProperties : Collections.EMPTY_SET;
    }

    /**
     * Makes a deep bean clone of the object.
     * <p>
     * To be used by classes extending CloneableBean. Although it works also for
     * classes using CloneableBean in a delegation pattern, for correctness
     * those classes should use the
     * 
     * @see #beanClone() beanClone method.
     *      <p>
     * @return a clone of the object bean.
     * @throws CloneNotSupportedException thrown if the object bean could not be
     *             cloned.
     * 
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return beanClone();
    }

    /**
     * Makes a deep bean clone of the object passed in the constructor.
     * <p>
     * To be used by classes using CloneableBean in a delegation pattern,
     * 
     * @see #CloneableBean(Object) constructor.
     * 
     * @return a clone of the object bean.
     * @throws CloneNotSupportedException thrown if the object bean could not be
     *             cloned.
     * 
     */
    public Object beanClone() throws CloneNotSupportedException {
        Object clonedBean;
        try {
            clonedBean = obj.getClass().newInstance();
            final PropertyDescriptor[] pds = BeanIntrospector.getPropertyDescriptors(obj.getClass());
            if (pds != null) {
                for (int i = 0; i < pds.length; i++) {
                    final Method pReadMethod = pds[i].getReadMethod();
                    final Method pWriteMethod = pds[i].getWriteMethod();
                    if (pReadMethod != null && pWriteMethod != null && // ensure
                                                                       // it has
                                                                       // getter
                                                                       // and
                                                                       // setter
                                                                       // methods
                            !ignoreProperties.contains(pds[i].getName()) && // is
                            // not
                            // in
                            // the
                            // list
                            // of
                            // properties
                            // to
                            // ignore
                            pReadMethod.getDeclaringClass() != Object.class && // filter
                                                                               // Object.class
                                                                               // getter
                                                                               // methods
                            pReadMethod.getParameterTypes().length == 0) { // filter
                                                                           // getter
                                                                           // methods
                                                                           // that
                                                                           // take
                                                                           // parameters
                        Object value = pReadMethod.invoke(obj, NO_PARAMS);
                        if (value != null) {
                            value = doClone(value);
                            pWriteMethod.invoke(clonedBean, new Object[] { value });
                        }
                    }
                }
            }
        } catch (final CloneNotSupportedException cnsEx) {
            throw cnsEx;
        } catch (final Exception ex) {
            System.out.println(ex);
            ex.printStackTrace(System.out);
            throw new CloneNotSupportedException("Cannot clone a " + obj.getClass() + " object");
        }
        return clonedBean;
    }

    private Object doClone(Object value) throws Exception {
        if (value != null) {
            final Class vClass = value.getClass();
            if (vClass.isArray()) {
                value = cloneArray(value);
            } else if (value instanceof Collection) {
                value = cloneCollection((Collection) value);
            } else if (value instanceof Map) {
                value = cloneMap((Map) value);
            } else if (isBasicType(vClass)) {
                // NOTHING SPECIAL TO DO HERE, THEY ARE INMUTABLE
            } else if (value instanceof Cloneable) {
                final Method cloneMethod = vClass.getMethod("clone", NO_PARAMS_DEF);
                if (Modifier.isPublic(cloneMethod.getModifiers())) {
                    value = cloneMethod.invoke(value, NO_PARAMS);
                } else {
                    throw new CloneNotSupportedException("Cannot clone a " + value.getClass() + " object, clone() is not public");
                }
            } else {
                throw new CloneNotSupportedException("Cannot clone a " + vClass.getName() + " object");
            }
        }
        return value;
    }

    private Object cloneArray(final Object array) throws Exception {
        final Class elementClass = array.getClass().getComponentType();
        final int length = Array.getLength(array);
        final Object newArray = Array.newInstance(elementClass, length);
        for (int i = 0; i < length; i++) {
            final Object element = doClone(Array.get(array, i));
            Array.set(newArray, i, element);
        }
        return newArray;
    }

    private Object cloneCollection(final Collection collection) throws Exception {
        final Class mClass = collection.getClass();
        final Collection newColl = (Collection) mClass.newInstance();
        final Iterator i = collection.iterator();
        while (i.hasNext()) {
            final Object element = doClone(i.next());
            newColl.add(element);
        }
        return newColl;
    }

    private Object cloneMap(final Map map) throws Exception {
        final Class mClass = map.getClass();
        final Map newMap = (Map) mClass.newInstance();
        final Iterator entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            final Map.Entry entry = (Map.Entry) entries.next();
            final Object key = doClone(entry.getKey());
            final Object value = doClone(entry.getValue());
            newMap.put(key, value);
        }
        return newMap;
    }

    private static final Set BASIC_TYPES = new HashSet();

    static {
        BASIC_TYPES.add(Boolean.class);
        BASIC_TYPES.add(Byte.class);
        BASIC_TYPES.add(Character.class);
        BASIC_TYPES.add(Double.class);
        BASIC_TYPES.add(Float.class);
        BASIC_TYPES.add(Integer.class);
        BASIC_TYPES.add(Long.class);
        BASIC_TYPES.add(Short.class);
        BASIC_TYPES.add(String.class);
    }

    private static final Map CONSTRUCTOR_BASIC_TYPES = new HashMap();

    static {
        CONSTRUCTOR_BASIC_TYPES.put(Boolean.class, new Class[] { Boolean.TYPE });
        CONSTRUCTOR_BASIC_TYPES.put(Byte.class, new Class[] { Byte.TYPE });
        CONSTRUCTOR_BASIC_TYPES.put(Character.class, new Class[] { Character.TYPE });
        CONSTRUCTOR_BASIC_TYPES.put(Double.class, new Class[] { Double.TYPE });
        CONSTRUCTOR_BASIC_TYPES.put(Float.class, new Class[] { Float.TYPE });
        CONSTRUCTOR_BASIC_TYPES.put(Integer.class, new Class[] { Integer.TYPE });
        CONSTRUCTOR_BASIC_TYPES.put(Long.class, new Class[] { Long.TYPE });
        CONSTRUCTOR_BASIC_TYPES.put(Short.class, new Class[] { Short.TYPE });
        CONSTRUCTOR_BASIC_TYPES.put(String.class, new Class[] { String.class });
    }

    private boolean isBasicType(final Class vClass) {
        return BASIC_TYPES.contains(vClass);
    }

    // THIS IS NOT NEEDED, BASIC TYPES ARE INMUTABLE, TUCU 20040710
    /**
     * private Object cloneBasicType(Object value) throws Exception { Class
     * pClass = value.getClass(); Class[] defType = (Class[])
     * CONSTRUCTOR_BASIC_TYPES.get(pClass); Constructor cons =
     * pClass.getDeclaredConstructor(defType); value = cons.newInstance(new
     * Object[]{value}); return value; }
     **/

}