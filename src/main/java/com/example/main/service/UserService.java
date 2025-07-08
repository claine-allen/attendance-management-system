package com.example.main.service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.main.dto.UserDTO;
import com.example.main.dto.UserRegisterRequest;
import com.example.main.exception.DuplicateEntryException;
import com.example.main.exception.ResourceNotFoundException;
import com.example.main.mapper.UserMapper;
import com.example.main.model.User;
import com.example.main.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing User entities.
 * Handles business logic related to user creation, retrieval, update, and deactivation.
 */
@Service
@Transactional // Ensures atomicity for database operations within service methods
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    /**
     * Registers a new user.
     * @param request The DTO containing user registration details.
     * @return The created UserDTO.
     * @throws DuplicateEntryException if a user with the given email already exists.
     */
    public UserDTO registerUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEntryException("User with email " + request.getEmail() + " already exists.");
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash the password
        user.setActive(true); // Default to active

        User savedUser = userRepository.save(user);
        return userMapper.toUserDTO(savedUser);
    }

    /**
     * Retrieves a user by their ID.
     * @param id The ID of the user.
     * @return The UserDTO if found.
     * @throws ResourceNotFoundException if the user is not found.
     */
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return userMapper.toUserDTO(user);
    }

    /**
     * Retrieves a user by their email.
     * @param email The email of the user.
     * @return The User entity if found.
     * @throws ResourceNotFoundException if the user is not found.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Retrieves all users.
     * @return A list of all UserDTOs.
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing user's details.
     * @param id The ID of the user to update.
     * @param userDTO The DTO containing updated user details.
     * @return The updated UserDTO.
     * @throws ResourceNotFoundException if the user is not found.
     * @throws DuplicateEntryException if an attempt is made to change email to an existing one.
     */
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Check if email is being changed to an already existing email (for another user)
        if (!existingUser.getEmail().equals(userDTO.getEmail()) && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateEntryException("User with email " + userDTO.getEmail() + " already exists.");
        }

        existingUser.setEmail(userDTO.getEmail());
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setRole(userDTO.getRole());
        existingUser.setActive(userDTO.isActive());

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toUserDTO(updatedUser);
    }

    /**
     * Deactivates a user account.
     * @param id The ID of the user to deactivate.
     * @throws ResourceNotFoundException if the user is not found.
     */
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Reactivates a user account.
     * @param id The ID of the user to reactivate.
     * @throws ResourceNotFoundException if the user is not found.
     */
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setActive(true);
        userRepository.save(user);
    }

    /**
     * Changes a user's password.
     * @param id The ID of the user.
     * @param newPassword The new password (will be hashed).
     * @throws ResourceNotFoundException if the user is not found.
     */
    public void changePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
