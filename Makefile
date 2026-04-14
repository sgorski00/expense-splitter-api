COMPOSE=docker compose
DEV=--profile dev
RUNTIME=--profile runtime

.PHONY: infra-up infra-down dev-up dev-down dev-logs dev-reset runtime-up runtime-down runtime-logs api-logs test psql lint

infra-up:
	$(COMPOSE) up -d

infra-down:
	$(COMPOSE) down

dev-up:
	$(COMPOSE) $(DEV) up -d --build

dev-down:
	$(COMPOSE) $(DEV) down

dev-logs:
	$(COMPOSE) $(DEV) logs -f

dev-reset:
	$(COMPOSE) $(DEV) down -v --remove-orphans

runtime-up:
	$(COMPOSE) $(RUNTIME) up -d --build

runtime-down:
	$(COMPOSE) $(RUNTIME) down

runtime-logs:
	$(COMPOSE) $(RUNTIME) logs -f

test:
	mvn clean verify -Ptest

psql:
	$(COMPOSE) exec es-postgres psql -U postgres -d es-db

lint:
	mvn spotless:apply