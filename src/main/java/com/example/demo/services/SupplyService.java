package com.example.demo.services;


import com.example.demo.dtos.SupplyDto;
import com.example.demo.entities.Crop;
import com.example.demo.entities.Supply;
import com.example.demo.interfaces.ISupplyService;
import com.example.demo.repositories.CropRepository;
import com.example.demo.repositories.SupplyRepository;
import com.example.demo.security.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplyService implements ISupplyService {

    @Autowired
    private SupplyRepository supplyRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    @Override
    public SupplyDto crearInsumo(SupplyDto dto) {
            Supply supply = modelMapper.map(dto, Supply.class);
            Long cropId = dto.getCrop().getId();
            Crop crop = cropRepository.findById(cropId)
                    .orElseThrow(() -> new RuntimeException("Cultivo no encontrado"));
            supply.setCrop(crop);
            supply = supplyRepository.save(supply);
            return modelMapper.map(supply, SupplyDto.class);
    }

    @Override
    public List<SupplyDto> listarInsumos() {
        return supplyRepository.findAll()
                .stream()
                .map(s -> modelMapper.map(s, SupplyDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public SupplyDto obtenerInsumoPorId(Long id) {
        Supply supply = supplyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));
        return modelMapper.map(supply, SupplyDto.class);
    }
    @Transactional
    @Override
    public SupplyDto actualizarInsumo(Long id, SupplyDto dto) {
        Supply updatedSupply = supplyRepository.findById(id)
                .map(existingSupply -> {
                    existingSupply.setName(dto.getName());
                    existingSupply.setQuantity(dto.getQuantity());
                    existingSupply.setUnit(dto.getUnit());
                    existingSupply.setDescription(dto.getDescription());
                    existingSupply.setCrop(cropRepository.findById(id).get());
                    return supplyRepository.save(existingSupply);
                })
                .orElseGet(() -> {
                    Supply newSupply = modelMapper.map(dto, Supply.class);
                    newSupply.setId(id);
                    return supplyRepository.save(newSupply);
                });

        return modelMapper.map(updatedSupply, SupplyDto.class);
    }

    @Override
    public void eliminarInsumo(Long id) {
        if (!supplyRepository.existsById(id)) {
            throw new RuntimeException("Insumo no existe");
        }
        supplyRepository.deleteById(id);
    }
}
