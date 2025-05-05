package com.example.mdb.mapper;

import com.example.mdb.dto.ShowResponse;
import com.example.mdb.entity.Show;
import org.springframework.stereotype.Component;

@Component
public class ShowMapper {

    public ShowResponse showResponseMapper(Show show) {
        if (show == null)
            return null;
        return new ShowResponse(
                show.getShowId(),
                show.getStartsAt(),
                show.getEndsAt()
        );
    }
}
