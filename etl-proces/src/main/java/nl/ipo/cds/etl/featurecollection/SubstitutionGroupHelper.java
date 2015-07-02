/**
 * 
 */
package nl.ipo.cds.etl.featurecollection;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.deegree.feature.types.AppSchema;

/**
 * Helper that checks for FeatureCollection and Feature xml types
 * 
 * @author reinoldp
 * 
 */
public final class SubstitutionGroupHelper {

	private static final QName FC_TYPE = new QName("http://www.opengis.net/gml",
			"_FeatureCollection");
	
	private static final QName FEATURE_MEMBER_TYPE = new QName("http://www.opengis.net/gml",
			"_Feature");

	public static boolean isFeatureCollectionType(
			final XMLStreamReader streamReader,
			final AppSchema applicationSchema) {
		boolean b = streamReader.isStartElement()
				&& (streamReader.getNamespaceURI().equals(
						WFSResponseReader.WFS_NS) || WFSResponseReader.GML_NAMESPACES
						.contains(streamReader.getNamespaceURI()))
				&& streamReader.getLocalName().equals("FeatureCollection");
		return b || isSubstitution(streamReader, applicationSchema, FC_TYPE);

	}

	private static boolean isSubstitution(final XMLStreamReader streamReader,
			final AppSchema applicationSchema, QName name) {
		// check substitution
		org.deegree.feature.types.FeatureType gmlFt = applicationSchema
				.getFeatureType(name);
		org.deegree.feature.types.FeatureType appFt = applicationSchema
				.getFeatureType(streamReader.getName());
		if (gmlFt != null && appFt != null) {
			return applicationSchema.isSubType(gmlFt, appFt);
		}
		return false;
	}

	public static boolean isFeatureMemberType(XMLStreamReader streamReader,
			AppSchema applicationSchema) {
		boolean b = streamReader.isStartElement()
				&& (WFSResponseReader.GML_NAMESPACES
						.contains(streamReader.getNamespaceURI()))
				&& streamReader.getLocalName().equals("FeatureMember");
		return b || isSubstitution(streamReader, applicationSchema, FEATURE_MEMBER_TYPE);
	}

}
