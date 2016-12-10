ALTER TABLE PASSE ADD COLUMN `endIndex` INTEGER;
UPDATE PASSE
SET `endIndex` = (SELECT COUNT(*)
  FROM PASSE p
  WHERE PASSE.round = p.round
  AND PASSE._id > p._id);
ALTER TABLE ROUND ADD COLUMN `size`;
UPDATE ROUND
SET `size` = (SELECT t.size || ' ' || t.target_unit
  FROM ROUND_TEMPLATE t
  WHERE template = t._id);
UPDATE ARROW SET diameter = diameter || ' ' || diameter_unit;
UPDATE ROUND_TEMPLATE SET distance = distance || ' ' || unit;
UPDATE ROUND_TEMPLATE SET `size` =  `size` || ' ' || target_unit;
ALTER TABLE ROUND ADD COLUMN `roundIndex` INTEGER;
ALTER TABLE ROUND ADD COLUMN `shotsPerEnd` INTEGER;
ALTER TABLE ROUND ADD COLUMN `endCount` INTEGER;
ALTER TABLE ROUND ADD COLUMN `distance` TEXT;
UPDATE ROUND
SET `roundIndex` = (SELECT t.r_index
  FROM ROUND_TEMPLATE t
  WHERE template = t._id);
UPDATE ROUND
SET `shotsPerEnd` = (SELECT t.passes
  FROM ROUND_TEMPLATE t
  WHERE template = t._id);
UPDATE ROUND
SET `endCount` = (SELECT t.arrows
  FROM ROUND_TEMPLATE t
  WHERE template = t._id);
UPDATE ROUND
SET `distance` = (SELECT t.distance
  FROM ROUND_TEMPLATE t
  WHERE template = t._id);
UPDATE VISIER SET distance =  distance || ' ' || unit;
DROP TABLE NUMBER;
UPDATE TRAINING
SET bow = null
WHERE bow < 1;
UPDATE TRAINING
SET arrow = null
WHERE arrow < 1;
UPDATE TRAINING
SET arrow = null
WHERE arrow < 1;

ALTER TABLE TRAINING ADD COLUMN `endCount` INTEGER;
UPDATE TRAINING
SET `indoor` = (SELECT t.indoor
  FROM STANDARD_ROUND t
  WHERE standard_round = t._id);

ALTER TABLE TRAINING RENAME TO TRAINING_OLD;



INSERT INTO Training SELECT * FROM TRAINING_OLD;
DROP TABLE TRAINING_OLD;