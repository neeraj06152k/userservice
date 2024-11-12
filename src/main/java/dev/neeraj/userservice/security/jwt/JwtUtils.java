package dev.neeraj.userservice.security.jwt;

import dev.neeraj.userservice.models.Role;
import dev.neeraj.userservice.models.User;
import dev.neeraj.userservice.security.models.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
public class JwtUtils {

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtExpireInHrs}")
    private String jwtExpireInHrs;

    @Value("${spring.app.jwtCookieName}")
    private String jwtCookieName;


    public String getJwtFromCookies(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return "";

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(jwtCookieName))
                .findFirst()
                .map(Cookie::getValue)
                .orElse("");
    }

    public ResponseCookie generateCleanJwtCookie(){
        return ResponseCookie.from(jwtCookieName, "")
                .path("/")
                .maxAge(0)
                .httpOnly(false)
                .build();
    }

    public ResponseCookie generateJwtCookie(UserDetails userDetails){
        long jwtExpireInSec = Long.parseLong(jwtExpireInHrs) * 60L * 60L;

        return ResponseCookie.from(jwtCookieName, generateJwtFromUserDetails(userDetails))
                .path("/")
                .maxAge(jwtExpireInSec)
                .httpOnly(false)
                .build();
    }


    private String generateJwtFromUserDetails(UserDetails userDetails){
        Date issuedAt = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(issuedAt);
        cal.add(Calendar.HOUR, Integer.parseInt(jwtExpireInHrs));
        Date expireDate = cal.getTime();

        User user = User.toUser(userDetails);
        Map<String,Object> claimsMap = toClaimsMap(user);

        return Jwts.builder()
                .setIssuedAt(issuedAt)
                .setExpiration(expireDate)
                .signWith(key())
                .setSubject(user.getEmail())
                .addClaims(claimsMap)
                .compact();
    }

    private Map<String,Object> toClaimsMap(User user){
        Map<String,Object> claimsMap = new HashMap<>();

        claimsMap.put("firstname", user.getFirstname());
        claimsMap.put("lastname", user.getLastname());
        claimsMap.put("email", user.getEmail());
        claimsMap.put("id", user.getId());
        claimsMap.put("roles", toCSV(user.getRoles()));

        return claimsMap;
    }

    private String toCSV(List<Role> roles){
        return String.join(",",
                roles.stream().map(Role::getName).toList());
    }


    public boolean isValidJwt(String jwt){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(jwt);

            return true;
        } catch (ExpiredJwtException ignored){
            System.out.println("JWT is expired");
        } catch (MalformedJwtException ignored){
            System.out.println("JWT is malformed");
        } catch (SignatureException ignored){
            System.out.println("JWT key is invalid");
        } catch (IllegalArgumentException ignored){
            System.out.println("JWT is invalid");
        }

        return false;
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }


    public String getSubjectFromJwt(String jwt){

        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }

}
