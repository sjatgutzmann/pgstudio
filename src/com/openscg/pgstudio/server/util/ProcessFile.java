package com.openscg.pgstudio.server.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

public class ProcessFile {

    public static void processCSVFile(InputStream iStream, List<String> columnList, List<ArrayList<String>> dataRows)
	    throws Exception {

	List<String> dataRow = null;
	CSVReader reader = null;
	BufferedReader br = null;
	try {
	    br = new BufferedReader(new InputStreamReader(iStream));
	    reader = new CSVReader(br);

	    String[] nextLine;
	    int line = 1;
	    while ((nextLine = reader.readNext()) != null) {
		// nextLine[] is an array of values from the line
		dataRow = new ArrayList<String>();
		for (int i = 0; i < nextLine.length; i++) {

		    if (line == 1) {
			columnList.add(nextLine[i]);
		    } else {
			dataRow.add(nextLine[i].trim());

		    }

		}
		if (line > 1) {
		    dataRows.add((ArrayList<String>) dataRow);
		}
		line += 1;
	    }

	} catch (Exception e) {

	    e.printStackTrace();
	    throw new Exception(e.getMessage());
	} finally {

	    iStream.close();
	    reader.close();
	}
    }

}
