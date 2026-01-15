# Структура проекта

## Обзор файлов

```
boot-yaml-plugin/
│
├── .git/                           # Git репозиторий
├── .gitignore                      # Игнорируемые файлы
│
├── README.md                       # Основная документация
├── ARCHITECTURE.md                 # Архитектура плагина
├── DEVELOPMENT.md                  # Руководство по разработке
├── PROJECT_STRUCTURE.md            # Этот файл
│
├── build.gradle.kts                # Основной Gradle build с IntelliJ Platform
├── build-simple.gradle.kts         # Упрощенный build для unit-тестов
├── settings.gradle.kts             # Настройки Gradle проекта
├── gradle.properties               # Свойства плагина
├── gradlew                         # Gradle wrapper (Linux/Mac)
│
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar      # Gradle wrapper JAR
│       └── gradle-wrapper.properties # Конфигурация wrapper
│
└── src/
    ├── main/
    │   ├── java/com/github/sudohackin/bootyaml/
    │   │   │
    │   │   ├── model/              # Модели данных (независимые от IntelliJ)
    │   │   │   ├── ConfigurationMetadata.java      # Корневая модель метаданных
    │   │   │   ├── PropertyMetadata.java           # Модель свойства
    │   │   │   ├── GroupMetadata.java              # Модель группы свойств
    │   │   │   ├── HintMetadata.java               # Модель подсказок
    │   │   │   └── DeprecationMetadata.java        # Модель устаревания
    │   │   │
    │   │   ├── parser/             # Парсинг JSON (независимый от IntelliJ)
    │   │   │   └── MetadataParser.java             # Парсер метаданных
    │   │   │
    │   │   ├── service/            # Бизнес-логика (использует IntelliJ API)
    │   │   │   └── ConfigurationMetadataService.java # Сервис загрузки метаданных
    │   │   │
    │   │   ├── completion/         # Автодополнение (использует IntelliJ API)
    │   │   │   └── SpringBootYamlCompletionContributor.java # Contributor
    │   │   │
    │   │   └── listener/           # Event listeners (использует IntelliJ API)
    │   │       └── ProjectDependencyChangeListener.java # Слушатель изменений
    │   │
    │   └── resources/
    │       └── META-INF/
    │           └── plugin.xml      # Конфигурация плагина IntelliJ
    │
    └── test/
        ├── java/com/github/sudohackin/bootyaml/
        │   ├── model/
        │   │   ├── ConfigurationMetadataTest.java  # Тесты модели метаданных
        │   │   └── PropertyMetadataTest.java       # Тесты модели свойства
        │   │
        │   └── parser/
        │       └── MetadataParserTest.java         # Тесты парсера
        │
        └── resources/
            ├── test-metadata/
            │   └── spring-configuration-metadata.json # Тестовые метаданные
            │
            └── test-yaml/
                └── application.yaml                   # Тестовый YAML файл
```

## Описание основных файлов

### Документация

- **README.md** - основная документация проекта, описание функциональности
- **ARCHITECTURE.md** - детальное описание архитектуры плагина
- **DEVELOPMENT.md** - руководство по разработке и тестированию
- **PROJECT_STRUCTURE.md** - структура проекта (этот файл)

### Конфигурация сборки

- **build.gradle.kts** - основной Gradle build файл с IntelliJ Platform Plugin
- **build-simple.gradle.kts** - упрощенная версия без IntelliJ зависимостей для unit-тестов
- **settings.gradle.kts** - настройки Gradle проекта
- **gradle.properties** - свойства плагина (версия, название, и т.д.)

### Исходный код

#### Model Layer (`model/`)

Независимые POJO классы для представления метаданных Spring Boot:

- **ConfigurationMetadata.java** - корневой объект, содержит списки properties, groups, hints
- **PropertyMetadata.java** - описание конфигурационного свойства (имя, тип, описание, defaultValue)
- **GroupMetadata.java** - группа связанных свойств
- **HintMetadata.java** - подсказки о возможных значениях
- **DeprecationMetadata.java** - информация об устаревших свойствах

#### Parser Layer (`parser/`)

- **MetadataParser.java** - парсит JSON файлы `spring-configuration-metadata.json` в объекты модели

#### Service Layer (`service/`)

- **ConfigurationMetadataService.java** - Project-level сервис IntelliJ:
  - Сканирует JAR зависимости
  - Загружает и парсит метаданные
  - Кэширует данные
  - Предоставляет API для поиска свойств

#### Completion Layer (`completion/`)

- **SpringBootYamlCompletionContributor.java** - IntelliJ CompletionContributor:
  - Определяет контекст в YAML
  - Запрашивает подходящие свойства
  - Формирует предложения автодополнения

#### Listener Layer (`listener/`)

- **ProjectDependencyChangeListener.java** - слушатель событий изменения зависимостей:
  - Отслеживает обновления Gradle/Maven
  - Инициирует перезагрузку метаданных

### Ресурсы

- **plugin.xml** - конфигурация плагина IntelliJ:
  - Регистрация extension points
  - Регистрация сервисов
  - Регистрация listeners

### Тесты

#### Unit тесты

- **ConfigurationMetadataTest.java** - тесты модели метаданных
- **PropertyMetadataTest.java** - тесты модели свойств
- **MetadataParserTest.java** - тесты парсера JSON

#### Тестовые ресурсы

- **spring-configuration-metadata.json** - примеры метаданных для тестов
- **application.yaml** - примеры YAML конфигураций

## Статистика проекта

```
Всего файлов Java:       12
- Production код:        9
- Тестовый код:          3

Всего классов:           12
- Model:                 5
- Parser:                1
- Service:               1
- Completion:            1
- Listener:              1
- Tests:                 3

Строк кода (примерно):
- Model layer:           ~300
- Parser layer:          ~50
- Service layer:         ~150
- Completion layer:      ~100
- Listener layer:        ~40
- Tests:                 ~300
- ИТОГО:                 ~940 строк
```

## Зависимости

### Production зависимости

- **IntelliJ Platform SDK** - базовая платформа
- **YAML Plugin** - поддержка YAML файлов
- **Gradle Plugin** - интеграция с Gradle
- **Gson** - парсинг JSON

### Test зависимости

- **JUnit 4** - тестовый фреймворк
- **Mockito** - моки для тестов

## Следующие шаги развития

1. **Валидация** - добавить валидацию значений свойств
2. **Quick Fixes** - исправления для deprecated свойств
3. **Documentation** - показ документации при hover
4. **Navigation** - навигация к source классам
5. **@ConfigurationProperties** - поддержка аннотированных классов проекта
