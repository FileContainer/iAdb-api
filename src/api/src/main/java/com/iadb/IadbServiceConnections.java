package com.iadb;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class IadbServiceConnections {

    private static final Map<String, IadbServiceConnection> CACHE = Collections.synchronizedMap(new HashMap<>());

    @NonNull
    static IadbServiceConnection get(Iadb.UserServiceArgs args) {
        String key = args.tag != null ? args.tag : args.componentName.getClassName();
        IadbServiceConnection connection = CACHE.get(key);

        if (connection == null) {
            connection = new IadbServiceConnection(args);
            CACHE.put(key, connection);
        }
        return connection;
    }

    static void remove(IadbServiceConnection connection) {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, IadbServiceConnection> entry : CACHE.entrySet()) {
            if (entry.getValue() == connection) {
                keys.add(entry.getKey());
            }
        }
        for (String key : keys) {
            CACHE.remove(key);
        }
    }
}
