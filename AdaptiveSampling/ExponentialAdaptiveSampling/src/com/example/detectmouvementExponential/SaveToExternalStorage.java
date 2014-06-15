package com.example.detectmouvementExponential;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class SaveToExternalStorage {

	String string;
	String fileName;

	public SaveToExternalStorage(String string, String fileName) {
		this.string = string;
		this.fileName = fileName;
	}

	public void save() {
		try {
			File path = Environment.getExternalStoragePublicDirectory(fileName);
			File file = new File(path.toString());

			FileWriter fStream = new FileWriter(file, true);
			BufferedWriter fOut = new BufferedWriter(fStream);
			fOut.write(string);
			fOut.close();
			fStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.i("TAG",
					"******* File not found. Did you"
							+ Environment
									.getExternalStoragePublicDirectory("dosyam"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
