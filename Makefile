.PHONY: help build up start stop restart status ps clean


DOCKER_COMPOSE = docker-compose
DOCKER_COMPOSE_FILE = docker-compose.yml

build_event:
	mvn clean package -Dquarkus.container-image.build=true -Dquarkus.container-image.builder=jib "-Dquarkus.container-image.image=unafamilia/events:latest" -Dmaven.test.skip --file events/pom.xml
build_orders:
	mvn clean package -Dquarkus.container-image.build=true -Dquarkus.container-image.builder=jib "-Dquarkus.container-image.image=unafamilia/orders:latest" -Dmaven.test.skip --file orders/pom.xml
build_wow_api:
	docker build wow-api -t "unafamilia/wow-api:latest" 
build_core:
	mvn clean package -Dquarkus.container-image.build=true -Dquarkus.container-image.builder=jib "-Dquarkus.container-image.image=unafamilia/core:latest" -Dmaven.test.skip --file unafamilia/pom.xml

build: build_core build_event build_orders build_wow_api

test:
	mvn test --file unafamilia/pom.xml
	mvn test --file events/pom.xml
	mvn test --file orders/pom.xml
	cd wow-api && go test

up: ## Start all or c=<name> containers in foreground
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) up $(c)

start: ## Start all or c=<name> containers in background
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) up -d $(c)

stop: ## Stop all or c=<name> containers
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) stop $(c)

status: ## Show status of containers
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) ps

restart: ## Restart all or c=<name> containers
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) stop $(c)
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) up $(c) -d

logs: ## Show logs for all or c=<name> containers
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) logs --tail=100 -f $(c)

clean: confirm ## Clean all data
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) down