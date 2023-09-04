package api.bootCoin.presentation;

import api.bootCoin.application.BootCoinService;
import api.bootCoin.domain.BootCoin;
import api.bootCoin.presentation.mapper.BootCoinMapper;
import api.bootCoin.presentation.model.BootCoinModel;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/bootCoin")
public class BootCoinController
{
    @Autowired(required = true)
    private BootCoinService bootCoinService;
    @Autowired
    private BootCoinMapper bootCoinMapper;

    @Operation(summary = "Listar todos los monederos BootCoin registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se listaron todos los monederos BootCoin registrados",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BootCoin.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @GetMapping("/findAll")
    @CircuitBreaker(name = "bootCoinCircuit", fallbackMethod = "fallbackGetAllBootCoin")
    @TimeLimiter(name = "bootCoinTimeLimiter")
    @Timed(description = "bootCoinGetAll")
    public Flux<BootCoinModel> getAll() {
        log.info("getAll executed");
        return bootCoinService.findAll()
                .map(bootCoin -> bootCoinMapper.entityToModel(bootCoin));
    }


    @Operation(summary = "Listar todos los monederos BootCoin por Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se listaron todos los monederos bootCoin por Id",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BootCoin.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @GetMapping("/findById/{id}")
    @CircuitBreaker(name = "bootCoinCircuit", fallbackMethod = "fallbackFindById")
    @TimeLimiter(name = "bootCoinTimeLimiter")
    @Timed(description = "bootCoinsGetById")
    public Mono<ResponseEntity<BootCoinModel>> findById(@PathVariable String id){
        return bootCoinService.findById(id)
                .map(bootCoin -> bootCoinMapper.entityToModel(bootCoin))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar todos los registros por DNI")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se listaron todos los registros por DNI",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BootCoin.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @GetMapping("/findByIdentityDni/{identityDni}")
    @CircuitBreaker(name = "bootCoinCircuit", fallbackMethod = "fallbackFindByIdentityDni")
    @TimeLimiter(name = "bootCoinTimeLimiter")
    public Mono<ResponseEntity<BootCoinModel>> findByIdentityDni(@PathVariable String identityDni){
        log.info("findByIdentityDni executed {}", identityDni);
        Mono<BootCoin> response = bootCoinService.findByIdentityDni(identityDni);
        return response
                .map(bootCoin -> bootCoinMapper.entityToModel(bootCoin))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Registro de los Monederos BootCoin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se registro el monedero de manera exitosa",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BootCoin.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @PostMapping
    @CircuitBreaker(name = "bootCoinCircuit", fallbackMethod = "fallbackCreateBootCoin")
    @TimeLimiter(name = "bootCoinTimeLimiter")
    public Mono<ResponseEntity<BootCoinModel>> create(@Valid @RequestBody BootCoinModel request){
        log.info("create executed {}", request);
        return bootCoinService.create(bootCoinMapper.modelToEntity(request))
                .map(bootCoin -> bootCoinMapper.entityToModel(bootCoin))
                .flatMap(c -> Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", "register", "9081", "bootCoin", c.getId())))
                        .body(c)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar el monedero BootCoin por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se actualizará el registro por el ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BootCoin.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @PutMapping("/{id}")
    @CircuitBreaker(name = "bootCoinCircuit", fallbackMethod = "fallbackUpdateBootCoin")
    @TimeLimiter(name = "bootCoinTimeLimiter")
    public Mono<ResponseEntity<BootCoinModel>> updateById(@PathVariable String id, @Valid @RequestBody BootCoinModel request){
        log.info("updateById executed {}:{}", id, request);
        return bootCoinService.update(id, bootCoinMapper.modelToEntity(request))
                .map(bootCoin -> bootCoinMapper.entityToModel(bootCoin))
                .flatMap(c -> Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", "register", "9081", "bootCoin", c.getId())))
                        .body(c)))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Eliminar Monedero BootCoin por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se elimino el registro por ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BootCoin.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @DeleteMapping("/{id}")
    @CircuitBreaker(name = "bootCoinCircuit", fallbackMethod = "fallbackDeleteBootCoin")
    @TimeLimiter(name = "bootCoinTimeLimiter")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable String id){
        log.info("deleteById executed {}", id);
        return bootCoinService.delete(id)
                .map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
