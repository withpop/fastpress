INSERT INTO post_taxonomy (post_id, taxonomy_id) VALUES (1, 3);
INSERT INTO post_taxonomy (post_id, taxonomy_id) VALUES (1, 7);
INSERT INTO post_taxonomy (post_id, taxonomy_id) VALUES (1, 8);
INSERT INTO post_taxonomy (post_id, taxonomy_id) VALUES (1, 9);

INSERT INTO taxonomy (id, parent_id, name, taxo_type, link) VALUES (1, 0, 'menu1', 'menu', null);
INSERT INTO taxonomy (id, parent_id, name, taxo_type, link) VALUES (2, 0, 'menu2', 'menu', null);
INSERT INTO taxonomy (id, parent_id, name, taxo_type, link) VALUES (3, 1, 'menu1-1', 'menu', null);
INSERT INTO taxonomy (id, parent_id, name, taxo_type, link) VALUES (4, 1, 'menu1-2', 'menu', null);
INSERT INTO taxonomy (id, parent_id, name, taxo_type, link) VALUES (5, 2, 'menu2-1', 'menu', null);
INSERT INTO taxonomy (id, parent_id, name, taxo_type, link) VALUES (6, 2, 'menu2-2', 'menu', null);
INSERT INTO taxonomy (id, parent_id, name, taxo_type, link) VALUES (7, 0, 'tag1', 'tag', null);
INSERT INTO taxonomy (id, parent_id, name, taxo_type, link) VALUES (8, 0, 'tag2', 'tag', null);
INSERT INTO taxonomy (id, parent_id, name, taxo_type, link) VALUES (9, 0, 'category1', 'category', null);
INSERT INTO taxonomy (id, parent_id, name, taxo_type, link) VALUES (10, 0, 'category2', 'category', null);

INSERT INTO post (id, title, content, path_name, status, author, post_type, created_at, updated_at) VALUES (1, 'test', '*from : https://gist.githubusercontent.com/rt2zz/e0a1d6ab2682d2c47746950b84c0b6ee/raw/83b8b4814c3417111b9b9bef86a552608506603e/markdown-sample.md*

An h1 header
============

Paragraphs are separated by a blank line.

2nd paragraph. *Italic*, **bold**, and `monospace`. Itemized lists
look like:

  * this one
  * that one
  * the other one

Note that --- not considering the asterisk --- the actual text
content starts at 4-columns in.

> Block quotes are
> written like so.
>
> They can span multiple paragraphs,
> if you like.

Use 3 dashes for an em-dash. Use 2 dashes for ranges (ex., "it''s all
in chapters 12--14"). Three dots ... will be converted to an ellipsis.
Unicode is supported. ☺
', 'test', 'public', null, 'post', '2018-01-10 01:23:18', '2018-01-10 01:41:12');