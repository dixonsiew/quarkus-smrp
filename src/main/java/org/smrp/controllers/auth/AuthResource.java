package org.smrp.controllers.auth;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.smrp.dtos.LoginDto;
import org.smrp.dtos.RefreshTokenDto;
import org.smrp.models.TokenResponse;
import org.smrp.services.TokenService;
import org.smrp.services.UserService;

import io.quarkus.security.UnauthorizedException;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;


@Tag(name = "Auth")
@Path("/o")
public class AuthResource {

    private final TokenService tokenService;
    private final UserService userService;
    
    public AuthResource(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }
    
    @POST
    @Path("/token")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> login(@Valid LoginDto data) {
        UnauthorizedException ex = new UnauthorizedException("Invalid Credentials");
        return userService.findByUsername(data.getUsername()).flatMap(user -> {
            if (user == null) {
                throw ex;
            }

            boolean valid = false;
            valid = userService.validateCredentials(user, data.getPassword());
            if (!valid) {
                throw ex;
            }

            return userService.updateLastLogin(user.getId()).flatMap(x -> {
                String token = tokenService.generateAccessToken(user);
                String refreshToken = tokenService.generateRefreshToken(user);

                TokenResponse tokenResponse = new TokenResponse();
                tokenResponse.setType("bearer");
                tokenResponse.setToken(token);
                tokenResponse.setRefreshToken(refreshToken);
                
                NewCookie cookie = createCookie("token", token, "/", 30 * 24 * 60 * 60);
                NewCookie refreshCookie = createCookie("refreshToken", refreshToken, "/smrp/o/refresh-token", 3650 * 24 * 60 * 60);
                return Uni.createFrom().item(Response.ok(tokenResponse)
                    .cookie(cookie)
                    .cookie(refreshCookie)
                    .build());
            });
        });
    }

    @POST
    @Path("/refresh-token")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> refresh(@Valid RefreshTokenDto data, @CookieParam("refreshToken") String cookieToken) {
        UnauthorizedException ex = new UnauthorizedException("Invalid Credentials");
        String refreshToken = cookieToken;
        if (cookieToken == null || cookieToken == "") {
            refreshToken = data.getRefreshToken();
        }

        if (refreshToken == null || refreshToken == "") {
            throw ex;
        }

        return tokenService.createAccessTokenFromRefreshToken(refreshToken).flatMap(mx -> {
            String token = (String) mx.get("token");
            String rtoken = (String) mx.get("refreshToken");
            
            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.setType("bearer");
            tokenResponse.setToken(token);
            tokenResponse.setRefreshToken(rtoken);
            
            NewCookie cookie = createCookie("token", token, "/", 30 * 24 * 60 * 60);
            NewCookie refreshCookie = createCookie("refreshToken", rtoken, "/smrp/o/refresh-token", 3650 * 24 * 60 * 60);
            return Uni.createFrom().item(Response.ok(tokenResponse)
                .cookie(cookie)
                .cookie(refreshCookie)
                .build());
        });
    }

    private NewCookie createCookie(String name, String value, String path, int maxAge) {
        return new NewCookie.Builder(name)
            .value(value)
            .httpOnly(true)
            .secure(false)
            .sameSite(NewCookie.SameSite.LAX)
            .path(path)
            .maxAge(maxAge)
            .build();
    }
}
