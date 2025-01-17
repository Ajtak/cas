package org.apereo.cas.support.saml;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link SamlIdPConfigurationTests}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Tag("SAML2")
class SamlIdPConfigurationTests extends BaseSamlIdPConfigurationTests {
    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Test
    void verifySigValidationFilterByRes() throws Exception {
        val filter = SamlUtils.buildSignatureValidationFilter(new ClassPathResource("metadata/idp-signing.crt"));
        assertNotNull(filter);
    }

    @Test
    void verifySigValidationFilterPublicKey() throws Exception {
        val filter = SamlUtils.buildSignatureValidationFilter(new ClassPathResource("public-key.pem"));
        assertNotNull(filter);
    }

    @Test
    void verifySigValidationFilter() {
        val filter = SamlUtils.buildSignatureValidationFilter(applicationContext, "classpath:metadata/idp-signing.crt");
        assertNotNull(filter);
    }

    @Test
    void verifySigValidationFilterByPath() throws Exception {
        val filter = SamlUtils.buildSignatureValidationFilter("classpath:metadata/idp-signing.crt");
        assertNotNull(filter);
    }
}
