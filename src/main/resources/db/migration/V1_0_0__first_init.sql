CREATE SCHEMA bedrock;
CREATE SCHEMA java;
CREATE SCHEMA java_group;

CREATE TABLE "bedrock"."board_alias"
(
    "board_id"   int4         NOT NULL,
    "alias_name" varchar(255) NOT NULL
);
CREATE UNIQUE INDEX "board_alias-alias_name-lower" ON "bedrock"."board_alias" USING btree (
                                                                                           LOWER(alias_name)
    );

CREATE TABLE "bedrock"."boards"
(
    "id"           serial4,
    "website_name" varchar(255) NOT NULL,
    "board_name"   varchar(255) NOT NULL,
    "clean_name"   varchar(255) NOT NULL,
    "update_time"  int4         NOT NULL,
    CONSTRAINT "1" PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "boards-board_name_lower" ON "bedrock"."boards" USING btree (
                                                                                 LOWER(board_name)
    );

CREATE TABLE "bedrock"."filter_reasons"
(
    "id"     serial4,
    "reason" text NOT NULL,
    PRIMARY KEY ("id"),
    CONSTRAINT "filter_reasons-reason" UNIQUE ("reason")
);

CREATE TABLE "bedrock"."filters"
(
    "id"             serial4,
    "player_id"      int4 NOT NULL,
    "leaderboard_id" int4 NOT NULL,
    "reason_id"      int4 NOT NULL,
    "filter_start"   timestamptz,
    "filter_end"     timestamptz,
    PRIMARY KEY ("id")
);

CREATE TABLE "bedrock"."game_alias"
(
    "game_id"    int4         NOT NULL,
    "alias_name" varchar(255) NOT NULL
);
CREATE UNIQUE INDEX "game_alias-alias_name" ON "bedrock"."game_alias" USING btree (
                                                                                   LOWER(alias_name)
    );

CREATE TABLE "bedrock"."game_category"
(
    "id"            serial4,
    "category_name" varchar(255) NOT NULL,
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "game_category-category_name-lower" ON "bedrock"."game_category" USING btree (
                                                                                                  LOWER(category_name)
    );

CREATE TABLE "bedrock"."games"
(
    "id"           serial4,
    "category_id"  int4         NOT NULL,
    "website_name" varchar(255) NOT NULL,
    "game_name"    varchar(255) NOT NULL,
    "clean_name"   varchar(255) NOT NULL,
    "description"  text,
    "wiki_url"     varchar(255),
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "games-game_name_lower" ON "bedrock"."games" USING btree (
                                                                              LOWER(game_name)
    );

CREATE TABLE "bedrock"."leaderboard_save_ids"
(
    "save_time"      timestamptz NOT NULL,
    "id"             serial4,
    "leaderboard_id" int4        NOT NULL,
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "leaderboard_save_ids-save_time-leaderboard_id" ON "bedrock"."leaderboard_save_ids" (
                                                                                                         "leaderboard_id",
                                                                                                         "save_time"
                                                                                                         DESC
    );

CREATE TABLE "bedrock"."leaderboard_saves"
(
    "leaderboard_save_id" int4 NOT NULL,
    "player_id"           int4 NOT NULL,
    "score"               int8 NOT NULL
);

CREATE TABLE "bedrock"."leaderboards"
(
    "id"                serial4,
    "game_id"           int4        NOT NULL,
    "stat_id"           int4        NOT NULL,
    "board_id"          int4        NOT NULL,
    "deprecated"        boolean     NOT NULL,
    "last_update"       timestamptz NOT NULL DEFAULT TO_TIMESTAMP(0),
    "last_cache_update" timestamptz NOT NULL DEFAULT TO_TIMESTAMP(0),
    PRIMARY KEY ("id"),
    CONSTRAINT "leaderboards-game-stat-board" UNIQUE ("game_id", "stat_id", "board_id")
);

CREATE TABLE "bedrock"."players"
(
    "id"          serial4,
    "player_name" varchar(32) NOT NULL,
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "players-player_name_lower" ON "bedrock"."players" (
                                                                        LOWER(player_name)
    );

CREATE TABLE "bedrock"."stat_alias"
(
    "stat_id"    int4         NOT NULL,
    "alias_name" varchar(255) NOT NULL
);
CREATE UNIQUE INDEX "stat_alias-alias_name-lower" ON "bedrock"."stat_alias" USING btree (
                                                                                         LOWER(alias_name)
    );

CREATE TABLE "bedrock"."stat_types"
(
    "id"        serial4,
    "type_name" text NOT NULL,
    PRIMARY KEY ("id"),
    CONSTRAINT "stat_types-type" UNIQUE ("type_name")
);

CREATE TABLE "bedrock"."stats"
(
    "id"               serial4,
    "website_name"     varchar(255) NOT NULL,
    "stat_name"        varchar(255) NOT NULL,
    "clean_name"       varchar(255) NOT NULL,
    "description"      text,
    "achievement"      bool         NOT NULL,
    "type_id"          int4         NOT NULL DEFAULT 1,
    "sorting_priority" int4         NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "stats-stat_name_lower" ON "bedrock"."stats" USING btree (
                                                                              LOWER(stat_name)
    );

CREATE TABLE "java"."board_alias"
(
    "board_id"   int4         NOT NULL,
    "alias_name" varchar(255) NOT NULL
);
CREATE UNIQUE INDEX "board_alias-alias_name-lower" ON "java"."board_alias" USING btree (
                                                                                        LOWER(alias_name)
    );

CREATE TABLE "java"."boards"
(
    "id"           serial4,
    "website_name" varchar(255) NOT NULL,
    "board_name"   varchar(255) NOT NULL,
    "clean_name"   varchar(255) NOT NULL,
    "update_time"  int4         NOT NULL,
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "boards-board_name_lower" ON "java"."boards" USING btree (
                                                                              LOWER(board_name)
    );

CREATE TABLE "java"."filter_reasons"
(
    "id"     serial4,
    "reason" text NOT NULL,
    PRIMARY KEY ("id"),
    CONSTRAINT "filter_reasons-reason" UNIQUE ("reason")
);

CREATE TABLE "java"."filters"
(
    "id"             serial4,
    "player_id"      int4 NOT NULL,
    "leaderboard_id" int4 NOT NULL,
    "reason_id"      int4 NOT NULL,
    "filter_start"   timestamptz,
    "filter_end"     timestamptz,
    PRIMARY KEY ("id")
);

CREATE TABLE "java"."game_alias"
(
    "game_id"    int4         NOT NULL,
    "alias_name" varchar(255) NOT NULL
);
CREATE UNIQUE INDEX "game_alias-alias_name-lower" ON "java"."game_alias" USING btree (
                                                                                      LOWER(alias_name)
    );

CREATE TABLE "java"."game_category"
(
    "id"            serial4,
    "category_name" varchar(255) NOT NULL,
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "game_category-category_name-lower" ON "java"."game_category" USING btree (
                                                                                               LOWER(category_name)
    );

CREATE TABLE "java"."games"
(
    "id"           serial4,
    "category_id"  int4         NOT NULL,
    "website_name" varchar(255) NOT NULL,
    "game_name"    varchar(255) NOT NULL,
    "clean_name"   varchar(255) NOT NULL,
    "description"  text,
    "wiki_url"     varchar(255),
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "games-game_name_lower" ON "java"."games" USING btree (
                                                                           LOWER(game_name)
    );

CREATE TABLE "java"."leaderboard_save_ids"
(
    "save_time"      timestamptz NOT NULL,
    "id"             serial4,
    "leaderboard_id" int4        NOT NULL,
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "leaderboard_save_ids-save_time-leaderboard_id" ON "java"."leaderboard_save_ids" (
                                                                                                      "leaderboard_id",
                                                                                                      "save_time" DESC
    );

CREATE TABLE "java"."leaderboard_saves"
(
    "leaderboard_save_id" int4 NOT NULL,
    "player_id"           int4 NOT NULL,
    "score"               int8 NOT NULL
);

CREATE TABLE "java"."leaderboards"
(
    "id"                serial4,
    "game_id"           int4        NOT NULL,
    "stat_id"           int4        NOT NULL,
    "board_id"          int4        NOT NULL,
    "deprecated"        boolean     NOT NULL,
    "last_update"       timestamptz NOT NULL DEFAULT TO_TIMESTAMP(0),
    "last_cache_update" timestamptz NOT NULL DEFAULT TO_TIMESTAMP(0),
    PRIMARY KEY ("id"),
    CONSTRAINT "leaderboards-game-stat-board" UNIQUE ("game_id", "stat_id", "board_id")
);

CREATE TABLE "java"."players"
(
    "id"          serial4,
    "player_uuid" uuid        NOT NULL,
    "player_name" varchar(16) NOT NULL,
    PRIMARY KEY ("id"),
    CONSTRAINT "player_uuid-uniq" UNIQUE ("player_uuid")
);

CREATE TABLE "java"."stat_alias"
(
    "stat_id"    int4         NOT NULL,
    "alias_name" varchar(255) NOT NULL
);
CREATE UNIQUE INDEX "stat_alias-alias_name-lower" ON "java"."stat_alias" USING btree (
                                                                                      LOWER(alias_name)
    );

CREATE TABLE "java"."stat_types"
(
    "id"        serial4,
    "type_name" text NOT NULL,
    PRIMARY KEY ("id"),
    CONSTRAINT "stat_types-type" UNIQUE ("type_name")
);

CREATE TABLE "java"."stats"
(
    "id"               serial4,
    "website_name"     varchar(255) NOT NULL,
    "stat_name"        varchar(255) NOT NULL,
    "clean_name"       varchar(255) NOT NULL,
    "description"      text,
    "achievement"      bool         NOT NULL,
    "type_id"          int4         NOT NULL DEFAULT 1,
    "sorting_priority" int4         NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "stats-stat_name_lower" ON "java"."stats" USING btree (
                                                                           LOWER(stat_name)
    );

CREATE TABLE "java_group"."group_alias_names"
(
    "group_id"   int4         NOT NULL,
    "alias_name" varchar(255) NOT NULL
);
CREATE UNIQUE INDEX "group_alias_names-alias_name-lower" ON "java_group"."group_alias_names" USING btree (
                                                                                                          LOWER(alias_name)
    );

CREATE TABLE "java_group"."group_games"
(
    "group_id" int4 NOT NULL,
    "game_id"  int4 NOT NULL,
    CONSTRAINT "group_games-group_id-game_id" UNIQUE ("group_id", "game_id")
);

CREATE TABLE "java_group"."groups"
(
    "id"                serial4,
    "group_name"        varchar(255) NOT NULL,
    "clean_name"        varchar(255) NOT NULL,
    "group_description" varchar(255),
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "groups-group_name_lower" ON "java_group"."groups" USING btree (
                                                                                    LOWER(group_name)
    );

DROP INDEX "bedrock"."board_alias-alias_name-lower";
ALTER TABLE "bedrock"."board_alias"
    ADD CONSTRAINT "board_alias-board_id-boards" FOREIGN KEY ("board_id") REFERENCES "bedrock"."boards" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
CREATE UNIQUE INDEX "board_alias-alias_name-lower" ON "bedrock"."board_alias" USING btree (
                                                                                           LOWER(alias_name)
    );
DROP INDEX "bedrock"."boards-board_name_lower";
CREATE UNIQUE INDEX "boards-board_name_lower" ON "bedrock"."boards" USING btree (
                                                                                 LOWER(board_name)
    );
ALTER TABLE "bedrock"."filters"
    ADD CONSTRAINT "filters-player_id-players-id" FOREIGN KEY ("player_id") REFERENCES "bedrock"."players" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "bedrock"."filters"
    ADD CONSTRAINT "filters-leaderboard_id-leaderboards-id" FOREIGN KEY ("leaderboard_id") REFERENCES "bedrock"."leaderboards" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "bedrock"."filters"
    ADD CONSTRAINT "filters_reason_id-filter_reasons-id" FOREIGN KEY ("reason_id") REFERENCES "bedrock"."filter_reasons" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
DROP INDEX "bedrock"."game_alias-alias_name";
ALTER TABLE "bedrock"."game_alias"
    ADD CONSTRAINT "game_alias-alias_name-games-game_name" FOREIGN KEY ("game_id") REFERENCES "bedrock"."games" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
CREATE UNIQUE INDEX "game_alias-alias_name" ON "bedrock"."game_alias" USING btree (
                                                                                   LOWER(alias_name)
    );
DROP INDEX "bedrock"."game_category-category_name-lower";
CREATE UNIQUE INDEX "game_category-category_name-lower" ON "bedrock"."game_category" USING btree (
                                                                                                  LOWER(category_name)
    );
DROP INDEX "bedrock"."games-game_name_lower";
ALTER TABLE "bedrock"."games"
    ADD CONSTRAINT "games-category_id-game_category-id" FOREIGN KEY ("category_id") REFERENCES "bedrock"."game_category" ("id") ON DELETE SET NULL ON UPDATE NO ACTION;
CREATE UNIQUE INDEX "games-game_name_lower" ON "bedrock"."games" USING btree (
                                                                              LOWER(game_name)
    );
DROP INDEX "bedrock"."leaderboard_save_ids-save_time-leaderboard_id";
ALTER TABLE "bedrock"."leaderboard_save_ids"
    ADD CONSTRAINT "leaderboard_save_ids-leaderboard_id-leaderboards-id" FOREIGN KEY ("leaderboard_id") REFERENCES "bedrock"."leaderboards" ON DELETE CASCADE ON UPDATE NO ACTION;
CREATE UNIQUE INDEX "leaderboard_save_ids-save_time-leaderboard_id" ON "bedrock"."leaderboard_save_ids" (
                                                                                                         "leaderboard_id",
                                                                                                         "save_time"
                                                                                                         DESC
    );
ALTER TABLE "bedrock"."leaderboard_saves"
    ADD CONSTRAINT "leaderboard_saves-leaderboard_save_id-leaderboard_save_ids-id" FOREIGN KEY ("leaderboard_save_id") REFERENCES "bedrock"."leaderboard_save_ids" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "bedrock"."leaderboard_saves"
    ADD CONSTRAINT "leaderboard_saves-player_id-players_id" FOREIGN KEY ("player_id") REFERENCES "bedrock"."players" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "bedrock"."leaderboards"
    ADD CONSTRAINT "leaderboards_game_id-games_id" FOREIGN KEY ("game_id") REFERENCES "bedrock"."games" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "bedrock"."leaderboards"
    ADD CONSTRAINT "leaderboards_stat_id-stats_id" FOREIGN KEY ("stat_id") REFERENCES "bedrock"."stats" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "bedrock"."leaderboards"
    ADD CONSTRAINT "leaderboards_board_id-boards_id" FOREIGN KEY ("board_id") REFERENCES "bedrock"."boards" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
DROP INDEX "bedrock"."players-player_name_lower";
CREATE UNIQUE INDEX "players-player_name_lower" ON "bedrock"."players" (
                                                                        LOWER(player_name)
    );
DROP INDEX "bedrock"."stat_alias-alias_name-lower";
ALTER TABLE "bedrock"."stat_alias"
    ADD CONSTRAINT "stat_alias-stat_id-stats-id" FOREIGN KEY ("stat_id") REFERENCES "bedrock"."stats" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
CREATE UNIQUE INDEX "stat_alias-alias_name-lower" ON "bedrock"."stat_alias" USING btree (
                                                                                         LOWER(alias_name)
    );
DROP INDEX "bedrock"."stats-stat_name_lower";
ALTER TABLE "bedrock"."stats"
    ADD CONSTRAINT "stats-type-stat_types-id" FOREIGN KEY ("type_id") REFERENCES "bedrock"."stat_types" ("id") ON DELETE SET DEFAULT ON UPDATE NO ACTION;
CREATE UNIQUE INDEX "stats-stat_name_lower" ON "bedrock"."stats" USING btree (
                                                                              LOWER(stat_name)
    );
DROP INDEX "java"."board_alias-alias_name-lower";
ALTER TABLE "java"."board_alias"
    ADD CONSTRAINT "board_alias-board_id-boards-id" FOREIGN KEY ("board_id") REFERENCES "java"."boards" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
CREATE UNIQUE INDEX "board_alias-alias_name-lower" ON "java"."board_alias" USING btree (
                                                                                        LOWER(alias_name)
    );
DROP INDEX "java"."boards-board_name_lower";
CREATE UNIQUE INDEX "boards-board_name_lower" ON "java"."boards" USING btree (
                                                                              LOWER(board_name)
    );
ALTER TABLE "java"."filters"
    ADD CONSTRAINT "filters_player_id-players_id" FOREIGN KEY ("player_id") REFERENCES "java"."players" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "java"."filters"
    ADD CONSTRAINT "filters_leaderboard_id-leaderboards_id" FOREIGN KEY ("leaderboard_id") REFERENCES "java"."leaderboards" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "java"."filters"
    ADD CONSTRAINT "filters_reason_id-filter_reasons-id" FOREIGN KEY ("reason_id") REFERENCES "java"."filter_reasons" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
DROP INDEX "java"."game_alias-alias_name-lower";
ALTER TABLE "java"."game_alias"
    ADD CONSTRAINT "game_alias_game_id-games_id" FOREIGN KEY ("game_id") REFERENCES "java"."games" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
CREATE UNIQUE INDEX "game_alias-alias_name-lower" ON "java"."game_alias" USING btree (
                                                                                      LOWER(alias_name)
    );
DROP INDEX "java"."game_category-category_name-lower";
CREATE UNIQUE INDEX "game_category-category_name-lower" ON "java"."game_category" USING btree (
                                                                                               LOWER(category_name)
    );
DROP INDEX "java"."games-game_name_lower";
ALTER TABLE "java"."games"
    ADD CONSTRAINT "games-category_id-game_category-id" FOREIGN KEY ("category_id") REFERENCES "java"."game_category" ("id") ON DELETE SET NULL ON UPDATE NO ACTION;
CREATE UNIQUE INDEX "games-game_name_lower" ON "java"."games" USING btree (
                                                                           LOWER(game_name)
    );
DROP INDEX "java"."leaderboard_save_ids-save_time-leaderboard_id";
ALTER TABLE "java"."leaderboard_save_ids"
    ADD CONSTRAINT "leaderboard_save_ids_leaderboard_id-leaderboards_id" FOREIGN KEY ("leaderboard_id") REFERENCES "java"."leaderboards" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
CREATE UNIQUE INDEX "leaderboard_save_ids-save_time-leaderboard_id" ON "java"."leaderboard_save_ids" (
                                                                                                      "leaderboard_id",
                                                                                                      "save_time" DESC
    );
ALTER TABLE "java"."leaderboard_saves"
    ADD CONSTRAINT "leaderboard_saves-player_id-players_id" FOREIGN KEY ("player_id") REFERENCES "java"."players" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "java"."leaderboard_saves"
    ADD CONSTRAINT "leaderboard_saves-leaderboard_save_id-leaderboard_save_ids_id" FOREIGN KEY ("leaderboard_save_id") REFERENCES "java"."leaderboard_save_ids" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "java"."leaderboards"
    ADD CONSTRAINT "leaderboards_game_id-games_id" FOREIGN KEY ("game_id") REFERENCES "java"."games" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "java"."leaderboards"
    ADD CONSTRAINT "leaderboards_stat_id-stats_id" FOREIGN KEY ("stat_id") REFERENCES "java"."stats" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "java"."leaderboards"
    ADD CONSTRAINT "leaderboards_board_id-boards_id" FOREIGN KEY ("board_id") REFERENCES "java"."boards" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
DROP INDEX "java"."stat_alias-alias_name-lower";
ALTER TABLE "java"."stat_alias"
    ADD CONSTRAINT "stat_alias-stat_id-stats-id" FOREIGN KEY ("stat_id") REFERENCES "java"."stats" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
CREATE UNIQUE INDEX "stat_alias-alias_name-lower" ON "java"."stat_alias" USING btree (
                                                                                      LOWER(alias_name)
    );
DROP INDEX "java"."stats-stat_name_lower";
ALTER TABLE "java"."stats"
    ADD CONSTRAINT "stats-type-stat_types-id" FOREIGN KEY ("type_id") REFERENCES "java"."stat_types" ("id") ON DELETE SET DEFAULT ON UPDATE NO ACTION;
CREATE UNIQUE INDEX "stats-stat_name_lower" ON "java"."stats" USING btree (
                                                                           LOWER(stat_name)
    );
DROP INDEX "java_group"."group_alias_names-alias_name-lower";
ALTER TABLE "java_group"."group_alias_names"
    ADD CONSTRAINT "group_alias_names-group_id-groups-id" FOREIGN KEY ("group_id") REFERENCES "java_group"."groups" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
CREATE UNIQUE INDEX "group_alias_names-alias_name-lower" ON "java_group"."group_alias_names" USING btree (
                                                                                                          LOWER(alias_name)
    );
ALTER TABLE "java_group"."group_games"
    ADD CONSTRAINT "group_games-group_id-groups_id" FOREIGN KEY ("group_id") REFERENCES "java_group"."groups" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "java_group"."group_games"
    ADD CONSTRAINT "group_games-game_id-games_id" FOREIGN KEY ("game_id") REFERENCES "java"."games" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
DROP INDEX "java_group"."groups-group_name_lower";
CREATE UNIQUE INDEX "groups-group_name_lower" ON "java_group"."groups" USING btree (
                                                                                    LOWER(group_name)
    );