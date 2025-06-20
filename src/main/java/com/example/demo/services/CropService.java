package com.example.demo.services;

import com.example.demo.dtos.CropDto;
import com.example.demo.entities.Crop;
import com.example.demo.interfaces.ICropService;
import com.example.demo.repositories.CropRepository;

import com.example.demo.security.entities.User;
import com.example.demo.security.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CropService implements ICropService {

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public CropDto crearCultivo(CropDto dto) {
        Long userId = dto.getUser().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Crop crop = modelMapper.map(dto, Crop.class);
        crop.setUser(user);
        crop = cropRepository.save(crop);

        return modelMapper.map(crop, CropDto.class);
    }

    @Override
    public List<CropDto> listarCultivos() {
        return cropRepository.findAll()
                .stream()
                .map(c -> modelMapper.map(c, CropDto.class))
                .collect(Collectors.toList());
    }

    public CropDto obtenerCultivoPorId(Long id) {
        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cultivo no encontrado"));
        return modelMapper.map(crop, CropDto.class);
    }

    @Transactional
    @Override
    public CropDto actualizarCultivo(Long id, CropDto dto) {
        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cultivo no encontrado"));

        modelMapper.map(dto, crop);

        Crop updated = cropRepository.save(crop);
        return modelMapper.map(updated, CropDto.class);
    }

    @Override
    public void eliminarCultivo(Long id) {
        if (!cropRepository.existsById(id)) {
            throw new RuntimeException("Cultivo no existe");
        }
        cropRepository.deleteById(id);
    }

    @Override
    public CropDto asignarRotacion(Long cropId, Long nextCropId) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new RuntimeException("Cultivo no encontrado"));

        crop.setNextCropId(nextCropId);

        Crop actualizado = cropRepository.save(crop);

        return modelMapper.map(actualizado, CropDto.class);
    }

    @Override
    public Long obtenerProximaRotacion(Long cropId) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new RuntimeException("Cultivo no encontrado"));

        return crop.getNextCropId();
    }
}
