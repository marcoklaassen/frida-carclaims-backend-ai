package click.klaassen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TtsAudioCacheTest {

    private TtsAudioCache cache;

    @BeforeEach
    void setUp() {
        cache = new TtsAudioCache();
    }

    @Test
    void storeAndRetrieveAudio() {
        byte[] audio = new byte[]{1, 2, 3};
        String id = cache.store(audio);
        assertTrue(cache.get(id).isPresent());
        assertEquals(3, cache.get(id).get().length);
    }

    @Test
    void unknownIdReturnsEmpty() {
        assertTrue(cache.get("nonexistent").isEmpty());
    }

    @Test
    void nullIdReturnsEmpty() {
        assertTrue(cache.get(null).isEmpty());
    }

    @Test
    void storeReturnsDifferentIds() {
        byte[] audio = new byte[]{1};
        String id1 = cache.store(audio);
        String id2 = cache.store(audio);
        assertTrue(!id1.equals(id2));
    }
}
