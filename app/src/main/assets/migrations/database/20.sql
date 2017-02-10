UPDATE Round SET targetScoringStyle=targetScoringStyle+1
    WHERE targetScoringStyle>0
    AND (targetId<7 OR targetId=26 OR targetId=27);
UPDATE RoundTemplate SET targetScoringStyle=targetScoringStyle+1
    WHERE targetScoringStyle>0
    AND (targetId<7 OR targetId=26 OR targetId=27);