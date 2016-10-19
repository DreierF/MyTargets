CREATE TABLE IF NOT EXISTS ARROW (
  _id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT,
  length TEXT,
  material TEXT,
  spine TEXT,
  weight TEXT,
  tip_weight TEXT,
  vanes TEXT,
  nock TEXT,
  comment TEXT,
  thumbnail BLOB,
  image TEXT);
ALTER TABLE ROUND ADD COLUMN arrow INTEGER REFERENCES ARROW ON DELETE SET NULL;
ALTER TABLE ROUND ADD COLUMN comment TEXT DEFAULT '';
ALTER TABLE SHOOT ADD COLUMN comment TEXT DEFAULT '';
UPDATE ROUND SET target=0 WHERE target=1 OR target=2 OR target=3;
UPDATE ROUND SET target=2 WHERE target=5 OR target=6 OR target=7;
UPDATE ROUND SET target=3 WHERE target=4;
UPDATE ROUND SET target=4 WHERE target=8;
UPDATE ROUND SET target=5 WHERE target=9;
UPDATE ROUND SET target=6 WHERE target=10;
UPDATE SHOOT SET points=2 WHERE _id IN (SELECT s._id
  FROM ROUND r, PASSE p, SHOOT s LEFT JOIN BOW b ON b._id=r.bow
  WHERE r._id=p.round AND s.passe=p._id
  AND (r.bow=-2 OR b.type=1) AND s.points=1 AND r.target=3)");
