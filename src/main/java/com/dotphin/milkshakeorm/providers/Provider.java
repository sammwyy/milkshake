package com.dotphin.milkshakeorm.providers;

import java.util.List;
import java.util.Map;

import com.dotphin.milkshakeorm.repository.FindOption;
import com.dotphin.milkshakeorm.utils.URI;

public interface Provider {

    /* Database management */
    public Provider connect(final URI uri);

    public void disconnect();

    public void prepare(final String entity, final Map<String, String> model);

    /* Create */
    public String create(final String entity, final Map<String, Object> props);

    /* Read */
    public List<Map<String, Object>> findMany(final String entity, final Map<String, Object> filter,
            final FindOption options);

    public Map<String, Object> findOne(final String entity, final Map<String, Object> filter);

    public Map<String, Object> findByID(final String entity, final String id);

    /* Update */
    public long updateMany(final String entity, final Map<String, Object> filter, final Map<String, Object> update);

    public boolean updateOne(final String entity, final Map<String, Object> filter, final Map<String, Object> update);

    public boolean updateByID(final String entity, final String id, final Map<String, Object> update);

    /* Delete */
    public long deleteMany(final String entity, final Map<String, Object> filter);

    public boolean deleteOne(final String entity, final Map<String, Object> filter);

    public boolean deleteByID(final String entity, final String id);
}
