package de.unistuttgart.iste.gits.content_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.content_service.persistence.entity.SectionEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.SectionRepository;
import de.unistuttgart.iste.gits.generated.dto.CreateSectionInput;
import de.unistuttgart.iste.gits.generated.dto.Section;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@GraphQlApiTest
@TablesToDelete({"stage_required_contents", "stage_optional_contents", "stage", "section", "content_tags", "user_progress_data", "content"})
class MutationCreateSectionTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Test
    @Transactional
    @Commit
    void testSectionCreation(final GraphQlTester tester){
        final CreateSectionInput input = CreateSectionInput.builder()
                .setChapterId(UUID.randomUUID())
                .setName("Test Section")
                .build();
        final String query = """
                mutation ($input: CreateSectionInput!) {
                    createSection(input: $input) {
                        id
                        name
                        chapterId
                        stages {
                            id
                            position
                            optionalContents {
                                id
                            }
                            requiredContents {
                                id
                            }
                        }
                    }
                }
                """;
        tester.document(query)
                .variable("input", input)
                .execute()
                .path("createSection")
                .entity(Section.class).satisfies(workPath -> {
                    assertNotNull(workPath.getId());
                    assertEquals(input.getName(), workPath.getName());
                    assertEquals(input.getChapterId() ,workPath.getChapterId());
                    assertTrue(workPath.getStages().isEmpty());
                });

        List<SectionEntity> sectionEntities = sectionRepository.findAll();
        assertEquals(1, sectionEntities.size());
        SectionEntity sectionEntity = sectionEntities.get(0);

        assertEquals(input.getName(), sectionEntity.getName());
        assertEquals(input.getChapterId(), sectionEntity.getChapterId());
        assertEquals(0, sectionEntity.getPosition());
        assertTrue(sectionEntity.getStages().isEmpty());
    }
}
