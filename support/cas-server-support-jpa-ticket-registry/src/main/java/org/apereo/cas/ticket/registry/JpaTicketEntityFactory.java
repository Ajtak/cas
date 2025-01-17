package org.apereo.cas.ticket.registry;

import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.configuration.support.RelaxedPropertyNames;
import org.apereo.cas.jpa.AbstractJpaEntityFactory;
import org.apereo.cas.ticket.AuthenticationAwareTicket;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.ticket.TicketGrantingTicketAwareTicket;
import org.apereo.cas.ticket.registry.generic.BaseTicketEntity;
import org.apereo.cas.ticket.registry.generic.JpaTicketEntity;
import org.apereo.cas.ticket.registry.mssql.MsSqlServerJpaTicketEntity;
import org.apereo.cas.ticket.registry.mysql.MySQLJpaTicketEntity;
import org.apereo.cas.ticket.registry.oracle.OracleJpaTicketEntity;
import org.apereo.cas.ticket.registry.postgres.PostgresJpaTicketEntity;
import org.apereo.cas.ticket.serialization.TicketSerializationManager;
import org.apereo.cas.util.function.FunctionUtils;
import org.apereo.cas.util.spring.ApplicationContextProvider;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ObjectUtils;
import jakarta.persistence.Table;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * This is {@link JpaTicketEntityFactory}.
 *
 * @author Misagh Moayyed
 * @since 6.4.0
 */
@Slf4j
public class JpaTicketEntityFactory extends AbstractJpaEntityFactory<BaseTicketEntity> {

    public JpaTicketEntityFactory(final String dialect) {
        super(dialect);
    }

    private static class ThreadSafeHolder {
        private static final TicketSerializationManager TICKET_SERIALIZATION_MANAGER =
            ApplicationContextProvider.getApplicationContext().getBean(TicketSerializationManager.class);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static TicketSerializationManager getTicketSerializationManager() {
        return ThreadSafeHolder.TICKET_SERIALIZATION_MANAGER;
    }

    public String getEntityName() {
        return getEntityClass().getSimpleName();
    }

    /**
     * From.
     *
     * @param ticket the ticket
     * @return the jpa ticket entity
     */
    public BaseTicketEntity fromTicket(final Ticket ticket) {
        val jsonBody = getTicketSerializationManager().serializeTicket(ticket);
        val authentication = ticket instanceof final AuthenticationAwareTicket authAware
            ? authAware.getAuthentication()
            : null;

        val parentTicket = ticket instanceof final TicketGrantingTicketAwareTicket tgtAware
            ? tgtAware.getTicketGrantingTicket()
            : null;

        val entity = FunctionUtils.doUnchecked(() -> getEntityClass().getDeclaredConstructor().newInstance());
        return entity
            .setId(ticket.getId())
            .setParentId(Optional.ofNullable(parentTicket).map(Ticket::getId).orElse(null))
            .setBody(jsonBody)
            .setType(ticket.getClass().getName())
            .setPrincipalId(Optional.ofNullable(authentication)
                .map(Authentication::getPrincipal)
                .map(Principal::getId)
                .orElse(null))
            .setCreationTime(ObjectUtils.defaultIfNull(ticket.getCreationTime(), ZonedDateTime.now(Clock.systemUTC())));
    }

    @Override
    public Class<BaseTicketEntity> getType() {
        return (Class<BaseTicketEntity>) getEntityClass();
    }

    /**
     * To registered service.
     *
     * @param entity the entity
     * @return the registered service
     */
    public Ticket toTicket(final BaseTicketEntity entity) {
        val ticket = getTicketSerializationManager().deserializeTicket(entity.getBody(), entity.getType());
        LOGGER.trace("Converted JPA entity [{}] to [{}]", this, ticket);
        return ticket;
    }

    /**
     * Gets table name.
     *
     * @return the table name
     */
    public String getTableName() {
        val tableName = getType().getAnnotation(Table.class).name();
        return RelaxedPropertyNames.NameManipulations.CAMELCASE_TO_UNDERSCORE_TITLE_CASE.apply(tableName);
    }

    private Class<? extends BaseTicketEntity> getEntityClass() {
        if (isOracle()) {
            return OracleJpaTicketEntity.class;
        }
        if (isMySql()) {
            return MySQLJpaTicketEntity.class;
        }
        if (isPostgres()) {
            return PostgresJpaTicketEntity.class;
        }
        if (isMsSqlServer()) {
            return MsSqlServerJpaTicketEntity.class;
        }
        return JpaTicketEntity.class;
    }
}
