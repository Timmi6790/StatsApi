CREATE TABLE `bedrock_filter`
(
    `id`             int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `leaderboard_id` int(11) UNSIGNED NULL,
    `player_id`      int(11) UNSIGNED NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `bedrock_leaderboard`
(
    `id`          int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `game_name`   varchar(50)      NULL,
    `last_update` datetime         NULL DEFAULT CURRENT_TIMESTAMP,
    `deprecated`  bit(1)           NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `bedrock_leaderboard_save`
(
    `id`                  int(11) UNSIGNED     NOT NULL AUTO_INCREMENT,
    `leaderboard_save_id` int(11) UNSIGNED     NULL,
    `player_id`           int(11) UNSIGNED     NULL,
    `score`               bigint(255) UNSIGNED NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `bedrock_leaderboard_save_id`
(
    `id`             int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `leaderboard_id` int(11) UNSIGNED NULL,
    `datetime`       datetime         NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `bedrock_leaderboard_save_id_leaderboard_id_datetime` (`leaderboard_id`, `datetime`) USING BTREE
);

CREATE TABLE `bedrock_player`
(
    `id`          int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `player_name` varchar(32)      NOT NULL,
    PRIMARY KEY (`id`, `player_name`),
    UNIQUE INDEX `bedrockPlayer-name` (`player_name`) USING HASH
);

CREATE TABLE `java_board`
(
    `id`          int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `board_name`  varchar(50)      NOT NULL,
    `update_time` int(11) UNSIGNED NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `java_board_board_name` (`board_name`) USING HASH,
    INDEX `java_board_update_time` (`update_time`) USING BTREE
);

CREATE TABLE `java_board_alias`
(
    `id`         int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `board_id`   int(11) UNSIGNED NOT NULL,
    `alias_name` varchar(50)      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `java_board_alias_alias_name` (`alias_name`) USING HASH
);

CREATE TABLE `java_filter`
(
    `id`             int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `leaderboard_id` int(11) UNSIGNED NOT NULL,
    `player_id`      int(11) UNSIGNED NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `java_stats_filter_player_id_leaderboard_id` (`player_id`, `leaderboard_id`) USING HASH
);

CREATE TABLE `java_game`
(
    `id`           int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `game_name`    varchar(50)      NOT NULL,
    `website_name` varchar(50)      NOT NULL,
    `category_id`  int(11) UNSIGNED NOT NULL,
    `wiki_url`     varchar(120)     NULL,
    `description`  varchar(255)     NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `java_game_game_name` (`game_name`) USING HASH
);

CREATE TABLE `java_game_alias`
(
    `id`         int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `game_id`    int(11) UNSIGNED NOT NULL,
    `alias_name` varchar(50)      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `java_game_alias_alias_name` (`alias_name`) USING HASH
);

CREATE TABLE `java_game_category`
(
    `id`            int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `category_name` varchar(50)      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `java_game_category_category_name` (`category_name`) USING HASH
);

CREATE TABLE `java_group`
(
    `id`          int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `group_name`  varchar(50)      NOT NULL,
    `description` varchar(255)     NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `java_group_name` (`group_name`) USING HASH
);

CREATE TABLE `java_group_alias`
(
    `id`         int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `group_id`   int(11) UNSIGNED NOT NULL,
    `alias_name` varchar(50)      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `java_group_alias_alias_name` (`alias_name`) USING HASH
);

CREATE TABLE `java_group_game`
(
    `id`       int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `group_id` int(11) UNSIGNED NOT NULL,
    `game_id`  int(11) UNSIGNED NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `java_leaderboad`
(
    `id`          int(11) UNSIGNED NOT NULL DEFAULT '1970-01-01 00:00:00' AUTO_INCREMENT,
    `game_id`     int(11) UNSIGNED NOT NULL,
    `stat_id`     int(11) UNSIGNED NOT NULL,
    `board_id`    int(11) UNSIGNED NOT NULL,
    `deprecated`  bit(1)           NOT NULL,
    `last_update` datetime         NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
);

CREATE TABLE `java_leaderboard_save_id`
(
    `id`             int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `leaderboard_id` int(11) UNSIGNED NOT NULL,
    `datetime`       datetime         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `javaLeaderboardSaveIDs-leaderboard_id` (`leaderboard_id`) USING HASH,
    UNIQUE INDEX `javaLeaderboardSaveIDs-leaderboard_id-datetime` (`leaderboard_id`, `datetime`) USING BTREE
);

CREATE TABLE `java_leaderboard_save_new`
(
    `id`                  bigint(25) UNSIGNED  NOT NULL AUTO_INCREMENT,
    `leaderboard_save_id` int(11) UNSIGNED     NOT NULL,
    `player_id`           int(11) UNSIGNED     NOT NULL,
    `score`               bigint(255) UNSIGNED NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `javaLeaderboardSaves-loaderboard_save_id` (`leaderboard_save_id`) USING HASH
);

CREATE TABLE `java_player`
(
    `id`          int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `uuid`        binary(16)       NOT NULL,
    `player_name` varchar(16)      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `javaPlayer-uuid_uniq` (`uuid`) USING HASH
);

CREATE TABLE `java_stat`
(
    `id`           int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `website_name` varchar(50)      NOT NULL,
    `stat_name`    varchar(50)      NULL,
    `display_name` varchar(50)      NULL,
    `description`  varchar(255)     NULL,
    `achievement`  bit(1)           NULL DEFAULT b'0',
    PRIMARY KEY (`id`)
);

CREATE TABLE `java_stat_alias`
(
    `id`         int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `stat_id`    int(11) UNSIGNED NOT NULL,
    `alias_name` varchar(50)      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `java_stat_alias_stat_id_alias_name` (`stat_id`, `alias_name`) USING HASH
);

ALTER TABLE `bedrock_filter`
    ADD CONSTRAINT `bedrockStatsFilter-player_id` FOREIGN KEY (`player_id`) REFERENCES `bedrock_player` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `bedrock_filter`
    ADD CONSTRAINT `bedrockStatsFilter-leaderboard_id` FOREIGN KEY (`leaderboard_id`) REFERENCES `bedrock_leaderboard` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `bedrock_leaderboard_save`
    ADD CONSTRAINT `bedrockLeaderboardSaves-player_id` FOREIGN KEY (`player_id`) REFERENCES `bedrock_player` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `bedrock_leaderboard_save`
    ADD CONSTRAINT `bedrockLeaderboardSaves-leaderboard_save_id` FOREIGN KEY (`leaderboard_save_id`) REFERENCES `bedrock_leaderboard_save_id` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `bedrock_leaderboard_save_id`
    ADD CONSTRAINT `bedrockLeaderboardSaveIDs-leaderboard_id` FOREIGN KEY (`leaderboard_id`) REFERENCES `bedrock_leaderboard` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `java_board_alias`
    ADD CONSTRAINT `java_board_alias_board_id` FOREIGN KEY (`board_id`) REFERENCES `java_board` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `java_filter`
    ADD CONSTRAINT `stats-filter_mc-players_id` FOREIGN KEY (`player_id`) REFERENCES `java_player` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `java_filter`
    ADD CONSTRAINT `stats-filter-leaderboard_id` FOREIGN KEY (`leaderboard_id`) REFERENCES `java_leaderboad` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `java_game`
    ADD CONSTRAINT `java_game_category_id` FOREIGN KEY (`category_id`) REFERENCES `java_game_category` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `java_game_alias`
    ADD CONSTRAINT `java_game_alias_game_id` FOREIGN KEY (`id`) REFERENCES `java_game` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `java_group_alias`
    ADD CONSTRAINT `java_group_alias_group_id` FOREIGN KEY (`group_id`) REFERENCES `java_group` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `java_group_game`
    ADD CONSTRAINT `java_group_game-group_id` FOREIGN KEY (`group_id`) REFERENCES `java_group` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `java_group_game`
    ADD CONSTRAINT `java_group_game_game_id` FOREIGN KEY (`game_id`) REFERENCES `java_game` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `java_leaderboad`
    ADD CONSTRAINT `java_leaderboard_game_id` FOREIGN KEY (`game_id`) REFERENCES `java_game` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `java_leaderboad`
    ADD CONSTRAINT `java_leaderboard_stat_id` FOREIGN KEY (`stat_id`) REFERENCES `java_stat` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE `java_leaderboad`
    ADD CONSTRAINT `java_leaderboard_board_id` FOREIGN KEY (`board_id`) REFERENCES `java_board` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `java_leaderboard_save_id`
    ADD CONSTRAINT `leaderboard-save-id_leaderboard_id` FOREIGN KEY (`leaderboard_id`) REFERENCES `java_leaderboad` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `java_leaderboard_save_new`
    ADD CONSTRAINT `leaderboard-stats_mc-players_id` FOREIGN KEY (`player_id`) REFERENCES `java_player` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `java_leaderboard_save_new`
    ADD CONSTRAINT `leaderboard-stats_leaderboard_save_id` FOREIGN KEY (`leaderboard_save_id`) REFERENCES `java_leaderboard_save_id` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `java_stat_alias`
    ADD CONSTRAINT `java_stat_alias_stat_id` FOREIGN KEY (`stat_id`) REFERENCES `java_stat` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;