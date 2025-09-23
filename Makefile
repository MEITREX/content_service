ProjectName := gits
AppContent := app-content

start-docker:
	docker compose -f docker-compose.yml --project-name $(ProjectName) up -d
build-docker:
	docker compose -f docker-compose.yml --project-name $(ProjectName) build
stop-docker:
	docker compose -f docker-compose.yml --project-name $(ProjectName) stop
app-content-stop:
	docker compose -f docker-compose.yml --project-name $(ProjectName) stop $(AppContent)
app-content-start:
	docker compose -f docker-compose.yml --project-name $(ProjectName) start $(AppContent)
app-content-build:
	docker compose -f docker-compose.yml --project-name $(ProjectName) build $(AppContent)
status-docker:
	docker compose -f docker-compose.yml --project-name $(ProjectName) ps
