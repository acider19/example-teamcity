# Домашнее задание к занятию 11 «Teamcity» - Муравский Артем

**Ссылка на репозиторий:** `https://github.com/acider19/example-teamcity`

### Выполненные пункты

1. **Проект** — создан `NetologyExample`, VCS root через SSH (`git@github.com:acider19/example-teamcity.git`, ключ `tc_deploy_key`).
2. **Build "Build"**:
   - `Maven Test` (`clean test`) — на всех ветках, кроме master
   - `Maven Deploy` (`clean deploy`, custom settings.xml с `admin/admin123` для nexus) — только на master
   - VCS trigger (+:\*), PerfMon, `artifactRules = target/*.jar`
3. **Nexus** — `http://nexus:8081/repository/maven-releases`, деплой проходит успешно.
4. **Versioned Settings** — включён, конфигурация хранится в `.teamcity/` в репозитории.
5. **Ветка `feature/add_reply`**:
   - Добавлен метод `saySarov()` → `"Hello, Sarov hunter!"`
   - Добавлен тест `welcomerSaySarov()` → проверяет `containsString("hunter")`
   - Сборки на ветке — SUCCESS (6/6 тестов)
6. **Merge** `feature/add_reply` → `master` — выполнен.
7. **Финальная сборка master** (#12) — SUCCESS: 6 тестов, деплой в Nexus, артефакты `.jar` собраны.

---

## Конфигурация стенда (локально, Orbstack)

### Состав

| Сервис | URL | Версия |
|---|---|---|
| **TeamCity** | http://localhost:8111 | 2024.12 |
| **Nexus** | http://localhost:8081 | 3.78.2 |

### Запуск

```bash
docker compose up -d
```

docker-compose.yml:

```yaml
services:
  nexus:
    image: sonatype/nexus3:3.78.2
    container_name: nexus
    ports:
      - "8081:8081"
    volumes:
      - nexus-data:/nexus-data
    restart: unless-stopped

  teamcity-server:
    image: jetbrains/teamcity-server:2024.12
    container_name: teamcity-server
    ports:
      - "8111:8111"
    volumes:
      - teamcity-data:/data/teamcity_server/datadir
      - teamcity-logs:/opt/teamcity/logs
      - ./teamcity-startup.properties:/opt/teamcity/conf/teamcity-startup.properties
      - ./tc_deploy_key:/shared/tc_deploy_key
    environment:
      - TEAMCITY_HTTPS_PROXY_ENABLED=false
      - TEAMCITY_SERVER_URL=http://localhost:8111
    restart: unless-stopped

  teamcity-agent:
    image: jetbrains/teamcity-agent:2024.12
    container_name: teamcity-agent
    depends_on:
      - teamcity-server
    volumes:
      - agent-data:/data/teamcity_agent/conf
      - ./tc_deploy_key:/shared/tc_deploy_key
    environment:
      SERVER_URL: "http://teamcity-server:8111"
    restart: unless-stopped

volumes:
  nexus-data:
  teamcity-data:
  teamcity-logs:
  agent-data:
```
