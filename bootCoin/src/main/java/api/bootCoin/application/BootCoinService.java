package api.bootCoin.application;

import api.bootCoin.config.CircuitResilienceListener;
import api.bootCoin.domain.BootCoin;
import api.bootCoin.domain.BootCoinRepository;
import api.bootCoin.presentation.mapper.BootCoinMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
public class BootCoinService 
{
    @Autowired
    private BootCoinRepository bootCoinRepository;
    @Autowired
    private CircuitResilienceListener circuitResilienceListener;
    @Autowired
    private TimeLimiterRegistry timeLimiterRegistry;
    @Autowired
    private BootCoinMapper bootCoinMapper;

    @Autowired
    private ReactiveHashOperations<String, String, BootCoin> hashOperations;

    @CircuitBreaker(name = "bootCoinCircuit", fallbackMethod = "fallbackGetAllBootCoin")
    @TimeLimiter(name = "bootCoinTimeLimiter")
    public Flux<BootCoin> findAll(){
        log.debug("findAll executed");

        // Intenta obtener todos los monederos bootCoin desde el caché de Redis
        Flux<BootCoin> cachedBootCoin = hashOperations.values("BootCoinRedis")
                .flatMap(bootCoin -> Mono.justOrEmpty((BootCoin) bootCoin));

        // Si hay datos en la caché de Redis, retornarlos
        return cachedBootCoin.switchIfEmpty(bootCoinRepository.findAll()
                .flatMap(bootCoin -> {
                    // Almacena cada monedero bootCoin en la caché de Redis
                    return hashOperations.put("BootCoinRedis", bootCoin.getId(), bootCoin)
                            .thenReturn(bootCoin);
                }));

    }

    @CircuitBreaker(name = "bootCoinCircuit", fallbackMethod = "fallbackFindById")
    @TimeLimiter(name = "bootCoinTimeLimiter")
    public Mono<BootCoin> findById(String bootCoinId)
    {
        log.debug("findById executed {}" , bootCoinId);
        return  hashOperations.get("BootCoinRedis",bootCoinId)
                .switchIfEmpty(bootCoinRepository.findById(bootCoinId)
                        .flatMap(bootCoin -> hashOperations.put("BootCoinRedis",bootCoin.getId(),bootCoin)
                                .thenReturn(bootCoin)));
    }

    @CircuitBreaker(name = "bootCoinCircuit", fallbackMethod = "fallbackGetAllItems")
    @TimeLimiter(name = "bootCoinTimeLimiter")
    public Mono<BootCoin> findByIdentityDni(String identityDni){
        log.debug("findByIdentityDni executed {}" , identityDni);
        return bootCoinRepository.findByIdentityDni(identityDni);
    }

    @CircuitBreaker(name = "bootCoinCircuit", fallbackMethod = "fallbackFindByIdentityDni")
    @TimeLimiter(name = "bootCoinTimeLimiter")
    public Mono<BootCoin> create(BootCoin bootCoin){
        log.debug("create executed {}",bootCoin);
        bootCoin.setDateRegister(LocalDate.now());
        return bootCoinRepository.save(bootCoin);
    }

    @CircuitBreaker(name = "bootCoinCircuit", fallbackMethod = "fallbackUpdateBootCoin")
    @TimeLimiter(name = "bootCoinTimeLimiter")
    public Mono<BootCoin> update(String bootCoinId, BootCoin bootCoin){
        log.debug("update executed {}:{}", bootCoinId, bootCoin);
        return bootCoinRepository.findById(bootCoinId)
                .flatMap(dbBootCoin -> {
                    bootCoin.setDateRegister(dbBootCoin.getDateRegister());
                    bootCoinMapper.update(dbBootCoin, bootCoin);
                    return bootCoinRepository.save(dbBootCoin);
                });
    }

    @CircuitBreaker(name = "bootCoinCircuit", fallbackMethod = "fallbackDeleteBootCoin")
    @TimeLimiter(name = "bootCoinTimeLimiter")
    public Mono<BootCoin>delete(String bootCoinId){
        log.debug("delete executed {}",bootCoinId);
        return bootCoinRepository.findById(bootCoinId)
                .flatMap(existingBootCoin -> bootCoinRepository.delete(existingBootCoin)
                        .then(Mono.just(existingBootCoin)));
    }
}
