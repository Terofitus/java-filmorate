MERGE INTO GENRE (id, genre) VALUES (1, 'Комедия'),
                                    (2, 'Драма'),
                                    (3, 'Мультфильм'),
                                    (4, 'Триллер'),
                                    (5, 'Документальный'),
                                    (6, 'Боевик');

MERGE INTO RATING_MPA (id, name, description) VALUES (1, 'G', 'Допускаются все возрасты'),
                                                     (2, 'PG', 'Некоторые материалы могут не подходить для детей'),
                                                     (3, 'PG-13',
                                                      'Некоторые материалы могут быть неподходящими для детей младше 13 лет'),
                                                     (4, 'R',
                                                      'Детям младше 17 лет требуется сопровождающий родитель или взрослый опекун'),
                                                     (5, 'R-17', 'Никто в возрасте 17 лет и младше не допускается');