package de.unistuttgart.iste.meitrex.content_service.persistence.mapper;

import de.unistuttgart.iste.meitrex.content_service.persistence.entity.ItemEntity;
import de.unistuttgart.iste.meitrex.generated.dto.Item;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemMapper {

    private final ModelMapper modelMapper;

    public Item entityToDto(final ItemEntity itemEntity) {
        if (itemEntity == null) {
            return null;
        }
        return modelMapper.map(itemEntity, Item.class);
    }
}