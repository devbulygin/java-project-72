INSERT INTO url
  (name, when_created)
VALUES
  ('http://example.test', CURRENT_TIMESTAMP);

INSERT INTO url_check
	(status_code, title, h1, description, url_id, when_created)
VALUES
    (200, 'github', 'github', 'best platform', 1, CURRENT_TIMESTAMP);
