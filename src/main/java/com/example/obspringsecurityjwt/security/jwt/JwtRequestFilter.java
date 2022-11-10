package com.example.obspringsecurityjwt.security.jwt;

import com.example.obspringsecurityjwt.security.service.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Filters incoming requests and installs a Spring Security principal if a header corresponding to a valid user is
 * found.
 *  Se ejecuta por cada petición entrante con el fin de validar el token JWT
 *  en caso de que lo sea se añade al contexto para indicar que un usuario está autenticado
 */
public class JwtRequestFilter extends OncePerRequestFilter {


    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    public static final String BEARER = "Bearer ";

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;


    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments insteas of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */

    /**
     * A partir de una cabecera Authorization extrae el token
     * @param request
     * @return
     */
    private String parseJwt(HttpServletRequest request) {

        String headerAuth = request.getHeader("Authorization");

        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith(BEARER))
            return headerAuth.substring(BEARER.length());

        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

      try {
          String jwt = parseJwt(request); // Extraer el token
          if (jwt != null && jwtTokenUtil.validateJwtToken(jwt)){
              String username = jwtTokenUtil.getUserNameFromJwtToken(jwt);

              // Cargar el token si existe en la BD
              UserDetails userDetails = userDetailsService.loadUserByUsername(username);
              UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                      userDetails, null, userDetails.getAuthorities());
              authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

              SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          }
      } catch (Exception e) {
          logger.error("Cannot set user authentication: {}", e);
      }

      filterChain.doFilter(request,response);
    }
}