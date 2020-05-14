package org.apereo.cas.gauth.credential;

import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationHandlersConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationMetadataConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPolicyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreMultifactorAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreServicesAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasCouchDbCoreConfiguration;
import org.apereo.cas.config.CasPersonDirectoryConfiguration;
import org.apereo.cas.config.GoogleAuthenticatorCouchDbConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.config.support.authentication.GoogleAuthenticatorAuthenticationEventExecutionPlanConfiguration;
import org.apereo.cas.config.support.authentication.GoogleAuthenticatorAuthenticationMultifactorProviderBypassConfiguration;
import org.apereo.cas.couchdb.core.CouchDbConnectorFactory;
import org.apereo.cas.couchdb.gauth.credential.GoogleAuthenticatorAccountCouchDbRepository;
import org.apereo.cas.logout.config.CasCoreLogoutConfiguration;
import org.apereo.cas.otp.repository.credentials.OneTimeTokenCredentialRepository;
import org.apereo.cas.util.junit.EnabledIfPortOpen;
import org.apereo.cas.web.config.CasCookieConfiguration;
import org.apereo.cas.web.flow.config.CasCoreWebflowConfiguration;
import org.apereo.cas.web.flow.config.CasMultifactorAuthenticationWebflowConfiguration;
import org.apereo.cas.web.flow.config.CasWebflowContextConfiguration;

import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * This is {@link CouchDbGoogleAuthenticatorTokenCredentialRepositoryTests}.
 *
 * @author Timur Duehr
 * @since 6.0.0
 */
@Tag("CouchDb")
@SpringBootTest(classes = {
    CasCouchDbCoreConfiguration.class,
    BaseOneTimeTokenCredentialRepositoryTests.BaseTestConfiguration.class,
    GoogleAuthenticatorCouchDbConfiguration.class,
    CasCoreWebflowConfiguration.class,
    CasWebflowContextConfiguration.class,
    CasCoreMultifactorAuthenticationConfiguration.class,
    CasMultifactorAuthenticationWebflowConfiguration.class,
    GoogleAuthenticatorAuthenticationMultifactorProviderBypassConfiguration.class,
    CasCoreTicketsConfiguration.class,
    CasCoreTicketIdGeneratorsConfiguration.class,
    CasCoreTicketCatalogConfiguration.class,
    CasCoreLogoutConfiguration.class,
    CasCoreHttpConfiguration.class,
    CasCoreServicesConfiguration.class,
    CasWebApplicationServiceFactoryConfiguration.class,
    CasCoreAuthenticationConfiguration.class,
    CasCoreServicesAuthenticationConfiguration.class,
    CasCoreAuthenticationMetadataConfiguration.class,
    CasCoreAuthenticationPolicyConfiguration.class,
    CasCoreAuthenticationPrincipalConfiguration.class,
    CasCoreAuthenticationHandlersConfiguration.class,
    CasCoreAuthenticationSupportConfiguration.class,
    CasPersonDirectoryConfiguration.class,
    GoogleAuthenticatorAuthenticationEventExecutionPlanConfiguration.class,
    AopAutoConfiguration.class,
    CasCookieConfiguration.class,
    CasCoreConfiguration.class,
    CasCoreAuthenticationServiceSelectionStrategyConfiguration.class,
    CasCoreUtilConfiguration.class,
    RefreshAutoConfiguration.class,
    CasCoreWebConfiguration.class},
    properties = {
        "cas.authn.mfa.gauth.crypto.enabled=false",
        "cas.authn.mfa.gauth.couch-db.username=cas",
        "cas.authn.mfa.gauth.couch-db.dbName=gauth_credential",
        "cas.authn.mfa.gauth.couchdb.password=password"
    })
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
@Getter
@EnabledIfPortOpen(port = 5984)
public class CouchDbGoogleAuthenticatorTokenCredentialRepositoryTests extends BaseOneTimeTokenCredentialRepositoryTests {

    @Autowired
    @Qualifier("oneTimeTokenAccountCouchDbFactory")
    private CouchDbConnectorFactory couchDbFactory;

    @Autowired
    @Qualifier("googleAuthenticatorAccountRegistry")
    private OneTimeTokenCredentialRepository registry;

    @Autowired
    @Qualifier("couchDbOneTimeTokenAccountRepository")
    private GoogleAuthenticatorAccountCouchDbRepository couchDbRepository;

    @BeforeEach
    @Override
    public void initialize() {
        couchDbFactory.getCouchDbInstance().createDatabaseIfNotExists(couchDbFactory.getCouchDbConnector().getDatabaseName());
        couchDbRepository.initStandardDesignDocument();
        super.initialize();
    }

    @AfterEach
    @Override
    public void afterEach() {
        super.afterEach();
        couchDbFactory.getCouchDbInstance().deleteDatabase(couchDbFactory.getCouchDbConnector().getDatabaseName());
    }
}