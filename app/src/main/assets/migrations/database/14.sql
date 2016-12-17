UPDATE PASSE SET exact=1 WHERE _id IN (SELECT DISTINCT p._id
  FROM PASSE p, ROUND r, TRAINING t
  WHERE p.round=r._id
  AND r.training=t._id
  AND t.exact=1);
