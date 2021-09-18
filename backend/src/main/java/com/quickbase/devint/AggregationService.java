package com.quickbase.devint;

import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregationService {

    private final IStatService statService;
    private final DBManager dbManager;
    private final IEntityAdapter entityAdapter;

    public AggregationService(IStatService statService, DBManager dbManager, IEntityAdapter entityAdapter) {
        this.statService = statService;
        this.dbManager = dbManager;
        this.entityAdapter = entityAdapter;
    }

    public Map<String, Integer> getAggregatedData() {
        Map<String, Integer> result = new HashMap<>();
        List<Pair<String, Integer>> pairs = statService.GetCountryPopulations();
        // ToDo we can re-use connection if alive as opening is expensive, use threadLocal to keep, revive if closed
        Connection connection = dbManager.getConnection();
        Map<String, Integer> countryToPopulationDbData = entityAdapter.getCountryToPopulationQuery(connection);
        pairs.stream().forEach(pair -> {
            String countryName = pair.getKey();
            int population = pair.getValue();
            if (countryToPopulationDbData.containsKey(countryName)) {
                result.put(countryName, countryToPopulationDbData.get(countryName));
                countryToPopulationDbData.remove(countryName);
            } else {
                result.put(countryName, population);
            }
        });
        // Add the missing data fetched from database.
        result.putAll(countryToPopulationDbData);

        return result;
    }
}
