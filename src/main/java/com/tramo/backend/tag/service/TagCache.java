package com.tramo.backend.tag.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tramo.backend.tag.entity.Tag;
import com.tramo.backend.tag.repository.TagRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/** Holds every tag row in memory so listing/autocomplete never hits the DB. TTL is a fallback net; writes call invalidate() directly. */
@Component
public class TagCache {

    private static final String KEY = "all";

    private final TagRepository tagRepository;
    private final Cache<String, List<Tag>> cache;

    public TagCache(TagRepository tagRepository,
                     @Value("${app.tags.cache-ttl-seconds:60}") long ttlSeconds) {
        this.tagRepository = tagRepository;
        this.cache = Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(ttlSeconds)).build();
    }

    public List<Tag> all() {
        return cache.get(KEY, k -> tagRepository.findAll());
    }

    public void invalidate() {
        cache.invalidate(KEY);
    }
}
