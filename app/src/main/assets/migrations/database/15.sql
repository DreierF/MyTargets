UPDATE SHOOT SET arrow=NULL WHERE arrow='-1';
UPDATE PASSE SET exact=1 WHERE _id IN (SELECT DISTINCT p._id
  FROM PASSE p, SHOOT s
  WHERE p._id=s.passe
  AND s.x!=0);
