ALTER TABLE PASSE ADD COLUMN `index` INTEGER;
UPDATE PASSE
SET `index` = (SELECT COUNT(*)
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
UPDATE VISIER SET distance =  distance || ' ' || unit;