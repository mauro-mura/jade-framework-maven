/*****************************************************************
 WADE - Workflow and Agent Development Environment is a framework to develop 
 multi-agent systems able to execute tasks defined according to the workflow
 metaphor.
 Copyright (C) 2008 Telecom Italia S.p.A. 

 GNU Lesser General Public License

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation, 
 version 2.1 of the License. 

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 Boston, MA  02111-1307, USA.
 *****************************************************************/
package jade.content.onto;

//#APIDOC_EXCLUDE_FILE

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jade.content.schema.AggregateSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.TermSchema;

public class AggregateHelper {

	private static final int ACC_ABSTRACT = 0x0400;
	private static final int ACC_INTERFACE = 0x0200;

	/**
	 * Get ontology schema associated to class
	 * Try to manage as aggregate
	 * 
	 * @param clazz class to get schema
	 * @param elementSchema aggregate element schema   
	 * @return associated class schema
	 */
	public static ObjectSchema getSchema(Class<?> clazz, TermSchema elementSchema) {
		ObjectSchema schema = null;
		
		// Sequence type
		if (java.util.List.class.isAssignableFrom(clazz) ||
			java.util.List.class.isAssignableFrom(clazz) ||
			(clazz.isArray() && clazz != byte[].class)) {

			schema = new AggregateSchema(BasicOntology.SEQUENCE, elementSchema);
		}

		// Set type
		else if (java.util.Set.class.isAssignableFrom(clazz) ||
			java.util.Set.class.isAssignableFrom(clazz)) {
			
			schema = new AggregateSchema(BasicOntology.SET, elementSchema);
		}

		return schema;
	}

	/**
	 * Try to convert, if possible, the aggregate value srcValue into an instance of destClass
	 * Possible source and destination classes are java array, java collection and jade collection 
	 * @throws Exception 
	 */
	public static Object adjustAggregateValue(Object srcValue, Class<?> destClass) throws Exception {
		Object destValue = srcValue;
		if (srcValue != null) {
			Class<?> srcClass = srcValue.getClass();
			if (srcClass != destClass) {
				
				// Destination is an array
				if (destClass.isArray()) {
					
					// Source is a java collection
					if (Collection.class.isAssignableFrom(srcClass)) {
						Collection javaCollection = (Collection)srcValue;
						destValue = collectionToArray(javaCollection.iterator(), destClass.getComponentType(), javaCollection.size());
					}
					
					// Source is a jade collection
					else if (Collection.class.isAssignableFrom(srcClass)) {
						Collection jadeCollection = (Collection)srcValue;
						destValue = collectionToArray(jadeCollection.iterator(), destClass.getComponentType(), jadeCollection.size());
					}
				}
				
				// Destination is a java collection
				else if (Collection.class.isAssignableFrom(destClass)) {

					// Source is an array
					if (srcClass.isArray()) {
						Collection javaCollection = createConcreteJavaCollection(destClass);
						int size = Array.getLength(srcValue);
						for (int index=0; index<size; index++) {
							javaCollection.add(Array.get(srcValue, index));
						}
						destValue = javaCollection;
					}
					
					// Source is a jade collection
					else if (Collection.class.isAssignableFrom(srcClass)) {
						Collection javaCollection = createConcreteJavaCollection(destClass);
						Collection jadeCollection = (Collection)srcValue;
						Iterator it = jadeCollection.iterator();
						while(it.hasNext()) {
							javaCollection.add(it.next());
						}
						destValue = javaCollection;
					}
				}
				
				// Destination is a jade collection
//				else if (jade.util.leap.Collection.class.isAssignableFrom(destClass)) {
//
//					// Source is an array
//					if (srcClass.isArray()) {
//						Collection jadeCollection = createConcreteJadeCollection(destClass);
//						int size = Array.getLength(srcValue);
//						for (int index=0; index<size; index++) {
//							jadeCollection.add(Array.get(srcValue, index));
//						}
//						destValue = jadeCollection;
//					}
//					
//					// Source is a java collection
//					else if (java.util.Collection.class.isAssignableFrom(srcClass)) {
//						Collection jadeCollection = createConcreteJadeCollection(destClass);
//						Collection javaCollection = (Collection)srcValue;
//						Iterator it = javaCollection.iterator();
//						while(it.hasNext()) {
//							jadeCollection.add(it.next());
//						}
//						destValue = jadeCollection;
//					}
//				}
			}
		}
		return destValue;
	}
	
	private static Object collectionToArray(Iterator it, Class<?> componentTypeClass, int size) {
		int index = 0;
		Object array = Array.newInstance(componentTypeClass, size);
		while(it.hasNext()) {
			Object item = it.next();
			Array.set(array, index, item);
			index++;
		}
		return array;
	}
	
	static Collection createConcreteJavaCollection(Class<?> clazz) throws InstantiationException, IllegalAccessException {
		int modifiers = clazz.getModifiers();
		Collection result = null;
		if ((modifiers & ACC_ABSTRACT) == 0 && (modifiers & ACC_INTERFACE) == 0) {
			// class is concrete, we can instantiate it directly
			result = (Collection) clazz.newInstance();
		} else {
			// class is either abstract or an interface, we have to somehow choose a concrete collection :-(
			if (List.class.isAssignableFrom(clazz)) {
				result = new ArrayList<>(); 
			} else if (Set.class.isAssignableFrom(clazz)) {
				result = new HashSet<>();
			}
		}
		return result;
	}

	static Collection createConcreteJadeCollection(Class<?> clazz) throws InstantiationException, IllegalAccessException {
		int modifiers = clazz.getModifiers();
		Collection result = null;
		if ((modifiers & ACC_ABSTRACT) == 0 && (modifiers & ACC_INTERFACE) == 0) {
			// class is concrete, we can instantiate it directly
			result = (Collection) clazz.newInstance();
		} else {
			// class is either abstract or an interface, we have to somehow choose a concrete collection :-(
			if (List.class.isAssignableFrom(clazz)) {
				result = new ArrayList<>(); 
			} else if (Set.class.isAssignableFrom(clazz)) {
				result = new HashSet<>();
			}
		}
		return result;
	}
}
