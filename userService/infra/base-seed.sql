-- CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- CREATE TABLE IF NOT EXISTS users(
--                                     id BIGSERIAL PRIMARY KEY,
--                                     name TEXT NOT NULL,
--                                     surname TEXT NOT NULL,
--                                     birth_date DATE NOT NULL,
--                                     email TEXT NOT NULL UNIQUE
-- );

-- INSERT INTO users(name, surname, birth_date, email)
-- SELECT
--     first_names[(gs % array_length(first_names, 1)) + 1],
--     last_names[(gs % array_length(last_names, 1)) + 1],
--     DATE '1950-01-01' + (random() * (DATE '2010-01-01' - DATE '1950-01-01'))::integer,
--         LOWER(
--                 first_names[(gs % array_length(first_names, 1)) + 1] || '.' ||
--                 last_names[(gs % array_length(last_names, 1)) + 1] ||
--                 (gs % 100000)::text || '@' ||
--                              (CASE (gs % 4)
--                                   WHEN 0 THEN 'gmail.com'
--                                   WHEN 1 THEN 'yahoo.com'
--                                   WHEN 2 THEN 'hotmail.com'
--                                   ELSE 'mail.com'
--                                  END)
--             )
-- FROM
--     generate_series(1, 1000) gs,
--     (SELECT ARRAY['Sophia','Emma','Olivia','Ava','Isabella','Mia','Zoe','Lily','Emily','Chloe','Layla','Madison','Madelyn','Abigail','Aubrey','Charlotte','Amelia','Evelyn','Elizabeth','Sofia','Avery','Ella','Scarlett','Grace','Victoria','Riley','Aria','Lillian','Aurora','Natalie','Hannah','Zoey','Penelope','Luna','Mila','Claire','Eleanor','Savannah','Audrey','Brooklyn'] AS first_names) f,
--     (SELECT ARRAY['Miller','Wilson','Moore','Taylor','Anderson','Thomas','Jackson','White','Harris','Martin','Thompson','Garcia','Martinez','Robinson','Clark','Rodriguez','Lewis','Lee','Walker','Hall','Allen','Young','King','Wright','Lopez','Hill','Scott','Green','Adams','Baker','Gonzalez','Nelson','Carter','Mitchell','Perez','Roberts','Turner','Phillips','Campbell','Parker','Evans','Edwards','Collins'] AS last_names) l;

-- CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
-- CREATE INDEX IF NOT EXISTS idx_users_birth_date ON users(birth_date);
-- CREATE INDEX IF NOT EXISTS idx_users_name_surname ON users(name, surname);

-- ANALYZE users;

-- SELECT count(*) FROM users;