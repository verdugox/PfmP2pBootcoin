package api.bootCoin.domain;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
@Repository
public interface BootCoinRepository extends ReactiveMongoRepository<BootCoin,String>
{
    Mono<BootCoin> findByIdentityDni(String identityDni);
}
