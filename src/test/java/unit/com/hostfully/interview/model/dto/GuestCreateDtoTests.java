package unit.com.hostfully.interview.model.dto;

import com.hostfully.interview.exception.BadRequestException;
import com.hostfully.interview.model.dto.GuestCreateDTO;
import com.hostfully.interview.model.dto.PropertyCreateDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GuestCreateDtoTests {

	@Test
	void validate_ValidDto_CallsAllValidateMethods() {
		var spy = Mockito.spy(new GuestCreateDTO("Guest Name"));
		Assertions.assertTrue(spy.validate());
		Mockito.verify(spy, Mockito.times(1)).validateName();
	}

	@Test
	void validateName_ValidaName_ReturnsTrue() {
		var dto = new GuestCreateDTO("Guest Name");
		Assertions.assertTrue(dto.validateName());
	}
	@Test
	void validateName_NullName_ThrowsException() {
		var dto = new GuestCreateDTO();
		var exception = Assertions.assertThrows(BadRequestException.class, dto::validateName);
		Assertions.assertEquals("Guest name is required", exception.getMessage());
	}

	@Test
	void validateName_BlankName_ThrowsException() {
		var dto = new GuestCreateDTO("");
		var exception = Assertions.assertThrows(BadRequestException.class, dto::validateName);
		Assertions.assertEquals("Guest name is required", exception.getMessage());
	}

	@Test
	void validateName_EmptyName_ThrowsException() {
		var dto = new GuestCreateDTO("");
		var exception = Assertions.assertThrows(BadRequestException.class, dto::validateName);
		Assertions.assertEquals("Guest name is required", exception.getMessage());
	}

}
