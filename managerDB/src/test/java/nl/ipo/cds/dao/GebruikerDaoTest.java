package nl.ipo.cds.dao;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import nl.ipo.cds.categories.IntegrationTests;
import nl.ipo.cds.dao.impl.ManagerDaoImpl;
import nl.ipo.cds.domain.Bronhouder;
import nl.ipo.cds.domain.BronhouderThema;
import nl.ipo.cds.domain.DbGebruiker;
import nl.ipo.cds.domain.Gebruiker;
import nl.ipo.cds.domain.GebruikerThemaAutorisatie;
import nl.ipo.cds.domain.Thema;
import nl.ipo.cds.domain.TypeGebruik;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.test.LdapTestUtils;

@Category(IntegrationTests.class)
public class GebruikerDaoTest extends BaseManagerDaoTest {

	private static final DistinguishedName BASE_NAME = new DistinguishedName ("dc=inspire,dc=idgis,dc=eu");
	private static final String PRINCIPAL = "uid=admin,ou=system";
	private static final String CREDENTIALS = "secret";
	private static final int PORT = 10389;
	private static final String[] gebruikers = {
		"brabant",
		"drenthe",
		"flevoland",
		"fryslan",
		"gelderland",
		"groningen",
		"limburg",
		"noord-holland",
		"overijssel",
		"utrecht",
		"zeeland",
		"zuid-holland"
	};
	
	private LdapTemplate ldapTemplate;
	
	@BeforeClass
	public static void setUpClass () throws Exception {
		LdapTestUtils.startApacheDirectoryServer (PORT, BASE_NAME.toString (), "odm-test", PRINCIPAL, CREDENTIALS);
	}
	
	@AfterClass
	public static void tearDownClass () throws Exception {
		LdapTestUtils.destroyApacheDirectoryServer (PRINCIPAL, CREDENTIALS);
	}
	
    @Before @Override
    public void buildDB() throws Exception {
    	super.buildDB ();

    	
		// Bind to the LDAP directory:
		final LdapContextSource contextSource = new LdapContextSource ();
		contextSource.setUrl ("ldap://127.0.0.1:" + PORT + "/dc=inspire,dc=idgis,dc=eu");
		contextSource.setUserDn (PRINCIPAL);
		contextSource.setPassword (CREDENTIALS);
		contextSource.setPooled (false);
		contextSource.afterPropertiesSet ();
		
		// Create an LDAP template:
		ldapTemplate = new LdapTemplate (contextSource);
		
		LdapTestUtils.cleanAndSetup (ldapTemplate.getContextSource (), new DistinguishedName (), new ClassPathResource ("nl/ipo/cds/dao/testdata.ldif"));
		
		((ManagerDaoImpl)managerDao).setLdapTemplate (ldapTemplate);
		
        entityManager.flush ();
    }
    
    private void createGebruikerThemaAutorisatie () {
		// Create GebruikerThemaAutorisatie instances for testing:
        createGebruikerThemaAutorisatie ("Thema 2", "Overijssel", "overijssel", TypeGebruik.RAADPLEGER);
        createGebruikerThemaAutorisatie ("Protected sites", "Limburg", "limburg", TypeGebruik.RAADPLEGER);
        createGebruikerThemaAutorisatie ("Thema 2", "Noord-Holland", "noord-holland", TypeGebruik.RAADPLEGER);
        createGebruikerThemaAutorisatie ("Protected sites", "Drenthe", "drenthe", TypeGebruik.RAADPLEGER);
        
    	entityManager.flush ();
    }

	private GebruikerThemaAutorisatie createGebruikerThemaAutorisatie (final String themeName, final String bronhouderName, final String gebruikerName, final TypeGebruik typeGebruik) {
		final Gebruiker gebruiker = managerDao.getGebruiker (gebruikerName);
		
		// Create database backing for the user:
		managerDao.update (gebruiker);
		
		final BronhouderThema bronhouderThema = entityManager
			.createQuery ("from BronhouderThema bt where bt.bronhouder.naam = ?1 and bt.thema.naam = ?2", BronhouderThema.class)
			.setParameter (1, bronhouderName)
			.setParameter (2, themeName)
			.getSingleResult ();
		
		final GebruikerThemaAutorisatie gta = new GebruikerThemaAutorisatie (gebruiker.getDbGebruiker (), bronhouderThema, typeGebruik);
		
		entityManager.persist (gta);
		
		return gta;
	}
	
