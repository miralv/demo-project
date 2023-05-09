package no.demo.project.demoproject.service.mapper;

import com.rometools.rome.feed.synd.SyndEntry;
import no.demo.project.demoproject.model.AlertItem;
import org.springframework.stereotype.Component;

@Component
public class FeedItemMapper {

    public AlertItem toDto(SyndEntry syndEntry) {
        return new AlertItem(syndEntry.getTitle(), syndEntry.getDescription().getValue(), syndEntry.getLink());
    }
}
