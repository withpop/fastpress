CREATE TABLE post
(
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  title TEXT,
  content TEXT,
  status VARCHAR(10) not null,
  auther int unsigned,
  type VARCHAR(10),
  created_at DATETIME not null DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE taxonomy
(
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT UNSIGNED not null default '0',
  title TEXT not null,
  type VARCHAR(10) not null, /* menu, tag, category */
  target_url TEXT
);

CREATE TABLE post_taxonomy
(
  post_id BIGINT UNSIGNED not NULL ,
  taxonomy_id BIGINT UNSIGNED not null default '0',
  PRIMARY KEY (post_id, taxonomy_id)
);
