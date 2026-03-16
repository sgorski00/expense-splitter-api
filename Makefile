.PHONY: up down build rebuild logs

up:
	docker-compose up -d

down:
	docker-compose down

build:
	docker-compose build --no-cache

rebuild:
	docker-compose build --no-cache
	docker-compose down
	docker-compose up -d

logs:
	docker-compose logs -f