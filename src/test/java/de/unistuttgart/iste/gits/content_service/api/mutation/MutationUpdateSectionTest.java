package de.unistuttgart.iste.gits.content_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.content_service.persistence.dao.SectionEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.SectionRepository;
import de.unistuttgart.iste.gits.generated.dto.Section;
import de.unistuttgart.iste.gits.generated.dto.UpdateSectionInput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@GraphQlApiTest
@TablesToDelete({"stage_required_contents", "stage_optional_content", "stage" ,"section" , "content_tags", "user_progress_data", "content", "tag"})
class MutationUpdateSectionTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Test
    void testSectionUpdate(GraphQlTester tester){

        // fill database
        SectionEntity sectionEntity = SectionEntity.builder()
                .name("Test Section")
                .chapterId(UUID.randomUUID())
                .stages(new HashSet<>())
                .build();
        sectionEntity = sectionRepository.save(sectionEntity);

        UpdateSectionInput input = UpdateSectionInput.builder()
                .setId(sectionEntity.getId())
                .setName("New Name")
                .build();

        String query = """
                mutation ($input: UpdateSectionInput!){
                    updateSection(input: $input){
                    id
                    name
                    chapterId
                    stages {
                        id                 
                        }
                    }
                }
                """;

        SectionEntity finalSectionEntity = sectionEntity;

        tester.document(query)
                .variable("input", input)
                .execute()
                .path("updateSection")
                .entity(Section.class)
                .satisfies(section -> {
                          assertEquals(finalSectionEntity.getId(), section.getId());
                          assertEquals(finalSectionEntity.getChapterId(), section.getChapterId());
                          assertEquals(input.getName(), section.getName());
                          assertTrue(section.getStages().isEmpty());
                      }
                );
    }
}
