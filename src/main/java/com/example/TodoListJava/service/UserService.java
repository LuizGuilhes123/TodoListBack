package com.example.TodoListJava.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import com.example.TodoListJava.dto.position.PositionDTO;
import com.example.TodoListJava.dto.user.UserDTO;
import com.example.TodoListJava.dto.user.UserInsertDTO;
import com.example.TodoListJava.entity.PositionEntity;
import com.example.TodoListJava.entity.UserEntity;
import com.example.TodoListJava.repository.PositionRepository;
import com.example.TodoListJava.repository.UserRepository;
import com.example.TodoListJava.service.exception.DataIntegratyViolationException;

import com.example.TodoListJava.service.exception.ObjectNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PositionRepository cargoRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ImgService imgService;

    @Transactional(readOnly = true)
    public UserDTO findById(UUID id) {
        Optional<UserEntity> obj = repository.findById(id);
        UserEntity entity = obj.orElseThrow(()-> new ObjectNotFoundException("Usuário não encontrado"));
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO create(UserInsertDTO objDto) {
        emailAlreadyExists(objDto);
        UserEntity usuario = new UserEntity();
        usuario.setName(objDto.getName());
        usuario.setEmail(objDto.getEmail());
        usuario.setPassword(passwordEncoder.encode(objDto.getSenha()));
        assignRole(usuario, objDto);
        usuario.setNotification(false);
        usuario.setImgUrl(null);
        repository.save(usuario);
        return new UserDTO(usuario);
    }

    @Transactional
    public void updateUser(UserDTO usuarioDTO, UUID id) {
        UserEntity entity = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado"));
        entity.setName(usuarioDTO.getName());
        entity.setEmail(usuarioDTO.getEmail());
        repository.save(entity);

    }

    @Transactional
    public UserDTO activateNotification(UUID id) {
        UserEntity entity = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado"));
        entity.setNotification(!entity.getNotification());
        repository.save(entity);
        return new UserDTO(entity);
    }


    protected void assignRole(UserEntity usuario, UserDTO objDto) {
        for(PositionDTO cargos : objDto.getCargos()) {
            PositionEntity cargo = cargoRepository.findById(cargos.getId())
                    .orElseThrow(() -> new ObjectNotFoundException("Cargos não encontrado"));
            usuario.getCargos().add(cargo);
        }
    }

    protected void emailAlreadyExists(UserDTO usuarioDTO) {
        Optional<UserEntity> entity = repository.findByEmail(usuarioDTO.getEmail());
        if (entity.isPresent() && !entity.get().getId().equals(usuarioDTO.getId())) {
            throw new DataIntegratyViolationException("Email já existe");
        }
    }

    public void uploadfile(UUID idUser, MultipartFile imagem) throws IOException {
        UserEntity usuario = repository.findById(idUser)
                .orElseThrow(() -> new ObjectNotFoundException("Usuario não encontrado."));
        BufferedImage img = imgService.getJpgImageFromFile(imagem);
        BufferedImage resizedImg = imgService.resize(img, 400);
        BufferedImage croppedImg = imgService.cropSquare(resizedImg);

        String base64Image = convertToBase64(croppedImg, "jpg");

        usuario.setImgUrl(base64Image);
        repository.save(usuario);
    }

    private String convertToBase64(BufferedImage img, String extension) throws IOException {
        InputStream is = imgService.getInputStream(img, extension);
        byte[] bytes = is.readAllBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

}