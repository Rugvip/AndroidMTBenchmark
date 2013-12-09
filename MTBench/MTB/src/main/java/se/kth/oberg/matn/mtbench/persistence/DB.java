package se.kth.oberg.matn.mtbench.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DB extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "results.db";
    public static final String RACE_TABLE_NAME = "race";
    public static final String RACE_COLUMN_ID = "raceId";
    public static final String RACE_COLUMN_WORKER_ID = "raceWorker";
    public static final String RACE_COLUMN_EXPONENT = "raceExponent";
    public static final String RACE_COLUMN_LAPS = "raceLaps";

    public static final String[] RACE_COLUMNS = {
            RACE_COLUMN_ID, RACE_COLUMN_WORKER_ID, RACE_COLUMN_EXPONENT, RACE_COLUMN_LAPS
    };

    public static final String LAP_TABLE_NAME = "lap";
    public static final String LAP_COLUMN_RACE_ID = "lapRaceId";
    public static final String LAP_COLUMN_INDEX = "lapIndex";
    public static final String LAP_COLUMN_WORKERS = "lapWorkers";
    public static final String LAP_COLUMN_ITEMS = "lapItems";
    public static final String LAP_COLUMN_TIME = "lapTime";

    public static final String[] LAP_COLUMNS = {
            LAP_COLUMN_RACE_ID, LAP_COLUMN_INDEX, LAP_COLUMN_WORKERS, LAP_COLUMN_ITEMS, LAP_COLUMN_TIME
    };

    public static final String[] RESULT_COLUMNS = {
            RACE_COLUMN_WORKER_ID, RACE_COLUMN_EXPONENT, LAP_COLUMN_WORKERS, LAP_COLUMN_ITEMS, LAP_COLUMN_TIME
    };

    public static final String CREATE_RACE_TABLE =
            "CREATE TABLE " + RACE_TABLE_NAME + "(" +
            RACE_COLUMN_ID + " integer primary key autoincrement, " +
            RACE_COLUMN_WORKER_ID + " integer not null, " +
            RACE_COLUMN_EXPONENT + " integer not null, " +
            RACE_COLUMN_LAPS + " integer not null " +
            ");";

    public static final String CREATE_LAP_TABLE =
            "CREATE TABLE " + LAP_TABLE_NAME + "(" +
            LAP_COLUMN_RACE_ID + " integer not null, " +
            LAP_COLUMN_INDEX + " integer not null, " +
            LAP_COLUMN_WORKERS + " integer not null, " +
            LAP_COLUMN_ITEMS + " integer not null, " +
            LAP_COLUMN_TIME + " integer not null, " +
            "FOREIGN KEY(" + LAP_COLUMN_INDEX + ")" +
            "REFERENCES " + RACE_TABLE_NAME + "(" + RACE_COLUMN_ID + ")" +
            ");";

    private DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteDatabase writable(Context context) {
        return new DB(context).getWritableDatabase();
    }

    public static SQLiteDatabase readable(Context context) {
        return new DB(context).getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RACE_TABLE);
        db.execSQL(CREATE_LAP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("DB", "DB onUpgrade should be run here, 404");
    }
}
