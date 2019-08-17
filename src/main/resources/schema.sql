CREATE TABLE USERS (
  id bigint NOT NULL AUTO_INCREMENT,
  first_name varchar(255),
  last_name varchar(255),
  email_id varchar(255),
  password varchar(255),
  active BOOLEAN,
  PRIMARY KEY (id)
);

CREATE TABLE CARD (
    id bigint NOT NULL AUTO_INCREMENT,
    deckId bigint,
  PRIMARY KEY (id)
);

CREATE TABLE DECK (
    id bigint NOT NULL AUTO_INCREMENT,
    bigint bigint,
    ownerId bigint,
    visibleAsPublic BOOLEAN,
    name varchar(255),
    description varchar(255),
    imagesOnCard bigint,
  PRIMARY KEY (id)
)
