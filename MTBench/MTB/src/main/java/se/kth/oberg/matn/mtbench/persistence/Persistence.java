package se.kth.oberg.matn.mtbench.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.LinkedList;
import java.util.List;

import se.kth.oberg.matn.mtbench.model.BenchmarkResult;

public class Persistence {
    public static void resetDatabase(Context context) {
        SQLiteDatabase db = DB.writable(context);
        db.execSQL("DROP TABLE IF EXISTS " + DB.LAP_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DB.CAR_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DB.RACE_TABLE_NAME);
        db.execSQL(DB.CREATE_RACE_TABLE);
        db.execSQL(DB.CREATE_CAR_TABLE);
        db.execSQL(DB.CREATE_LAP_TABLE);
        db.close();
    }

    public static void deleteResult(Context context, int id) {
        SQLiteDatabase db = DB.writable(context);
        String[] args = new String[] {"" + id};
        db.execSQL(" DELETE " + DB.CAR_TABLE_NAME + ", " + DB.LAP_TABLE_NAME +
                " FROM " + DB.LAP_TABLE_NAME +
                " INNER JOIN " + DB.CAR_TABLE_NAME +
                " ON " + DB.CAR_COLUMN_ID + " = " + DB.LAP_COLUMN_CAR_ID +
                " WHERE " + DB.CAR_COLUMN_RACE_ID + " = ?", args);
        db.delete(DB.CAR_TABLE_NAME, DB.CAR_COLUMN_RACE_ID + " = ?", args);
        db.delete(DB.RACE_TABLE_NAME, DB.RACE_COLUMN_ID + " = ?", args);
        db.close();
    }

    public static void saveResult(Context context, int workerId, BenchmarkResult result) {
        SQLiteDatabase db = DB.writable(context);

        ContentValues race = new ContentValues();
        race.put(DB.RACE_COLUMN_EXPONENT, result.getExponent());
        race.put(DB.RACE_COLUMN_WORKER_ID, workerId);
        long raceId = db.insert(DB.RACE_TABLE_NAME, null, race);

        for (BenchmarkResult.Result singleResult : result.getResults()) {
            ContentValues car = new ContentValues();
            car.put(DB.CAR_COLUMN_WORKERS, singleResult.getWorkerCount());
            car.put(DB.CAR_COLUMN_ITEMS, singleResult.getWorkItems());
            car.put(DB.CAR_COLUMN_RACE_ID, raceId);
            long carId = db.insert(DB.CAR_TABLE_NAME, null, car);

            for (Long time : singleResult.getTimes()) {
                ContentValues lap = new ContentValues();
                lap.put(DB.LAP_COLUMN_TIME, time);
                lap.put(DB.LAP_COLUMN_CAR_ID, carId);
                db.insert(DB.LAP_TABLE_NAME, null, lap);
            }
        }

        db.close();
    }

    public static final String getResultGroupsQueryCarCount = "carCount";

    public static final String getResultGroupsQuery =
            " SELECT " + DB.RACE_COLUMN_WORKER_ID + ", " + DB.RACE_COLUMN_EXPONENT + ", count(" + DB.CAR_COLUMN_ID + ") as " + getResultGroupsQueryCarCount +
            " FROM " + DB.RACE_TABLE_NAME +
            " INNER JOIN " + DB.CAR_TABLE_NAME +
            " ON " + DB.RACE_COLUMN_ID + " = " + DB.CAR_COLUMN_RACE_ID +
            " GROUP BY " + DB.RACE_COLUMN_WORKER_ID + ", " + DB.RACE_COLUMN_EXPONENT +
            " ORDER BY " + DB.RACE_COLUMN_WORKER_ID + " ASC, " + DB.RACE_COLUMN_EXPONENT + " ASC";

    public static ResultSummary getResultSummary(Context context) {
        ResultSummary.Builder result = ResultSummary.createBuilder();

        SQLiteDatabase db = DB.readable(context);

        try {
            Cursor cursor = db.rawQuery(getResultGroupsQuery, null);

            while (cursor.moveToNext()) {
                int workerId = cursor.getInt(cursor.getColumnIndex(DB.RACE_COLUMN_WORKER_ID));
                int exponent = cursor.getInt(cursor.getColumnIndex(DB.RACE_COLUMN_EXPONENT));
                int lapCount = cursor.getInt(cursor.getColumnIndex(getResultGroupsQueryCarCount));
                result.add(workerId, exponent, lapCount);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return result.build();
    }

    public static final String getResultQeury =
            " SELECT " + DB.LAP_COLUMN_TIME + ", " + DB.CAR_COLUMN_WORKERS + ", " + DB.CAR_COLUMN_ITEMS +
            " FROM " + DB.RACE_TABLE_NAME +
            " INNER JOIN " + DB.CAR_TABLE_NAME +
            " ON " + DB.RACE_COLUMN_ID + " = " + DB.CAR_COLUMN_RACE_ID +
            " INNER JOIN " + DB.LAP_TABLE_NAME +
            " ON " + DB.CAR_COLUMN_ID + " = " + DB.LAP_COLUMN_CAR_ID +
            " WHERE " + DB.RACE_COLUMN_WORKER_ID + " = ?" +
            " AND " + DB.RACE_COLUMN_EXPONENT + " = ?";

    public static BenchmarkResult getResult(Context context, int workerId, int exponent) {
        BenchmarkResult.Builder resultBuilder = BenchmarkResult.createBuilder(exponent);

        SQLiteDatabase db = DB.readable(context);

        try {
            Cursor cursor = db.rawQuery(getResultQeury, new String[]{"" + workerId, "" + exponent});

            if (cursor.getCount() == 0) {
                return null;
            }

            while (cursor.moveToNext()) {
                long time = cursor.getLong(cursor.getColumnIndex(DB.LAP_COLUMN_TIME));
                int workItems = cursor.getInt(cursor.getColumnIndex(DB.CAR_COLUMN_ITEMS));
                int workerCount = cursor.getInt(cursor.getColumnIndex(DB.CAR_COLUMN_WORKERS));
                resultBuilder.addResult(time, workerCount, workItems);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return resultBuilder.build();
    }

    public static BenchmarkResult[] getAllWorkerResults(Context context, int workerId) {
        List<BenchmarkResult> resultList = new LinkedList<>();

        for (int i = 0; i <= 20; i++) {
            BenchmarkResult result = getResult(context, workerId, i);
            if (result != null) {
                resultList.add(result);
            }
        }

        return resultList.toArray(new BenchmarkResult[0]);
    }
}
