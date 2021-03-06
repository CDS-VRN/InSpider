delete from inspire.area_management;

insert into inspire.area_management (
	id,
 	job_id,
	gfid,
	inspire_id_namespace,
	inspire_id_local_id,
	zonetype_code,
	environmental_domain_code,
	thematic_id_identifier,
	thematic_id_identifier_scheme,
	name_spelling,
	competent_authority_organisation_name,
	legislation_citation_gml_id,
	legal_basis_name,
	legal_basis_link,	
	legal_basis_date,
	specialised_zone_type_code,
	specialised_zone_type_codespace,
	designation_period_begin_designation,
	designation_period_end_designation,
	designation_period_end_indeterminate,	
	vergunde_kuubs,
	vergunde_diepte,
	noise_low_value,
	noise_high_value,
	geometry,
	geom_simple)
select
    id,
    job_id,
    gfid,
    'NL.' || inspire_id_dataset_code inspire_id_namespace,    
    inspire_id_local_id,
    zonetype_code,
    environmental_domain_code,
    thematic_id_identifier,
    thematic_id_identifier_scheme,
    name_spelling,
    competent_authority_organisation_name,
    'BASE2_LEGISLATION_CITATION_AM_' || gfid legislation_citation_gml_id,
    legal_basis_name,
    legal_basis_link,
    legal_basis_date,
    specialised_zone_type_code,
	specialised_zone_type_codespace,
    designation_period_begin_designation,
    designation_period_end_designation,
    CASE WHEN designation_period_end_designation IS NULL THEN 'unknown'
    END designation_period_end_indeterminate,
    vergunde_kuubs,
    vergunde_diepte,
    noise_low_value,
    noise_high_value,
    geometry,
    ST_SimplifyPreserveTopology(geometry,10)
 from bron.area_management;
