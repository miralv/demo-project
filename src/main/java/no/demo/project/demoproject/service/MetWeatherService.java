package no.demo.project.demoproject.service;

import com.rometools.rome.feed.synd.SyndFeed;
import no.demo.project.demoproject.integration.MetWeatherApiConsumer;
import no.demo.project.demoproject.model.AlertItem;
import no.demo.project.demoproject.service.mapper.FeedItemMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static java.util.Optional.ofNullable;

@Service
public class MetWeatherService {

    private final MetWeatherApiConsumer metWeatherApiConsumer;
    private final FeedItemMapper feedItemMapper;

    public MetWeatherService(MetWeatherApiConsumer metWeatherApiConsumer, FeedItemMapper feedItemMapper) {
        this.metWeatherApiConsumer = metWeatherApiConsumer;
        this.feedItemMapper = feedItemMapper;
    }

    public List<AlertItem> fetchFromFeed(String county,
                                         String eventType,
                                         String filter) {
        SyndFeed metData = metWeatherApiConsumer.getMetData(county, eventType);

        return ofNullable(metData)
                .map(SyndFeed::getEntries)
                .orElse(Collections.emptyList())
                .stream()
                .map(feedItemMapper::toDto)
                .filter(it -> applyFilter(it, filter))
                .toList();
    }

    private boolean applyFilter(AlertItem alertItem, String filter) {
        if (!StringUtils.isEmpty(filter)) {
            return alertItem.toString().toLowerCase().contains(filter.toLowerCase());
        }
        return true;
    }
}
