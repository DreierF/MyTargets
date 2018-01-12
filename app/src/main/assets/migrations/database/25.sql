UPDATE Training SET weather = "" WHERE weather = NULL;
UPDATE Training SET location = "" WHERE location = NULL;
DELETE FROM StandardRound WHERE (SELECT COUNT(r._id) FROM RoundTemplate r WHERE r.standardRound=StandardRound._id)=0
