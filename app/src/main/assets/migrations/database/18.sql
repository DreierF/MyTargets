-- Rename existing tables
ALTER TABLE ARROW RENAME TO ARROW_OLD;
ALTER TABLE BOW RENAME TO BOW_OLD;
ALTER TABLE PASSE RENAME TO PASSE_OLD;
ALTER TABLE ROUND RENAME TO ROUND_OLD;
ALTER TABLE ROUND_TEMPLATE RENAME TO ROUND_TEMPLATE_OLD;
ALTER TABLE SHOOT RENAME TO SHOOT_OLD;
ALTER TABLE STANDARD_ROUND_TEMPLATE RENAME TO STANDARD_ROUND_TEMPLATE_OLD;
ALTER TABLE TRAINING RENAME TO TRAINING_OLD;
ALTER TABLE VISIER RENAME TO VISIER_OLD;

-- Arrow migration
CREATE TABLE IF NOT EXISTS `Arrow`(
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `name` TEXT,
    `length` TEXT,
    `material` TEXT,
    `spine` TEXT,
    `weight` TEXT,
    `tipWeight` TEXT,
    `vanes` TEXT,
    `nock` TEXT,
    `comment` TEXT,
    `diameter` TEXT,
    `thumbnail` BLOB,
    `imageFile` TEXT
);
INSERT INTO `Arrow`
    SELECT `_id`,`name`,`length`,`material`,`spine`,
    `weight`,`tip_weight`,`vanes`,`nock`,
    `comment`,`diameter` || ' ' || `diameter_unit`,
    `thumbnail`,`image`
    FROM ARROW_OLD;

-- Bow migration
CREATE TABLE IF NOT EXISTS `Bow`(
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `name` TEXT,
    `type` INTEGER,
    `brand` TEXT,
    `size` TEXT,
    `braceHeight` TEXT,
    `tiller` TEXT,
    `limbs` TEXT,
    `sight` TEXT,
    `drawWeight` TEXT,
    `stabilizer` TEXT,
    `clicker` TEXT,
    `description` TEXT,
    `thumbnail` BLOB,
    `imageFile` TEXT
);
INSERT INTO `Bow`
    SELECT `_id`,`name`,`type`,`brand`,`size`,
           `height`,`tiller`,`limbs`,`sight`,
           `draw_weight`,`stabilizer`,`clicker`,
           `description`,`thumbnail`,`image`
    FROM BOW_OLD;

-- StandardRound migration
CREATE TABLE IF NOT EXISTS `StandardRound`(
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `club` INTEGER,
    `name` TEXT
);
INSERT INTO `StandardRound`
    SELECT `_id`,`club`,`name`
    FROM STANDARD_ROUND_TEMPLATE_OLD
    WHERE `club` < 512;

