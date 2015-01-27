package nl.ipo.cds.admin.ba.controller.gebruikersbeheer.beans;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import nl.ipo.cds.domain.Bronhouder;

public class BronhouderForm {

	@NotNull
	@Size (min = 1)
	private String code = "";
	
	@NotNull
	@Size (min = 2)
	private String naam = "";
	
	private String contactNaam;
	
	private String contactAdres;

	private String contactPlaats;

	private String contactPostcode;

	private String contactTelefoonnummer;

	private String contactEmailadres;
	
	@NotNull
	@Size (min = 2)
	private String commonName = "";
	
	public BronhouderForm () {
	}
	
	public BronhouderForm (final Bronhouder bronhouder) {
		setCode (bronhouder.getCode ());
		setNaam (bronhouder.getNaam ());
		setContactNaam (bronhouder.getContactNaam ());
		setContactAdres (bronhouder.getContactAdres ());
		setContactPlaats (bronhouder.getContactPlaats ());
		setContactPostcode (bronhouder.getContactPostcode ());
		setContactTelefoonnummer (bronhouder.getContactTelefoonnummer ());
		setContactEmailadres (bronhouder.getContactEmailadres ());
	}

	public String getCode () {
		return code;
	}

	public void setCode (final String code) {
		this.code = code;
	}

	public String getNaam () {
		return naam;
	}

	public void setNaam (final String naam) {
		this.naam = naam;
	}

	public String getContactNaam () {
		return contactNaam;
	}

	public void setContactNaam (final String contactNaam) {
		this.contactNaam = contactNaam;
	}

	public String getContactAdres () {
		return contactAdres;
	}

	public void setContactAdres (final String contactAdres) {
		this.contactAdres = contactAdres;
	}

	public String getContactPlaats () {
		return contactPlaats;
	}

	public void setContactPlaats (final String contactPlaats) {
		this.contactPlaats = contactPlaats;
	}

	public String getContactPostcode () {
		return contactPostcode;
	}

	public void setContactPostcode (final String contactPostcode) {
		this.contactPostcode = contactPostcode;
	}

	public String getContactTelefoonnummer () {
		return contactTelefoonnummer;
	}

	public void setContactTelefoonnummer (final String contactTelefoonnummer) {
		this.contactTelefoonnummer = contactTelefoonnummer;
	}

	public String getContactEmailadres () {
		return contactEmailadres;
	}

	public void setContactEmailadres (final String contactEmailadres) {
		this.contactEmailadres = contactEmailadres;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
}
