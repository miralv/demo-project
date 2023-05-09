package no.demo.project.demoproject.integration;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URL;

import static java.util.Optional.ofNullable;

@Service
public class MetWeatherApiConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MetWeatherApiConsumer.class);
    private final String baseUrl;

    @Autowired
    public MetWeatherApiConsumer(@Value("${integration.endpoint.met-weather-api}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public SyndFeed getMetData(String county, String eventType) {

        try {
            String uriString = UriComponentsBuilder.fromUriString(baseUrl)
                    .queryParamIfPresent("county", ofNullable(county))
                    .queryParamIfPresent("event", ofNullable(eventType))
                    .toUriString();

            URL feedSource = new URL(uriString);
            SyndFeedInput input = new SyndFeedInput();
            logger.info("Fetch data from url={}", uriString);
            SyndFeed feed = input.build(new XmlReader(feedSource));
            logger.info("Received the following data: {}", feed);
            return feed;
        } catch (IOException | FeedException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
