package io.abdiresul.coronavirustracker.services;

import io.abdiresul.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataService {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationStats> allStats = new ArrayList<>();
    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData(){
        HttpClient client  = HttpClient.newHttpClient();
        HttpRequest request = (HttpRequest) HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
        List<LocationStats> newStats = new ArrayList<>();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println(httpResponse.body());
            StringReader in = new StringReader(httpResponse.body());
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);

            for (CSVRecord record : records) {
                String state = record.get("Province/State");
                String country = record.get("Country/Region");
                Integer lastState = Integer.valueOf(record.get(record.size()-1));
                Integer prevDayState = Integer.valueOf(record.get(record.size()-2));

//                System.out.println("State: "+state + "  Country: "+country);
                LocationStats lStat = new LocationStats(state,country,lastState);
                lStat.setDiffFromPrevDay(lastState - prevDayState);
//                System.out.println(lStat.toString());
                newStats.add(lStat);
//                String name = record.get("Name");
            }
            this.allStats = newStats;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<LocationStats> getAllStats() {
        return allStats;
    }
}
