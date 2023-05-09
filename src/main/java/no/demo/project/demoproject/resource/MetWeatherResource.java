package no.demo.project.demoproject.resource;

import no.demo.project.demoproject.integration.MetWeatherApiConsumer;
import no.demo.project.demoproject.model.AlertItem;
import no.demo.project.demoproject.service.MetWeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather-alerts")
public class MetWeatherResource {

    private static final Logger logger = LoggerFactory.getLogger(MetWeatherApiConsumer.class);
    private final MetWeatherService metWeatherService;

    @Autowired
    public MetWeatherResource(MetWeatherService metWeatherService) {
        this.metWeatherService = metWeatherService;
    }

    @GetMapping
    @ResponseBody
    ResponseEntity<List<AlertItem>> readFromAlertFeed(@RequestParam(required = false) String county,
                                                      @RequestParam(required = false) String eventType,
                                                      @RequestParam(required = false) String filter) {
        logger.info("Fetch events with county={}, eventType={}, filter={}", county, eventType, filter);
        return ResponseEntity.ok(metWeatherService.fetchFromFeed(county, eventType, filter));
    }
}
