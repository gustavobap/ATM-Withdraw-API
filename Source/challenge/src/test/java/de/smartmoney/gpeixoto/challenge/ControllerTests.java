package de.smartmoney.gpeixoto.challenge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureJsonTesters
@AutoConfigureMockMvc
public class ControllerTests extends BaseTest {

	@Autowired
	protected MockMvc mvc;

}
