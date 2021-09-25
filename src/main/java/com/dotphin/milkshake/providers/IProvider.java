package com.dotphin.milkshake.providers;

import java.util.List;
import java.util.Map;

import com.dotphin.milkshake.DataQuery;

public interface IProvider {
    /* Connection Methods */
    public IProvider connect(final String connectionURI);

    public IProvider disconnect();

    /* Crud Methods */
    // Create
    public String create(final String entity, final Map<String, Object> props);

    // Read
    public Map<String, Object> findByID(final String entity, final String ID);

    public List<Map<String, Object>> findMany(final String entity, final DataQuery query);

    public Map<String, Object> findOne(final String entity, final DataQuery query);

    // Update
    public DataQuery findByIDAndUpdate(final String entity, final String ID, final DataQuery update);

    public DataQuery[] findManyAndUpdate(final String entity, final DataQuery query, final DataQuery update);

    public DataQuery findOneAndUpdate(final String entity, final String query, final DataQuery update);
}
