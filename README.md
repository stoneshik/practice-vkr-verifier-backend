# VKR Verifier Service

Backend-сервис для автоматизированной проверки шаблона выпускной квалификационной работы (ВКР).

Сервис принимает `.docx` файл, создаёт запись о проверке в базе данных, сохраняет файл на диск, а затем фоновый обработчик запускает Python-верификатор, получает JSON-отчёт и сохраняет его в БД.

---

## Назначение

Проект предназначен для автоматизации первичной проверки оформления ВКР в формате `.docx`.

Система решает две основные задачи:

1. принимает и сохраняет документы на проверку;
2. запускает анализ документа и сохраняет подробный JSON-отчёт.

---

## Ссылки на репозитории

- Ссылка на репозиторий Spring Boot бэкенда - https://github.com/stoneshik/practice-vkr-verifier-backend
- Ссылка на репозиторий React Typescript фронтенда - https://github.com/stoneshik/practice-vkr-verifier-frontend
- Ссылка на репозиторий скрипта верификации на Python - https://github.com/stoneshik/practice-vkr-verifier

---

## Дополнительные материалы в данном репозитории

- Для тестирования используется POSTMAN, импортированная коллекция в формате json - https://github.com/stoneshik/practice-vkr-verifier-backend/blob/main/REST%20API%20VKR-Verifier.postman_collection.json
- Также есть подробное описание технического задания в формате pdf - https://github.com/stoneshik/practice-vkr-verifier-backend/blob/main/%D0%A2%D0%97_%D0%A1%D1%82%D1%80%D0%B5%D0%BB%D1%8C%D0%B1%D0%B8%D1%86%D0%BA%D0%B8%D0%B9_%D0%98_%D0%9F.pdf

---

## Функциональность

Поддерживаются следующие возможности:

- загрузка файла `.docx`;
- проверка расширения и MIME-типа файла;
- создание сущности отчёта с `UUID` в качестве идентификатора;
- установка начального статуса обработки;
- сохранение исходного файла на диск под именем UUID;
- получение краткой информации о созданном отчёте;
- получение списка отчётов:
  - с пагинацией;
  - с поиском по частичному совпадению UUID;
- получение полного отчёта по UUID;
- фоновый запуск Python-скрипта для необработанных файлов;
- сохранение JSON-результата проверки в базу данных;
- удаление исходного файла после завершения обработки.

---

## Архитектура

Проект состоит из двух частей:

### 1. Spring Boot backend

Отвечает за:

- REST API;
- загрузку и валидацию файлов;
- работу с PostgreSQL;
- хранение файлов;
- запуск Python verifier;
- фоновую обработку заданий.

### 2. Python verifier

Отвечает за:

- разбор `.docx`;
- извлечение структуры документа;
- выполнение проверок по правилам ВКР;
- формирование JSON-отчёта.

---

## Стек технологий

### Backend

- Java 17+
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Lombok

### Frontend

- React TypeScript
- SCSS
- Vite

### Python verifier

- Python 3.10+
- python-docx
- lxml

---

## Жизненный цикл отчёта

### 1. Загрузка файла

При загрузке документа backend:

* проверяет, что файл является `.docx`;
* создаёт запись `Report` в базе данных;
* генерирует `UUID`;
* присваивает статус `PENDING`;
* сохраняет файл в директорию хранения под именем UUID.

### 2. Ожидание обработки

Пока Python verifier ещё не запускался:

* `reportStatus = PENDING`
* `reportJson = null`

### 3. Фоновая обработка

Периодический обработчик:

* ищет необработанные отчёты;
* находит соответствующий файл на диске;
* запускает Python verifier;
* получает JSON-отчёт;
* сохраняет отчёт в БД;
* меняет статус на DONE;
* удаляет исходный файл.

### 4. Получение результата

Пользователь может:

* запросить список отчётов, с пагинацией и фильтрацией по частичному совпадению UUID;
* получить полный JSON-отчёт по UUID.

---

## Статусы

