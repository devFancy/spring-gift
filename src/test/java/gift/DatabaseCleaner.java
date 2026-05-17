package gift;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DatabaseCleaner implements InitializingBean {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        tableNames = entityManager.getMetamodel().getEntities().stream()
            .map(this::resolveTableName)
            .toList();
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN id RESTART WITH 1").executeUpdate();
        }
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private String resolveTableName(EntityType<?> entityType) {
        Class<?> javaType = entityType.getJavaType();
        Table table = javaType.getAnnotation(Table.class);
        if (table != null && !table.name().isEmpty()) {
            return table.name();
        }
        return toSnakeCase(javaType.getSimpleName());
    }

    private String toSnakeCase(String input) {
        StringBuilder result = new StringBuilder();
        for (int index = 0; index < input.length(); index++) {
            char character = input.charAt(index);
            if (Character.isUpperCase(character) && index > 0) {
                result.append('_');
            }
            result.append(Character.toLowerCase(character));
        }
        return result.toString();
    }
}
