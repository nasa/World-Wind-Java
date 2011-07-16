/* Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.applications.wss;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.cache.*;
import gov.nasa.worldwind.database.*;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.servers.applications.ogc.OGCWebServiceOperation;
import gov.nasa.worldwind.servers.applications.ogc.wfs.OGCWebFeatureService;
import gov.nasa.worldwind.servers.http.HTTPRequest;
import gov.nasa.worldwind.util.*;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

/**
 * @author dcollins
 * @version $Id$
 */
public class WebShapeService extends OGCWebFeatureService
{
    protected static final String FEATURE_TYPE_ALL = "All";

    protected static final String WSS_TABLE_FEATURE = "FEATURE";
    protected static final String WSS_TABLE_PROPERTY = "PROPERTY";

    protected static final String WSS_SQL_QUERY_GET_FEATURE = "SELECT * FROM " + WSS_TABLE_FEATURE + " WHERE name = ?";

    protected FileStore fileStore;
    protected Map<String, WSSFeatureType> featureTypes = new HashMap<String, WSSFeatureType>();
    protected DatabaseConnectionPool connectionPool = null;

    public WebShapeService()
    {
    }

    public WebShapeService(AVList config)
    {
        super(config);
    }

    public FileStore getFileStore()
    {
        return this.fileStore;
    }

    public WSSFeatureType getFeatureType(String name)
    {
        return this.featureTypes.get(name);
    }

    public Iterable<WSSFeatureType> getAllFeatureTypes()
    {
        return this.featureTypes.values();
    }

    protected void loadFeatureTypes()
    {
        KMZFeatureType kmzFeatureType = new KMZFeatureType(FEATURE_TYPE_ALL, this.getFileStore(), this );
        this.featureTypes.put(FEATURE_TYPE_ALL, kmzFeatureType);
    }

    protected void connectToDatabase()
    {
        DatabaseConnectionPoolFactory factory = null;

        Object o = this.getValue(AVKey.DATABASE_CONNECTION_POOL_FACTORY_CLASS_NAME);
        if (!WWUtil.isEmpty(o) && o instanceof String)
        {
            String className = (String) o;
            Object of = WorldWind.createComponent(className);
            if (null != of && of instanceof DatabaseConnectionPoolFactory)
            {
                factory = (DatabaseConnectionPoolFactory) of;
            }
            else
            {
                String message = Logging.getMessage("generic.CannotCreateObject", className);
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }
        }

        if (null != factory)
        {
            try
            {
                this.connectionPool = factory.create(this);

                this.createTables(); // Only if tables do not exist already
            }
            catch (Throwable t)
            {
                String message = WWUtil.extractExceptionReason(t);
                Logging.logger().log(Level.SEVERE, message, t);
                throw new WWRuntimeException(message);
            }
        }
    }

