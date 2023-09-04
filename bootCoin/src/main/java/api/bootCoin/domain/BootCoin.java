package api.bootCoin.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ToString
@Builder
@EqualsAndHashCode(of={"identityDni"})
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "bootCoin")
public class BootCoin
{
    @Id
    private String id;
    @NotNull
    @Indexed(unique = true)
    @Column(nullable = false, length = 8)
    private String identityDni;
//    @NotNull
//    @ManyToOne
//    private Yanki senderWalletYankiId;
//
//    @NotNull
//    @ManyToOne
//    private Yanki receiverWalletYankiId;

    @NotNull
    private BigDecimal amount;

    @NotEmpty
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String transactionType;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dateRegister;

    @JsonIgnore
    private boolean scanAvailable;
    @JsonIgnore
    private int prefetch;
}
