package api.bootCoin.events;

import api.bootCoin.domain.BootCoin;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BootCoinCreatedEvent extends Event<BootCoin> {

}
