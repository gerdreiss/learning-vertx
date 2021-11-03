BEGIN;

CREATE TABLE IF NOT EXISTS watchlists
(
  account_id VARCHAR,
  asset      VARCHAR,
  foreign key (asset) references assets (symbol),
  primary key (account_id, asset)
);

COMMIT;
