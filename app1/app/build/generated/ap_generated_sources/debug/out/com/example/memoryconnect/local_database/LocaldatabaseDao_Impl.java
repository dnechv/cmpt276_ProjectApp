package com.example.memoryconnect.local_database;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.memoryconnect.model.Patient;
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
public final class LocaldatabaseDao_Impl implements LocaldatabaseDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Patient> __insertionAdapterOfPatient;

  private final EntityInsertionAdapter<PhotoEntry> __insertionAdapterOfPhotoEntry;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public LocaldatabaseDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPatient = new EntityInsertionAdapter<Patient>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `patients` (`id`,`name`,`nickname`,`age`,`comment`,`photoUrl`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Patient value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        if (value.getNickname() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getNickname());
        }
        stmt.bindLong(4, value.getAge());
        if (value.getComment() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getComment());
        }
        if (value.getPhotoUrl() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getPhotoUrl());
        }
      }
    };
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
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM patients";
        return _query;
      }
    };
  }

  @Override
  public void insert(final List<Patient> patients) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfPatient.insert(patients);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertPhotoEntry(final PhotoEntry photoEntry) {
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
  public void deleteAll() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAll.release(_stmt);
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
  public LiveData<List<Patient>> getAllPatients() {
    final String _sql = "SELECT * from patients ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"patients"}, false, new Callable<List<Patient>>() {
      @Override
      public List<Patient> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfNickname = CursorUtil.getColumnIndexOrThrow(_cursor, "nickname");
          final int _cursorIndexOfAge = CursorUtil.getColumnIndexOrThrow(_cursor, "age");
          final int _cursorIndexOfComment = CursorUtil.getColumnIndexOrThrow(_cursor, "comment");
          final int _cursorIndexOfPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUrl");
          final List<Patient> _result = new ArrayList<Patient>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Patient _item;
            _item = new Patient();
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            _item.setId(_tmpId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            _item.setName(_tmpName);
            final String _tmpNickname;
            if (_cursor.isNull(_cursorIndexOfNickname)) {
              _tmpNickname = null;
            } else {
              _tmpNickname = _cursor.getString(_cursorIndexOfNickname);
            }
            _item.setNickname(_tmpNickname);
            final int _tmpAge;
            _tmpAge = _cursor.getInt(_cursorIndexOfAge);
            _item.setAge(_tmpAge);
            final String _tmpComment;
            if (_cursor.isNull(_cursorIndexOfComment)) {
              _tmpComment = null;
            } else {
              _tmpComment = _cursor.getString(_cursorIndexOfComment);
            }
            _item.setComment(_tmpComment);
            final String _tmpPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfPhotoUrl)) {
              _tmpPhotoUrl = null;
            } else {
              _tmpPhotoUrl = _cursor.getString(_cursorIndexOfPhotoUrl);
            }
            _item.setPhotoUrl(_tmpPhotoUrl);
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
  public LiveData<Patient> getPatientById(final String patientId) {
    final String _sql = "SELECT * FROM patients WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (patientId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, patientId);
    }
    return __db.getInvalidationTracker().createLiveData(new String[]{"patients"}, false, new Callable<Patient>() {
      @Override
      public Patient call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfNickname = CursorUtil.getColumnIndexOrThrow(_cursor, "nickname");
          final int _cursorIndexOfAge = CursorUtil.getColumnIndexOrThrow(_cursor, "age");
          final int _cursorIndexOfComment = CursorUtil.getColumnIndexOrThrow(_cursor, "comment");
          final int _cursorIndexOfPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUrl");
          final Patient _result;
          if(_cursor.moveToFirst()) {
            _result = new Patient();
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            _result.setId(_tmpId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            _result.setName(_tmpName);
            final String _tmpNickname;
            if (_cursor.isNull(_cursorIndexOfNickname)) {
              _tmpNickname = null;
            } else {
              _tmpNickname = _cursor.getString(_cursorIndexOfNickname);
            }
            _result.setNickname(_tmpNickname);
            final int _tmpAge;
            _tmpAge = _cursor.getInt(_cursorIndexOfAge);
            _result.setAge(_tmpAge);
            final String _tmpComment;
            if (_cursor.isNull(_cursorIndexOfComment)) {
              _tmpComment = null;
            } else {
              _tmpComment = _cursor.getString(_cursorIndexOfComment);
            }
            _result.setComment(_tmpComment);
            final String _tmpPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfPhotoUrl)) {
              _tmpPhotoUrl = null;
            } else {
              _tmpPhotoUrl = _cursor.getString(_cursorIndexOfPhotoUrl);
            }
            _result.setPhotoUrl(_tmpPhotoUrl);
          } else {
            _result = null;
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

  @Override
  public List<String> getAllPatientIds() {
    final String _sql = "SELECT id FROM patients";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final List<String> _result = new ArrayList<String>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final String _item;
        if (_cursor.isNull(0)) {
          _item = null;
        } else {
          _item = _cursor.getString(0);
        }
        _result.add(_item);
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
