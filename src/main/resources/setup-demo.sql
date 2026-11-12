-- Initialisation complete de la base de demonstration SIRH.
-- A lancer depuis la base postgres (schema.sql recree sirh2) :
-- psql -h localhost -U postgres -d postgres -f setup-demo.sql
-- Ce script reinitialise sirh2 et efface son contenu existant.

\set ON_ERROR_STOP on

\ir schema.sql
\ir data.sql
\ir data-bloc6.sql
\ir data-demo-confirmation-rh.sql
