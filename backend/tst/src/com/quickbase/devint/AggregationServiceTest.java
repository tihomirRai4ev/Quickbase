package src.com.quickbase.devint;

import com.quickbase.devint.AggregationService;
import com.quickbase.devint.IEntityAdapter;
import com.quickbase.devint.IStatService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ToDo sorry for been minimalistic and not using the frameworks (mockito and ~junit)
public class AggregationServiceTest {

    private MockIStatService statService = new MockIStatService();
    private MockEntityAdapter entityAdapter = new MockEntityAdapter();
    private AggregationService aggregationService = new AggregationService(statService, () -> null, entityAdapter);

    @Test
    public void whenHavingOnlyOverlappingCountries_populationFromDatabaseIsUsed() {
        List<Pair<String, Integer>> output = new ArrayList<Pair<String, Integer>>();
        output.add(new ImmutablePair<>("India", 3));
        output.add(new ImmutablePair<>("United Kingdom", 3));
        statService.setData(output);
        Map<String, Integer> dbData = new HashMap<>();
        dbData.put("India", 4);
        dbData.put("United Kingdom", 4);
        entityAdapter.setData(dbData);
        Map<String, Integer> aggregatedData = aggregationService.getAggregatedData();

        assert(aggregatedData.size() == 2);
        assert(aggregatedData.get("India") == 4);
        assert(aggregatedData.get("United Kingdom") == 4);
    }

    @Test
    public void whenNonOverlappingCountries_dataIsAggregatedFromBothSources() {
        List<Pair<String, Integer>> output = new ArrayList<Pair<String, Integer>>();
        output.add(new ImmutablePair<>("India", 3));
        output.add(new ImmutablePair<>("United Kingdom", 3));
        statService.setData(output);
        Map<String, Integer> dbData = new HashMap<>();
        dbData.put("Pakistan", 4);
        dbData.put("USA", 4);
        entityAdapter.setData(dbData);
        Map<String, Integer> aggregatedData = aggregationService.getAggregatedData();

        assert(aggregatedData.size() == 4);
        assert(aggregatedData.get("India") == 3);
        assert(aggregatedData.get("United Kingdom") == 3);
        assert(aggregatedData.get("USA") == 4);
        assert(aggregatedData.get("Pakistan") == 4);
    }

    @Test
    public void whenRestApiReturnsEmptyResponse_onlyDataFromDatabaseIsUsed() {
        statService.setData(new ArrayList<>());
        Map<String, Integer> dbData = new HashMap<>();
        dbData.put("Pakistan", 4);
        dbData.put("USA", 4);
        entityAdapter.setData(dbData);
        Map<String, Integer> aggregatedData = aggregationService.getAggregatedData();

        assert(aggregatedData.size() == 2);
        assert(aggregatedData.get("USA") == 4);
        assert(aggregatedData.get("Pakistan") == 4);
    }

    @Test
    public void whenDataFromDatabaseIsEmpty_onlyDataFromRestEndpointIsUsed() {
        List<Pair<String, Integer>> output = new ArrayList<Pair<String, Integer>>();
        output.add(new ImmutablePair<>("India", 3));
        output.add(new ImmutablePair<>("United Kingdom", 3));
        statService.setData(output);
        entityAdapter.setData(new HashMap<>());
        Map<String, Integer> aggregatedData = aggregationService.getAggregatedData();

        assert(aggregatedData.size() == 2);
        assert(aggregatedData.get("India") == 3);
        assert(aggregatedData.get("United Kingdom") == 3);
    }

    @Test
    public void whenBothSourcesReturnEmptyResponse_emptyMapIsReturned() {
        statService.setData(new ArrayList<>());
        entityAdapter.setData(new HashMap<>());
        Map<String, Integer> aggregatedData = aggregationService.getAggregatedData();

        assert(aggregatedData.isEmpty());
    }

    //// Private section

    private class MockIStatService implements IStatService {

        private List<Pair<String, Integer>> data;

        @Override
        public List<Pair<String, Integer>> GetCountryPopulations() {
            return this.data;
        }

        public List<Pair<String, Integer>> getData() {
            return data;
        }

        public void setData(List<Pair<String, Integer>> data) {
            this.data = data;
        }
    }


    private class MockEntityAdapter implements IEntityAdapter {
        private static Map<String, Integer> data = new HashMap<>();

        @Override
        public Map<String, Integer> getCountryToPopulationQuery(Connection con) {
            return this.data;
        }

        public Map<String, Integer> getData() {
            return data;
        }

        public void setData(Map<String, Integer> data) {
            MockEntityAdapter.data = data;
        }
    }
}
