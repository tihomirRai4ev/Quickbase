package com.quickbase;

import com.quickbase.devint.*;

import java.sql.Connection;
import java.util.Map;

/**
 * The main method of the executable JAR generated from this repository. This is to let you
 * execute something from the command-line or IDE for the purposes of demonstration, but you can choose
 * to demonstrate in a different way (e.g. if you're using a framework)
 */
public class Main {
    public static void main( String args[] ) {
        System.out.println("Starting.");
        System.out.print("Getting DB Connection...");
        DBManager dbm = new DBManagerImpl();
        IStatService statService = new ConcreteStatService();
        IEntityAdapter entityAdapter = new EntityAdapter();
        AggregationService aggregationService = new AggregationService(statService, dbm, entityAdapter);
        Map<String, Integer> aggregatedData = aggregationService.getAggregatedData();
        System.out.println("Countries size after aggregation: " + aggregatedData.size());
    }
}