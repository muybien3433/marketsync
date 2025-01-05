package pl.muybien.scraper;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ScraperController {

    private final ScraperService scraperService;
}
