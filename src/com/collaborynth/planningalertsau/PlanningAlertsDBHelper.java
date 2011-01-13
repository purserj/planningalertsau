package com.collaborynth.planningalertsau;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class PlanningAlertsDBHelper extends SQLiteOpenHelper{
	
	

	private static final int DATABASE_VERSION=1;
	private static final String DATABASE_NAME="planningalertsdb";
	
	private static final String SEARCH_TABLE_NAME="searches";
	private static final String SEARCH_RESULTS_TABLE_NAME = "search_results";
	
	private static final String SEARCH_TABLE_CREATE =
		"CREATE TABLE "+ SEARCH_TABLE_NAME +
		"(search_id INTEGER PRIMARY KEY AUTOINCREMENT," +
		"search_type INTEGER," +
		"search_value TEXT," +
		"search_state TEXT," +
		"search_radius INTEGER)";
	
	private static final String SEARCH_RESULTS_TABLE_CREATE=
		"CREATE TABLE "+ SEARCH_RESULTS_TABLE_NAME +
		"(result_id INTEGER PRIMARY KEY AUTOINCREMENT," +
		"result_search_id INTEGER," +
		"result_title TEXT," +
		"result_description TEXT," +
		"result_date TEXT," +
		"result_url TEXT" +
		"result_long DOUBLE," +
		"result_lat DOUBLE)";
	
	public PlanningAlertsDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(SEARCH_TABLE_CREATE);
		db.execSQL(SEARCH_RESULTS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
