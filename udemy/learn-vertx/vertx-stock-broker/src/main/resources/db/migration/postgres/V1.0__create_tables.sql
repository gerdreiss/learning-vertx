BEGIN;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS assets (
  symbol VARCHAR PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS quotes (
  bid NUMERIC,
  ask NUMERIC,
  last_price NUMERIC,
  volume NUMERIC,
  asset VARCHAR,
  FOREIGN KEY (asset) REFERENCES assets(symbol),
  CONSTRAINT last_price_is_positive CHECK (last_price > 0),
  CONSTRAINT volume_is_positive CHECK (volume > 0)
);

INSERT INTO assets (symbol) VALUES ('AAPL');
INSERT INTO assets (symbol) VALUES ('AMZN');
INSERT INTO assets (symbol) VALUES ('FB');
INSERT INTO assets (symbol) VALUES ('GOOG');
INSERT INTO assets (symbol) VALUES ('MSTF');
INSERT INTO assets (symbol) VALUES ('NFLX');
INSERT INTO assets (symbol) VALUES ('TSLA');

INSERT INTO quotes (bid, ask, last_price, volume, asset)
VALUES (random(), random(), random(), random(), 'AAPL');

INSERT INTO quotes (bid, ask, last_price, volume, asset)
VALUES (random(), random(), random(), random(), 'AMZN');

INSERT INTO quotes (bid, ask, last_price, volume, asset)
VALUES (random(), random(), random(), random(), 'FB');

INSERT INTO quotes (bid, ask, last_price, volume, asset)
VALUES (random(), random(), random(), random(), 'GOOG');

INSERT INTO quotes (bid, ask, last_price, volume, asset)
VALUES (random(), random(), random(), random(), 'MSTF');

INSERT INTO quotes (bid, ask, last_price, volume, asset)
VALUES (random(), random(), random(), random(), 'NFLX');

INSERT INTO quotes (bid, ask, last_price, volume, asset)
VALUES (random(), random(), random(), random(), 'TSLA');

COMMIT;
