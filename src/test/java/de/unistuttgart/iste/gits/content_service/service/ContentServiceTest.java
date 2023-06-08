package de.unistuttgart.iste.gits.content_service.service;

import de.unistuttgart.iste.gits.content_service.persistence.dao.ContentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.TagEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.gits.content_service.persistence.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {
    @Mock
    private ContentRepository contentRepository;
    @Mock
    private TagRepository tagRepository;
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

    Set<String> getTagNames(Collection<TagEntity> tags) {
        return tags.stream().filter(Objects::nonNull).map(TagEntity::getName).collect(Collectors.toSet());
    }

    @Test
    void testTagSynchronisationNoExistingTagsSuccessful() {
        List<String> tagNames = List.of("Tag1", "Tag2", "Tag3");
        ContentService.TagSynchronizationResult result = contentService.prepareSynchronization(tagNames, Collections.emptyList(), Collections.emptyList());
        assertThat(getTagNames(result.newTagsToAdd()), containsInAnyOrder(tagNames.toArray()));
    }

    @Test
    void testTagSynchronisationWithExistingTagsSuccessful() {
        List<String> tagNames = List.of("Tag1", "Tag2", "Tag3");
        TagEntity existingTag = TagEntity.fromName(tagNames.get(1));
        List<String> newTagNamesToAdd = List.of(tagNames.get(0), tagNames.get(2));
        ContentService.TagSynchronizationResult result = contentService.prepareSynchronization(tagNames,
                Collections.emptyList(),
                List.of(existingTag));
        assertThat(getTagNames(result.newTagsToAdd()), containsInAnyOrder(newTagNamesToAdd.toArray()));
        assertThat(getTagNames(result.existingTagsToAdd()), containsInAnyOrder(List.of(existingTag.getName()).toArray()));
    }

    @Test
    void testTagSynchronisationWithAlreadyAssignedTagSuccessful() {
        List<String> tagNames = List.of("Tag1", "Tag2", "Tag3");
        TagEntity existingTag = TagEntity.fromName(tagNames.get(1));
        List<String> newTagNamesToAdd = List.of(tagNames.get(0), tagNames.get(2));
        ContentService.TagSynchronizationResult result = contentService.prepareSynchronization(tagNames,
                List.of(existingTag),
                List.of(existingTag));
        assertThat(getTagNames(result.newTagsToAdd()), containsInAnyOrder(newTagNamesToAdd.toArray()));
        assertThat(getTagNames(result.existingTagsToAdd()), containsInAnyOrder(Collections.emptyList().toArray()));
        assertThat(getTagNames(result.existingTagsToRemove()), containsInAnyOrder(Collections.emptyList().toArray()));
    }

    @Test
    void testTagSynchronisationWithTagsToRemoveSuccessful() {
        List<String> tagNames = List.of("Tag1");
        TagEntity existingTag = TagEntity.fromName("Tag2");
        List<String> newTagNamesToAdd = List.of(tagNames.get(0));
        List<String> existingTagNamesToRemove = List.of("Tag2");
        ContentService.TagSynchronizationResult result = contentService.prepareSynchronization(tagNames,
                List.of(existingTag),
                List.of(existingTag));
        assertThat(getTagNames(result.newTagsToAdd()), containsInAnyOrder(newTagNamesToAdd.toArray()));
        assertThat(getTagNames(result.existingTagsToAdd()), containsInAnyOrder(Collections.emptyList().toArray()));
        assertThat(getTagNames(result.existingTagsToRemove()), containsInAnyOrder(existingTagNamesToRemove.toArray()));
    }

    @Test
    void testSynchronizeWithDbAddNewTagsSuccessful() {
        List<String> tagNames = List.of("Tag1");
        TagEntity newTag = TagEntity.fromName(tagNames.get(0));
        ContentService.TagSynchronizationResult preparation = new ContentService.TagSynchronizationResult(
                Collections.emptyList(),
                List.of(newTag),
                Collections.emptyList()
        );
        ContentEntity content = new ContentEntity();
        content.setId(UUID.randomUUID());
        when(tagRepository.save(Mockito.any(TagEntity.class))).thenAnswer(i -> i.getArguments()[0]);
        contentService.synchronizeWithDatabase(content, preparation);
        verify(tagRepository).save(newTag);
        assertThat(content.getTags(), is(equalTo(Set.of(newTag))));
        assertThat(newTag.getContents(), is(equalTo(Set.of(content))));
    }

    @Test
    void testSynchronizeWithDbAddNewContentToExistingTagSuccessful() {
        List<String> tagNames = List.of("Tag1");
        TagEntity existingTag = TagEntity.fromName(tagNames.get(0));
        ContentService.TagSynchronizationResult preparation = new ContentService.TagSynchronizationResult(
                List.of(existingTag),
                Collections.emptyList(),
                Collections.emptyList()
        );
        ContentEntity content = new ContentEntity();
        content.setId(UUID.randomUUID());
        contentService.synchronizeWithDatabase(content, preparation);
        assertThat(content.getTags(), is(equalTo(Set.of(existingTag))));
        assertThat(existingTag.getContents(), is(equalTo(Set.of(content))));
    }

    @Test
    void testSynchronizeWithDbRemoveContentFromExistingTagSuccessful() {
        ContentEntity content = new ContentEntity();
        content.setId(UUID.randomUUID());
        List<String> tagNames = Collections.emptyList();
        TagEntity existingTag = TagEntity.fromName("Tag1");
        content.addToTags(existingTag);
        existingTag.addToContents(content);
        ContentService.TagSynchronizationResult preparation = new ContentService.TagSynchronizationResult(
                Collections.emptyList(),
                Collections.emptyList(),
                List.of(existingTag)
        );
        contentService.synchronizeWithDatabase(content, preparation);
        assertThat(content.getTags().size(), is(equalTo(0)));
        assertThat(existingTag.getContents().size(), is(equalTo(0)));
    }


}
