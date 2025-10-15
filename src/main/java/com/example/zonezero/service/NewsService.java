package com.example.zonezero.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Service providing disaster news articles. Currently returns a static
 * list; replace with an API call in a real implementation.
 */
@Service
public class NewsService {

    public List<Map<String, String>> getLatestNews() {
        List<Map<String, String>> list = new ArrayList<>();
        list.add(create("Major Floods Impact Southeast Asia",
                "Heavy rains have triggered widespread flooding in parts of Thailand and Cambodia, displacing thousands of people and causing significant damage to infrastructure.",
                "https://www.example.com/news/floods-southeast-asia"));
        list.add(create("Wildfire Season Intensifies in California",
                "Multiple blazes are spreading rapidly across northern California as dry conditions persist. Fire crews are working around the clock to contain the flames.",
                "https://www.example.com/news/wildfires-california"));
        list.add(create("Earthquake Strikes Near Tokyo, Minor Damage Reported",
                "A magnitude 5.4 earthquake shook the outskirts of Tokyo early this morning. There were no immediate reports of injuries.",
                "https://www.example.com/news/earthquake-tokyo"));
        list.add(create("Cyclone Approaches India’s East Coast",
                "Meteorological departments warn of a severe cyclone developing in the Bay of Bengal and advise coastal residents to prepare for heavy rain and strong winds.",
                "https://www.example.com/news/cyclone-india"));
        list.add(create("Heatwave Sweeps Across Europe",
                "Record‑breaking temperatures are being recorded across southern and eastern Europe, with authorities urging residents to take precautions against heat stroke.",
                "https://www.example.com/news/heatwave-europe"));
        return list;
    }

    private Map<String, String> create(String title, String description, String url) {
        Map<String, String> m = new HashMap<>();
        m.put("title", title);
        m.put("description", description);
        m.put("url", url);
        return m;
    }
}