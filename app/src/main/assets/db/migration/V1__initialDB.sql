
CREATE TABLE food(
    id              CHAR(36) PRIMARY KEY NOT NULL,
    name            VARCHAR(1024) NOT NULL UNIQUE,
    kcal_per100g    INTEGER NOT NULL,
    fat_per100g     DECIMAL(4,1) NOT NULL,
    carbo_per100g   DECIMAL(4,1) NOT NULL,
    sugar_per100g   DECIMAL(4,1) NOT NULL,
    protein_per100g DECIMAL(4,1) NOT NULL,
    default_amount_g DECIMAL(7,1) NOT NULL
);

CREATE TABLE nutrition(
    id              CHAR(36) PRIMARY KEY NOT NULL,
    food_id         CHAR(36) NOT NULL REFERENCES food,
    ts              DATETIME NOT NULL DEFAULT CURRENT,
    amount_g        DECIMAL(7,1) NOT NULL
);
