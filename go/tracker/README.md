# Task Tracker Backend

Backend для приложения трекера задач на Go с поддержкой досок задач, спринтов и дашбордов.

## Функционал

### Основные возможности:
- **Доски задач со спринтами** - управление задачами с поддержкой drag&drop на фронтенде
- **Дашборды** - гибкие доски для отчетов, диаграмм и записок
- **Работа с командами** - регистрация с кодом приглашения или создание новой команды
- **JWT аутентификация** - безопасная авторизация пользователей

## Структура проекта

```
tracker/
├── cmd/
│   └── server/
│       └── main.go              # Точка входа приложения
├── internal/
│   ├── config/
│   │   └── config.go            # Конфигурация приложения
│   ├── database/
│   │   ├── database.go          # Подключение к БД
│   │   └── migrations.go        # Миграции БД
│   ├── middleware/
│   │   ├── auth.go              # JWT middleware
│   │   └── cors.go              # CORS middleware
│   ├── models/
│   │   ├── user.go              # Модель пользователя
│   │   ├── team.go              # Модель команды
│   │   ├── board.go             # Модель доски
│   │   ├── sprint.go            # Модель спринта
│   │   ├── task.go              # Модель задачи
│   │   └── dashboard.go         # Модель дашборда
│   ├── repository/
│   │   ├── user_repository.go
│   │   ├── team_repository.go
│   │   ├── board_repository.go
│   │   ├── sprint_repository.go
│   │   ├── task_repository.go
│   │   └── dashboard_repository.go
│   └── utils/
│       ├── jwt.go               # JWT утилиты
│       ├── password.go          # Хеширование паролей
│       ├── response.go          # HTTP ответы
│       └── invitation.go        # Генерация кодов приглашений
├── go.mod
├── .env.example
└── README.md
```

## Установка и запуск

### Требования:
- Go 1.21+
- PostgreSQL 12+

### Шаги установки:

1. Клонируйте репозиторий и перейдите в директорию:
```bash
cd tracker
```

2. Установите зависимости:
```bash
go mod download
```

3. Создайте базу данных PostgreSQL:
```bash
createdb tracker_db
```

4. Скопируйте `.env.example` в `.env` и настройте переменные окружения:
```bash
cp .env.example .env
```

5. Отредактируйте `.env` файл с вашими настройками:
```env
DB_HOST=localhost
DB_PORT=5432
DB_USER=postgres
DB_PASSWORD=your_password
DB_NAME=tracker_db

JWT_SECRET=your-secret-key-change-this
JWT_EXPIRATION=24h

SERVER_PORT=8080
```

6. Запустите сервер:
```bash
go run cmd/server/main.go
```

Сервер запустится на `http://localhost:8080`

## API Endpoints (TODO)

### Аутентификация
- `POST /api/auth/register` - Регистрация пользователя
- `POST /api/auth/login` - Вход пользователя

### Команды
- `GET /api/teams` - Получить информацию о команде
- `POST /api/teams` - Создать команду
- `POST /api/teams/join` - Присоединиться к команде по коду
- `GET /api/teams/members` - Получить участников команды

### Доски
- `GET /api/boards` - Получить все доски команды
- `POST /api/boards` - Создать доску
- `GET /api/boards/:id` - Получить доску по ID
- `PUT /api/boards/:id` - Обновить доску
- `DELETE /api/boards/:id` - Удалить доску

### Спринты
- `GET /api/boards/:id/sprints` - Получить спринты доски
- `POST /api/boards/:id/sprints` - Создать спринт
- `PUT /api/sprints/:id` - Обновить спринт
- `DELETE /api/sprints/:id` - Удалить спринт

### Задачи
- `GET /api/boards/:id/tasks` - Получить все задачи доски
- `GET /api/sprints/:id/tasks` - Получить задачи спринта
- `GET /api/boards/:id/backlog` - Получить backlog
- `POST /api/tasks` - Создать задачу
- `PUT /api/tasks/:id` - Обновить задачу
- `PUT /api/tasks/:id/move` - Переместить задачу (drag&drop)
- `DELETE /api/tasks/:id` - Удалить задачу

### Дашборды
- `GET /api/dashboards` - Получить все дашборды команды
- `POST /api/dashboards` - Создать дашборд
- `GET /api/dashboards/:id` - Получить дашборд
- `PUT /api/dashboards/:id` - Обновить дашборд
- `DELETE /api/dashboards/:id` - Удалить дашборд
- `GET /api/dashboards/:id/widgets` - Получить виджеты дашборда
- `POST /api/dashboards/:id/widgets` - Создать виджет
- `PUT /api/widgets/:id` - Обновить виджет
- `DELETE /api/widgets/:id` - Удалить виджет

## База данных

### Таблицы:
- `teams` - Команды
- `users` - Пользователи
- `boards` - Доски задач
- `sprints` - Спринты
- `tasks` - Задачи
- `dashboards` - Дашборды
- `dashboard_widgets` - Виджеты дашбордов

Миграции запускаются автоматически при старте приложения.

## Модели данных

### User
- Регистрация с email/password
- Привязка к команде
- Роли: admin, member

### Team
- Уникальный код приглашения
- Управление участниками

### Board
- Принадлежит команде
- Содержит спринты и задачи

### Sprint
- Принадлежит доске
- Статусы: planned, active, completed
- Даты начала и окончания

### Task
- Может быть в спринте или в backlog
- Статусы: todo, in_progress, review, done
- Приоритеты: low, medium, high, critical
- Поддержка drag&drop через поле position

### Dashboard
- Принадлежит команде
- Содержит виджеты

### DashboardWidget
- Типы: chart, report, note, table
- Позиционирование через координаты и размеры

## Технологии

- **Go 1.21** - Язык программирования
- **PostgreSQL** - База данных
- **gorilla/mux** - HTTP роутер
- **golang-jwt/jwt** - JWT токены
- **bcrypt** - Хеширование паролей
- **lib/pq** - PostgreSQL драйвер

## TODO

- [ ] Реализовать handlers для всех endpoints
- [ ] Добавить валидацию входных данных
- [ ] Добавить логирование
- [ ] Добавить unit тесты
- [ ] Добавить Docker support
- [ ] Добавить WebSocket для real-time обновлений
- [ ] Добавить rate limiting
- [ ] Добавить пагинацию для списков

## Лицензия

MIT
