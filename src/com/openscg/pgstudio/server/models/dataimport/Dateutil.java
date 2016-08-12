package com.openscg.pgstudio.server.models.dataimport;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Dateutil {
    
    public static java.sql.Date convertStringToSqlDate(String dateInString) throws ParseException {

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	return new java.sql.Date(formatter.parse(dateInString).getTime());

    }
    
    public static java.sql.Timestamp convertStringToSqlTimestamp(String dateInString) throws ParseException {

   	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

   	return new java.sql.Timestamp(formatter.parse(dateInString).getTime());

       }
    
    public static java.sql.Timestamp convertStringToSqlTimestampWithTimeZone(String dateInString) throws ParseException {

   	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

   	return new java.sql.Timestamp(formatter.parse(dateInString).getTime());

       }

    public static java.sql.Time convertStringToSqlTime(String timeInString) throws ParseException {

	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

	return new java.sql.Time(formatter.parse(timeInString).getTime());

    }

}
