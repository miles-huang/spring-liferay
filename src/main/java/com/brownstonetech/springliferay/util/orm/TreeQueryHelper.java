package com.brownstonetech.springliferay.util.orm;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.liferay.portal.NoSuchModelException;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.BaseModel;
import com.liferay.portal.service.persistence.BasePersistence;

public class TreeQueryHelper<T extends BaseModel<T>, PK extends Serializable> {

	private Class<T> modelClass;
	private WeakReference<ClassLoader> classLoader;
	private BasePersistence<T> persistence;
	private String primaryKeyName;
	private String leftProperty;
	private Method leftPropertyReadMethod;
	private String rightProperty;
	private Method rightPropertyReadMethod;
	private String treeScopeProperty;
	private Method treeScopePropertyReadMethod;

	public TreeQueryHelper(Class<T> modelClass, ClassLoader classLoader, BasePersistence<T> persistence,
			String primaryKeyName, String leftProperty, String rightProperty) {
		this(modelClass, classLoader, persistence, primaryKeyName, leftProperty, rightProperty, "groupId");
	}

	public TreeQueryHelper(Class<T> modelClass, ClassLoader classLoader, BasePersistence<T> persistence,
			String primaryKeyName, String leftProperty, String rightProperty, String treeScopeProperty) {
		this.classLoader = new WeakReference<ClassLoader>(classLoader);
		this.modelClass = modelClass;
		this.persistence = persistence;
		this.primaryKeyName = primaryKeyName;
		this.leftProperty = leftProperty;
		this.rightProperty = rightProperty;
		this.treeScopeProperty = treeScopeProperty;
		try {
			leftPropertyReadMethod = new PropertyDescriptor(leftProperty, modelClass).getReadMethod();
			rightPropertyReadMethod = new PropertyDescriptor(rightProperty, modelClass).getReadMethod();
			treeScopePropertyReadMethod = new PropertyDescriptor(treeScopeProperty, modelClass).getReadMethod();
			new PropertyDescriptor(primaryKeyName, modelClass);
		} catch (IntrospectionException e) {
			throw new IllegalArgumentException("Fail to initalize TreeQueryHelper, one of the tree information property can't be addressed", e);
		}
	}
	
	private DynamicQuery createDynamicQuery() {
		ClassLoader classLoader = this.classLoader.get();
		if ( classLoader == null ) throw new IllegalStateException("The class loader for managing the entity "+modelClass+" has been unloaded.");
		DynamicQuery query = DynamicQueryFactoryUtil.forClass(modelClass,
				classLoader);
		return query;
	}

	private T findByPrimaryKey(PK primaryKey) throws NoSuchModelException, SystemException {
		return persistence.findByPrimaryKey(primaryKey);
	}

	private Object getValue(T modelObject, Method method) throws NoSuchModelException, SystemException {
		try {
			Object value = method.invoke(modelObject);
			return value;
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if ( t instanceof NoSuchModelException ) {
				throw (NoSuchModelException)t;
			}
			if ( t instanceof SystemException) {
				throw (SystemException)t;
			}
			throw new SystemException("Fail to access method "+method.getName()+" of model class "+modelObject.getClass()+": "+e.getMessage(), t);
		} catch (Exception e) {
			throw new SystemException("Fail to access method "+method.getName()+" of model class "+modelObject.getClass(), e);
		}
	}

	public DynamicQuery getDescendantsQuery(PK primaryKey,
			boolean containsSelf, OrderByComparator obc)
					throws NoSuchModelException, SystemException {
		T modelObject = findByPrimaryKey(primaryKey);
		Object treeScopeValue = getValue(modelObject, this.treeScopePropertyReadMethod);
		DynamicQuery query = createDynamicQuery();
		query.add(PropertyFactoryUtil.forName(this.treeScopeProperty).eq(treeScopeValue));

		if (containsSelf) {
			query.add(PropertyFactoryUtil.forName(this.leftProperty)
					.between(getValue(modelObject, this.leftPropertyReadMethod),
							getValue(modelObject,this.rightPropertyReadMethod)));
		}
		else {
			query.add(PropertyFactoryUtil.forName(this.leftProperty)
					.gt(getValue(modelObject, this.leftPropertyReadMethod)));
			query.add(PropertyFactoryUtil.forName(this.leftProperty)
					.lt(getValue(modelObject, this.rightPropertyReadMethod)));
		}

		if ((obc == null) || Validator.isNull(obc.getOrderBy())) {
			query.addOrder(OrderFactoryUtil.asc(this.leftProperty));
		}
		else {
			String orderBy = obc.getOrderBy();
			String[] parts = StringUtil.split(orderBy);

			for (String part : parts) {
				int y = part.indexOf(StringPool.SPACE);
				boolean asc = true;
				String fieldName = part;

				if (y != -1) {
					String orderPart = part.substring(y + 1, part.length())
							.toUpperCase();

					if (orderPart.endsWith("DESC")) {
						asc = false;
					}

					fieldName = part.substring(0, y);
				}

				query.addOrder(asc ? OrderFactoryUtil.asc(fieldName)
						: OrderFactoryUtil.desc(fieldName));
			}
		}

		return query;
	}

