package nl.ipo.cds.etl.postvalidation;

import nl.ipo.cds.etl.PersistableFeature;
import org.deegree.geometry.Geometry;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * Geometry Store to temporarily store possible overlapping geometries.
 */
public interface IGeometryStore<T extends Serializable> {

    public void createStore(final String uuId) throws SQLException;

    public void addToStore(final String uuId, Geometry geometry, T feature);

    public void destroyStore(final String uuId);
}
