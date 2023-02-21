INSERT INTO urls
  (name, when_created)
VALUES
  ('https://github.com', CURRENT_TIMESTAMP);

INSERT INTO url_check
	(status_code, title, h1, description, url_id, when_created)
VALUES
    (200, 'github', 'github', 'best platform', 1, CURRENT_TIMESTAMP);
