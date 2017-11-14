-- Rename existing tables
ALTER TABLE `Shot` RENAME TO SHOT_OLD;
ALTER TABLE `End` RENAME TO END_OLD;
ALTER TABLE `Training` RENAME TO TRAINING_OLD;

-- Training migration
CREATE TABLE IF NOT EXISTS `Training`(
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `title` TEXT,
    `date` TEXT,
    `standardRound` INTEGER,
    `bow` INTEGER,
    `arrow` INTEGER,
    `arrowNumbering` INTEGER,
    `indoor` INTEGER,
    `weather` INTEGER,
    `windDirection` INTEGER,
    `windSpeed` INTEGER,
    `location` TEXT,
    `comment` TEXT,
    FOREIGN KEY(`standardRound`) REFERENCES StandardRound(`_id`) ON UPDATE NO ACTION ON DELETE SET NULL,
    FOREIGN KEY(`bow`) REFERENCES Bow(`_id`) ON UPDATE NO ACTION ON DELETE SET NULL,
    FOREIGN KEY(`arrow`) REFERENCES Arrow(`_id`) ON UPDATE NO ACTION ON DELETE SET NULL
);

INSERT INTO `Training`
    SELECT `_id`, `title`, date(`date`/1000, 'unixepoch', 'localtime'),
    `standardRound`, `bow`, `arrow`,
    `arrowNumbering`, `indoor`,
    `weather`, `windDirection`, `windSpeed`, `location`, ''
    FROM TRAINING_OLD;

-- End migration
CREATE TABLE IF NOT EXISTS `End`(
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `index` INTEGER,
    `round` INTEGER,
    `exact` INTEGER,
    `saveTime` TEXT,
    `comment` TEXT,
    FOREIGN KEY(`round`) REFERENCES Round(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);

INSERT INTO `End`
SELECT e.`_id`, e.`index`, e.`round`, e.`exact`, time(e.`saveTime`/1000, 'unixepoch', 'localtime'),
    TRIM(GROUP_CONCAT(s.`comment`, x'0a'), x'0a' || " ")
FROM END_OLD e LEFT OUTER JOIN SHOT_OLD s ON s.`end`=e._id
GROUP BY e._id;

CREATE TABLE IF NOT EXISTS `Shot`(
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `index` INTEGER,
    `end` INTEGER,
    `x` REAL,
    `y` REAL,
    `scoringRing` INTEGER,
    `arrowNumber` TEXT,
    FOREIGN KEY(`end`) REFERENCES End(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);
INSERT INTO `Shot`
    SELECT `_id`,`index`,`end`,`x`,`y`,`scoringRing`,`arrowNumber`
    FROM SHOT_OLD;

-- Remove old tables
DROP TABLE SHOT_OLD;
DROP TABLE END_OLD;
DROP TABLE TRAINING_OLD;
