package br.com.leticiamara.generatejsonfilesgeonames;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

public class FileUtils {

	public static void saveJsonInFile(JSONObject jsonObject, String pathAndFileName) {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(pathAndFileName);
			fileWriter.write(jsonObject.toString());
			System.out.println("Copied JSON Object to File!");
			System.out.println("\nFile: " + pathAndFileName 
					+ "\nJSON Object: " + jsonObject);
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		} finally {
			try {
				fileWriter.close();
			} catch (IOException ioe) {
				// TODO Auto-generated catch block
				ioe.printStackTrace();
			}
		}
	}

	public static String readJsonFileToString(String path, Charset encoding) {
		byte[] encoded = null;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}
		return new String(encoded, encoding);
	}

	public static void createErrorDirAndFile(String path, String countryCode, String errorMessage) {
		String errorPathDir = path + "errors/";
		String errorFileName = errorPathDir + countryCode + "-error" + ".txt";

		//Check if the directory exists and create the directory
		File errorDir = new File(errorPathDir);
		if(!errorDir.exists()) {
			errorDir.mkdirs();
		}
		//Check if the file exists
		File errorFile = new File(errorFileName);
		if(!errorFile.exists()) {
			FileWriter fileWriter = null;
			try {
				fileWriter = new FileWriter(errorFileName);
				fileWriter.write(errorMessage);
				fileWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					fileWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			FileWriter fileWriter = null;
			try {
				fileWriter = new FileWriter(errorFileName, true);
				fileWriter.append("\n"+errorMessage);
				fileWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					fileWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
