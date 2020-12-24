package project.system.service;

public interface TokenService {
    String createToken(Integer userId);
    Integer getUserId(String token);
    boolean isExpiration(String token);
}
