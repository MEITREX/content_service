package de.unistuttgart.iste.meitrex.content_service.controller;

import de.unistuttgart.iste.meitrex.content_service.ContentServiceApplication;
import de.unistuttgart.iste.meitrex.content_service.config.TestEventPublisher;
import de.unistuttgart.iste.meitrex.content_service.config.TestsContainer;
import de.unistuttgart.iste.meitrex.content_service.event.subscribe.quiz.QuestionEventType;
import de.unistuttgart.iste.meitrex.content_service.event.subscribe.quiz.UpdateQuizEvent;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.AssessmentEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.ContentEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.ContentMetadataEmbeddable;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.ItemEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.AssessmentRepository;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.meitrex.generated.dto.ContentType;
import io.dapr.springboot.DaprAutoConfiguration;
import io.dapr.testcontainers.DaprContainer;
import io.restassured.RestAssured;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SpringBootTest(
        classes = {
                ContentServiceApplication.class,
                TestsContainer.class,
                DaprAutoConfiguration.class },
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class SubscriptionControllerTest {


    // create with beans from tests containers
    private TestEventPublisher eventPublisher;
    private DaprContainer daprContainer;
    private TestsContainer.RedisTestContainer redisContainer;

    private SubscriptionController controller;

    // start a test container for PostgreSQL
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private AssessmentRepository assessmentRepository;
    @Autowired
    private SessionFactory sessionFactory;


    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Value("${server.port}")
    private int serverPort;




    @BeforeEach
    void setUp() {
        daprContainer.start();

        RestAssured.baseURI = "http://localhost:" + serverPort;
        org.testcontainers.Testcontainers.exposeHostPorts(serverPort);
        //
        Wait.forLogMessage(".*Connected to placement.*", 1)
                .waitUntilReady(daprContainer);
    }



    @Autowired
    public SubscriptionControllerTest(
            TestEventPublisher eventPublisher,
            TestsContainer.RedisTestContainer redisContainer,
            SubscriptionController controller,
            DaprContainer daprContainer) {
        this.eventPublisher = eventPublisher;
        this.daprContainer = daprContainer;
        this.redisContainer = redisContainer;
        this.controller = controller;

    }

    @Transactional
    protected AssessmentEntity prepareDbOnQuizUpdate(UUID courseId, UUID chapterId) {
        assessmentRepository.deleteAll();
        contentRepository.deleteAll();

        // Prepare Database for the test
        AssessmentEntity contentEntity = new AssessmentEntity();
        ContentMetadataEmbeddable metadata = new ContentMetadataEmbeddable();
        OffsetDateTime suggestedDate = OffsetDateTime.now().plusDays(1);
        metadata.setTags(Set.of(new String[] { "tag1", "tag2" }));
        metadata.setCourseId(courseId);
        metadata.setSuggestedDate(suggestedDate);
        metadata.setType(ContentType.QUIZ);
        metadata.setChapterId(chapterId);
        metadata.setName("test");
        contentEntity.setMetadata(metadata);

        return contentRepository.save(contentEntity);
    }


    @Transactional
    public AssessmentEntity getAssessmentEntity(UUID id) {
        return assessmentRepository.findById(id).orElse(null);
    }

    @Transactional
    public ContentEntity createContentEntity(ContentType type, UUID courseId, UUID chapterId) {
        ContentEntity contentEntity = new ContentEntity();
        ContentMetadataEmbeddable metadata = new ContentMetadataEmbeddable();
        OffsetDateTime suggestedDate = OffsetDateTime.now().plusDays(1);
        metadata.setTags(Set.of(new String[] { "tag1", "tag2" }));
        metadata.setCourseId(courseId);
        metadata.setSuggestedDate(suggestedDate);
        metadata.setType(type);
        metadata.setChapterId(chapterId);
        metadata.setName("test");
        contentEntity.setMetadata(metadata);

        contentEntity = contentRepository.save(contentEntity);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
        return contentEntity;
    }

    @Test
    @Transactional
    public void onQuizUpdateEvent() throws InterruptedException {


        Session session = sessionFactory.openSession();
        session.beginTransaction();
        final UUID courseId = UUID.randomUUID();
        final UUID chapterId = UUID.randomUUID();


        ContentEntity contentEntity = createContentEntity(ContentType.QUIZ, courseId, chapterId);

        session.getTransaction().commit();


        final UUID assessmentId = contentEntity.getId();

        List<QuestionEventType> questionPool = new ArrayList<>();

        questionPool.add(QuestionEventType
                .builder()
                .aiGenerated(true)
                .type("MULTIPLE_CHOICE")
                .itemId(UUID.randomUUID())
                .number(1)
                .build());

        questionPool.add(QuestionEventType
                .builder()
                .aiGenerated(false)
                .type("FREE_TEXT")
                .itemId(UUID.randomUUID())
                .number(2)
                .build());

        UpdateQuizEvent e = UpdateQuizEvent
                .builder()
                .courseId(courseId)
                .assessmentId(assessmentId)
                .questionPool(questionPool)
                .requiredCorrectAnswers(3)
                .questionPoolingMode("RANDOM")
                .build();

        // allow some time for the event to be processed
        Thread.sleep(250);
        session.beginTransaction();

        AssessmentEntity beforeUpdateContent = getAssessmentEntity(assessmentId);

        assert beforeUpdateContent != null;
        assert beforeUpdateContent.getItems().isEmpty();

        eventPublisher.publishUpdateQuizEvent(e).block();

        TestTransaction.flagForCommit();
        TestTransaction.end();

        Thread.sleep(250);
        TestTransaction.start();


        AssessmentEntity updatedContent = getAssessmentEntity(assessmentId);

        // verify that the content entity has been updated
        assert updatedContent != null;
        assert updatedContent.getItems().size() == 2;

        List<UUID> insertedItemIds = updatedContent.getItems().stream().map(ItemEntity::getId).toList();
        List<UUID> expectedItemIds = questionPool.stream().map(QuestionEventType::getItemId).toList();

        assert insertedItemIds.containsAll(expectedItemIds);



    }

}
