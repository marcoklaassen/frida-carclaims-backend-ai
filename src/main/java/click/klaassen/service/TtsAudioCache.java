package click.klaassen.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class TtsAudioCache {

    private static final Duration TTL = Duration.ofMinutes(5);
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public String store(byte[] audio) {
        evictExpired();
        String id = UUID.randomUUID().toString();
        cache.put(id, new CacheEntry(audio, Instant.now()));
        return id;
    }

    public Optional<byte[]> get(String id) {
        if (id == null) {
            return Optional.empty();
        }
        CacheEntry entry = cache.get(id);
        if (entry == null) {
            return Optional.empty();
        }
        if (entry.isExpired()) {
            cache.remove(id);
            return Optional.empty();
        }
        return Optional.of(entry.audio());
    }

    private void evictExpired() {
        Instant cutoff = Instant.now().minus(TTL);
        cache.entrySet().removeIf(e -> e.getValue().createdAt().isBefore(cutoff));
    }

    record CacheEntry(byte[] audio, Instant createdAt) {
        boolean isExpired() {
            return Instant.now().isAfter(createdAt.plus(TTL));
        }
    }
}
