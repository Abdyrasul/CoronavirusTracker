package io.abdiresul.coronavirustracker.controllers;

import io.abdiresul.coronavirustracker.models.LocationStats;
import io.abdiresul.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.IntStream;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model){
        List<LocationStats> allStats = coronaVirusDataService.getAllStats();

        Integer totalCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        model.addAttribute( "locationsStats",  allStats);
        model.addAttribute( "totalReportedCases",  totalCases);
        return "home";
    }
}
