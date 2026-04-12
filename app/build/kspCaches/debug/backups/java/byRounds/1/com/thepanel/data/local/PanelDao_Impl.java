package com.thepanel.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PanelDao_Impl implements PanelDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WeatherCacheEntity> __insertionAdapterOfWeatherCacheEntity;

  private final EntityInsertionAdapter<AlarmEntity> __insertionAdapterOfAlarmEntity;

  private final SharedSQLiteStatement __preparedStmtOfSetAlarmEnabled;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAlarm;

  public PanelDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWeatherCacheEntity = new EntityInsertionAdapter<WeatherCacheEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `weather_cache` (`id`,`latitude`,`longitude`,`summary`,`temperatureC`,`feelsLikeC`,`windSpeedKmh`,`humidityPercent`,`sunriseIso`,`sunsetIso`,`fetchedAtEpochMs`,`weatherCode`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WeatherCacheEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getLatitude());
        statement.bindDouble(3, entity.getLongitude());
        statement.bindString(4, entity.getSummary());
        statement.bindDouble(5, entity.getTemperatureC());
        statement.bindDouble(6, entity.getFeelsLikeC());
        statement.bindDouble(7, entity.getWindSpeedKmh());
        statement.bindLong(8, entity.getHumidityPercent());
        statement.bindString(9, entity.getSunriseIso());
        statement.bindString(10, entity.getSunsetIso());
        statement.bindLong(11, entity.getFetchedAtEpochMs());
        statement.bindLong(12, entity.getWeatherCode());
      }
    };
    this.__insertionAdapterOfAlarmEntity = new EntityInsertionAdapter<AlarmEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `alarms` (`id`,`title`,`hour`,`minute`,`enabled`,`repeatDaily`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AlarmEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindLong(3, entity.getHour());
        statement.bindLong(4, entity.getMinute());
        final int _tmp = entity.getEnabled() ? 1 : 0;
        statement.bindLong(5, _tmp);
        final int _tmp_1 = entity.getRepeatDaily() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
      }
    };
    this.__preparedStmtOfSetAlarmEnabled = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE alarms SET enabled = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAlarm = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM alarms WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsertWeatherCache(final WeatherCacheEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWeatherCacheEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertAlarm(final AlarmEntity entity,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAlarmEntity.insertAndReturnId(entity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object setAlarmEnabled(final long id, final boolean enabled,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetAlarmEnabled.acquire();
        int _argIndex = 1;
        final int _tmp = enabled ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetAlarmEnabled.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAlarm(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAlarm.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAlarm.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<WeatherCacheEntity> observeWeatherCache() {
    final String _sql = "SELECT * FROM weather_cache WHERE id = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"weather_cache"}, new Callable<WeatherCacheEntity>() {
      @Override
      @Nullable
      public WeatherCacheEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfTemperatureC = CursorUtil.getColumnIndexOrThrow(_cursor, "temperatureC");
          final int _cursorIndexOfFeelsLikeC = CursorUtil.getColumnIndexOrThrow(_cursor, "feelsLikeC");
          final int _cursorIndexOfWindSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "windSpeedKmh");
          final int _cursorIndexOfHumidityPercent = CursorUtil.getColumnIndexOrThrow(_cursor, "humidityPercent");
          final int _cursorIndexOfSunriseIso = CursorUtil.getColumnIndexOrThrow(_cursor, "sunriseIso");
          final int _cursorIndexOfSunsetIso = CursorUtil.getColumnIndexOrThrow(_cursor, "sunsetIso");
          final int _cursorIndexOfFetchedAtEpochMs = CursorUtil.getColumnIndexOrThrow(_cursor, "fetchedAtEpochMs");
          final int _cursorIndexOfWeatherCode = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherCode");
          final WeatherCacheEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final double _tmpTemperatureC;
            _tmpTemperatureC = _cursor.getDouble(_cursorIndexOfTemperatureC);
            final double _tmpFeelsLikeC;
            _tmpFeelsLikeC = _cursor.getDouble(_cursorIndexOfFeelsLikeC);
            final double _tmpWindSpeedKmh;
            _tmpWindSpeedKmh = _cursor.getDouble(_cursorIndexOfWindSpeedKmh);
            final int _tmpHumidityPercent;
            _tmpHumidityPercent = _cursor.getInt(_cursorIndexOfHumidityPercent);
            final String _tmpSunriseIso;
            _tmpSunriseIso = _cursor.getString(_cursorIndexOfSunriseIso);
            final String _tmpSunsetIso;
            _tmpSunsetIso = _cursor.getString(_cursorIndexOfSunsetIso);
            final long _tmpFetchedAtEpochMs;
            _tmpFetchedAtEpochMs = _cursor.getLong(_cursorIndexOfFetchedAtEpochMs);
            final int _tmpWeatherCode;
            _tmpWeatherCode = _cursor.getInt(_cursorIndexOfWeatherCode);
            _result = new WeatherCacheEntity(_tmpId,_tmpLatitude,_tmpLongitude,_tmpSummary,_tmpTemperatureC,_tmpFeelsLikeC,_tmpWindSpeedKmh,_tmpHumidityPercent,_tmpSunriseIso,_tmpSunsetIso,_tmpFetchedAtEpochMs,_tmpWeatherCode);
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
  public Flow<List<AlarmEntity>> observeAlarms() {
    final String _sql = "SELECT * FROM alarms ORDER BY hour ASC, minute ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"alarms"}, new Callable<List<AlarmEntity>>() {
      @Override
      @NonNull
      public List<AlarmEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfHour = CursorUtil.getColumnIndexOrThrow(_cursor, "hour");
          final int _cursorIndexOfMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "minute");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfRepeatDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatDaily");
          final List<AlarmEntity> _result = new ArrayList<AlarmEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlarmEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final int _tmpHour;
            _tmpHour = _cursor.getInt(_cursorIndexOfHour);
            final int _tmpMinute;
            _tmpMinute = _cursor.getInt(_cursorIndexOfMinute);
            final boolean _tmpEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp != 0;
            final boolean _tmpRepeatDaily;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfRepeatDaily);
            _tmpRepeatDaily = _tmp_1 != 0;
            _item = new AlarmEntity(_tmpId,_tmpTitle,_tmpHour,_tmpMinute,_tmpEnabled,_tmpRepeatDaily);
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
  public Object getAlarmById(final long id, final Continuation<? super AlarmEntity> $completion) {
    final String _sql = "SELECT * FROM alarms WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AlarmEntity>() {
      @Override
      @Nullable
      public AlarmEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfHour = CursorUtil.getColumnIndexOrThrow(_cursor, "hour");
          final int _cursorIndexOfMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "minute");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfRepeatDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatDaily");
          final AlarmEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final int _tmpHour;
            _tmpHour = _cursor.getInt(_cursorIndexOfHour);
            final int _tmpMinute;
            _tmpMinute = _cursor.getInt(_cursorIndexOfMinute);
            final boolean _tmpEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp != 0;
            final boolean _tmpRepeatDaily;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfRepeatDaily);
            _tmpRepeatDaily = _tmp_1 != 0;
            _result = new AlarmEntity(_tmpId,_tmpTitle,_tmpHour,_tmpMinute,_tmpEnabled,_tmpRepeatDaily);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
