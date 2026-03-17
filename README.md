ALTER TABLE oviuser
ALTER COLUMN email SET NOT NULL;

ALTER TABLE oviuser
ALTER COLUMN username SET NOT NULL;

ALTER TABLE instructor
RENAME COLUMN id_instructor TO IDNumber;

ALTER TABLE instructor
DROP CONSTRAINT ak_instructor_phone;

ALTER TABLE pap_pati
ALTER COLUMN email TYPE VARCHAR(100);

ALTER TABLE instructor
ADD COLUMN username VARCHAR(50);

ALTER TABLE instructor
ADD COLUMN userpassword VARCHAR(255);

ALTER TABLE pap_pati
ADD COLUMN username VARCHAR(50);

ALTER TABLE pap_pati
ADD COLUMN userpassword VARCHAR(255);

ALTER TABLE instructor
ALTER COLUMN idnumber TYPE CHAR(10);

ALTER TABLE oviuser
ALTER COLUMN idnumber TYPE CHAR(10);


CREATE TABLE activity (
    idnumber        CHAR(10)            NOT NULL,
    date            DATE                NOT NULL,
    time            TIME                NOT NULL,
    location        VARCHAR(150)        NOT NULL,
    capacity        INTEGER             NOT NULL,
    price           DOUBLE PRECISION    NOT NULL DEFAULT 0,
    description     VARCHAR(500),

    CONSTRAINT pk_activity PRIMARY KEY (idnumber),
    CONSTRAINT ak_activity_datetime_location UNIQUE (date, time, location)
);

CREATE TABLE imparts (
    idactivity      CHAR(10)    NOT NULL,
    idinstructor    CHAR(10)    NOT NULL,

    CONSTRAINT pk_imparts PRIMARY KEY (idactivity, idinstructor),

    CONSTRAINT fk_imparts_activity
        FOREIGN KEY (idactivity)
        REFERENCES activity(idnumber)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_imparts_instructor
        FOREIGN KEY (idinstructor)
        REFERENCES instructor(idnumber)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE external_activity_assistants (
    idnumber        CHAR(10)            NOT NULL,
    name            VARCHAR(50)         NOT NULL,
    surname         VARCHAR(50)         NOT NULL,
    email           VARCHAR(100)        NOT NULL,
    phonenumber     VARCHAR(15)         NOT NULL,

    CONSTRAINT pk_external_assistant PRIMARY KEY (idnumber),
    CONSTRAINT ak_external_assistant_email UNIQUE (email)
);

CREATE TABLE inscription (
    idnumber        CHAR(10)        NOT NULL,
    idovi           CHAR(10),
    idpap           CHAR(10),
    idext           CHAR(10),
    idactivity      CHAR(10)        NOT NULL,

    CONSTRAINT pk_inscription PRIMARY KEY (idnumber),

    CONSTRAINT fk_inscription_ovi
        FOREIGN KEY (idovi)
        REFERENCES oviuser(idnumber)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_inscription_pap
        FOREIGN KEY (idpap)
        REFERENCES pap_pati(idnumber)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_inscription_ext
        FOREIGN KEY (idext)
        REFERENCES external_activity_assistants(idnumber)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_inscription_activity
        FOREIGN KEY (idactivity)
        REFERENCES activity(idnumber)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT ck_inscription_one_user CHECK (
        (idovi IS NOT NULL)::int +
        (idpap IS NOT NULL)::int +
        (idext IS NOT NULL)::int = 1
    )
);

CREATE TABLE request_for_pap_pati (
    idnumber            CHAR(10)        NOT NULL,
    iduser              CHAR(10)        NOT NULL,
    date                DATE            NOT NULL,
    requiredsupport     VARCHAR(500)    NOT NULL,
    description         VARCHAR(1000)   NOT NULL,
    requirements        VARCHAR(1000)   NOT NULL,
    lifeproject         VARCHAR(1000)   NOT NULL,

    CONSTRAINT pk_request PRIMARY KEY (idnumber),
    CONSTRAINT ak_request_user_date UNIQUE (iduser, date),

    CONSTRAINT fk_request_ovi
        FOREIGN KEY (iduser)
        REFERENCES oviuser(idnumber)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE contract (
    idnumber        CHAR(10)        NOT NULL,
    iduser          CHAR(10)        NOT NULL,
    idpap           CHAR(10)        NOT NULL,
    date            DATE            NOT NULL,
    contracturl     VARCHAR(500)    NOT NULL,

    CONSTRAINT pk_contract PRIMARY KEY (idnumber),
    CONSTRAINT ak_contract_user_pap_date UNIQUE (iduser, idpap, date),

    CONSTRAINT fk_contract_ovi
        FOREIGN KEY (iduser)
        REFERENCES oviuser(idnumber)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_contract_pap
        FOREIGN KEY (idpap)
        REFERENCES pap_pati(idnumber)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE match (
    idnumber    CHAR(10)        NOT NULL,
    iduser      CHAR(10)        NOT NULL,
    idpap       CHAR(10)        NOT NULL,
    idrequest   CHAR(10)        NOT NULL,
    date        DATE            NOT NULL,

    CONSTRAINT pk_match PRIMARY KEY (idnumber),
    CONSTRAINT ak_match_user_pap_date UNIQUE (iduser, idpap, date),

    CONSTRAINT fk_match_ovi
        FOREIGN KEY (iduser)
        REFERENCES oviuser(idnumber)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_match_pap
        FOREIGN KEY (idpap)
        REFERENCES pap_pati(idnumber)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_match_request
        FOREIGN KEY (idrequest)
        REFERENCES request_for_pap_pati(idnumber)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE chat (
    idnumber        CHAR(10)            NOT NULL,
    messagecontent  VARCHAR(2000)       NOT NULL,
    timestamp       TIMESTAMP           NOT NULL,
    sendertype      VARCHAR(3)          NOT NULL,
    idmatch         CHAR(10)            NOT NULL,

    CONSTRAINT pk_chat PRIMARY KEY (idnumber),
    CONSTRAINT ak_chat_timestamp_match UNIQUE (timestamp, idmatch),
    CONSTRAINT ck_chat_sendertype CHECK (sendertype IN ('OVI', 'PAP')),

    CONSTRAINT fk_chat_match
        FOREIGN KEY (idmatch)
        REFERENCES match(idnumber)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