	public DynamicQuery getDescendantIdsQuery(PK primaryKey,
			boolean containsSelf, OrderByComparator obc)
					throws NoSuchModelException, SystemException {
		DynamicQuery query = getDescendantsQuery(primaryKey, containsSelf, obc);
		query.setProjection(ProjectionFactoryUtil.property(primaryKeyName));

		return query;
	}

	public List<T> findDescendants(PK primaryKey,
			boolean containsSelf) throws NoSuchModelException, SystemException {
		List<T> ret = findDescendants(primaryKey, containsSelf,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		return ret;
	}

	public List<T> findDescendants(PK primaryKey,
			boolean containsSelf, int start, int end)
					throws NoSuchModelException, SystemException {
		List<T> ret = findDescendants(primaryKey, containsSelf,
				start, end, null);

		return ret;
	}

	public List<T> findDescendants(PK primaryKey,
			boolean containsSelf, int start, int end, OrderByComparator obc)
					throws NoSuchModelException, SystemException {
		DynamicQuery query = getDescendantsQuery(primaryKey, containsSelf, obc);

		if ((start == QueryUtil.ALL_POS) || (end == QueryUtil.ALL_POS)) {
			@SuppressWarnings({ "unchecked" })
			List<T> ret = persistence.findWithDynamicQuery(query);
			return ret;
		}

		@SuppressWarnings({ "unchecked" })
		List<T> ret = persistence.findWithDynamicQuery(query, start, end);
		return ret;

	}

	public List<PK> findDescendantIds(PK primaryKey, boolean containsSelf)
			throws NoSuchModelException, SystemException {
		return findDescendantIds(primaryKey, containsSelf, null);
	}

	public List<PK> findDescendantIds(PK primaryKey, boolean containsSelf,
			OrderByComparator obc) throws NoSuchModelException, SystemException {
		DynamicQuery query = getDescendantIdsQuery(primaryKey, containsSelf, obc);

		@SuppressWarnings("unchecked")
		List<PK> ret = persistence.findWithDynamicQuery(query);
		return ret;
	}

	public int countDescendants(Serializable primaryKey)
			throws NoSuchModelException, SystemException {
		T modelObject = persistence.findByPrimaryKey(primaryKey);
		long rightId = (Long)getValue(modelObject, this.rightPropertyReadMethod);
		long leftId = (Long)getValue(modelObject, this.leftPropertyReadMethod);
		return (int)(rightId -leftId - 1) / 2;
	}

	public List<T> findAncestors(Serializable primaryKey)
			throws NoSuchModelException, SystemException {
		T modelObject = persistence.findByPrimaryKey(primaryKey);
		DynamicQuery query = createDynamicQuery();
		Object leftValue;
		Object rightValue;
		Object scopeValue;
		try {
			leftValue = leftPropertyReadMethod.invoke(modelObject);
			rightValue = rightPropertyReadMethod.invoke(modelObject);
			scopeValue = treeScopePropertyReadMethod.invoke(modelObject);
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if ( t instanceof NoSuchModelException ) {
				throw (NoSuchModelException)t;
			}
			if ( t instanceof SystemException) {
				throw (SystemException)t;
			}
			throw new SystemException("Fail to access one of the tree structure property from model class "+modelObject.getClass()+": "+e.getMessage(), t);
		} catch (Exception e) {
			throw new SystemException("Fail to access one of the tree structure property from model class "+modelObject.getClass(), e);
		}

		query.add(PropertyFactoryUtil.forName(treeScopeProperty).eq(scopeValue));
		query.add(PropertyFactoryUtil.forName(leftProperty)
				.lt(leftValue));
		query.add(PropertyFactoryUtil.forName(rightProperty)
				.gt(rightValue));
		query.addOrder(OrderFactoryUtil.asc(leftProperty));

		@SuppressWarnings("unchecked")
		List<T> ret = persistence.findWithDynamicQuery(query);

		return ret;
	}

}
