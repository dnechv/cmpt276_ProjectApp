package com.example.memoryconnect.local_database;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.memoryconnect.model.PhotoEntry;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PhotoEntryDatabaseDAO_Impl implements PhotoEntryDatabaseDAO {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PhotoEntry> __insertionAdapterOfPhotoEntry;

  private final EntityDeletionOrUpdateAdapter<PhotoEntry> __deletionAdapterOfPhotoEntry;

  private final EntityDeletionOrUpdateAdapter<PhotoEntry> __updateAdapterOfPhotoEntry;

  private final SharedSQLiteStatement __preparedStmtOfDeletePhotosByPatient;

  public PhotoEntryDatabaseDAO_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPhotoEntry = new EntityInsertionAdapter<PhotoEntry>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `photos` (`id`,`photoUrl`,`youtubeUrl`,`patientId`,`timeWhenPhotoAdded`,`title`,`date`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, PhotoEntry value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
        if (value.getPhotoUrl() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getPhotoUrl());
        }
        if (value.getYoutubeUrl() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getYoutubeUrl());
        }
        if (value.getPatientId() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getPatientId());
        }
        stmt.bindLong(5, value.getTimeWhenPhotoAdded());
        if (value.getTitle() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getTitle());
        }
        if (value.getDate() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getDate());
        }
      }
    };
    this.__deletionAdapterOfPhotoEntry = new EntityDeletionOrUpdateAdapter<PhotoEntry>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `photos` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, PhotoEntry value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
      }
    };
    this.__updateAdapterOfPhotoEntry = new EntityDeletionOrUpdateAdapter<PhotoEntry>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `photos` SET `id` = ?,`photoUrl` = ?,`youtubeUrl` = ?,`patientId` = ?,`timeWhenPhotoAdded` = ?,`title` = ?,`date` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, PhotoEntry value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
        if (value.getPhotoUrl() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getPhotoUrl());
        }
        if (value.getYoutubeUrl() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getYoutubeUrl());
        }
        if (value.getPatientId() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getPatientId());
        }
        stmt.bindLong(5, value.getTimeWhenPhotoAdded());
        if (value.getTitle() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getTitle());
        }
        if (value.getDate() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getDate());
        }
        if (value.getId() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getId());
        }
      }
    };
    this.__preparedStmtOfDeletePhotosByPatient = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM photos WHERE patientId = ?";
        return _query;
      }
    };
  }

  @Override
  public void insert(final PhotoEntry photoEntry) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfPhotoEntry.insert(photoEntry);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final PhotoEntry photoEntry) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfPhotoEntry.handle(photoEntry);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final PhotoEntry photoEntry) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfPhotoEntry.handle(photoEntry);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deletePhotosByPatient(final String patientId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePhotosByPatient.acquire();
    int _argIndex = 1;
    if (patientId == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, patientId);
    }
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeletePhotosByPatient.release(_stmt);
    }
  }

  @Override
  public LiveData<List<PhotoEntry>> getAllPhotosForPatient(final String patientId) {
    final String _sql = "SELECT * FROM photos WHERE patientId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (patientId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, patientId);
    }
    return __db.getInvalidationTracker().createLiveData(new String[]{"photos"}, false, new Callable<List<PhotoEntry>>() {
      @Override
      public List<PhotoEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUrl");
          final int _cursorIndexOfYoutubeUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "youtubeUrl");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfTimeWhenPhotoAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "timeWhenPhotoAdded");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final List<PhotoEntry> _result = new ArrayList<PhotoEntry>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final PhotoEntry _item;
            _item = new PhotoEntry();
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            _item.setId(_tmpId);
            final String _tmpPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfPhotoUrl)) {
              _tmpPhotoUrl = null;
            } else {
              _tmpPhotoUrl = _cursor.getString(_cursorIndexOfPhotoUrl);
            }
            _item.setPhotoUrl(_tmpPhotoUrl);
            final String _tmpYoutubeUrl;
            if (_cursor.isNull(_cursorIndexOfYoutubeUrl)) {
              _tmpYoutubeUrl = null;
            } else {
              _tmpYoutubeUrl = _cursor.getString(_cursorIndexOfYoutubeUrl);
            }
            _item.setYoutubeUrl(_tmpYoutubeUrl);
            final String _tmpPatientId;
            if (_cursor.isNull(_cursorIndexOfPatientId)) {
              _tmpPatientId = null;
            } else {
              _tmpPatientId = _cursor.getString(_cursorIndexOfPatientId);
            }
            _item.setPatientId(_tmpPatientId);
            final long _tmpTimeWhenPhotoAdded;
            _tmpTimeWhenPhotoAdded = _cursor.getLong(_cursorIndexOfTimeWhenPhotoAdded);
            _item.setTimeWhenPhotoAdded(_tmpTimeWhenPhotoAdded);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            _item.setTitle(_tmpTitle);
            final String _tmpDate;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmpDate = null;
            } else {
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
            }
            _item.setDate(_tmpDate);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<PhotoEntry>> getAllPhotos() {
    final String _sql = "SELECT * FROM photos";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"photos"}, false, new Callable<List<PhotoEntry>>() {
      @Override
      public List<PhotoEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUrl");
          final int _cursorIndexOfYoutubeUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "youtubeUrl");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfTimeWhenPhotoAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "timeWhenPhotoAdded");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final List<PhotoEntry> _result = new ArrayList<PhotoEntry>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final PhotoEntry _item;
            _item = new PhotoEntry();
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            _item.setId(_tmpId);
            final String _tmpPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfPhotoUrl)) {
              _tmpPhotoUrl = null;
            } else {
              _tmpPhotoUrl = _cursor.getString(_cursorIndexOfPhotoUrl);
            }
            _item.setPhotoUrl(_tmpPhotoUrl);
            final String _tmpYoutubeUrl;
            if (_cursor.isNull(_cursorIndexOfYoutubeUrl)) {
              _tmpYoutubeUrl = null;
            } else {
              _tmpYoutubeUrl = _cursor.getString(_cursorIndexOfYoutubeUrl);
            }
            _item.setYoutubeUrl(_tmpYoutubeUrl);
            final String _tmpPatientId;
            if (_cursor.isNull(_cursorIndexOfPatientId)) {
              _tmpPatientId = null;
            } else {
              _tmpPatientId = _cursor.getString(_cursorIndexOfPatientId);
            }
            _item.setPatientId(_tmpPatientId);
            final long _tmpTimeWhenPhotoAdded;
            _tmpTimeWhenPhotoAdded = _cursor.getLong(_cursorIndexOfTimeWhenPhotoAdded);
            _item.setTimeWhenPhotoAdded(_tmpTimeWhenPhotoAdded);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            _item.setTitle(_tmpTitle);
            final String _tmpDate;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmpDate = null;
            } else {
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
            }
            _item.setDate(_tmpDate);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public int isPatientIdExists(final String patientId) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM patients WHERE id = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (patientId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, patientId);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if(_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