- `PENDING` - файл загружен и ждёт обработки
- `PROCESSING` - файл сейчас обрабатывается python-скриптом
- `COMPLETED` - обработка успешна завершёна и json отчет сохранен в БД
- `FAILED` - обработка завершилась ошибкой

---

## API

## 1. Загрузка документа

### `POST /api/v1/reports`

Принимает multipart-файл и создаёт новый отчёт.

### Ответ

```json
{
  "id": "2f0c13fc-f4f4-4aaa-b812-c1a3a9cd1072",
  "reportStatus": "PROCESSING",
  "createdAt": "2026-03-28T12:34:56Z"
}
```

---

## 2. Получение списка отчётов

### `GET /api/v1/reports`

Поддерживает:

* пагинацию;
* поиск по `partUuid`.

### Query-параметры

* `partUuid` — необязательная часть UUID для поиска;
* `page` — номер страницы;
* `size` — размер страницы;
* `sort` — параметры сортировки.

### Пример

```http
GET /api/v1/reports?partUuid=2f0c&page=0&size=10&sort=createdAt,desc
```

### Ответ

```json
{
  "totalElements": 5,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 10,
  "elements": [
    {
      "id": "2f0c13fc-f4f4-4aaa-b812-c1a3a9cd1072",
      "reportStatus": "COMPLETED",
      "createdAt": "2026-03-28T12:34:56Z"
    }
  ]
}
```

---

## 3. Получение полного отчёта

### `GET /api/v1/reports/{id}`

Возвращает полную информацию по конкретному UUID.

### Ответ

```json
{
  "id": "2f0c13fc-f4f4-4aaa-b812-c1a3a9cd1072",
  "reportStatus": "COMPLETED",
  "reportJson": "{...json...}",
  "createdAt": "2026-03-28T12:34:56Z"
}
```

---

## Работа с Python verifier

Backend запускает Python verifier через `ProcessBuilder`.

Ожидаемая команда по сути такая:

```bash
python -m verifier /absolute/path/to/file.docx
```

### Важные условия

Для корректного запуска нужно, чтобы:

* `working-dir` указывал на каталог, внутри которого лежит Python-пакет `verifier`;
* `python-executable` указывал на корректный интерпретатор, обычно из `.venv`;
* `module-name` совпадал с именем Python-пакета;
* в Python-проекте был `__main__.py`.

### Пример клиента запуска

```java
ProcessBuilder processBuilder = new ProcessBuilder(
    pythonExecutablePath.toString(),
    "-m",
    verifierProperties.getModuleName(),
    filePath.toAbsolutePath().normalize().toString()
);

processBuilder.directory(workingDirPath.toFile());
processBuilder.redirectErrorStream(true);
```

---

## Локальный запуск

## 1. Подготовить PostgreSQL

Создать БД, например:

```sql
CREATE DATABASE vkr_verifier;
```

## 2. Настроить backend

Для хранения приватной информации по типу подключения к БД и некоторых настроек используется файл .env с переменными окружения

Для запуска тестов нужно задать значение переменной окружения SPRING_PROFILES_ACTIVE равное `test` - в этом профиле используется БД поднимаемая testcontainers (тесты не реализованы профиль сделан для дальнейшего расширения)

Для запуска в обычном режиме значение переменной окружения SPRING_PROFILES_ACTIVE равное `dev`

Для запуска на сервере `prod`

Файл .env должен находится в директории resources

Пример .env файла находится в env.example

Заполнить `application.yml`:

* `storage.dir`;
* `verifier.working-dir`;
* `verifier.python-executable`.

## 3. Подготовить Python verifier

Перейти в каталог Python-проекта:

```bash
cd ../verifier
python3 -m venv .venv
source .venv/bin/activate
pip install requirements.txt
```

## 4. Проверить ручной запуск verifier

```bash
cd ../verifier
.venv/bin/python -m verifier /absolute/path/to/file.docx
```

Если эта команда не работает вручную, backend тоже не сможет её запустить.


## 5. Собрать и запустить Spring Boot

Сбор jar файла с пропуском тестов
```bash
mvn package -DskipTests
```
Запуск:
```bash
java -jar backend/soa-0.0.1-SNAPSHOT.jar
```
