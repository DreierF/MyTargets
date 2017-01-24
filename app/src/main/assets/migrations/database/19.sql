UPDATE Round SET distance="-1 m" WHERE distance IS NULL;
UPDATE Round SET targetDiameter="-1 cm" WHERE targetDiameter IS NULL;
UPDATE RoundTemplate SET distance="-1 m" WHERE distance IS NULL;
UPDATE RoundTemplate SET targetDiameter="-1 cm" WHERE targetDiameter IS NULL;