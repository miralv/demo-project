package no.demo.project.demoproject.service.mapper;

import com.rometools.rome.feed.synd.SyndEntry;
import no.demo.project.demoproject.model.WeatherAlertItem;
import org.springframework.stereotype.Component;

@Component
public class WeatherAlertItemMapper {

    public WeatherAlertItem toDto(SyndEntry syndEntry) {
        return new WeatherAlertItem(syndEntry.getTitle(), syndEntry.getDescription().getValue(), syndEntry.getLink());
    }
}
