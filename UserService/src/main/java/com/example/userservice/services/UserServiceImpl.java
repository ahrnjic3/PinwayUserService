package com.example.userservice.services;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.dto.UserVisibilityTypeDTO;
import com.example.userservice.exception.PinwayError;
import com.example.userservice.infrastructure.EventService;
import com.example.userservice.models.Role;
import com.example.userservice.models.User;
import com.example.userservice.models.UserVisibilityType;
import com.example.userservice.repositories.RoleRepository;
import com.example.userservice.repositories.UserRepository;
import com.example.userservice.repositories.UserVisibilityTypeRepository;
import com.example.userservice.security.PBKDF2Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService{
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PBKDF2Encoder passwordEncoder;

    @Autowired
    private EventService eventService;

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserVisibilityTypeRepository userVisibilityTypeRepository;
    @Override
    public User Create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role r = roleRepository.findByName("ROLE_USER");
        List<Role> roles = new ArrayList<>();
        roles.add(r);
        user.setRoles(roles);
        user.setCreatedAt(LocalDate.from(LocalDateTime.now()));
        roleRepository.save(r);
        user = userRepository.save(user);
        return user;
    }

    @Override
    public Iterable<User> List() {
        Iterable<User> users = userRepository.findAll();
        return users;
    }

    public UserDTO Details(Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent())
            throw new PinwayError("Not found User with id = " + id);

        User userEntity = user.get();
        UserVisibilityTypeDTO userVisibilityTypeDTO = new UserVisibilityTypeDTO(
                userEntity.getUserVisibilityType().getId(),
                userEntity.getUserVisibilityType().getType()
        );

        UserDTO userDTO = convertToDTO(userEntity, userVisibilityTypeDTO);
        return userDTO;
    }


    private UserDTO convertToDTO(User user, UserVisibilityTypeDTO userVisibilityTypeDTO) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setGuid(user.getGuid());
        userDTO.setName(user.getName());
        userDTO.setSurname(user.getSurname());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUserVisibilityType(userVisibilityTypeDTO);

        List<UserDTO> followingDTOList = convertToDTOList(user.getFollowing());
        userDTO.setFollowing(followingDTOList);

        return userDTO;
    }

    private List<UserDTO> convertToDTOList(List<User> userList) {
        List<UserDTO> userDTOList = new ArrayList<>();
        for (User user : userList) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setGuid(user.getGuid());
            userDTO.setName(user.getName());
            userDTO.setSurname(user.getSurname());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setPassword(user.getPassword());
            userDTO.setCreatedAt(user.getCreatedAt());

            UserVisibilityTypeDTO userVisibilityTypeDTO = new UserVisibilityTypeDTO(
                    user.getUserVisibilityType().getId(),
                    user.getUserVisibilityType().getType()
            );

            userDTO.setUserVisibilityType(userVisibilityTypeDTO);

            userDTOList.add(userDTO);
        }
        return userDTOList;
    }

    @Override
    public Boolean Delete(Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        throw new PinwayError("Not found User with id = " + id);
    }

    @Override
    public User Update(Integer id, User user) {
        Optional<User> u = userRepository.findById(id);

        if (!u.isPresent())
            throw new PinwayError("Not found User with id = " + id);

        User newUser = u.get();

        newUser.setName(user.getName());
        newUser.setSurname(user.getSurname());
        newUser.setUsername(user.getUsername());
        // newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(newUser);
        return newUser;
    }

    @Override
    public Iterable<UserVisibilityType> ListUserVisibilityTypes() {
        Iterable<UserVisibilityType> userVisibilityTypes = userVisibilityTypeRepository.findAll();
        return userVisibilityTypes;
    }

    @Override
    public UserDTO AddFollower(Integer userId, Integer followingId) {
        Optional<User> optUser = userRepository.findById(userId);
        if (!optUser.isPresent())
            throw new PinwayError("Not found User with id = " + userId);
        // prvjera da li ima user sa IDem ovim datim

        Optional<User> optFollower = userRepository.findById(followingId);
        if (!optFollower.isPresent())
            throw new PinwayError("Not found User with id = " + followingId);

        User user = optUser.get();
        User follower = optFollower.get();

        user.getFollowing().add(follower);

        userRepository.save(user);
        UserVisibilityTypeDTO userVisibilityTypeDTO = new UserVisibilityTypeDTO(
                user.getUserVisibilityType().getId(),
                user.getUserVisibilityType().getType()
        );
        UserDTO userDTO = convertToDTO(user, userVisibilityTypeDTO);

        if(user.getId() != followingId)
            eventService.FollowCreated(followingId, user.getId(), user.getUsername());

        return  userDTO;
    }

    @Override
    public List<UserDTO> GetAllFollowersForUser(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent())
            throw new PinwayError("Not found User with id = " + userId);

        User user = optionalUser.get();

        List<Integer> ids = new ArrayList<>();
        for (User follower : user.getFollowers()) {
            Optional<User> optFollower = userRepository.findById(follower.getId());
            if (!optFollower.isPresent())
                throw new PinwayError("Not found User with id = " + follower.getId());
            ids.add(follower.getId());
        }

        Iterable<User> users = userRepository.findAllById(ids);
        ArrayList<UserDTO> userDTOS = new ArrayList<UserDTO>();

        for (User u: users) {
            UserDTO userDTO = new UserDTO(u.getId(), u.getGuid(), u.getName(), u.getSurname(), u.getUsername(), u.getEmail(), u.getPassword(), u.getCreatedAt());
            userDTOS.add(userDTO);
        }

        return userDTOS;
    }

    @Override
    public User addRoleToUser(String username, String name) {
        Role role = roleRepository.findByName(name);
        if (role == null)
            throw new PinwayError( "Role with name does not exist!");
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new PinwayError( "User with username does not exist!");
        user.getRoles().add(role);
        roleRepository.save(role);
        return null;
    }

    @Override
    public Mono<User> getByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user==null){
            throw new RuntimeException("User not found!");
        }
        return Mono.justOrEmpty(user);
    }

    @Override
    public User registerUser(User user) {
        if (!userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail()).isEmpty())
            throw new PinwayError("Username or email already exists!");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role r = roleRepository.findByName("ROLE_USER");
        List<Role> roles = new ArrayList<>();
        roles.add(r);
        user.setRoles(roles);
        roleRepository.save(r);
        user = userRepository.save(user);
        return user;
    }

    @Override
    public User updateImage(Integer userId, String fileName) {
        Optional<User> user =userRepository.findById(userId);
        if (user==null){
            throw new RuntimeException("User not found!");
        }

        User newUser = user.get();
        newUser.setImage_path(fileName);
        Optional.of(userRepository.save(newUser));
        return newUser;
    }


}


