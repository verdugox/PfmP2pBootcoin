package api.bootCoin.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Yanki
{
    private String id;
    private String identityDni;
    private String phoneNumber;
    private BigDecimal balance;
    private String linkedCardId;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dateRegister;

    @JsonIgnore
    private boolean scanAvailable;
    @JsonIgnore
    private int prefetch;
}
