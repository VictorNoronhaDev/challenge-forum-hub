CREATE TABLE topicos (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  titulo        VARCHAR(180) NOT NULL,
  mensagem      TEXT NOT NULL,
  data_criacao  DATETIME,
  status        ENUM('RESPONDIDO','NAO_RESPONDIDO') NOT NULL DEFAULT 'NAO_RESPONDIDO',
  autor         VARCHAR(100)  NOT NULL,
  curso         VARCHAR(120)  NOT NULL
);