    // =========================================================================
    // Gebruiker CRUD:
    // =========================================================================
	@Test
	public void testGetGebruiker () {
		final Gebruiker gebruiker = managerDao.getGebruiker ("overijssel");
		
		assertNotNull (gebruiker);
		assertEquals ("uid=overijssel,ou=OtherPeople", gebruiker.getLdapGebruiker ().getDistinguishedName ());
		assertEquals ("overijssel", gebruiker.getGebruikersnaam ());
		assertEquals ("test@idgis.nl", gebruiker.getEmail ());
		assertNull (gebruiker.getMobile ());
		assertNotNull (gebruiker.getWachtwoordHash ());
	}
	
	@Test
	public void testGetGebruikerInvalid () {
		final Gebruiker gebruiker = managerDao.getGebruiker ("overijssl");
		
		assertNull (gebruiker);
	}
	
	@Test
	public void testGetAllGebruikers () {
		final List<Gebruiker> gebruikers = managerDao.getAllGebruikers ();
		
		assertEquals (GebruikerDaoTest.gebruikers.length, gebruikers.size ());
		
		final String[] names = new String[gebruikers.size ()];
		for (int i = 0; i < gebruikers.size (); ++ i) {
			names[i] = gebruikers.get (i).getGebruikersnaam ();
		}
		
		assertArrayEquals(GebruikerDaoTest.gebruikers, names);
	}
	
	@Test
	public void testCreateGebruiker () {
		final Gebruiker gebruiker = new Gebruiker ();
		
		gebruiker.setGebruikersnaam ("idgis");
		gebruiker.setEmail("test@idgis.nl");
		gebruiker.setMobile (null);
		gebruiker.setWachtwoord ("12test34");
		
		managerDao.create (gebruiker);
		
		final Gebruiker result = managerDao.getGebruiker ("idgis");
		
		assertNotNull (result);
		assertEquals ("idgis", result.getGebruikersnaam ());
		assertEquals ("test@idgis.nl", result.getEmail ());
		assertNull (result.getMobile ());
		assertNotNull (result.getWachtwoordHash ());
	}
	
