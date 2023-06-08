package de.unistuttgart.iste.gits.content_service.service;

import de.unistuttgart.iste.gits.content_service.persistence.repository.ContentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {
    @Mock
    private ContentRepository contentRepository;
    @InjectMocks
    private ContentService contentService;

    @Test
    void testDeleteContentSuccessful() {
        UUID testContentId = UUID.randomUUID();
        doNothing().when(contentRepository).deleteById(any());
        doReturn(true).when(contentRepository).existsById(any());
        UUID deletedContentId = contentService.deleteContent(testContentId);
        assertThat(deletedContentId, is(testContentId));
        verify(contentRepository).deleteById(testContentId);
    }

}
