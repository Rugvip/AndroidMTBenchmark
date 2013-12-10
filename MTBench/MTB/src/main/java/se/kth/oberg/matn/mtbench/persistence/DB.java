package se.kth.oberg.matn.mtbench.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DB extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "results.db";

    public static final String COLUMN_ID = "_id";
    public static final String RACE_TABLE_NAME = "race";
    public static final String RACE_COLUMN_WORKER_ID = "raceWorkerId";
    public static final String RACE_COLUMN_EXPONENT = "raceExponent";

    public static final String CAR_TABLE_NAME = "car";
    public static final String CAR_COLUMN_RACE_ID = "carRaceId";
    public static final String CAR_COLUMN_WORKERS = "carWorkers";
    public static final String CAR_COLUMN_ITEMS = "carItems";

    public static final String LAP_TABLE_NAME = "lap";
    public static final String LAP_COLUMN_CAR_ID = "lapCarId";
    public static final String LAP_COLUMN_TIME = "lapTime";

    public static final String CREATE_RACE_TABLE = "CREATE TABLE " + RACE_TABLE_NAME + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            RACE_COLUMN_WORKER_ID + " integer not null, " +
            RACE_COLUMN_EXPONENT + " integer not null " +
            ");";

    public static final String CREATE_CAR_TABLE = "CREATE TABLE " + CAR_TABLE_NAME + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            CAR_COLUMN_RACE_ID + " integer not null, " +
            CAR_COLUMN_WORKERS + " integer not null, " +
            CAR_COLUMN_ITEMS + " integer not null, " +
            "FOREIGN KEY(" + CAR_COLUMN_RACE_ID + ")" +
            "REFERENCES " + RACE_TABLE_NAME + "(" + COLUMN_ID + ")" +
            ");";

    public static final String CREATE_LAP_TABLE = "CREATE TABLE " + LAP_TABLE_NAME + "(" +
            LAP_COLUMN_CAR_ID + " integer not null, " +
            LAP_COLUMN_TIME + " integer not null, " +
            "FOREIGN KEY(" + LAP_COLUMN_CAR_ID + ")" +
            "REFERENCES " + CAR_TABLE_NAME + "(" + COLUMN_ID + ")" +
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
        db.execSQL(CREATE_CAR_TABLE);
        db.execSQL(CREATE_LAP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB.LAP_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DB.CAR_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DB.RACE_TABLE_NAME);
        db.execSQL(DB.CREATE_RACE_TABLE);
        db.execSQL(DB.CREATE_CAR_TABLE);
        db.execSQL(DB.CREATE_LAP_TABLE);
    }
}
