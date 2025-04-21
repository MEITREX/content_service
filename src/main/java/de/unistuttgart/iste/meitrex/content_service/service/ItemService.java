package de.unistuttgart.iste.meitrex.content_service.service;

import de.unistuttgart.iste.meitrex.content_service.persistence.mapper.ItemMapper;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.ItemRepository;
import de.unistuttgart.iste.meitrex.generated.dto.Item;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public List<Item> getItemsById(final List<UUID> ids) {
        var items = itemRepository.findAllByIdPreservingOrder(ids);
        return items.stream().map(itemMapper::entityToDto).toList();
    }
}