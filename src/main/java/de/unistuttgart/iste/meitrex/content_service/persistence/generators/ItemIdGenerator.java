package de.unistuttgart.iste.meitrex.content_service.persistence.generators;

import de.unistuttgart.iste.meitrex.content_service.persistence.entity.ItemEntity;
import de.unistuttgart.iste.meitrex.generated.dto.Item;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.annotations.IdGeneratorType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Properties;
import java.util.UUID;

/**
 * This class generates unique identifiers for Item entities using UUIDs.
 *
 * It ensures that it is possible to set the id manually (e.g. when importing items from the quiz service).
 */
public class ItemIdGenerator implements IdentifierGenerator, Configurable {


    @Override
    public Serializable generate(
            SharedSessionContractImplementor session, Object obj)
            throws HibernateException {

        boolean isItem = obj instanceof ItemEntity;
        if (!isItem) {
            return UUID.randomUUID();
        }

        // if item already has an id, return it
        ItemEntity item = (ItemEntity) obj;
        if (item.getId() != null) {
            return item.getId();
        }


        return UUID.randomUUID();
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException{

    }

    @IdGeneratorType(ItemIdGenerator.class)
    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ItemGeneratedId {

    }
}
