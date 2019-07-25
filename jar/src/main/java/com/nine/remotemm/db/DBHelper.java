package com.nine.remotemm.db;

import android.os.Environment;


import org.core.DbManager;
import org.core.ex.DbException;
import org.core.x;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2016/4/18.
 */
public class DBHelper {

    public static final String DB_NAME = "remote.db";
    public static final int DB_VERSION = 1;

    private static DBHelper dbHelper;
    private DbManager.DaoConfig daoConfig;

    public DBHelper() {
        // for test
        daoConfig = new DbManager.DaoConfig()
                .setDbName(DB_NAME)
                .setDbVersion(DB_VERSION)
                .setDbDir(Environment.getExternalStorageDirectory())
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                    }
                });
    }

    public static DBHelper getInstance() {
        if (dbHelper == null) {
            synchronized (DBHelper.class) {
                if (dbHelper == null) {
                    dbHelper = new DBHelper();
                }
            }
        }
        return dbHelper;
    }

    public synchronized void close() {
        try {
            DbManager db = x.getDb(daoConfig);
            if (db != null)
                db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void save(Object object) {
        try {
            DbManager db = x.getDb(daoConfig);
            db.saveOrUpdate(object);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void deleteAll(Class<?> clazz) {
        try {
            DbManager db = x.getDb(daoConfig);
            db.delete(clazz);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void deleteById(Class<?> clazz, Object idValue) {
        try {
            DbManager db = x.getDb(daoConfig);
            db.deleteById(clazz, idValue);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public <T> List<T> findAll(Class<T> t) {
        try {
            DbManager db = x.getDb(daoConfig);
            return db.findAll(t);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T findById(Class<T> t, Object id) {
        try {
            DbManager db = x.getDb(daoConfig);
            return db.findById(t, id);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
}
