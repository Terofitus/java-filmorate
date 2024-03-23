# Java-filmorate

Технологии: Java + Spring Boot + Maven + Lombok + JUnit + RESTful API + JDBC


Данный проект представляет собой бэкенд для сервиса, который работает с фильмами и оценками пользователей и рекомендует фильмы к просмотру.

Основная задача приложения - решить проблему поиска фильмов на вечер. С его помощью вы можете легко найти фильм, который вам понравится.

### Реализованы следующие эндпоинты:

#### 1. Фильмы
+ POST /films - создание фильма

+ PUT /films - редактирование фильма

+ GET /films - получение списка всех фильмов

+ GET /films/{id} - получение информации о фильме по его id

+ PUT /films/{id}/like/{userId} — поставить лайк фильму

+ DELETE /films/{id}/like/{userId} — удалить лайк фильма

+ GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано, возвращает первые 10.

#### 2. Пользователи

+ POST /users - создание пользователя

+ PUT /users - редактирование пользователя

+ GET /users - получение списка всех пользователей

+ GET /users/{id} - получение данных о пользователе по id

+ PUT /users/{id}/friends/{friendId} — добавление в друзья

+ DELETE /users/{id}/friends/{friendId} — удаление из друзей

+ GET /users/{id}/friends — возвращает список друзей

+ GET /users/{id}/friends/common/{otherId} — возвращает список друзей, общих с другим пользователем

### Валидация
Данные, которые приходят в запросе на добавление нового фильма или пользователя, проходят проверку по следующим критериям:

#### 1. Фильмы
+ название не может быть пустым.

+ максимальная длина описания — 200 символов

+ дата релиза — не раньше 28 декабря 1895 года

+ продолжительность фильма должна быть положительной

#### 2. Пользователи
+ электронная почта не может быть пустой и должна быть электронной почтой (аннотация @Email)

+ логин не может быть пустым и содержать пробелы

+ имя для отображения может быть пустым — в таком случае будет использован логин

+ дата рождения не может быть в будущем.

### Схема базы данных
  Схема отображает отношения таблиц в базе данных:

+ films - данные о фильмах (primary key - id, foreign keys - rating_id)
+ genre - названия жанров фильма
+ film_genre - данные о жанрах какого-то фильма (primary key - (film_id,genre_id), foreign keys - film_id, genre_id)
+ rating_mpa - определяет возрастное ограничение для фильма
+ film_likes - информация о лайках фильма и кто их поставил (primary key - (user_id,film_id), foreign keys - user_id, film_id)
+ users - данные о пользователях
+ users_friends - содержит информации о статусе «дружбы» между двумя пользователями (primary key - (user_id,friend_id), foreign keys - user_id, friend_id)


°  status = true — в таблице две записи о дружбе двух пользователей (id1 = id2; id2 = id1)

°  status = false — в таблице одна запись о дружбе двух пользователей(id1 = id2).

![database_schema](https://github.com/Terofitus/java-filmorate/assets/118897418/732fd237-fb38-4255-9f04-5314405f2441)
#### Примеры запросов:




1. Пользователи


создание пользователя

 ```sql
 INSERT INTO users (email, login, name, birthday)
 VALUES ( ?, ?, ?, ? );
 ```

редактирование пользователя

 ```sql
 UPDATE users
 SET email = ?,
 login = ?,
 name = ?,
 birthday = ?
 WHERE id = ?
 ```

получение списка всех пользователей

 ```sql
 SELECT *
 FROM users
 ```
получение информации о пользователе по его id

 ```sql
 SELECT *
 FROM users
 WHERE id = ?
 ```

добавление в друзья

 ```sql
 INSERT INTO users_friends (user_id, user_friend_id)
 VALUES (?, ?)
 ```

удаление из друзей

 ```sql
 DELETE
 FROM users_friends
 WHERE user_id = ? AND user_friend_id = ?
 ```

получение списка друзей пользователя
 ```sql
 SELECT u.*
 FROM users_friends AS uf
 INNER JOIN users AS u ON u.id = uf.user_friend_id
 WHERE uf.user_id = ?
 ```

2. Фильмы

создание фильма

 ```sql
 INSERT INTO films (name, description, release_date, duration, rating_id)
 VALUES (?, ?, ?, ?, ?)
 ```

редактирование фильма

 ```sql
 UPDATE films
 SET name = ?,
 description = ?,
 release_date = ?,
 duration = ?,
 rating_id = ?
 WHERE id = ?
 ```

получение списка всех фильмов

 ```sql
 SELECT f.*, mpt.name, COUNT(flt.user_id) AS rate
 FROM films AS f
 LEFT JOIN rating_mpa AS mpt ON f.rating_id = mpt.id
 LEFT JOIN film_likes AS flt ON f.id = flt.film_id
 GROUP BY f.id
 ORDER BY f.id
 ```

получение информации о фильме по его id

 ```sql
 SELECT f.*, mp.name, COUNT(fl.user_id) AS rate
 FROM films AS f
 LEFT JOIN rating_mpa AS mp ON f.rating_id = mp.id
 LEFT JOIN film_likes AS fl ON f.id = fl.film_id
 WHERE f.id = ?
 GROUP BY f.id
 ```

пользователь ставит лайк фильму

  ```sql
 INSERT INTO film_likes (film_id, user_id)
 VALUES (?, ?)
  ```

пользователь удаляет лайк

  ```sql
 DELETE
 FROM film_likes
 WHERE film_id = ? AND user_id = ?
  ```

возвращает список из первых count фильмов по количеству лайков
 ```sql
SELECT f.*, mp.name, COUNT(fl.user_id) AS rate
FROM films AS f
LEFT JOIN rating_mpa AS mp ON f.rating_id = mp.id
LEFT JOIN film_likes AS fl ON f.id = fl.film_id
GROUP BY f.id
ORDER BY rate DESC, f.id
LIMIT ?
 ```
