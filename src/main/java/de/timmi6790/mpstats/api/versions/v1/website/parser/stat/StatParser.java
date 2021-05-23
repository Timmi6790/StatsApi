package de.timmi6790.mpstats.api.versions.v1.website.parser.stat;

import org.jsoup.nodes.Document;

public interface StatParser {
    ParserResult parse(Document document);
}
