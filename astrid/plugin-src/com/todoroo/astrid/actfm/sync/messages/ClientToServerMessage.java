package com.todoroo.astrid.actfm.sync.messages;

import org.json.JSONObject;

import com.todoroo.andlib.data.AbstractModel;
import com.todoroo.andlib.data.Table;
import com.todoroo.astrid.dao.DaoReflectionHelpers;
import com.todoroo.astrid.dao.RemoteModelDao;
import com.todoroo.astrid.data.RemoteModel;

@SuppressWarnings("nls")
public abstract class ClientToServerMessage<TYPE extends RemoteModel> {

    protected final Class<TYPE> modelClass;
    protected final Table table;
    protected final long id;
    protected final String uuid;
    protected final long pushedAt;

    public static final String TYPE_KEY = "type";
    public static final String TABLE_KEY = "table";
    public static final String UUID_KEY = "uuid";
    public static final String PUSHED_AT_KEY = "pushed_at";

    public ClientToServerMessage(Class<TYPE> modelClass, String uuid, long pushedAt) {
        this.modelClass = modelClass;
        this.table = DaoReflectionHelpers.getStaticFieldByReflection(modelClass, Table.class, "TABLE");
        this.uuid = uuid;
        this.pushedAt = pushedAt;
        this.id = AbstractModel.NO_ID;
    }

    public ClientToServerMessage(long id, Class<TYPE> modelClass, RemoteModelDao<TYPE> modelDao) {
        this.id = id;
        this.modelClass = modelClass;
        this.table = DaoReflectionHelpers.getStaticFieldByReflection(modelClass, Table.class, "TABLE");

        TYPE entity = getEntity(id, modelDao);
        if (entity == null) {
            this.uuid = RemoteModel.NO_UUID;
            this.pushedAt = 0;
        } else {
            this.uuid = entity.getValue(RemoteModel.UUID_PROPERTY);
            this.pushedAt = entity.getValue(RemoteModel.PUSHED_AT_PROPERTY);
        }
    }

    private TYPE getEntity(long localId, RemoteModelDao<TYPE> modelDao) {
        return modelDao.fetch(localId, RemoteModel.UUID_PROPERTY, RemoteModel.PUSHED_AT_PROPERTY);
    }

    public final String getUUID() {
        return uuid;
    }

    public final long getPushedAt() {
        return pushedAt;
    }

    public abstract JSONObject serializeToJSON();

}