	@Test(expected = RuntimeException.class)
	public void testCreateGebruikerInvalid () {
		final Gebruiker gebruiker = new Gebruiker ();
		gebruiker.setGebruikersnaam ("idgis");
		
		managerDao.create (gebruiker);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreateGebruikerDuplicate () {
		final Gebruiker gebruiker = new Gebruiker ();
		
		gebruiker.setGebruikersnaam ("overijssel");
		gebruiker.setEmail("test@idgis.nl");
		gebruiker.setMobile (null);
		gebruiker.setWachtwoord ("12test34");
		
		managerDao.create (gebruiker);
	}
	
	@Test
	public void testDeleteGebruiker () {
		final Gebruiker gebruiker = managerDao.getGebruiker ("overijssel");
		
		assertNotNull (gebruiker);
		
		managerDao.delete (gebruiker);
		
		final Gebruiker deletedGebruiker = managerDao.getGebruiker ("overijssel");
		
		assertNull (deletedGebruiker);
	}
	
	@Test(expected = RuntimeException.class)
	public void testDeleteGebruikerInvalid () {
		final Gebruiker gebruiker = new Gebruiker ();
		
		gebruiker.setGebruikersnaam ("idgis");
		gebruiker.setEmail ("test@idgis.nl");
		gebruiker.setWachtwoord ("12test34");
		
		managerDao.delete (gebruiker);
	}
	
	@Test 
	public void testUpdateGebruiker () {
		final Gebruiker gebruiker = managerDao.getGebruiker ("overijssel");
		
		assertNotNull (gebruiker);
		
		gebruiker.setEmail ("test2@idgis.nl");
		
		managerDao.update (gebruiker);
		
		final Gebruiker updatedGebruiker = managerDao.getGebruiker ("overijssel");
		
		assertNotNull (updatedGebruiker);
		assertEquals ("test2@idgis.nl", updatedGebruiker.getEmail ());
	}
	
	@Test(expected = RuntimeException.class) 
	public void testUpdateGebruikerInvalid () {
		final Gebruiker gebruiker = new Gebruiker ();
		
		gebruiker.setGebruikersnaam ("idgis");
		gebruiker.setEmail("test@idgis.nl");
		gebruiker.setWachtwoord ("12test34");
		
		managerDao.update (gebruiker);
	}
	
	@Test(expected = RuntimeException.class) 
	public void testUpdateGebruikerConstraints () {
		final Gebruiker gebruiker = managerDao.getGebruiker ("overijssel");
		
		assertNotNull (gebruiker);
		
		gebruiker.setEmail (null);
		
		managerDao.update (gebruiker);
		
		final Gebruiker updatedGebruiker = managerDao.getGebruiker ("overijssel");
		
		assertNotNull (updatedGebruiker);
		assertEquals ("test2@idgis.nl", updatedGebruiker.getEmail ());
	}
	
    // =========================================================================
    // GebruikersRol:
    // =========================================================================
	@Test
	public void testAuthenticate () {
		final Gebruiker gebruiker = managerDao.getGebruiker ("overijssel");
		
		assertFalse (managerDao.authenticate(gebruiker.getGebruikersnaam(), "12test34"));
		
		gebruiker.setWachtwoord ("12test34");
		managerDao.update (gebruiker);
		
		assertTrue (managerDao.authenticate(gebruiker.getGebruikersnaam(), "12test34"));
	}
	
	/**
	 * Verifies that a user initially has no database backing and that
	 * a corresponding record is inserted into the database when
	 * persisting.
	 */
	public @Test void testGebruikerCreateDatabaseBacking () throws Throwable {
		final Gebruiker gebruiker = managerDao.getGebruiker ("flevoland");
		
		entityManager.flush ();
		
		assertNull (entityManager.find (DbGebruiker.class, gebruiker.getGebruikersnaam ()));
		
		gebruiker.setSuperuser (true);
		
		managerDao.update (gebruiker);
		
		entityManager.flush ();
		
		final DbGebruiker dbGebruiker = entityManager.find (DbGebruiker.class, gebruiker.getGebruikersnaam ());
		
		assertNotNull (dbGebruiker);
		assertEquals ("flevoland", dbGebruiker.getGebruikersnaam ());
		assertTrue (dbGebruiker.isSuperuser ());
	}
	
	public @Test void testGetAllThemasForBronhouder () throws Throwable {
    	createGebruikerThemaAutorisatie ();
		
		final Thema thema = managerDao.getThemaByName ("Protected sites");
		final Bronhouder bronhouder = managerDao.getBronhouderByCommonName ("overijssel");
		final BronhouderThema bronhouderThema = new BronhouderThema (thema, bronhouder);
		
		entityManager.persist (bronhouderThema);
		
		entityManager.flush ();
		
		final List<Thema> themas = managerDao.getAllThemas (bronhouder);
		
		assertNotNull (themas);
		assertEquals (2, themas.size ());
		assertEquals ("Protected sites", themas.get (0).getNaam ());
		assertEquals ("Thema 2", themas.get (1).getNaam ());
	}
	
    @Test
    public void testCreateGebruikerThemaAutorisatie () throws Throwable {
    	createGebruikerThemaAutorisatie ();

    	assertEquals (4, entityManager.createQuery ("from GebruikerThemaAutorisatie", GebruikerThemaAutorisatie.class).getResultList ().size ());
    	
    	final Gebruiker gebruiker = managerDao.getGebruiker ("utrecht");
    	final BronhouderThema bronhouderThema = managerDao.getBronhouderThemas ().get (0);
    	
    	managerDao.createGebruikerThemaAutorisatie (gebruiker, bronhouderThema, TypeGebruik.DATABEHEERDER);
    	
    	entityManager.flush ();
    	
    	assertEquals (5, entityManager.createQuery ("from GebruikerThemaAutorisatie", GebruikerThemaAutorisatie.class).getResultList ().size ());
    	
    	final List<GebruikerThemaAutorisatie> result = entityManager
    		.createQuery ("from GebruikerThemaAutorisatie gta where gta.gebruiker.gebruikersnaam = ?1 and gta.bronhouderThema.bronhouder.naam = ?2 and gta.bronhouderThema.thema.naam = ?3", GebruikerThemaAutorisatie.class)
    		.setParameter (1, "utrecht")
    		.setParameter (2, bronhouderThema.getBronhouder ().getNaam ())
    		.setParameter (3, bronhouderThema.getThema ().getNaam ())
    		.getResultList ();
    	
    	assertEquals (1, result.size ());
    	assertEquals (TypeGebruik.DATABEHEERDER, result.get (0).getTypeGebruik ());
    }
    
    @Test
    public void testDeleteGebruikerThemaAutorisatie () {
    	createGebruikerThemaAutorisatie ();
    	
    	final GebruikerThemaAutorisatie gta = entityManager
    			.createQuery ("from GebruikerThemaAutorisatie", GebruikerThemaAutorisatie.class)
    			.getResultList ()
    			.get (0);
    	
    	assertEquals (4, entityManager.createQuery ("from GebruikerThemaAutorisatie", GebruikerThemaAutorisatie.class).getResultList ().size ());

    	managerDao.delete (gta);
    	
    	entityManager.flush ();
    	
    	assertEquals (3, entityManager.createQuery ("from GebruikerThemaAutorisatie", GebruikerThemaAutorisatie.class).getResultList ().size ());
    }
    
    @Test
    public void testGetGebruikerThemaAutorisatie () {
    	createGebruikerThemaAutorisatie ();
    	
    	final List<GebruikerThemaAutorisatie> gtas = managerDao.getGebruikerThemaAutorisatie ();
    	
    	assertEquals (4, gtas.size ());
    	
    	assertEquals ("drenthe", gtas.get (0).getGebruiker ().getGebruikersnaam ());
    	assertEquals ("limburg", gtas.get (1).getGebruiker ().getGebruikersnaam ());
    	assertEquals ("noord-holland", gtas.get (2).getGebruiker ().getGebruikersnaam ());
    	assertEquals ("overijssel", gtas.get (3).getGebruiker ().getGebruikersnaam ());
    	
    	assertEquals ("Drenthe", gtas.get (0).getBronhouderThema ().getBronhouder ().getNaam ());
    	assertEquals ("Limburg", gtas.get (1).getBronhouderThema ().getBronhouder ().getNaam ());
    	assertEquals ("Noord-Holland", gtas.get (2).getBronhouderThema ().getBronhouder ().getNaam ());
    	assertEquals ("Overijssel", gtas.get (3).getBronhouderThema ().getBronhouder ().getNaam ());
    	
    	assertEquals ("Protected sites", gtas.get (0).getBronhouderThema ().getThema ().getNaam ());
    	assertEquals ("Protected sites", gtas.get (1).getBronhouderThema ().getThema ().getNaam ());
    	assertEquals ("Thema 2", gtas.get (2).getBronhouderThema ().getThema ().getNaam ());
    	assertEquals ("Thema 2", gtas.get (3).getBronhouderThema ().getThema ().getNaam ());
    	
    	assertEquals (TypeGebruik.RAADPLEGER, gtas.get (0).getTypeGebruik ());
    	assertEquals (TypeGebruik.RAADPLEGER, gtas.get (1).getTypeGebruik ());
    	assertEquals (TypeGebruik.RAADPLEGER, gtas.get (2).getTypeGebruik ());
    	assertEquals (TypeGebruik.RAADPLEGER, gtas.get (3).getTypeGebruik ());
    }
    
    @Test
    public void testGetGebruikerThemaAutorisatieByGebruiker () {
    	createGebruikerThemaAutorisatie ();
    	
    	final List<GebruikerThemaAutorisatie> gtas = managerDao.getGebruikerThemaAutorisatie (managerDao.getGebruiker ("drenthe"));
    	
    	assertEquals (1, gtas.size ());
    	assertEquals ("drenthe", gtas.get (0).getGebruiker ().getGebruikersnaam ());
    	assertEquals ("Drenthe", gtas.get (0).getBronhouderThema ().getBronhouder ().getNaam ());
    	assertEquals ("Protected sites", gtas.get (0).getBronhouderThema ().getThema ().getNaam ());
    	assertEquals (TypeGebruik.RAADPLEGER, gtas.get (0).getTypeGebruik ());
    }
    
    @Test
    public void testGetGebruikerThemaAutorisatieByBronhouder () {
    	createGebruikerThemaAutorisatie ();
    	
    	final List<GebruikerThemaAutorisatie> gtas = managerDao.getGebruikerThemaAutorisatie (managerDao.getBronhouderByNaam ("Drenthe"));
    	
    	assertEquals (1, gtas.size ());
    	assertEquals ("drenthe", gtas.get (0).getGebruiker ().getGebruikersnaam ());
    	assertEquals ("Drenthe", gtas.get (0).getBronhouderThema ().getBronhouder ().getNaam ());
    	assertEquals ("Protected sites", gtas.get (0).getBronhouderThema ().getThema ().getNaam ());
    	assertEquals (TypeGebruik.RAADPLEGER, gtas.get (0).getTypeGebruik ());
    }
    
    @Test
    public void testGetGebruikerThemaAutorisatieByThema () {
    	createGebruikerThemaAutorisatie ();
    	
    	final List<GebruikerThemaAutorisatie> gtas = managerDao.getGebruikerThemaAutorisatie (managerDao.getThemaByName ("Protected sites"));
    	
    	assertEquals (2, gtas.size ());
    	assertEquals ("drenthe", gtas.get (0).getGebruiker ().getGebruikersnaam ());
    	assertEquals ("Drenthe", gtas.get (0).getBronhouderThema ().getBronhouder ().getNaam ());
    	assertEquals ("Protected sites", gtas.get (0).getBronhouderThema ().getThema ().getNaam ());
    	assertEquals (TypeGebruik.RAADPLEGER, gtas.get (0).getTypeGebruik ());
    	
    	assertEquals ("limburg", gtas.get (1).getGebruiker ().getGebruikersnaam ());
    	assertEquals ("Limburg", gtas.get (1).getBronhouderThema ().getBronhouder ().getNaam ());
    	assertEquals ("Protected sites", gtas.get (1).getBronhouderThema ().getThema ().getNaam ());
    	assertEquals (TypeGebruik.RAADPLEGER, gtas.get (1).getTypeGebruik ());
    }
}
