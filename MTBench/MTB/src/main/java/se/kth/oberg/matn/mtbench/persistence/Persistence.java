package se.kth.oberg.matn.mtbench.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import se.kth.oberg.matn.mtbench.model.BenchmarkResult;

public class Persistence {
    public static void resetDatabase(Context context){
        SQLiteDatabase db = DB.writable(context);
        db.execSQL("DROP TABLE IF EXISTS " + DB.LAP_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DB.RACE_TABLE_NAME);
        db.execSQL(DB.CREATE_RACE_TABLE);
        db.execSQL(DB.CREATE_LAP_TABLE);
        db.close();
    }

    public static void deleteResult(Context context, int id) {
        SQLiteDatabase db = DB.writable(context);
        db.delete(DB.LAP_TABLE_NAME, DB.LAP_COLUMN_RACE_ID + " = ?", new String[] {"" + id});
        db.delete(DB.RACE_TABLE_NAME, DB.RACE_COLUMN_ID + " = ?", new String[] {"" + id});
        db.close();
    }

    public static void saveResult(Context context, BenchmarkResult result) {
        SQLiteDatabase db = DB.writable(context);
        ContentValues race = new ContentValues();

        race.put(DB.RACE_COLUMN_EXPONENT, result.getExponent());
        race.put(DB.RACE_COLUMN_LAPS, result.getResults().size());
        race.put(DB.RACE_COLUMN_WORKER_ID, result.getWorkerId());
        long raceId = db.insert(DB.RACE_TABLE_NAME, null, race);
        Log.i("SaveResult", "race id: " + raceId);

        int i = 0;
        for (BenchmarkResult.Result singleResult : result.getResults()) {
            i++;
            ContentValues lap = new ContentValues();
            lap.put(DB.LAP_COLUMN_INDEX, i);
            lap.put(DB.LAP_COLUMN_ITEMS, singleResult.getWorkerCount());
            lap.put(DB.LAP_COLUMN_WORKERS, singleResult.getWorkItems());
            lap.put(DB.LAP_COLUMN_TIME, singleResult.getTime());
            lap.put(DB.LAP_COLUMN_RACE_ID, raceId);
            db.insert(DB.LAP_TABLE_NAME, null, lap);
        }

        db.close();
    }

//    RACE_COLUMN_WORKER_ID, RACE_COLUMN_EXPONENT, LAP_COLUMN_WORKERS, LAP_COLUMN_ITEMS, LAP_COLUMN_TIME

    public static BenchmarkResult getResult(Context context, int workerId, int exponent) {
        BenchmarkResult.Builder resultBuilder = BenchmarkResult.createBuilder(workerId, exponent);

        SQLiteDatabase db = DB.readable(context);

        try {
            Cursor cursor = db.query(DB.RACE_TABLE_NAME + ", " + DB.LAP_TABLE_NAME, DB.RESULT_COLUMNS,
                    DB.RACE_COLUMN_WORKER_ID + " = ?" + " and " +
                    DB.RACE_COLUMN_EXPONENT + " = ?" +
                    DB.LAP_COLUMN_RACE_ID + " = " + DB.RACE_COLUMN_ID,
                    new String[] {"" + workerId, "" + exponent}, null, null, null);
            if (!cursor.isFirst()) {
                return null;
            }

            while (cursor.moveToNext()) {
                long time = cursor.getLong(cursor.getColumnIndex(DB.LAP_COLUMN_TIME));
                int workItems = cursor.getInt(cursor.getColumnIndex(DB.LAP_COLUMN_ITEMS));
                int workerCount = cursor.getInt(cursor.getColumnIndex(DB.LAP_COLUMN_WORKERS));
                resultBuilder.addWorkResult(time, workerCount, workItems);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return resultBuilder.build();
    }

    public static List<BenchmarkResult> getAllWorkerResults(Context context, int workerId) {
        List<BenchmarkResult> resultList = new LinkedList<>();

        for (int i = 0; i <= 20; i++) {
            BenchmarkResult result = getResult(context, workerId, i);
            if (result != null) {
                resultList.add(result);
            }
        }

        return resultList;
    }
}
