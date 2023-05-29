package ru.yandex.practicum.filmorate.service.mpa;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class MpaServiceImpl implements MpaService {
    @Override
    public Set<RatingMpa> getAllRatings() {
        return new LinkedHashSet<>(List.of(RatingMpa.values()));
    }

    @Override
    public RatingMpa getRatingMpa(Integer id) {
        switch (id) {
            case 1:
                return RatingMpa.G;
            case 2:
                return RatingMpa.PG;
            case 3:
                return RatingMpa.PG13;
            case 4:
                return RatingMpa.R;
            case 5:
                return RatingMpa.NC17;
            default:
                throw new ObjectNotFoundException("Запрошен рейтинг с несуществующим id.");
        }
    }
}
