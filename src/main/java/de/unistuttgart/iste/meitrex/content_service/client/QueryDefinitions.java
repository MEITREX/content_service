package de.unistuttgart.iste.meitrex.content_service.client;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class QueryDefinitions {
    public static final String CONTENTS_FRAGMENT = """
            fragment ContentFragment on Content {
                id
                metadata {
                    name
                    tagNames
                    suggestedDate
                    type
                    chapterId
                    rewardPoints
                    courseId
                }
                ...on Assessment {
                     assessmentMetadata {
                         skillTypes
                         skillPoints
                         initialLearningInterval
                     }
                     items{
                        id
                        associatedSkills{
                            skillName
                            skillCategory
                            isCustomSkill
                        }
                        associatedBloomLevels
                     }
                 }
                progressDataForUser(userId: $userId) {
                    userId
                    contentId
                    learningInterval
                    nextLearnDate
                    lastLearnDate
                    isLearned
                    isDueForReview
                    log {
                        timestamp
                        success
                        correctness
                        hintsUsed
                        timeToComplete
                    }
                }
            }
            
            
            """;

    public static final String CONTENTS_BY_COURSE_IDS_QUERY = CONTENTS_FRAGMENT + """
            query($courseIds: [UUID!]!, $userId: UUID!) {
                _internal_noauth_contentsByCourseIds(courseIds: $courseIds) {
                    ...ContentFragment
                }
            }
            """;

    public static final String CONTENTS_BY_CHAPTER_IDS_QUERY = CONTENTS_FRAGMENT + """
            query($chapterIds: [UUID!]!, $userId: UUID!) {
                _internal_noauth_contentsByChapterIds(chapterIds: $chapterIds) {
                    ...ContentFragment
                }
            }
            """;

    public static final String CONTENTS_BY_CONTENT_IDS_QUERY = CONTENTS_FRAGMENT + """
            query($ids: [UUID!]!, $userId: UUID!) {
                _internal_noauth_contentsByIds(ids: $ids) {
                    ...ContentFragment
                }
            }
            """;

    public static final String PROGRESS_BY_CHAPTER_ID = """
            query($chapterId: UUID!, $userId: UUID!) {
                _internal_noauth_progressByChapterId(chapterId: $chapterId, userId: $userId) {
                    completedContents
                    totalContents
                    progress
                }
            }
            """;

    public static final String CONTENTS_BY_COURSE_ID_QUERY_NAME = "_internal_noauth_contentsByCourseIds";

    public static final String CONTENTS_BY_CHAPTER_ID_QUERY_NAME = "_internal_noauth_contentsByChapterIds";

    public static final String CONTENTS_BY_CONTENT_IDS_QUERY_NAME = "_internal_noauth_contentsByIds";

    public static final String PROGRESS_BY_CHAPTER_ID_QUERY_NAME = "_internal_noauth_progressByChapterId";
}
