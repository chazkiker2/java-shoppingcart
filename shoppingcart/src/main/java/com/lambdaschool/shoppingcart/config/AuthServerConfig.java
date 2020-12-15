package com.lambdaschool.shoppingcart.config;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;


/**
 * This class enables and configures the Authorization Server. The class is also responsible for granting authorization to the client.
 * This class is responsible for generating and maintaining the access tokens.
 */
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig
		extends AuthorizationServerConfigurerAdapter {

	static final String CLIENT_ID = System.getenv("OAUTHCLIENTID");

	static final String CLIENT_SECRET = System.getenv("OAUTHCLIENTSECRET"); // read from environment variable

	static final String GRANT_TYPE_PASSWORD = "password";

	static final String AUTHORIZATION_CODE = "authorization_code";

	static final String SCOPE_READ = "read";

	static final String SCOPE_WRITE = "write";

	static final String SCOPE_TRUST = "trust";

	static final int ACCESS_TOKEN_VALIDITY_SECONDS = -1;

	//	@Autowired
	private final TokenStore tokenStore;

	//	@Autowired
	private final AuthenticationManager authenticationManager;

	//	@Autowired
	private final PasswordEncoder encoder;

	@Autowired
	public AuthServerConfig(
			TokenStore tokenStore,
			AuthenticationManager authenticationManager,
			PasswordEncoder encoder
	) {
		this.tokenStore            = tokenStore;
		this.authenticationManager = authenticationManager;
		this.encoder               = encoder;
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer configurer)
			throws
			Exception {
		configurer.inMemory()
		          .withClient(CLIENT_ID)
		          .secret(encoder.encode(CLIENT_SECRET))
		          .authorizedGrantTypes(
				          GRANT_TYPE_PASSWORD,
				          AUTHORIZATION_CODE
		          )
		          .scopes(
				          SCOPE_READ,
				          SCOPE_WRITE,
				          SCOPE_TRUST
		          )
		          .accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints)
			throws
			Exception {
		endpoints.tokenStore(tokenStore)
		         .authenticationManager(authenticationManager);
		// here instead of our clients requesting authentication at the endpoint /oauth/token, they request it at the endpoint /login
		endpoints.pathMapping(
				"/oauth/token",
				"/login"
		);
	}

}