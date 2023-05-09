package no.demo.project.demoproject.service;

import com.rometools.rome.feed.synd.*;
import no.demo.project.demoproject.integration.MetWeatherApiConsumer;
import no.demo.project.demoproject.model.AlertItem;
import no.demo.project.demoproject.service.mapper.FeedItemMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class MetWeatherServiceTest {

    private static final String COUNTY = "42";
    private static final String EVENT_TYPE = "wind";
    private static final String TITLE_FIRE = "Skogbrannfare ventes, gult nivå, Kysten av Norland sør for Bodø";
    private static final String DESCRIPTION_FIRE = "Alert: Det er ventet lokal gress- og lyngbrannfare i snøfrie områder  " +
            "langs kysten sør i Nordland inntil det kommer nedbør av betydning.";
    private static final String TITLE_WIND = "Kuling, gult nivå, Slettnes fyr - Vardø";
    private static final String DESCRIPTION_WIND = "Alert: Tirsdag morgen økning til vest eller nordvest stiv kuling 15 m/s, " +
            "minkende vind tirsdag kveld.";

    @Mock
    private MetWeatherApiConsumer metWeatherApiConsumer;
    @Mock
    private FeedItemMapper feedItemMapper;

    @InjectMocks
    MetWeatherService metWeatherService;

    @ParameterizedTest
    @MethodSource("createInputAndExpectedOutput")
    void fetchFromFeed(String title, String filter, List<AlertItem> expectedResult) {
        SyndEntry syndEntryFire = createSyndEntry(TITLE_FIRE, DESCRIPTION_FIRE);
        SyndEntry syndEntryWind = createSyndEntry(TITLE_WIND, DESCRIPTION_WIND);
        SyndFeed syndFeed = createSyndFeed(List.of(syndEntryWind, syndEntryFire));

        Mockito.when(metWeatherApiConsumer.getMetData(COUNTY, EVENT_TYPE))
                .thenReturn(syndFeed);
        Mockito.when(feedItemMapper.toDto(syndEntryWind))
                .thenReturn(createFeedItem(TITLE_WIND, DESCRIPTION_WIND));
        Mockito.when(feedItemMapper.toDto(syndEntryFire))
                .thenReturn(createFeedItem(TITLE_FIRE, DESCRIPTION_FIRE));

        List<AlertItem> result = metWeatherService.fetchFromFeed(COUNTY, EVENT_TYPE, filter);

        Assertions.assertThat(result)
                .isNotNull()
                .containsExactlyElementsOf(expectedResult);
    }

    private static Stream<Arguments> createInputAndExpectedOutput() {
        AlertItem alertItemWind = createFeedItem(TITLE_WIND, DESCRIPTION_WIND);
        AlertItem alertItemFire = createFeedItem(TITLE_FIRE, DESCRIPTION_FIRE);

        return Stream.of(
                Arguments.of("Should return 1 element", "vind", List.of(alertItemWind)),
                Arguments.of("Should return 2 elements", "GUL", List.of(alertItemWind, alertItemFire)),
                Arguments.of("Should return 2 elements", null, List.of(alertItemWind, alertItemFire)),
                Arguments.of("Should return 0 elements", "random", Collections.emptyList())
        );
    }

    private static SyndFeed createSyndFeed(List<SyndEntry> syndEntries) {
        SyndFeed syndFeed = new SyndFeedImpl();
        syndFeed.setEntries(syndEntries);
        return syndFeed;
    }

    private static SyndEntry createSyndEntry(String title, String description) {
        SyndEntryImpl syndEntry = new SyndEntryImpl();
        SyndContentImpl syndContent = new SyndContentImpl();
        syndContent.setValue(description);
        syndEntry.setTitle(title);
        syndEntry.setDescription(syndContent);
        return syndEntry;
    }

    private static AlertItem createFeedItem(String title, String description) {
        return new AlertItem(title, description, null);
    }
}