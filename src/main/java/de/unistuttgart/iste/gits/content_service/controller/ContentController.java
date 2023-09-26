package de.unistuttgart.iste.gits.content_service.controller;

import de.unistuttgart.iste.gits.common.user_handling.LoggedInUser;
import de.unistuttgart.iste.gits.content_service.service.ContentService;
import de.unistuttgart.iste.gits.content_service.service.SuggestionService;
import de.unistuttgart.iste.gits.content_service.service.UserProgressDataService;
import de.unistuttgart.iste.gits.generated.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final UserProgressDataService userProgressDataService;
    private final SuggestionService suggestionService;

    @QueryMapping
    public ContentPayload contents() {
        return contentService.getAllContents();
    }

    @QueryMapping
    public List<Content> contentsByIds(@Argument final List<UUID> ids) {
        return contentService.getContentsById(ids);
    }

    @QueryMapping
    public List<Content> findContentsByIds(@Argument final List<UUID> ids) {
        return contentService.findContentsById(ids);
    }

    @QueryMapping
    public List<Suggestion> suggestionsByChapterIds(@Argument final List<UUID> chapterIds,
                                                    @Argument final int amount,
                                                    @Argument final List<SkillType> skillTypes,
                                                    @ContextValue final LoggedInUser currentUser) {
        return suggestionService.createSuggestions(chapterIds, currentUser.getId(), amount, skillTypes);
    }

    @QueryMapping
    public List<List<Content>> contentsByChapterIds(@Argument final List<UUID> chapterIds) {
        return contentService.getContentsByChapterIds(chapterIds);
    }

    @QueryMapping(name = "_internal_noauth_achievableSkillTypesByChapterIds")
    public List<List<SkillType>> internalAchievableSkillTypesByChapterIds(@Argument final List<UUID> chapterIds) {
        return contentService.getAchievableSkillTypesByChapterIds(chapterIds);
    }

    @MutationMapping
    public ContentMutation mutateContent(@Argument final UUID contentId) {
        //parent object for nested mutations
        return new ContentMutation(contentId, contentId);
    }

    @MutationMapping
    public MediaContent createMediaContent(@Argument final CreateMediaContentInput input) {
        return contentService.createMediaContent(input);
    }

    @SchemaMapping(typeName = "ContentMutation")
    public MediaContent updateMediaContent(@Argument final UpdateMediaContentInput input, final ContentMutation contentMutation) {
        return contentService.updateMediaContent(contentMutation.getContentId(), input);
    }

    @MutationMapping
    public Assessment createAssessment(@Argument final CreateAssessmentInput input) {
        return contentService.createAssessment(input);
    }

    @SchemaMapping(typeName = "ContentMutation")
    public Assessment updateAssessment(@Argument final UpdateAssessmentInput input, final ContentMutation contentMutation) {
        return contentService.updateAssessment(contentMutation.getContentId(), input);
    }

    @SchemaMapping(typeName = "ContentMutation")
    public UUID deleteContent(final ContentMutation contentMutation) {
        return contentService.deleteContent(contentMutation.getContentId());
    }

    @SchemaMapping(typeName = "ContentMutation")
    public Content addTagToContent(@Argument final String tagName, final ContentMutation contentMutation) {
        return contentService.addTagToContent(contentMutation.getContentId(), tagName);
    }

    @SchemaMapping(typeName = "ContentMutation")
    public Content removeTagFromContent(@Argument final String tagName, final ContentMutation contentMutation) {
        return contentService.removeTagFromContent(contentMutation.getContentId(), tagName);
    }

    @QueryMapping
    public List<CompositeProgressInformation> progressByChapterIds(@Argument final List<UUID> chapterIds, @ContextValue final LoggedInUser currentUser) {
        return userProgressDataService.getProgressByChapterIdsForUser(chapterIds, currentUser.getId());
    }


    @QueryMapping(name = "_internal_noauth_contentWithNoSectionByChapterIds")
    public List<List<Content>> contentWithNoSectionByChapterIds(@Argument final List<UUID> chapterIds) {
        return contentService.getContentWithNoSection(chapterIds);
    }

    public abstract class ContentResolver<T extends Content> {
        @SchemaMapping(field = "userProgressData")
        public UserProgressData userProgressData(final T content, @ContextValue final LoggedInUser currentUser) {
            return userProgressDataService.getUserProgressData(currentUser.getId(), content.getId());
        }

        @SchemaMapping(field = "progressDataForUser")
        public UserProgressData progressDataForUser(final T content, @Argument final UUID userId) {
            return userProgressDataService.getUserProgressData(userId, content.getId());
        }
    }

    public abstract class AssessmentResolver<T extends Assessment> extends ContentResolver<T> {
    }

    @Controller
    public class MediaContentResolver extends ContentResolver<MediaContent> {
    }

    @Controller
    public class FlashcardSetAssessmentResolver extends ContentResolver<FlashcardSetAssessment> {
    }

    @Controller
    public class QuizAssessmentResolver extends ContentResolver<QuizAssessment> {
    }

}

