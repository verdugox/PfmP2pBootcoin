package api.bootCoin.presentation.mapper;

import api.bootCoin.domain.BootCoin;
import api.bootCoin.presentation.model.BootCoinModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BootCoinMapper
{
    BootCoin modelToEntity (BootCoinModel model);
    BootCoinModel entityToModel(BootCoin event);
    @Mapping(target = "id", ignore=true)
    void update(@MappingTarget BootCoin entity, BootCoin updateEntity);
}
