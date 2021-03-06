package com.google.uaa.granter;

import cn.hutool.core.util.StrUtil;
import com.google.core.common.constant.Oauth2Constant;
import com.google.core.redis.core.RedisService;
import com.google.uaa.sms.SmsCodeAuthenticationToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author iris
 */
public class SmsCodeTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "sms";

    private final AuthenticationManager authenticationManager;

    private RedisService redisService;

    public SmsCodeTokenGranter(AuthenticationManager authenticationManager,
                               AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                               OAuth2RequestFactory requestFactory, RedisService redisService) {
        this(authenticationManager, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.redisService = redisService;
    }

    protected SmsCodeTokenGranter(AuthenticationManager authenticationManager,
                                  AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                                  OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String mobile = parameters.get("mobile");
        String code = parameters.get("code");

        if (StrUtil.isBlank(code)) {
            throw new UserDeniedAuthorizationException("?????????????????????");
        }

        String codeFromRedis = null;
        // ???Redis?????????????????????????????????
        try {
            codeFromRedis = redisService.get(Oauth2Constant.SMS_CODE_KEY + mobile).toString();
        } catch (Exception e) {
            throw new UserDeniedAuthorizationException("?????????????????????");
        }

        if (codeFromRedis == null) {
            throw new UserDeniedAuthorizationException("?????????????????????");
        }
        // ????????????????????????????????????
        if (!StrUtil.equalsIgnoreCase(code, codeFromRedis)) {
            throw new UserDeniedAuthorizationException("?????????????????????");
        }

        redisService.del(Oauth2Constant.SMS_CODE_KEY + mobile);


        Authentication userAuth = new SmsCodeAuthenticationToken(mobile);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        try {
            userAuth = authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException | BadCredentialsException ase) {
            //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            throw new InvalidGrantException(ase.getMessage());
        }
        // If the username/password are wrong the spec says we should send 400/invalid grant

        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate user: " + mobile);
        }

        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }
}

