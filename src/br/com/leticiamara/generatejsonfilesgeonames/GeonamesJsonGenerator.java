package br.com.leticiamara.generatejsonfilesgeonames;

import java.io.File;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.leticiamara.generatejsonfilesgeonames.HttpConection;

public class GeonamesJsonGenerator {

	private static final String CITIES_NAME = "-cities";
	private static final String CITIES_FIELD = "cities";
	private static final int SLEEP_TIME = 5000;
	private static final String STATES_FIELD = "states";
	private static final String COUNTRY_ID_FIELD = "countryId";
	private static final String GEONAME_CHILDREN_URL_PART_2 = "&type=json&formatted=true&username=";
	private static final String GEONAMES_CHILDREN_URL_PART_1 = "http://api.geonames.org/children?geonameId=";
	private static final String COUNTRY_CODE_FIELD = "countryCode";
	private static final String GEONAME_ID_FIELD = "geonameId";
	private static final String GEONAMES_FIELD = "geonames";
	
	/* Change the user name to your user name */
	private static final String GEONAMES_USER_NAME = "demo";
	
	private static final String JSON_FILE = ".json";
	private static final String COUNTRIES_FILE_NAME = "geonames_countries";
	private static String WORLD_COUNTRIES_URL = 
			"http://ws.geonames.org/countryInfoJSON?formatted=true&username=" + GEONAMES_USER_NAME;

	private String rootPathToSaveFiles;
	private String countriesFilePathAndName;
	private String statesPath;
	private String citiesPath;

	public GeonamesJsonGenerator(String rootPathToSaveFiles) {
		this.setRootPathToSaveFiles(rootPathToSaveFiles);
		countriesFilePathAndName = rootPathToSaveFiles + COUNTRIES_FILE_NAME + JSON_FILE;
		statesPath = rootPathToSaveFiles + "states/";
		citiesPath = rootPathToSaveFiles + "cities/";
	}

	public void generateFiles() {
		generateWorldCountriesFile(WORLD_COUNTRIES_URL);
		generateWorldStatesFile();
		generateWorldCitiesFile();
	}

	public void generateWorldCountriesFile(String geonameUrl) {
		String worldCities = HttpConection.makeRequest(geonameUrl);
		JSONObject worldCountriesJson = new JSONObject(worldCities);
		worldCountriesJson.getJSONArray(GEONAMES_FIELD);
		FileUtils.saveJsonInFile(worldCountriesJson, countriesFilePathAndName);
	}

	public void generateWorldStatesFile() {
		JSONArray geonamesCountriesArray = getGeonamesCountriesArray();
		File file = new File(statesPath);
		file.mkdirs();

		for (int i = 0; i < geonamesCountriesArray.length(); i++) {
			JSONObject countryObject = (JSONObject) geonamesCountriesArray.get(i);
			long geonameId = countryObject.getLong(GEONAME_ID_FIELD);
			String countryCode = countryObject.getString(COUNTRY_CODE_FIELD);

			String statesFromCountry = HttpConection.makeRequest(GEONAMES_CHILDREN_URL_PART_1 
					+ geonameId + GEONAME_CHILDREN_URL_PART_2 + GEONAMES_USER_NAME);

			JSONObject statesObject = new JSONObject();
			statesObject.put(COUNTRY_ID_FIELD, geonameId);

			JSONObject statesFromCountryObject = new JSONObject(statesFromCountry);
			try {
				statesObject.put(STATES_FIELD, statesFromCountryObject.getJSONArray(GEONAMES_FIELD));
			} catch (JSONException je) {
				FileUtils.createErrorDirAndFile(statesPath, countryCode, 
						"Exception: " + je.toString() +
						"\nCountry geoname ID: " + geonameId +
						"\nStatesFromCountry: " + statesFromCountry);
				je.printStackTrace();
			}

			FileUtils.saveJsonInFile(statesObject, statesPath + countryCode + JSON_FILE);

			sleepThread();
		}
	}

	
	private JSONArray getGeonamesCountriesArray() {
		String worldCountriesStr = FileUtils.readJsonFileToString(countriesFilePathAndName, 
				Charset.defaultCharset());

		JSONObject worldCountriesObject = new JSONObject(worldCountriesStr);
		JSONArray geonamesCountriesArray = worldCountriesObject.getJSONArray(GEONAMES_FIELD);
		return geonamesCountriesArray;
	}

	public void generateWorldCitiesFile() {
		JSONArray geonamesCountriesArray = getGeonamesCountriesArray();
		File file = new File(citiesPath);
		file.mkdirs();

		for(int i = 0; i < geonamesCountriesArray.length(); i++) {
			JSONObject countryObject = (JSONObject) geonamesCountriesArray.get(i);
			String countryCode = countryObject.getString(COUNTRY_CODE_FIELD);
			long countyGeonameId = countryObject.getLong(GEONAME_ID_FIELD);
			String statesStr = FileUtils.readJsonFileToString(statesPath 
					+ countryCode + JSON_FILE, Charset.defaultCharset());
			JSONObject statesObject = new JSONObject(statesStr);

			try {
				/*Few countries does not contain states.
				 * This line generate JSONException when the states field not found */
				JSONArray statesArrayRead = statesObject.getJSONArray(STATES_FIELD);

				JSONArray statesArray = new JSONArray();
				for(int j = 0; j < statesArrayRead.length(); j++) {
					JSONObject stateObject = statesArrayRead.getJSONObject(j);
					long geonameId = stateObject.getLong(GEONAME_ID_FIELD);
					String citiesResponse = HttpConection.makeRequest(GEONAMES_CHILDREN_URL_PART_1 
							+ geonameId + GEONAME_CHILDREN_URL_PART_2 + GEONAMES_USER_NAME);
					try {
						JSONObject citiesObject = new JSONObject(citiesResponse);
						stateObject.put(CITIES_FIELD, citiesObject.getJSONArray(GEONAMES_FIELD));
						statesArray.put(stateObject);
					} catch (JSONException jse) {
						FileUtils.createErrorDirAndFile(citiesPath, countryCode, 
								"Exception: " + jse.toString() +
								"\nState geoname ID: " + geonameId
								+ "\nCitiesResponse: " + citiesResponse);
						jse.printStackTrace();
					}
					sleepThread();	
				}
				statesObject.put(STATES_FIELD, statesArray);

				FileUtils.saveJsonInFile(statesObject, citiesPath 
						+ countryCode + CITIES_NAME + JSON_FILE);
			} catch(JSONException je) {
				FileUtils.createErrorDirAndFile(citiesPath, countryCode, 
						"Exception: " + je.toString() +
						"County Geoname ID: " + countyGeonameId);
			}
		}
	}

	private void sleepThread() {
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getRootPathToSaveFiles() {
		return rootPathToSaveFiles;
	}

	public void setRootPathToSaveFiles(String rootPathToSaveFiles) {
		this.rootPathToSaveFiles = rootPathToSaveFiles;
	}
}
