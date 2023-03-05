package az.nadir.springsecurity.repository;

import az.nadir.springsecurity.model.token.Token;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query(value = """
            select t from Token t inner join User u on t.user.id = u.id
            where u.id = :id and (t.expired = false or t.revoked = false)
            """)
    List<Token> findAllValidTokensByUser(Integer id);

    Optional<Token> findByToken(String token);
}
