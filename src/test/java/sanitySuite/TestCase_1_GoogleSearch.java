package sanitySuite;

import org.testng.annotations.Test;

import base.TestBase;


public class TestCase_1_GoogleSearch extends TestBase{




	@Test (priority=1, description = "Open Google Search URL")	
	public void open_url() {

		log.info("Open Google Search URL.");
		driver.get(data.getProperty("base.url"));


		//	  Assert.assertTrue(obj_google_search.get_first_option().equals(getPropertyValue("TestCase_1.assertString_1")));

	}

	@Test (priority=2, description = "Click on first search option")	
	public void click_first_search_option() {




	}

}
