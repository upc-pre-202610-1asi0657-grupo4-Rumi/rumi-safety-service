package com.rumi.safety.infrastructure.persistence;

import com.rumi.safety.SafetyFixtures;
import com.rumi.safety.domain.model.ChecklistItem;
import com.rumi.safety.domain.model.ChecklistItemAnswer;
import com.rumi.safety.domain.model.ChecklistResponse;
import com.rumi.safety.domain.model.ChecklistTemplate;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static com.rumi.safety.SafetyFixtures.BUILDING_ID;
import static com.rumi.safety.SafetyFixtures.GAS_ITEM_ID;
import static com.rumi.safety.SafetyFixtures.NOW;
import static com.rumi.safety.SafetyFixtures.RESIDENT_ID;
import static com.rumi.safety.SafetyFixtures.TEMPLATE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(showSql = false, properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Import({JpaChecklistTemplateRepository.class, JpaChecklistResponseRepository.class})
class JpaChecklistRepositoryTest {

    @Autowired
    private JpaChecklistTemplateRepository templateRepository;

    @Autowired
    private JpaChecklistResponseRepository responseRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void savesATemplateAndReadsItsItemsOrderedByPosition() {
        assertThat(templateRepository.isEmpty()).isTrue();
        templateRepository.save(SafetyFixtures.template());
        entityManager.flush();
        entityManager.clear();

        ChecklistTemplate found = templateRepository.findById(TEMPLATE_ID).orElseThrow();

        assertThat(templateRepository.isEmpty()).isFalse();
        assertThat(found.items()).extracting(ChecklistItem::position).containsExactly(1, 2, 3);
        assertThat(found).isEqualTo(SafetyFixtures.template());
    }

    @Test
    void theCurrentTemplateIsTheOneWithTheHighestVersion() {
        templateRepository.save(SafetyFixtures.template());
        templateRepository.save(new ChecklistTemplate(UUID.randomUUID(), "POST_EARTHQUAKE_SELF_CHECK", 2,
                List.of(new ChecklistItem(UUID.randomUUID(), 1, "Gas"))));
        entityManager.flush();
        entityManager.clear();

        assertThat(templateRepository.findCurrent()).get().extracting(ChecklistTemplate::version).isEqualTo(2);
    }

    @Test
    void savesAResponseWithItsAnswers() {
        ChecklistTemplate template = templateRepository.save(SafetyFixtures.template());
        ChecklistResponse response = ChecklistResponse.submit(
                template, RESIDENT_ID, BUILDING_ID, SafetyFixtures.completeAnswers(), NOW);

        responseRepository.save(response);
        entityManager.flush();
        entityManager.clear();

        ChecklistResponse found = responseRepository.findById(response.id()).orElseThrow();
        assertThat(found).isEqualTo(response);
        assertThat(found.answers()).extracting(ChecklistItemAnswer::checked).containsExactly(false, true, false);
    }

    @Test
    void anItemCannotBeAnsweredTwiceInTheSameResponse() {
        templateRepository.save(SafetyFixtures.template());
        ChecklistResponse duplicated = new ChecklistResponse(UUID.randomUUID(), TEMPLATE_ID, RESIDENT_ID, BUILDING_ID,
                NOW, List.of(SafetyFixtures.answer(GAS_ITEM_ID, true), SafetyFixtures.answer(GAS_ITEM_ID, false)));

        responseRepository.save(duplicated);

        assertThatThrownBy(() -> entityManager.flush()).isInstanceOf(PersistenceException.class);
    }
}
