package org.apereo.cas.web.flow.login;

import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.scripting.ExecutableCompiledGroovyScript;
import org.apereo.cas.util.scripting.ScriptResourceCacheManager;
import org.apereo.cas.util.scripting.ScriptingUtils;
import org.apereo.cas.web.flow.actions.BaseCasWebflowAction;
import org.apereo.cas.web.support.WebUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.net.URI;

/**
 * This is {@link RedirectUnauthorizedServiceUrlAction}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Slf4j
@RequiredArgsConstructor
@Getter
public class RedirectUnauthorizedServiceUrlAction extends BaseCasWebflowAction {
    private final ServicesManager servicesManager;

    private final ApplicationContext applicationContext;

    private final ScriptResourceCacheManager<String, ExecutableCompiledGroovyScript> scriptResourceCacheManager;

    @Override
    public Event doExecute(final RequestContext requestContext) {
        var redirectUrl = determineUnauthorizedServiceRedirectUrl(requestContext);
        val url = redirectUrl.toString();
        if (ScriptingUtils.isGroovyScript(url)) {
            val registeredService = WebUtils.getRegisteredService(requestContext);
            val authentication = WebUtils.getAuthentication(requestContext);
            val args = CollectionUtils.<String, Object>wrap("registeredService", registeredService,
                "authentication", authentication,
                "requestContext", requestContext,
                "applicationContext", applicationContext,
                "logger", LOGGER);
            val scriptToExec = scriptResourceCacheManager.resolveScriptableResource(url);
            scriptToExec.setBinding(args);
            redirectUrl = scriptToExec.execute(args.values().toArray(), URI.class);
        }

        LOGGER.debug("Redirecting to unauthorized redirect URL [{}]", redirectUrl);
        WebUtils.putUnauthorizedRedirectUrlIntoFlowScope(requestContext, redirectUrl);
        return null;
    }

    /**
     * Determine unauthorized service redirect url.
     *
     * @param context the context
     * @return the uri
     */
    protected URI determineUnauthorizedServiceRedirectUrl(final RequestContext context) {
        val redirectUrl = WebUtils.getUnauthorizedRedirectUrlFromFlowScope(context);
        val currentEvent = context.getCurrentEvent();
        val eventAttributes = currentEvent.getAttributes();
        LOGGER.debug("Finalizing the unauthorized redirect URL [{}] when processing event [{}] with attributes [{}]",
            redirectUrl, currentEvent.getId(), eventAttributes);
        return redirectUrl;
    }
}
