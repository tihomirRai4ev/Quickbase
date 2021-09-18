package com.quickbase.devint;

import java.sql.Connection;
import java.util.Map;

public interface IEntityAdapter {
    Map<String, Integer> getCountryToPopulationQuery(Connection con);
}
