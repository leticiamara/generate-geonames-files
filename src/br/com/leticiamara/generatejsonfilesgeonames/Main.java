package br.com.leticiamara.generatejsonfilesgeonames;

public class Main {
	
	/*Change this path to your path */
	private static String PATH = "/home/GeonameFiles/";

	public static void main(String[] args) {
		GeonamesJsonGenerator geonamesJsonParser = new GeonamesJsonGenerator(PATH);
		geonamesJsonParser.generateFiles();
	}

}
