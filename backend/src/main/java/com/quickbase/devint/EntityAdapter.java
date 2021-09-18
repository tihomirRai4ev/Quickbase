package com.quickbase.devint;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class EntityAdapter implements IEntityAdapter {

    // Assume the data can be loaded in memory
    public Map<String, Integer> getCountryToPopulationQuery(Connection con) {
        String q = "select country.countryName, sum(city.population) as 'countryPopulation' from city join state on " +
                "city.stateId=state.stateId join country on state.countryId=country.countryId " +
                "group by country.countryName";
        Map<String, Integer> countryToPopulation = new HashMap<>();
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(q);
            while (rs.next()) {
                countryToPopulation.put(rs.getString("countryName"),
                        rs.getInt("countryPopulation"));
            }
        } catch (SQLException e) {
            System.out.println(e);

            throw new RuntimeException(e);
        }

        return countryToPopulation;
    }
}
