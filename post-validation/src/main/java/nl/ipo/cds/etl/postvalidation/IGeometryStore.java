package nl.ipo.cds.etl.postvalidation;

import com.vividsolutions.jts.io.ParseException;
import org.deegree.geometry.Geometry;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Geometry Store to temporarily store possible overlapping geometries.
 */
public interface IGeometryStore {

    public DataSource createStore(final String uuId) throws SQLException;

    public DataSource loadStore(final String uuId) throws SQLException;

    public void addToStore(final DataSource dataSource, Geometry geometry, String identifier, String localId) throws
            SQLException,
            ParseException, IOException;

    public void destroyStore(final DataSource dataSource);

}
