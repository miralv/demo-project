package no.demo.project.demoproject.service;

import com.rometools.rome.feed.synd.SyndFeed;
import no.demo.project.demoproject.integration.MetWeatherApiConsumer;
import no.demo.project.demoproject.model.WeatherAlertItem;
import no.demo.project.demoproject.service.mapper.WeatherAlertItemMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static java.util.Optional.ofNullable;

@Service
public class MetWeatherService {

    private final MetWeatherApiConsumer metWeatherApiConsumer;
    private final WeatherAlertItemMapper weatherAlertItemMapper;

    public MetWeatherService(MetWeatherApiConsumer metWeatherApiConsumer, WeatherAlertItemMapper weatherAlertItemMapper) {
        this.metWeatherApiConsumer = metWeatherApiConsumer;
        this.weatherAlertItemMapper = weatherAlertItemMapper;
    }

    public List<WeatherAlertItem> fetchFromFeed(String county,
                                                String eventType,
                                                String filter) {
        SyndFeed metData = metWeatherApiConsumer.getWeatherAlerts(county, eventType);

        return ofNullable(metData)
                .map(SyndFeed::getEntries)
                .orElse(Collections.emptyList())
                .stream()
                .map(weatherAlertItemMapper::toDto)
                .filter(it -> applyFilter(it, filter))
                .toList();
    }

    private boolean applyFilter(WeatherAlertItem weatherAlertItem, String filter) {
        if (!StringUtils.isEmpty(filter)) {
            return weatherAlertItem.toString().toLowerCase().contains(filter.toLowerCase());
        }
        return true;
    }
}
