INSERT INTO url
  (name, created_at)
VALUES
  ('http://example.test', CURRENT_TIMESTAMP);

INSERT INTO url_check
	(status_code, title, h1, description, url_id, created_at)
VALUES
    (200, 'github', 'github', 'best platform', 1, CURRENT_TIMESTAMP);
