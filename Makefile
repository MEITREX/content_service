ProjectName := gits

start-docker:
	docker compose -f docker-compose.yml --project-name $(ProjectName) up -d
build-docker:
	docker compose -f docker-compose.yml --project-name $(ProjectName) build
stop-docker:
	docker compose -f docker-compose.yml --project-name $(ProjectName) down
status-docker:
	docker compose -f docker-compose.yml --project-name $(ProjectName) ps
