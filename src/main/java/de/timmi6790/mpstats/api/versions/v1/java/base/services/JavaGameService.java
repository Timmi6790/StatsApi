package de.timmi6790.mpstats.api.versions.v1.java.base.services;

import de.timmi6790.mpstats.api.versions.v1.java.base.repository.JavaGameRepository;
import de.timmi6790.mpstats.api.versions.v1.java.base.repository.models.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JavaGameService {
    private final JavaGameRepository javaGameRepository;

    private final Set<String> gameNames = Collections.synchronizedSet(new TreeSet<>(String.CASE_INSENSITIVE_ORDER));

    @Autowired
    public JavaGameService(final JavaGameRepository javaGameRepository) {
        this.javaGameRepository = javaGameRepository;
    }

    public boolean hasGame(final String gameName) {
        return this.gameNames.contains(gameName);
    }

    public List<Game> getGames() {
        return new ArrayList<>();
    }

    public Optional<Game> getGame(final String gameName) {
        return Optional.empty();
    }

    public Game createGame(final String websiteName, final String gameName) {
        return null;
    }

    public void deleteGame(final String gameName) {

    }
}
