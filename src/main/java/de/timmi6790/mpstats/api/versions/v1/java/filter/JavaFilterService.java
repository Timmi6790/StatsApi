package de.timmi6790.mpstats.api.versions.v1.java.filter;

import de.timmi6790.mpstats.api.versions.v1.java.filter.repository.JavaFilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JavaFilterService {
    private final JavaFilterRepository javaFilterRepository;

    @Autowired
    public JavaFilterService(final JavaFilterRepository javaFilterRepository) {
        this.javaFilterRepository = javaFilterRepository;
    }
}
