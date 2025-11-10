.PHONY: run build test clean migrate

# Запуск приложения
run:
	go run cmd/server/main.go

# Сборка приложения
build:
	go build -o bin/server cmd/server/main.go

# Запуск тестов
test:
	go test -v ./...

# Очистка
clean:
	rm -rf bin/

# Установка зависимостей
deps:
	go mod download
	go mod tidy

# Создание базы данных
db-create:
	createdb tracker_db

# Удаление базы данных
db-drop:
	dropdb tracker_db

# Пересоздание базы данных
db-reset: db-drop db-create

# Запуск с hot reload (требует air: go install github.com/cosmtrek/air@latest)
dev:
	air
