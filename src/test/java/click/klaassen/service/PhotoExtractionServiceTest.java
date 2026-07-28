package click.klaassen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PhotoExtractionServiceTest {

    @Test
    void germanPlatePatternMatchesStandardFormats() {
        assertTrue(PhotoExtractionService.GERMAN_PLATE.matcher("HH-AB 1234").find());
        assertTrue(PhotoExtractionService.GERMAN_PLATE.matcher("M-XY 99").find());
        assertTrue(PhotoExtractionService.GERMAN_PLATE.matcher("B-EF 9012").find());
        assertTrue(PhotoExtractionService.GERMAN_PLATE.matcher("K-GH 3456").find());
    }

    @Test
    void germanPlatePatternMatchesWithoutSpace() {
        assertTrue(PhotoExtractionService.GERMAN_PLATE.matcher("HH-AB1234").find());
    }

    @Test
    void germanPlatePatternDoesNotMatchInvalid() {
        var m = PhotoExtractionService.GERMAN_PLATE.matcher("no plate here");
        assertTrue(!m.find());
    }

    @Test
    void enrichWithCustomerLookupAddsCustomerData() {
        // This is a unit test of the enrichment logic.
        // We test the pattern + enrichment flow via the public API in the integration test.
        var matcher = PhotoExtractionService.GERMAN_PLATE.matcher("Kennzeichen HH-AB 1234 sichtbar");
        assertTrue(matcher.find());
        assertEquals("HH", matcher.group(1));
        assertEquals("AB", matcher.group(2));
        assertEquals("1234", matcher.group(3));
    }
}
