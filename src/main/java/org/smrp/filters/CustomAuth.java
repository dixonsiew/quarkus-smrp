package org.smrp.filters;

import java.util.Collections;
import java.util.Set;

import org.smrp.services.TokenService;

import io.jsonwebtoken.Claims;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class CustomAuth implements HttpAuthenticationMechanism {

    private final TokenService tokenService;

    public CustomAuth(TokenService tokenService) {
        this.tokenService = tokenService;
    }
    
    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
        String token = context.request().getHeader("Authorization");
        if (token == null || token == "") {
            // No token found, delegate to other mechanisms or return anonymous identity
            Cookie cookieToken = context.request().getCookie("token");
            if (cookieToken == null) {
                return Uni.createFrom().nullItem();
            }
            
            token = cookieToken.getValue();
            if (token == null || token == "") {
                return Uni.createFrom().nullItem();
            }
        }

        String tokenStr = token.replace("Bearer ", "");
        Claims claims = tokenService.decodeToken(tokenStr);
        String sub = claims.getSubject();
        String username = (String) claims.get("username");
        SecurityIdentity identity = QuarkusSecurityIdentity.builder()
                .setPrincipal(new QuarkusPrincipal(username))
                .addAttribute("id", Long.parseLong(sub))
                .build();
        return Uni.createFrom().item(identity);
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        // Return a 401 Unauthorized challenge if authentication fails
        ChallengeData challenge = new ChallengeData(401, HttpHeaderNames.WWW_AUTHENTICATE, "Custom-Token-Auth");
        return Uni.createFrom().item(challenge);
    }
    
    @Override
    public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
        return Collections.emptySet();
    }
}
