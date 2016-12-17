ALTER TABLE ARROW ADD COLUMN diameter TEXT DEFAULT '5.0';
ALTER TABLE ARROW ADD COLUMN diameter_unit TEXT DEFAULT 'mm';
ALTER TABLE PASSE ADD COLUMN save_time INTEGER;
UPDATE PASSE
SET save_time=(
  SELECT t.datum + 43200000 + COUNT(p2._id) * 300000
  FROM TRAINING t
  JOIN  ROUND r1 ON r1.training = t._id AND r1._id = PASSE.round
  LEFT OUTER JOIN ROUND r2 ON r2.training = t._id
  LEFT OUTER JOIN PASSE p2 ON r2._id = p2.round AND p2._id < PASSE._id
  GROUP BY t._id, t.datum
);
