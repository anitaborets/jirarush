Список выполненных задач:

1.Удалить соцсети

2.Вынести чувствительную информацию в файл - env.file/передача через переменные окружения

3.Тесты для метода ProfileRestController - ProfileRestControllerTest

4.Добавление тегов к задаче - и бек, и фронт. Методы public String addTag в TaskUIController и public ResponseEntity<Task> addTags
в TaskRestController.java + my.html - таги можно добавить только к задачам, приписанным на пользователя
  
5.Подписка на задачи - фронт и бек - решена в 2 вариантах:
  
а) Взять задачу себе - метод  assign в TaskService, пользователь выбирает задачи из свободных(free.html),
задача перемещается в его задачи(my.html), происходит запись в таблицы user_belong и activity

  b) Подписаться как наблюдатель - метод watch в TaskService - для этого создана промежуточная сущность Watcher без изменения сущности Task 
+ таблица watchers. Запись регистрируется также как событие в DomainEvents в TaskService.
Логика наблюдателей дальше не реализована.
6.Подсчет времени - фронт и бек.
  
Пользователь в своих задачах может установить отправку задачи в тестирование и завершение работы - my.html.
В all.html можем увидеть время выполнения задачи. Методы  getTime(long id) в TaskService,getTime  в TaskUIController
getTime  в TaskRestController
  
7.Локализация - стартовая страница и шаблоны писем
  
8.Backlog - backlog.html, методы getAll в TaskUIController и Page<TaskTo> getAllPageable в TaskRestController
  
9.JWT token - до конца нереализован, происходит регистрация нового пользователя и выдача токена через REST
  
10.изменен баннер)

В файле screens.docx прилагаю скрины фронта



## [REST API](http://localhost:8080/doc)

## Концепция:
- Spring Modulith
  - [Spring Modulith: достигли ли мы зрелости модульности](https://habr.com/ru/post/701984/)
  - [Introducing Spring Modulith](https://spring.io/blog/2022/10/21/introducing-spring-modulith)
  - [Spring Modulith - Reference documentation](https://docs.spring.io/spring-modulith/docs/current-SNAPSHOT/reference/html/)

```
  url: jdbc:postgresql://localhost:5432/jira
  username: jira
  password: JiraRush
```
- Есть 2 общие таблицы, на которых не fk
  - _Reference_ - справочник. Связь делаем по _code_ (по id нельзя, тк id привязано к окружению-конкретной базе)
  - _UserBelong_ - привязка юзеров с типом (owner, lead, ...) к объекту (таска, проект, спринт, ...). FK вручную будем проверять

## Аналоги
- https://java-source.net/open-source/issue-trackers

## Тестирование
- https://habr.com/ru/articles/259055/

