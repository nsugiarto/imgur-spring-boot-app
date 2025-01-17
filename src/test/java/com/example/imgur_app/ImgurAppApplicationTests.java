package com.example.imgur_app;

import com.example.imgur_app.controller.ImageController;
import com.example.imgur_app.controller.UserController;
import com.example.imgur_app.dto.UserRegistrationDTO;
import com.example.imgur_app.entity.Image;
import com.example.imgur_app.entity.User;
import com.example.imgur_app.repository.ImageRepository;
import com.example.imgur_app.repository.UserRepository;
import com.example.imgur_app.service.ImgurClient;
import com.example.imgur_app.service.ImgurClient.ImageMetadata;
import com.example.imgur_app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ImgurAppApplicationTests {

	@Mock
	private ImgurClient imgurClient;

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	@Mock
	private ImageRepository imageRepository;

	@InjectMocks
	private ImageController imageController;

	@Mock
	private Authentication authentication;

	@Mock
	private MultipartFile mockFile;

	private User user;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		user = new User();
		user.setId(1L);
		user.setUsername("testuser");
		user.setPassword("password");
	}

	@Test
	void testRegisterUser_Success() {
		// Arrange
		UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
		userRegistrationDTO.setUsername("testuser");
		userRegistrationDTO.setPassword("password");
		userRegistrationDTO.setEmail("testuser@example.com");

		when(userService.registerUser(any(UserRegistrationDTO.class))).thenReturn(user);

		// Act
		ResponseEntity<?> response = userController.registerUser(userRegistrationDTO);

		// Assert
		verify(userService).registerUser(any(UserRegistrationDTO.class));
		assertEquals(200, response.getStatusCode().value());
		assertTrue(response.getBody().toString().contains("User registered successfully"));
	}

	@Test
	void testRegisterUser_UsernameAlreadyExists() {
		// Arrange
		UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
		userRegistrationDTO.setUsername("testuser");
		userRegistrationDTO.setPassword("password");
		userRegistrationDTO.setEmail("testuser@example.com");

		when(userService.registerUser(any(UserRegistrationDTO.class)))
				.thenThrow(new IllegalArgumentException("Username is already taken"));

		// Act
		ResponseEntity<?> response = userController.registerUser(userRegistrationDTO);

		// Assert
		verify(userService).registerUser(any(UserRegistrationDTO.class));
		assertEquals(400, response.getStatusCode().value());
		assertTrue(response.getBody().toString().contains("Username is already taken"));
	}

	@Test
	void testUploadImage_Success() throws Exception {
		// Mock authentication
		when(authentication.getName()).thenReturn("testuser");
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		// Mock ImgurClient upload
		ImageMetadata metadata = new ImageMetadata("1","deleteHash123", "https://imgur.com/image123");
		when(imgurClient.uploadImage(any())).thenReturn(metadata);

		// Mock MultipartFile
		when(mockFile.getBytes()).thenReturn(new byte[0]);

		// Call uploadImage
		ResponseEntity<?> response = imageController.uploadImage(mockFile, authentication);

		// Verify interactions
		verify(imgurClient).uploadImage(any());
		verify(imageRepository).save(any(Image.class));

		// Assert response
		assertEquals(200, response.getStatusCode().value());
		assertTrue(response.getBody().toString().contains("Image uploaded and associated with user successfully!"));
	}

	@Test
	void testUploadImage_UserNotFound() {
		// Mock authentication
		when(authentication.getName()).thenReturn("testuser");
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

		// Call uploadImage
		ResponseEntity<?> response = imageController.uploadImage(mockFile, authentication);

		// Verify no interactions with ImgurClient
		verifyNoInteractions(imgurClient);

		// Assert response
		assertEquals(500, response.getStatusCode().value());
		assertTrue(response.getBody().toString().contains("User not found"));
	}

	@Test
	void testGetImage_Success() {
		// Mock authentication
		when(authentication.getName()).thenReturn("testuser");
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		// Mock image repository
		Image image = new Image();
		image.setId(1L);
		image.setDeleteHash("deleteHash123");
		image.setLink("https://imgur.com/image123");
		image.setUser(user);
		when(imageRepository.findById(1L)).thenReturn(Optional.of(image));

		// Call viewImage
		ResponseEntity<?> response = imageController.viewImage(1L, authentication);

		// Assert response
		assertEquals(200, response.getStatusCode().value());
		assertTrue(response.getBody().toString().contains("https://imgur.com/image123"));
	}

	@Test
	void testGetImage_ImageNotFound() {
		// Mock authentication
		when(authentication.getName()).thenReturn("testuser");
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		// Mock image repository
		when(imageRepository.findById(1L)).thenReturn(Optional.empty());

		// Call getImage
		ResponseEntity<?> response = imageController.viewImage(1L, authentication);

		// Assert response
		assertEquals(500, response.getStatusCode().value());
		assertTrue(response.getBody().toString().contains("Image not found"));
	}

	@Test
	void testDeleteImage_Success() throws Exception {
		// Mock authentication
		when(authentication.getName()).thenReturn("testuser");
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		// Mock image repository
		Image image = new Image();
		image.setId(1L);
		image.setDeleteHash("deleteHash123");
		image.setUser(user);
		when(imageRepository.findByDeleteHash("deleteHash123")).thenReturn(Optional.of(image));

		// Call deleteImage
		ResponseEntity<?> response = imageController.deleteImage("deleteHash123", authentication);

		// Verify interactions
		verify(imgurClient).deleteImage("deleteHash123");
		verify(imageRepository).delete(image);

		// Assert response
		assertEquals(200, response.getStatusCode().value());
		assertTrue(response.getBody().toString().contains("Image deleted successfully!"));
	}

	@Test
	void testDeleteImage_ImageNotFound() {
		// Mock authentication
		when(authentication.getName()).thenReturn("testuser");
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		// Mock image repository
		when(imageRepository.findByDeleteHash("deleteHash123")).thenReturn(Optional.empty());

		// Call deleteImage
		ResponseEntity<?> response = imageController.deleteImage("deleteHash123", authentication);

		// Verify no interactions with ImgurClient
		verifyNoInteractions(imgurClient);

		// Assert response
		assertEquals(500, response.getStatusCode().value());
		assertTrue(response.getBody().toString().contains("Image not found"));
	}

	@Test
	void testDeleteImage_UnauthorizedAccess() {
		// Mock authentication
		when(authentication.getName()).thenReturn("testuser");
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		// Mock image repository
		User otherUser = new User();
		otherUser.setId(2L);

		Image image = new Image();
		image.setId(1L);
		image.setDeleteHash("deleteHash123");
		image.setUser(otherUser);
		when(imageRepository.findByDeleteHash("deleteHash123")).thenReturn(Optional.of(image));

		// Call deleteImage
		ResponseEntity<?> response = imageController.deleteImage("deleteHash123", authentication);

		// Verify no interactions with ImgurClient
		verifyNoInteractions(imgurClient);

		// Assert response
		assertEquals(403, response.getStatusCode().value());
		assertTrue(response.getBody().toString().contains("Access denied"));
	}
}
