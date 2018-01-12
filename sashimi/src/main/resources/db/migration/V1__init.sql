CREATE TABLE post
(
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  title TEXT,
  content TEXT,
  path_name VARCHAR(20) not null UNIQUE,
  status VARCHAR(10) not null,
  author int unsigned,
  post_type VARCHAR(10),
  created_at DATETIME not null DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX path_name_idx(path_name),
  INDEX created_at_idx(created_at),
  INDEX author_idx(author),
  INDEX status_idx(status),
  INDEX post_type_idx(post_type)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE taxonomy
(
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT UNSIGNED not null default '0',
  name varchar(50) not null,
  taxo_type VARCHAR(10) not null, /* menu, tag, category */
  link TEXT,
  INDEX taxo_type_index(taxo_type),
  INDEX name_idx(name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE post_taxonomy
(
  post_id BIGINT UNSIGNED not NULL ,
  taxonomy_id BIGINT UNSIGNED not null default '0',
  PRIMARY KEY (post_id, taxonomy_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user
(
  id varchar(255) PRIMARY KEY ,
  username varchar(255),
  password varchar(255),
  serialized_profile varchar(10000)
);

ALTER TABLE user
  ADD PRIMARY KEY (id),
  ADD KEY username (username);