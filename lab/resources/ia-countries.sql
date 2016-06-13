DROP TABLE IF EXISTS Country;

CREATE TABLE Country (
  Code char(3) NOT NULL DEFAULT '',
  Name char(52) NOT NULL DEFAULT '',
  Capital char(35) NOT NULL DEFAULT '',
  PRIMARY KEY (Code)
) ;


INSERT INTO Country VALUES ('CA','Canada','Ottawa');
INSERT INTO Country VALUES ('CN','China','Beijing');
INSERT INTO Country VALUES ('FR','France','Paris');
INSERT INTO Country VALUES ('IN','India','New Delhi');
INSERT INTO Country VALUES ('NL','Netherlands','Amsterdam');
INSERT INTO Country VALUES ('RU','Russia','Moscow');
INSERT INTO Country VALUES ('SE','Sweden','Stockholm');
INSERT INTO Country VALUES ('US','United States of America','Washington, DC');
