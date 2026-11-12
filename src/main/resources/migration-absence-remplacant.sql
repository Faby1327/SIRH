-- Ajoute le remplaçant choisi sur une absence, sans recréer la base.
\set ON_ERROR_STOP on

BEGIN;

ALTER TABLE absence ADD COLUMN IF NOT EXISTS remplacant_id INT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_absence_remplacant'
          AND conrelid = 'absence'::regclass
    ) THEN
        ALTER TABLE absence
            ADD CONSTRAINT fk_absence_remplacant
            FOREIGN KEY (remplacant_id) REFERENCES employe(id);
    END IF;
END $$;

COMMIT;