-- RoundTemplate migration
CREATE TABLE IF NOT EXISTS `RoundTemplate`(
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `standardRound` INTEGER,
    `index` INTEGER,
    `shotsPerEnd` INTEGER,
    `endCount` INTEGER,
    `distance` TEXT,
    `targetId` INTEGER,
    `targetScoringStyle` INTEGER,
    `targetDiameter` TEXT,
    FOREIGN KEY(`standardRound`) REFERENCES StandardRound(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);
INSERT INTO `RoundTemplate`
    SELECT `_id`,`sid`,`r_index`,`arrows`,`passes`,
    `distance` || ' ' || `unit`,
    `target`,`scoring_style`,`size` || ' ' || `target_unit`
    FROM ROUND_TEMPLATE_OLD;

-- Training migration
CREATE TABLE IF NOT EXISTS `Training`(
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `title` TEXT,
    `date` INTEGER,
    `standardRound` INTEGER,
    `bow` INTEGER,
    `arrow` INTEGER,
    `arrowNumbering` INTEGER,
    `timePerEnd` INTEGER,
    `indoor` INTEGER,
    `weather` INTEGER,
    `windDirection` INTEGER,
    `windSpeed` INTEGER,
    `location` TEXT,
    FOREIGN KEY(`standardRound`) REFERENCES StandardRound(`_id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
    FOREIGN KEY(`bow`) REFERENCES Bow(`_id`) ON UPDATE NO ACTION ON DELETE SET NULL,
    FOREIGN KEY(`arrow`) REFERENCES Arrow(`_id`) ON UPDATE NO ACTION ON DELETE SET NULL
);
UPDATE TRAINING_OLD SET bow = null WHERE bow < 1;
UPDATE TRAINING_OLD SET arrow = null WHERE arrow < 1;

INSERT INTO `Training`
    SELECT t.`_id`,t.`title`,t.`datum`,
    CASE WHEN s.club < 512 THEN t.`standard_round` ELSE NULL END,
    t.`bow`,t.`arrow`, t.`arrow_numbering`,t.`time`,
    s.`indoor`,t.`weather`,t.`wind_direction`,t.`wind_speed`,t.`location`
    FROM TRAINING_OLD t, STANDARD_ROUND_TEMPLATE_OLD s
    WHERE t.standard_round=s._id;

-- Round migration
CREATE TABLE IF NOT EXISTS `Round`(
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `training` INTEGER,
    `index` INTEGER,
    `shotsPerEnd` INTEGER,
    `maxEndCount` INTEGER,
    `distance` TEXT,
    `comment` TEXT,
    `targetId` INTEGER,
    `targetScoringStyle` INTEGER,
    `targetDiameter` TEXT,
    FOREIGN KEY(`training`) REFERENCES Training(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);
INSERT INTO `Round`
    SELECT r.`_id`,r.`training`,t.`r_index`,t.`passes`,
    CASE WHEN s.club = 512 THEN NULL ELSE t.`arrows` END,
    t.`distance` || ' ' || t.`unit`, r.`comment`,r.`target`,r.`scoring_style`,
    t.`size` || ' ' || t.`target_unit`
    FROM ROUND_OLD r, ROUND_TEMPLATE_OLD t, STANDARD_ROUND_TEMPLATE_OLD s
    WHERE r.template=t._id
    AND s._id=t.sid;

-- End migration
CREATE TABLE IF NOT EXISTS `End`(
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `index` INTEGER,
    `images` TEXT,
    `round` INTEGER,
    `exact` INTEGER,
    `saveTime` INTEGER,
    FOREIGN KEY(`round`) REFERENCES Round(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);
INSERT INTO `End`
SELECT `_id`, (SELECT COUNT(*)
                FROM PASSE_OLD p
                WHERE PASSE_OLD.round = p.round
                AND PASSE_OLD._id > p._id),
       `image`,`round`,`exact`,`save_time`
FROM PASSE_OLD;

-- Shot migration
CREATE TABLE IF NOT EXISTS `Shot`(
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `index` INTEGER,
    `end` INTEGER,
    `x` REAL,
    `y` REAL,
    `scoringRing` INTEGER,
    `comment` TEXT,
    `arrowNumber` TEXT,
    FOREIGN KEY(`end`) REFERENCES End(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
);
INSERT INTO `Shot`
    SELECT `_id`,`arrow_index`,`passe`,`x`,`y`,`points`,`comment`,`arrow`
    FROM SHOOT_OLD;

-- SightMark migration
CREATE TABLE IF NOT EXISTS `SightMark`(
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `bow` INTEGER,
    `distance` TEXT,
    `value` TEXT,
    FOREIGN KEY(`bow`) REFERENCES Bow(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);
INSERT INTO `SightMark`
    SELECT `_id`,`bow`,`distance` || ' ' || `unit`,`setting`
    FROM VISIER_OLD;

-- Remove old tables
DROP TABLE ARROW_OLD;
DROP TABLE BOW_OLD;
DROP TABLE NUMBER;
DROP TABLE PASSE_OLD;
DROP TABLE ROUND_OLD;
DROP TABLE ROUND_TEMPLATE_OLD;
DROP TABLE SHOOT_OLD;
DROP TABLE STANDARD_ROUND_TEMPLATE_OLD;
DROP TABLE TRAINING_OLD;
DROP TABLE VISIER_OLD;