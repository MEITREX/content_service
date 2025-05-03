package de.unistuttgart.iste.meitrex.content_service.controller;

import de.unistuttgart.iste.meitrex.content_service.service.ItemService;
import de.unistuttgart.iste.meitrex.generated.dto.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @QueryMapping(name = ContentController.INTERNAL_NOAUTH_PREFIX + "items")
    public List<Item> itemsById(@Argument final List<UUID> ids) {
        return itemService.getItemsById(ids);
    }
}