/*
 * Copyright (c) 2007 the original author or authors.
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
 *
 */

package org.jpublish.module.cayenne;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.DeleteDenyException;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.Query;

import java.util.List;
import java.util.Map;

/**
 * Just publishing the methods of the CayenneTemplate
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Sep 23, 2007 3:28:28 PM)
 */
public class JPCayenneService extends CayenneTemplate {
    /**
     * Instantiates new object and registers it with itself. Object class must
     * have a default constructor.
     *
     * @param dataObjectClass the data object class to create and register
     * @return the new registered data object
     */
    public Persistent createAndRegisterNewObject(Class dataObjectClass) {
        return super.createAndRegisterNewObject(dataObjectClass);
    }

    /**
     * Commit any changes in the thread local DataContext.
     */
    public void commitChanges() {
        super.commitChanges();
    }

    /**
     * Schedules an object for deletion on the next commit of this DataContext. Object's
     * persistence state is changed to PersistenceState.DELETED; objects related to this
     * object are processed according to delete rules, i.e. relationships can be unset
     * ("nullify" rule), deletion operation is cascaded (cascade rule).
     *
     * @param dataObject a persistent data object that we want to delete
     * @throws org.apache.cayenne.DeleteDenyException
     *          if a DENY delete rule
     *          is applicable for object deletion
     */
    public void deleteObject(DataObject dataObject) throws DeleteDenyException {
        super.deleteObject(dataObject);
    }

    /**
     * Find the data object for the specified class, property name and property
     * value, or null if no data object was found.
     *
     * @param dataObjectClass the data object class to find
     * @param property        the name of the property
     * @param value           the value of the property
     * @return the data object for the specified class, property name and property value
     * @throws RuntimeException if more than one data object was identified for the
     *                          given property name and value
     */
    public DataObject findObject(Class dataObjectClass, String property,
                                 Object value) {

        return super.findObject(dataObjectClass, property, value);
    }

    /**
     * Return the thread local DataContext.
     *
     * @return the thread local DataContext
     * @throws IllegalStateException if there is no DataContext bound to the current thread
     */
    public DataContext getDataContext() {
        return super.getDataContext();
    }

    /**
     * Perform a database query returning the data object specified by the
     * class and the primary key. This method will perform a database query
     * and refresh the object cache.
     *
     * @param doClass the data object class to retrieve
     * @param id      the data object primary key
     * @return the data object for the given class and id
     */
    public DataObject getObjectForPK(Class doClass, Object id) {
        return super.getObjectForPK(doClass, id);
    }

    /**
     * Perform a query returning the data object specified by the
     * class and the primary key value. If the refresh parameter is true a
     * database query will be performed, otherwise the a query against the
     * object cache will be performed first.
     *
     * @param dataObjectClass the data object class to retrieve
     * @param id              the data object primary key
     * @param refresh         the refresh the object cache mode
     * @return the data object for the given class and id
     */
    public DataObject getObjectForPK(Class dataObjectClass, Object id, boolean refresh) {
        return super.getObjectForPK(dataObjectClass, id, refresh);
    }

    /**
     * Return the database primary key column name for the given data object.
     *
     * @param dataObjectClass the class of the data object
     * @return the primary key column name
     */
    public String getPkName(Class dataObjectClass) {
        return super.getPkName(dataObjectClass);
    }

    /**
     * Performs a single selecting query. Various query setting control the behavior of
     * this method and the results returned:
     * <ul>
     * <li>Query caching policy defines whether the results are retrieved from cache or
     * fetched from the database. Note that queries that use caching must have a name that
     * is used as a caching key.
     * </li>
     * <li>Query refreshing policy controls whether to refresh existing data objects and
     * ignore any cached values.
     * </li>
     * <li>Query data rows policy defines whether the result should be returned as
     * DataObjects or DataRows.
     * </li>
     * </ul>
     *
     * @param query the query to perform
     * @return a list of DataObjects or a DataRows for the query
     */
    public List performQuery(Query query) {
        return super.performQuery(query);
    }

    /**
     * Returns a list of objects or DataRows for a named query stored in one of the
     * DataMaps. Internally Cayenne uses a caching policy defined in the named query. If
     * refresh flag is true, a refresh is forced no matter what the caching policy is.
     *
     * @param queryName a name of a GenericSelectQuery defined in one of the DataMaps. If
     *                  no such query is defined, this method will throw a CayenneRuntimeException
     * @param refresh   A flag that determines whether refresh of <b>cached lists</b>
     *                  is required in case a query uses caching.
     * @return the list of data object or DataRows for the named query
     */
    public List performQuery(String queryName, boolean refresh) {
        return super.performQuery(queryName, refresh);
    }

    /**
     * Returns a list of objects or DataRows for a named query stored in one of the
     * DataMaps. Internally Cayenne uses a caching policy defined in the named query. If
     * refresh flag is true, a refresh is forced no matter what the caching policy is.
     *
     * @param queryName  a name of a GenericSelectQuery defined in one of the DataMaps. If
     *                   no such query is defined, this method will throw a CayenneRuntimeException
     * @param parameters A map of parameters to use with stored query
     * @param refresh    A flag that determines whether refresh of <b>cached lists</b>
     *                   is required in case a query uses caching.
     * @return the list of data object or DataRows for the named query
     */
    public List performQuery(String queryName, Map parameters, boolean refresh) {

        return super.performQuery(queryName, parameters, refresh);
    }

    /**
     * Return a list of data object of the specified class for the given property
     * and value.
     *
     * @param dataObjectClass the data object class to return
     * @param property        the name of the property to select
     * @param value           the property value to select
     * @return a list of data objects for the given class and property name and value
     */
    public List performQuery(Class dataObjectClass, String property, Object value) {

        return super.performQuery(dataObjectClass, property, value);
    }

    /**
     * Performs a single database query that does not select rows. Returns an
     * array of update counts.
     *
     * @param query the query to perform
     * @return the array of update counts
     */
    public int[] performNonSelectingQuery(Query query) {
        return super.performNonSelectingQuery(query);
    }

    /**
     * Performs a named mapped query that does not select rows. Returns an array
     * of update counts.
     *
     * @param queryName the name of the query to perform
     * @return the array of update counts
     */
    public int[] performNonSelectingQuery(String queryName) {
        return super.performNonSelectingQuery(queryName);
    }

    /**
     * Performs a named mapped non-selecting query using a map of parameters.
     * Returns an array of update counts.
     *
     * @param queryName  the name of the query to perform
     * @param parameters the Map of query paramater names and values
     * @return the array of update counts
     */
    public int[] performNonSelectingQuery(String queryName, Map parameters) {
        return super.performNonSelectingQuery(queryName, parameters);
    }

    /**
     * Registers a transient object with the context, recursively registering all
     * transient DataObjects attached to this object via relationships.
     *
     * @param dataObject new object that needs to be made persistent
     */
    public void registerNewObject(DataObject dataObject) {
        super.registerNewObject(dataObject);
    }

    /**
     * Reverts any changes that have occurred to objects registered in the
     * thread local DataContext.
     */
    public void rollbackChanges() {
        super.rollbackChanges();
    }

    /**
     * Return a Map containing the given key name and value.
     *
     * @param key   the map key name
     * @param value the map key value
     * @return a Map containing the given key name and value
     */
    public Map toMap(String key, Object value) {
        return super.toMap(key, value);
    }
}