    @Override
    protected void doStart()
    {
        this.connectToDatabase();

        Object o = this.getValue(AVKey.FILE_STORE_LOCATION);
        if (WWUtil.isEmpty(o))
        {
            String message = Logging.getMessage("nullValue.FileStorePathIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        String fileStorePath = (String) o;

        this.fileStore = new BasicDataFileStore(new File(fileStorePath));
        this.loadFeatureTypes(); // TODO: Periodically refresh the feature types and their named features.

        super.doStart();
    }

    @Override
    protected OGCWebServiceOperation createGetCapabilitiesOperation(HTTPRequest request)
    {
        return new WSSGetCapabilitiesOperation();
    }

    @Override
    protected OGCWebServiceOperation createGetFeatureOperation(HTTPRequest request)
    {
        return new WSSGetFeatureOperation();
    }

    @Override
    protected OGCWebServiceOperation createFeatureUploadOperation(HTTPRequest request)
    {
        return new WSSFeatureUploadOperation();
    }

    @Override
    protected void doStop()
    {
        if (null != this.connectionPool)
        {
            this.connectionPool.closeAllConnections();
        }
    }

    protected void createTables()
    {
        if (null == this.connectionPool)
        {
            String message = Logging.getMessage("nullValue.DatabaseIsNull");
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        Connection connection = null;
        Statement statement = null;

        try
        {
            connection = this.connectionPool.getConnection();

            if (!this.isTableExists(connection, WSS_TABLE_FEATURE))
            {
                statement = connection.createStatement();

                String sql = "CREATE TABLE " + WSS_TABLE_FEATURE + " ("
                    + " id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),\n"
                    + " name VARCHAR(1024) NOT NULL,\n"
                    + " url  VARCHAR(4096) NOT NULL,\n"
                    + " UNIQUE (name)\n"
                    + ")";

                statement.execute(sql);
                this.close(statement);
            }

            if (!this.isTableExists(connection, WSS_TABLE_PROPERTY))
            {
                statement = connection.createStatement();

                String sql = "CREATE TABLE " + WSS_TABLE_PROPERTY + " ("
                    + " id INTEGER,\n"
                    + " name VARCHAR(1024) NOT NULL,\n"
                    + " value  VARCHAR(4096) NOT NULL,\n"
                    + " FOREIGN KEY (id) REFERENCES " + WSS_TABLE_FEATURE + "(id) "
                    + ")";

                statement.execute(sql);
                this.close(statement);
            }
        }
        catch (Throwable t)
        {
            String message = WWUtil.extractExceptionReason(t);
            Logging.logger().log(Level.SEVERE, message, t);
        }
        finally
        {
            this.close(statement);
            this.connectionPool.releaseConnection(connection);
        }
    }

    protected boolean isTableExists(Connection connection, String tableName)
    {
        if (null == connection)
        {
            String message = Logging.getMessage("nullValue.ConnectionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (null == tableName)
        {
            String message = Logging.getMessage("nullValue.TableNameIsNullOrEmpty");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        boolean tableFound = false;
        ResultSet rs = null;
        try
        {
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getTables(null, null, tableName, new String[] {"TABLE"});
            tableFound = rs.next();
        }
        catch (Throwable t)
        {
            String message = WWUtil.extractExceptionReason(t);
            Logging.logger().log(Level.FINEST, message, t);
            tableFound = false;
        }
        finally
        {
            this.close(rs);
        }

        return tableFound;
    }

    protected int addFeature(String featureName, String featureFilename)
        throws IllegalArgumentException, SQLException, WWRuntimeException
    {
        if (null == this.connectionPool)
        {
            String message = Logging.getMessage("nullValue.DatabaseIsNull");
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        if (WWUtil.isEmpty(featureName))
        {
            String message = Logging.getMessage("nullValue.FeatureNameIsNullOrEmpty");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (WWUtil.isEmpty(featureFilename))
        {
            String message = Logging.getMessage("nullValue.FilenameIsNullOrEmpty");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try
        {
            connection = this.connectionPool.getConnection();
            statement = connection.createStatement();

            String sqlQuery = "INSERT INTO " + WSS_TABLE_FEATURE + " ( name , url ) "
                + " VALUES ( '" + featureName + "' , '" + featureFilename + "' )";
//            Logging.logger().info(sqlQuery);
            statement.execute(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            rs = statement.getGeneratedKeys();
            if (rs.next())
            {
                return rs.getInt(1); // returns FEATURE.id (the newly generated primary key)
            }
        }
        catch (SQLException sqle)
        {
            String message = WWUtil.extractExceptionReason(sqle);
            Logging.logger().log(Level.FINE, message, sqle);
            throw sqle;
        }
        catch (Throwable t)
        {
            String message = WWUtil.extractExceptionReason(t);
            Logging.logger().log(Level.SEVERE, message, t);
        }
        finally
        {
            this.close(rs);
            this.close(statement);
            this.connectionPool.releaseConnection(connection);
        }

        return 0; // TODO add real id
    }


    public String queryGetFeatureURL(String featureName)
    {
        if (null == this.connectionPool)
        {
            String message = Logging.getMessage("nullValue.DatabaseIsNull");
            Logging.logger().severe(message);
            return null;
        }

        if (WWUtil.isEmpty(featureName))
        {
            String message = Logging.getMessage("nullValue.FeatureNameIsNullOrEmpty");
            Logging.logger().severe(message);
            return null;
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try
        {
            connection = this.connectionPool.getConnection();
            statement = connection.prepareStatement(WSS_SQL_QUERY_GET_FEATURE);
            statement.setString(1, featureName);
            rs = statement.executeQuery();
            if (null != rs && rs.next())
            {
                return rs.getString("url");
            }
        }
        catch (SQLException sqle)
        {
            String message = WWUtil.extractExceptionReason(sqle);
            Logging.logger().log(Level.FINE, message, sqle);
        }
        catch (Throwable t)
        {
            String message = WWUtil.extractExceptionReason(t);
            Logging.logger().log(Level.SEVERE, message, t);
        }
        finally
        {
            this.close(rs);
            this.close(statement);
            this.connectionPool.releaseConnection(connection);
        }

        return null;
    }

    public Iterable<String> queryGetAllFeatureNames()
    {
        if (null == this.connectionPool)
        {
            String message = Logging.getMessage("nullValue.DatabaseIsNull");
            Logging.logger().severe(message);
            return null;
        }

        ArrayList<String> featureNames = new ArrayList<String>();

        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try
        {
            connection = this.connectionPool.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT name FROM " + WSS_TABLE_FEATURE );
            while (rs.next())
            {
                featureNames.add( rs.getString("name") );
            }
        }
        catch (SQLException sqle)
        {
            String message = WWUtil.extractExceptionReason(sqle);
            Logging.logger().log(Level.FINE, message, sqle);
        }
        catch (Throwable t)
        {
            String message = WWUtil.extractExceptionReason(t);
            Logging.logger().log(Level.SEVERE, message, t);
        }
        finally
        {
            this.close(rs);
            this.close(statement);
            this.connectionPool.releaseConnection(connection);
        }

        return featureNames;
    }

    /**
     * A helper method to close <code>java.sql.Statement</code>
     *
     * @param statement An instance of <code>java.sql.Statement</code>
     */
    protected void close(java.sql.Statement statement)
    {
        try
        {
            if (null != statement && !statement.isClosed())
                statement.close();
        }
        catch (Throwable t)
        {
            String message = WWUtil.extractExceptionReason(t);
            Logging.logger().log(Level.FINEST, message, t);
        }
    }

    /**
     * A helper method to close <code>java.sql.ResultSet</code> objects
     *
     * @param rs An instance of <code>java.sql.ResultSet</code>
     */
    protected void close(java.sql.ResultSet rs)
    {
        try
        {
            if (null != rs && !rs.isClosed())
                rs.close();
        }
        catch (Throwable t)
        {
            String message = WWUtil.extractExceptionReason(t);
            Logging.logger().log(Level.FINEST, message, t);
        }
    }
}
